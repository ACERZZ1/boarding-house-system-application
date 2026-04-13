<?php
// fetch_my_reservations.php — boarder fetches their own reservations with owner contact if approved
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

$user_id = intval($_GET['user_id'] ?? 0);
if ($user_id <= 0) {
    echo json_encode(["status" => "error", "message" => "Invalid user_id"]);
    exit;
}

$sql = "SELECT r.id, r.boarding_id, r.unit_id, r.move_in_date, r.message, r.status, r.created_at,
               b.title AS property_name,
               pu.unit_name,
               bc.first_name, bc.last_name, bc.email AS owner_email, bc.phone AS owner_phone
        FROM reservations r
        JOIN add_boardinghouse b  ON r.boarding_id = b.id
        LEFT JOIN property_units pu ON r.unit_id = pu.id
        LEFT JOIN boarding_contacts bc ON r.boarding_id = bc.boarding_id
        WHERE r.user_id = $user_id
        ORDER BY r.created_at DESC";

$result = $conn->query($sql);
$reservations = [];

while ($row = $result->fetch_assoc()) {
    $item = [
        "id"            => $row['id'],
        "boarding_id"   => $row['boarding_id'],
        "unit_id"       => $row['unit_id'],
        "property_name" => $row['property_name'],
        "unit_name"     => $row['unit_name'] ?? "",
        "move_in_date"  => $row['move_in_date'],
        "message"       => $row['message'],
        "status"        => $row['status'],
        "created_at"    => $row['created_at'],
        // Only include owner contact if approved
        "owner_name"    => $row['status'] === 'approved'
                            ? $row['first_name'] . ' ' . $row['last_name'] : "",
        "owner_email"   => $row['status'] === 'approved' ? $row['owner_email'] : "",
        "owner_phone"   => $row['status'] === 'approved' ? $row['owner_phone'] : "",
    ];
    $reservations[] = $item;
}

echo json_encode(["status" => "success", "reservations" => $reservations]);
$conn->close();
?>