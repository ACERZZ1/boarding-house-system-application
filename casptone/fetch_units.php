<?php
// fetch_units.php — returns all units for a given boarding_id
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

$boarding_id = intval($_GET['boarding_id'] ?? 0);
if ($boarding_id <= 0) {
    echo json_encode(["status" => "error", "message" => "Invalid boarding_id"]);
    exit;
}

$result = $conn->query("SELECT id, unit_name, price, deposit, description, image_path, is_reserved
                         FROM property_units
                         WHERE boarding_id = $boarding_id
                         ORDER BY id ASC");

$units = [];
while ($row = $result->fetch_assoc()) {
    // Build full image URL if image exists
    $imagePath = $row['image_path'];
    $imageUrl  = (!empty($imagePath))
        ? "http://192.168.254.104/casptone/uploads/units/" . $imagePath
        : "";

    $units[] = [
        "id"          => $row['id'],
        "unit_name"   => $row['unit_name'],
        "price"       => $row['price'],
        "deposit"     => $row['deposit'],
        "description" => $row['description'],
        "image_url"   => $imageUrl,
        "is_reserved" => $row['is_reserved']
    ];
}

echo json_encode(["status" => "success", "units" => $units]);
$conn->close();
?>