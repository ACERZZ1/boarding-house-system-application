<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "boarding_house";

$conn = new mysqli($host, $user, $pass, $db);

$id = $_POST['id'] ?? '';

if(!empty($id)){
    // Delete from all related tables
    $conn->query("DELETE FROM boarding_images WHERE boarding_id = '$id'");
    $conn->query("DELETE FROM boarding_contacts WHERE boarding_id = '$id'");
    $conn->query("DELETE FROM add_boardinghouse WHERE id = '$id'");
    
    echo "success";
} else {
    echo "error: No ID provided";
}

$conn->close();
?>
