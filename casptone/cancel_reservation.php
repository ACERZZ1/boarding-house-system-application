<?php
// cancel_reservation.php — boarder cancels their own pending reservation
header('Content-Type: application/json');

$host = "localhost";
$user = "root";
$pass = "";
$db   = "boarding_house";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "DB connection failed"]);
    exit;
}

$reservation_id = intval($_POST['reservation_id'] ?? 0);
$user_id        = intval($_POST['user_id']        ?? 0);

if ($reservation_id <= 0 || $user_id <= 0) {
    echo json_encode(["status" => "error", "message" => "Invalid parameters"]);
    exit;
}

// Only allow cancel if it belongs to this user and is still pending
$stmt = $conn->prepare(
    "DELETE FROM reservations WHERE id = ? AND user_id = ? AND status = 'pending'"
);
$stmt->bind_param("ii", $reservation_id, $user_id);
$stmt->execute();

if ($stmt->affected_rows > 0) {
    echo json_encode(["status" => "success", "message" => "Reservation cancelled"]);
} else {
    echo json_encode(["status" => "error", "message" => "Could not cancel. It may already be approved or rejected."]);
}

$stmt->close();
$conn->close();
?>