<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "boarding_house";

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {
    $conn = new mysqli($host, $user, $pass, $db);

    // 1. Collect and escape data
    $title          = $conn->real_escape_string($_POST['title'] ?? '');
    $property_type  = $conn->real_escape_string($_POST['property_type'] ?? '');
    $max_guest      = $conn->real_escape_string($_POST['max_guest'] ?? '0');
    $bedrooms       = $conn->real_escape_string($_POST['bedrooms'] ?? '0');
    $beds           = $conn->real_escape_string($_POST['beds'] ?? '0');
    $bathrooms      = $conn->real_escape_string($_POST['bathrooms'] ?? '0');
    $description    = $conn->real_escape_string($_POST['description'] ?? '');
    $street_address = $conn->real_escape_string($_POST['street_address'] ?? '');
    $city           = $conn->real_escape_string($_POST['city'] ?? '');
    $province       = $conn->real_escape_string($_POST['province'] ?? '');
    $zip_code       = $conn->real_escape_string($_POST['zip_code'] ?? '');
    $country        = $conn->real_escape_string($_POST['country'] ?? '');
    $services       = $conn->real_escape_string($_POST['services'] ?? '');
    $price          = $conn->real_escape_string($_POST['price'] ?? '0');
    $deposit        = $conn->real_escape_string($_POST['deposit'] ?? '0');
    $rules          = $conn->real_escape_string($_POST['rules'] ?? '');
    $owner_id       = $conn->real_escape_string($_POST['owner_id'] ?? '1');
    $first_name     = $conn->real_escape_string($_POST['first_name'] ?? '');
    $last_name      = $conn->real_escape_string($_POST['last_name'] ?? '');
    $email          = $conn->real_escape_string($_POST['email'] ?? '');
    $phone          = $conn->real_escape_string($_POST['phone'] ?? '');
    $gallery_count  = intval($_POST['gallery_count'] ?? 0);
    $latitude       = $conn->real_escape_string($_POST['latitude'] ?? '');
    $longitude      = $conn->real_escape_string($_POST['longitude'] ?? '');

    // 2. Insert boarding house
    $sql_house = "INSERT INTO add_boardinghouse 
        (title, property_type, max_guest, bedrooms, beds, bathrooms, description,
         street_address, city, province, zip_code, country, services,
         price, deposit, rules, owner_id, latitude, longitude)
        VALUES ('$title','$property_type','$max_guest','$bedrooms','$beds','$bathrooms',
                '$description','$street_address','$city','$province','$zip_code',
                '$country','$services','$price','$deposit','$rules','$owner_id',
                '$latitude','$longitude')";
    $conn->query($sql_house);
    $boarding_id = $conn->insert_id;

    // 3. Insert contact
    $sql_contact = "INSERT INTO boarding_contacts (boarding_id, first_name, last_name, email, phone)
                    VALUES ('$boarding_id','$first_name','$last_name','$email','$phone')";
    $conn->query($sql_contact);

    // 4. Setup upload dir
    $upload_dir = "uploads/";
    if (!is_dir($upload_dir)) mkdir($upload_dir, 0777, true);

    // 5. Handle cover image
    $cover_name = "no_image.jpg";
    if (isset($_FILES['cover_image']) && $_FILES['cover_image']['error'] == 0) {
        $ext = pathinfo($_FILES['cover_image']['name'], PATHINFO_EXTENSION) ?: "jpg";
        $cover_name = "cover_" . time() . "_" . $boarding_id . "." . $ext;
        move_uploaded_file($_FILES['cover_image']['tmp_name'], $upload_dir . $cover_name);
    }
    $sql_cover = "INSERT INTO boarding_images (boarding_id, image_path, is_cover)
                  VALUES ('$boarding_id','$cover_name', 1)";
    $conn->query($sql_cover);

    // 6. Handle multiple gallery images
    for ($i = 0; $i < $gallery_count; $i++) {
        $key = "gallery_image_" . $i;
        if (isset($_FILES[$key]) && $_FILES[$key]['error'] == 0) {
            $ext = pathinfo($_FILES[$key]['name'], PATHINFO_EXTENSION) ?: "jpg";
            $gallery_name = "gallery_" . time() . "_" . $boarding_id . "_" . $i . "." . $ext;
            if (move_uploaded_file($_FILES[$key]['tmp_name'], $upload_dir . $gallery_name)) {
                $sql_gal = "INSERT INTO boarding_images (boarding_id, image_path, is_cover)
                            VALUES ('$boarding_id','$gallery_name', 0)";
                $conn->query($sql_gal);
            }
        }
    }

    // ── 7. Save units (NEW — only runs if owner added units) ──────────
    $unitsJson = $_POST['units'] ?? '[]';
    $units     = json_decode($unitsJson, true);

    if (is_array($units) && count($units) > 0) {

        $unit_upload_dir = "uploads/units/";
        if (!is_dir($unit_upload_dir)) mkdir($unit_upload_dir, 0777, true);

        foreach ($units as $index => $unit) {
            $uName    = $conn->real_escape_string($unit['unit_name']   ?? '');
            $uPrice   = $conn->real_escape_string($unit['price']       ?? '0');
            $uDeposit = $conn->real_escape_string($unit['deposit']     ?? '0');
            $uDesc    = $conn->real_escape_string($unit['description'] ?? '');

            // Optional unit photo sent as unit_photo_0, unit_photo_1 ...
            $uImage   = '';
            $photoKey = 'unit_photo_' . $index;
            if (isset($_FILES[$photoKey]) && $_FILES[$photoKey]['error'] == 0) {
                $ext    = pathinfo($_FILES[$photoKey]['name'], PATHINFO_EXTENSION) ?: "jpg";
                $uImage = "unit_" . time() . "_" . $boarding_id . "_" . $index . "." . $ext;
                move_uploaded_file($_FILES[$photoKey]['tmp_name'], $unit_upload_dir . $uImage);
            }

            $conn->query("INSERT INTO property_units
                          (boarding_id, unit_name, price, deposit, description, image_path)
                          VALUES ('$boarding_id','$uName','$uPrice','$uDeposit','$uDesc','$uImage')");
        }
    }
    // ─────────────────────────────────────────────────────────────────

    echo "success";

} catch (mysqli_sql_exception $e) {
    echo "error: " . $e->getMessage();
} finally {
    if (isset($conn)) $conn->close();
}
?>