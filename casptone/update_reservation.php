<?php
header('Content-Type: application/json');

$conn = new mysqli("localhost", "root", "", "boarding_house");
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "DB connection failed"]);
    exit;
}

$reservation_id = intval($_POST['reservation_id'] ?? 0);
$action         = trim($_POST['action'] ?? '');

if ($reservation_id <= 0 || !in_array($action, ['approve', 'reject'])) {
    echo json_encode(["status" => "error", "message" => "Invalid parameters"]);
    exit;
}

// ── Lock the row immediately to prevent double-approval ──────────────
$conn->begin_transaction();

$res = $conn->query(
    "SELECT unit_id, boarding_id, status 
     FROM reservations 
     WHERE id = $reservation_id 
     FOR UPDATE"  // locks the row — second click must wait
);
$row = $res->fetch_assoc();

if (!$row) {
    $conn->rollback();
    echo json_encode(["status" => "error", "message" => "Reservation not found"]);
    exit;
}

// Already processed — don't allow double action
if ($row['status'] !== 'pending') {
    $conn->rollback();
    $statusMsg = $row['status'] === 'rejected' 
    ? "This reservation was already rejected (another tenant was approved first)."
    : "This reservation is already " . $row['status'] . ".";
    echo json_encode(["status" => "error", "message" => $statusMsg]);
    exit;
}

$unit_id     = ($row['unit_id'] !== null && $row['unit_id'] != 0) 
               ? intval($row['unit_id']) 
               : null;
$boarding_id = intval($row['boarding_id']);

// ── If approving, check nothing is already approved ──────────────────
if ($action === 'approve') {
    if ($unit_id !== null) {
        // Is this unit already approved for someone else?
        $chk = $conn->prepare(
            "SELECT id FROM reservations 
             WHERE unit_id = ? AND id != ? AND status = 'approved'"
        );
        $chk->bind_param("ii", $unit_id, $reservation_id);
        $chk->execute();
        $chk->store_result();
        $already = $chk->num_rows > 0;
        $chk->close();
        if ($already) {
            $conn->rollback();
            echo json_encode(["status" => "error", "message" => "This unit was already approved for another tenant."]);
            exit;
        }
    } else {
        // Is this whole property already approved for someone else?
        $chk = $conn->prepare(
            "SELECT id FROM reservations 
             WHERE boarding_id = ? AND id != ? AND status = 'approved' AND unit_id IS NULL"
        );
        $chk->bind_param("ii", $boarding_id, $reservation_id);
        $chk->execute();
        $chk->store_result();
        $already = $chk->num_rows > 0;
        $chk->close();
        if ($already) {
            $conn->rollback();
            echo json_encode(["status" => "error", "message" => "This property was already approved for another tenant."]);
            exit;
        }
    }
}

// ── Update this reservation ───────────────────────────────────────────
$new_status = ($action === 'approve') ? 'approved' : 'rejected';
$stmt = $conn->prepare("UPDATE reservations SET status = ? WHERE id = ?");
$stmt->bind_param("si", $new_status, $reservation_id);
$stmt->execute();
$stmt->close();

// ── If approved, lock everything and reject all competing pending ─────
if ($action === 'approve') {

    if ($unit_id !== null) {
        // Mark this unit reserved
        $conn->query("UPDATE property_units SET is_reserved = 1 WHERE id = $unit_id");

        // Reject all other pending for this same unit
        $stmt2 = $conn->prepare(
            "UPDATE reservations SET status = 'rejected'
             WHERE unit_id = ? AND id != ? AND status = 'pending'"
        );
        $stmt2->bind_param("ii", $unit_id, $reservation_id);
        $stmt2->execute();
        $stmt2->close();

        // If ALL units of this property are now reserved, mark whole property reserved
        $chk = $conn->prepare(
            "SELECT COUNT(*) as total, SUM(is_reserved) as reserved_count
             FROM property_units WHERE boarding_id = ?"
        );
        $chk->bind_param("i", $boarding_id);
        $chk->execute();
        $chk->bind_result($total, $reserved_count);
        $chk->fetch();
        $chk->close();

        if ($total > 0 && $total == $reserved_count) {
            $conn->query("UPDATE add_boardinghouse SET is_reserved = 1 WHERE id = $boarding_id");
            // Also reject any whole-property pending reservations
            $stmt3 = $conn->prepare(
                "UPDATE reservations SET status = 'rejected'
                 WHERE boarding_id = ? AND unit_id IS NULL AND status = 'pending'"
            );
            $stmt3->bind_param("i", $boarding_id);
            $stmt3->execute();
            $stmt3->close();
        }

    } else {
        // Whole property approved — lock everything
        $conn->query("UPDATE add_boardinghouse SET is_reserved = 1 WHERE id = $boarding_id");
        $conn->query("UPDATE property_units SET is_reserved = 1 WHERE boarding_id = $boarding_id");

        // Reject ALL other pending for this property (any unit or whole-property)
        $stmt2 = $conn->prepare(
            "UPDATE reservations SET status = 'rejected'
             WHERE boarding_id = ? AND id != ? AND status = 'pending'"
        );
        $stmt2->bind_param("ii", $boarding_id, $reservation_id);
        $stmt2->execute();
        $stmt2->close();
    }
}

$conn->commit();
echo json_encode(["status" => "success", "message" => "Reservation " . $new_status]);
$conn->close();
?>