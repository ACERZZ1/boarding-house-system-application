<?php
$conn = new mysqli("localhost", "root", "", "boarding_house");

$username = $_POST['username'];
$password = $_POST['password'];
$role = $_POST['role'];

// Optional: basic validation
if(empty($username) || empty($password) || empty($role)){
    echo json_encode(["status"=>"error","message"=>"All fields are required"]);
    exit;
}

// Check if username already exists
$stmt = $conn->prepare("SELECT id FROM login WHERE username=?");
$stmt->bind_param("s", $username);
$stmt->execute();
$result = $stmt->get_result();

if($result->num_rows > 0){
    echo json_encode(["status"=>"error","message"=>"Username already exists"]);
    exit;
}

// Insert new user
$stmt = $conn->prepare("INSERT INTO login (username, password, role) VALUES (?, ?, ?)");
$stmt->bind_param("sss", $username, $password, $role);

if($stmt->execute()){
    echo json_encode(["status"=>"success","role"=>$role]);
} else {
    echo json_encode(["status"=>"error","message"=>"Database error"]);
}
?>