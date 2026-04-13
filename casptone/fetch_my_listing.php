<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "boarding_house";

header("Content-Type: application/json");

$base_url = "http://192.168.254.104/casptone/uploads/";
$owner_id = intval($_GET['owner_id'] ?? 0);

if ($owner_id <= 0) {
    echo json_encode(["status" => "error", "message" => "Invalid owner_id"]);
    exit;
}

try {
    $conn = new mysqli($host, $user, $pass, $db);

    $sql = "SELECT b.id, b.title, b.property_type, b.price, b.deposit,
                   b.description, b.rules, b.services,
                   b.max_guest, b.bedrooms, b.beds, b.bathrooms,
                   b.street_address, b.city, b.province, b.zip_code, b.country,
                   b.latitude, b.longitude,
                   b.is_reserved,
                   c.first_name, c.last_name, c.email, c.phone,
                   i.image_path AS cover_image
            FROM add_boardinghouse b
            LEFT JOIN boarding_contacts c ON c.boarding_id = b.id
            LEFT JOIN boarding_images i ON i.boarding_id = b.id AND i.is_cover = 1
            WHERE b.owner_id = ?
            ORDER BY b.id DESC";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $owner_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $data   = [];

    while ($row = $result->fetch_assoc()) {
        $img = $row['cover_image'];
        $row['cover_image_url'] = (!empty($img) && $img !== 'no_image.jpg')
            ? $base_url . $img
            : "";
        unset($row['cover_image']);
        $data[] = $row;
    }

    echo json_encode(["status" => "success", "data" => $data]);

} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
} finally {
    if (isset($conn)) $conn->close();
}
?>
