<?php
header('Content-Type: application/json');

$conn = new mysqli("localhost", "root", "", "boarding_house");
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "DB connection failed"]);
    exit;
}

$type       = trim($_POST['type'] ?? '');       // "property" or "unit"
$id         = intval($_POST['id'] ?? 0);
$owner_id   = intval($_POST['owner_id'] ?? 0);  // for ownership verification

if (!in_array($type, ['property', 'unit']) || $id <= 0 || $owner_id <= 0) {
    echo json_encode(["status" => "error", "message" => "Invalid parameters"]);
    exit;
}

if ($type === 'property') {
    // Verify this property belongs to the owner
    $check = $conn->prepare("SELECT id FROM add_boardinghouse WHERE id = ? AND owner_id = ?");
    $check->bind_param("ii", $id, $owner_id);
    $check->execute();
    $check->store_result();
    if ($check->num_rows === 0) {
        echo json_encode(["status" => "error", "message" => "Unauthorized"]);
        exit;
    }
    $check->close();

    // Reset the property
    $conn->query("UPDATE add_boardinghouse SET is_reserved = 0 WHERE id = $id");
    // Also reset all its units
    $conn->query("UPDATE property_units SET is_reserved = 0 WHERE boarding_id = $id");
    // Cancel all approved reservations for this property so old tenant doesn't still show as approved
    $conn->query("UPDATE reservations SET status = 'rejected' WHERE boarding_id = $id AND status = 'approved'");

    echo json_encode(["status" => "success", "message" => "Property is now available again"]);

} else {
    // type = unit — verify ownership via boarding_id
    $check = $conn->prepare("
        SELECT pu.id FROM property_units pu
        JOIN add_boardinghouse b ON b.id = pu.boarding_id
        WHERE pu.id = ? AND b.owner_id = ?
    ");
    $check->bind_param("ii", $id, $owner_id);
    $check->execute();
    $check->store_result();
    if ($check->num_rows === 0) {
        echo json_encode(["status" => "error", "message" => "Unauthorized"]);
        exit;
    }
    $check->close();

    // Reset just this unit
    $conn->query("UPDATE property_units SET is_reserved = 0 WHERE id = $id");
    // Cancel the approved reservation for this unit
    $conn->query("UPDATE reservations SET status = 'rejected' WHERE unit_id = $id AND status = 'approved'");

    // If the parent property was fully reserved, un-reserve it too
    $res = $conn->query("SELECT boarding_id FROM property_units WHERE id = $id");
    $row = $res->fetch_assoc();
    if ($row) {
        $boarding_id = intval($row['boarding_id']);
        $conn->query("UPDATE add_boardinghouse SET is_reserved = 0 WHERE id = $boarding_id");
    }

    echo json_encode(["status" => "success", "message" => "Unit is now available again"]);
}

$conn->close();
?>