<?php
header('Content-Type: application/json');

$conn = new mysqli("localhost", "root", "", "boarding_house");
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "DB connection failed"]);
    exit;
}

$user_id     = intval($_POST['user_id']     ?? 0);
$boarding_id = intval($_POST['boarding_id'] ?? 0);
$move_in     = trim($_POST['move_in_date']  ?? '');
$message     = trim($_POST['message']       ?? '');
// CRITICAL: keep as true NULL if not provided
$unit_id     = isset($_POST['unit_id']) && $_POST['unit_id'] !== '' 
               ? intval($_POST['unit_id']) 
               : null;

if ($user_id <= 0 || $boarding_id <= 0 || empty($move_in)) {
    echo json_encode(["status" => "error", "message" => "Missing required fields"]);
    exit;
}

// ── Check 1: Is unit/property already reserved? ──────────────────────
if ($unit_id !== null) {
    $chk = $conn->prepare("SELECT is_reserved FROM property_units WHERE id = ?");
    $chk->bind_param("i", $unit_id);
    $chk->execute();
    $chk->bind_result($is_reserved);
    $chk->fetch();
    $chk->close();
    if ($is_reserved == 1) {
        echo json_encode(["status" => "error", "message" => "This unit is already reserved."]);
        exit;
    }
} else {
    $chk = $conn->prepare("SELECT is_reserved FROM add_boardinghouse WHERE id = ?");
    $chk->bind_param("i", $boarding_id);
    $chk->execute();
    $chk->bind_result($is_reserved);
    $chk->fetch();
    $chk->close();
    if ($is_reserved == 1) {
        echo json_encode(["status" => "error", "message" => "This property is already reserved."]);
        exit;
    }
}

// ── Check 2: Same user already has active reservation for this ───────
if ($unit_id !== null) {
    $chk = $conn->prepare(
        "SELECT id FROM reservations 
         WHERE user_id = ? AND boarding_id = ? AND unit_id = ? 
         AND status IN ('pending','approved')"
    );
    $chk->bind_param("iii", $user_id, $boarding_id, $unit_id);
} else {
    $chk = $conn->prepare(
        "SELECT id FROM reservations 
         WHERE user_id = ? AND boarding_id = ? AND unit_id IS NULL
         AND status IN ('pending','approved')"
    );
    $chk->bind_param("ii", $user_id, $boarding_id);
}
$chk->execute();
$chk->store_result();
if ($chk->num_rows > 0) {
    $chk->close();
    echo json_encode(["status" => "error", "message" => "You already have an active reservation for this."]);
    exit;
}
$chk->close();

// ── Insert — bind NULL correctly using a variable ────────────────────
$stmt = $conn->prepare(
    "INSERT INTO reservations (user_id, boarding_id, unit_id, move_in_date, message, status, created_at)
     VALUES (?, ?, ?, ?, ?, 'pending', NOW())"
);
// bind_param cannot take null literal — must use variable
$stmt->bind_param("iiiss", $user_id, $boarding_id, $unit_id, $move_in, $message);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Reservation submitted"]);
} else {
    echo json_encode(["status" => "error", "message" => "Failed: " . $stmt->error]);
}
$stmt->close();
$conn->close();
?>