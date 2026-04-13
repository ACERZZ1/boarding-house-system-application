<?php
$conn = new mysqli("localhost", "root", "", "boarding_house");

echo "<h3>property_units columns:</h3>";
$r = $conn->query("DESCRIBE property_units");
while($row = $r->fetch_assoc()) print_r($row);

echo "<h3>add_boardinghouse columns:</h3>";
$r = $conn->query("DESCRIBE add_boardinghouse");
while($row = $r->fetch_assoc()) print_r($row);

echo "<h3>reservations columns:</h3>";
$r = $conn->query("DESCRIBE reservations");
while($row = $r->fetch_assoc()) print_r($row);

echo "<h3>All reservations:</h3>";
$r = $conn->query("SELECT * FROM reservations");
while($row = $r->fetch_assoc()) print_r($row);
?>