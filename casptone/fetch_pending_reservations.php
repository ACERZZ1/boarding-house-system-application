<?php
header('Content-Type: application/json');

$conn = new mysqli("localhost", "root", "", "boarding_house");
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit;
}

$owner_id = isset($_GET['owner_id']) ? intval($_GET['owner_id']) : 0;
if ($owner_id === 0) {
    echo json_encode(["status" => "error", "message" => "Missing owner_id"]);
    exit;
}

$sql = "SELECT 
            r.id,
            r.move_in_date,
            r.message,
            r.status,
            b.title        AS property_name,
            l.username     AS boarder_name,
            pu.unit_name   AS unit_name
        FROM reservations r
        JOIN add_boardinghouse b  ON r.boarding_id = b.id
        JOIN login l              ON r.user_id = l.id
        LEFT JOIN property_units pu ON r.unit_id = pu.id
        WHERE b.owner_id = ?
        AND r.status = 'pending'
        ORDER BY r.created_at DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $owner_id);
$stmt->execute();
$result = $stmt->get_result();

$reservations = [];
while ($row = $result->fetch_assoc()) {
    $reservations[] = [
        "id"            => (int)$row['id'],
        "boarder_name"  => $row['boarder_name'] ?? "",
        "boarder_email" => "",
        "property_name" => $row['property_name'] ?? "",
        "unit_name"     => $row['unit_name'] ?? "",
        "move_in_date"  => $row['move_in_date'] ?? "",
        "message"       => $row['message'] ?? ""
    ];
}

echo json_encode(["status" => "success", "reservations" => $reservations]);

$stmt->close();
$conn->close();
?>