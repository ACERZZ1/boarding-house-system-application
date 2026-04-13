<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "boarding_house";

header("Content-Type: application/json");

$boarding_id = intval($_GET['boarding_id'] ?? 0);
if ($boarding_id <= 0) {
    echo json_encode(["status" => "error", "message" => "Invalid boarding_id"]);
    exit;
}

try {
    $conn = new mysqli($host, $user, $pass, $db);

    $stmt = $conn->prepare(
        "SELECT id, unit_name, price, deposit, description, is_reserved
         FROM property_units
         WHERE boarding_id = ?
         ORDER BY id ASC"
    );
    $stmt->bind_param("i", $boarding_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $units = [];
    while ($row = $result->fetch_assoc()) {
        $units[] = $row;
    }

    echo json_encode(["status" => "success", "units" => $units]);

} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
} finally {
    if (isset($conn)) $conn->close();
}
?>