<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "boarding_house";

header("Content-Type: application/json");

$base_url = "http://192.168.254.104/casptone/uploads/";

try {
    $conn = new mysqli($host, $user, $pass, $db);

    $sql = "SELECT 
                b.id, b.title, b.property_type, b.max_guest, b.bedrooms, b.beds,
                b.bathrooms, b.description, b.street_address, b.city, b.province,
                b.zip_code, b.country, b.services, b.price, b.deposit, b.rules,
                b.latitude, b.longitude,b.is_reserved,
                c.first_name, c.last_name, c.email, c.phone
            FROM add_boardinghouse b
            LEFT JOIN boarding_contacts c ON c.boarding_id = b.id
            ORDER BY b.id DESC";

    $result = $conn->query($sql);
    $data = [];

    while ($row = $result->fetch_assoc()) {
        $boarding_id = $row['id'];

        $img_sql = "SELECT image_path, is_cover FROM boarding_images WHERE boarding_id = '$boarding_id' ORDER BY is_cover DESC";
        $img_result = $conn->query($img_sql);

        $cover_url = "";
        $gallery_urls = [];

        while ($img = $img_result->fetch_assoc()) {
            $path = $img['image_path'];
            if (empty($path) || $path === 'no_image.jpg') continue;
            $url = $base_url . $path;
            if ($img['is_cover'] == 1) {
                $cover_url = $url;
            } else {
                $gallery_urls[] = $url;
            }
        }

        $row['cover_image_url']    = $cover_url;
        $row['gallery_image_urls'] = $gallery_urls;

        $data[] = $row;
    }

    echo json_encode(["status" => "success", "data" => $data]);

} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
} finally {
    if (isset($conn)) $conn->close();
}
?>
