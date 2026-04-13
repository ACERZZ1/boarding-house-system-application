<?php
$host = "localhost";
$user = "root";
$pass = "";
$db   = "boarding_house";

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {
    $conn = new mysqli($host, $user, $pass, $db);

    // ── 1. ID ─────────────────────────────────────────────────────────────────
    $id = intval($_POST['id'] ?? 0);
    if ($id <= 0) { echo "error: Invalid ID"; exit; }

    // ── 2. Main fields (same as before) ───────────────────────────────────────
    $title          = $conn->real_escape_string($_POST['title']          ?? '');
    $property_type  = $conn->real_escape_string($_POST['property_type']  ?? '');
    $max_guest      = $conn->real_escape_string($_POST['max_guest']      ?? '0');
    $bedrooms       = $conn->real_escape_string($_POST['bedrooms']       ?? '0');
    $beds           = $conn->real_escape_string($_POST['beds']           ?? '0');
    $bathrooms      = $conn->real_escape_string($_POST['bathrooms']      ?? '0');
    $description    = $conn->real_escape_string($_POST['description']    ?? '');
    $street_address = $conn->real_escape_string($_POST['street_address'] ?? '');
    $city           = $conn->real_escape_string($_POST['city']           ?? '');
    $province       = $conn->real_escape_string($_POST['province']       ?? '');
    $zip_code       = $conn->real_escape_string($_POST['zip_code']       ?? '');
    $country        = $conn->real_escape_string($_POST['country']        ?? '');
    $services       = $conn->real_escape_string($_POST['services']       ?? '');
    $price          = $conn->real_escape_string($_POST['price']          ?? '0');
    $deposit        = $conn->real_escape_string($_POST['deposit']        ?? '0');
    $rules          = $conn->real_escape_string($_POST['rules']          ?? '');
    $first_name     = $conn->real_escape_string($_POST['first_name']     ?? '');
    $last_name      = $conn->real_escape_string($_POST['last_name']      ?? '');
    $email          = $conn->real_escape_string($_POST['email']          ?? '');
    $phone          = $conn->real_escape_string($_POST['phone']          ?? '');
    $latitude       = floatval($_POST['latitude']  ?? 0);
    $longitude      = floatval($_POST['longitude'] ?? 0);

    // ── 3. Update main listing ────────────────────────────────────────────────
    $conn->query("UPDATE add_boardinghouse SET
        title          = '$title',
        property_type  = '$property_type',
        max_guest      = '$max_guest',
        bedrooms       = '$bedrooms',
        beds           = '$beds',
        bathrooms      = '$bathrooms',
        description    = '$description',
        street_address = '$street_address',
        city           = '$city',
        province       = '$province',
        zip_code       = '$zip_code',
        country        = '$country',
        services       = '$services',
        price          = '$price',
        deposit        = '$deposit',
        rules          = '$rules',
        latitude       = $latitude,
        longitude      = $longitude
    WHERE id = $id");

    // ── 4. Update contact ─────────────────────────────────────────────────────
    $conn->query("UPDATE boarding_contacts SET
        first_name = '$first_name',
        last_name  = '$last_name',
        email      = '$email',
        phone      = '$phone'
    WHERE boarding_id = $id");

    // ── 5. Images (same as before) ────────────────────────────────────────────
    $upload_dir = "uploads/";
    if (!is_dir($upload_dir)) mkdir($upload_dir, 0777, true);

    if (isset($_FILES['cover_image']) && $_FILES['cover_image']['error'] == 0) {
        $ext        = pathinfo($_FILES['cover_image']['name'], PATHINFO_EXTENSION) ?: "jpg";
        $cover_name = "cover_" . time() . "_" . $id . "." . $ext;
        if (move_uploaded_file($_FILES['cover_image']['tmp_name'], $upload_dir . $cover_name)) {
            $conn->query("DELETE FROM boarding_images WHERE boarding_id = $id AND is_cover = 1");
            $conn->query("INSERT INTO boarding_images (boarding_id, image_path, is_cover)
                          VALUES ('$id', '$cover_name', 1)");
        }
    }

    if (isset($_FILES['gallery_image']) && $_FILES['gallery_image']['error'] == 0) {
        $ext          = pathinfo($_FILES['gallery_image']['name'], PATHINFO_EXTENSION) ?: "jpg";
        $gallery_name = "gallery_" . time() . "_" . $id . "." . $ext;
        if (move_uploaded_file($_FILES['gallery_image']['tmp_name'], $upload_dir . $gallery_name)) {
            $conn->query("INSERT INTO boarding_images (boarding_id, image_path, is_cover)
                          VALUES ('$id', '$gallery_name', 0)");
        }
    }

    // ── 6. Units — NEW ────────────────────────────────────────────────────────

    // 6a. Delete removed units
    $deleted_raw = trim($_POST['deleted_unit_ids'] ?? '');
    if (!empty($deleted_raw)) {
        $deleted_raw = preg_replace('/[^0-9,]/', '', $deleted_raw);
        if (!empty($deleted_raw)) {
            $conn->query("DELETE FROM property_units
                          WHERE id IN ($deleted_raw) AND boarding_id = $id");
        }
    }

    // 6b. Insert or update each unit row
    $unit_count = intval($_POST['unit_count'] ?? 0);
    for ($n = 0; $n < $unit_count; $n++) {
        $unit_id          = intval($_POST["unit_id_$n"]          ?? 0);
        $unit_name        = $conn->real_escape_string($_POST["unit_name_$n"]        ?? '');
        $unit_price       = $conn->real_escape_string($_POST["unit_price_$n"]       ?? '0');
        $unit_deposit     = $conn->real_escape_string($_POST["unit_deposit_$n"]     ?? '0');
        $unit_description = $conn->real_escape_string($_POST["unit_description_$n"] ?? '');

        if (empty($unit_name)) continue; // skip blank rows

        // Handle unit image if uploaded
        $unit_image_name = null;
        $file_key = "unit_image_$n";
        if (isset($_FILES[$file_key]) && $_FILES[$file_key]['error'] == 0) {
            $ext             = pathinfo($_FILES[$file_key]['name'], PATHINFO_EXTENSION) ?: "jpg";
            $unit_image_name = "unit_" . time() . "_" . $id . "_" . $n . "." . $ext;
            if (!move_uploaded_file($_FILES[$file_key]['tmp_name'], $upload_dir . $unit_image_name)) {
                $unit_image_name = null; // upload failed, keep existing
            }
        }

        if ($unit_id > 0) {
            // Update existing unit (never touch is_reserved)
            if ($unit_image_name) {
                // Update with new image
                $conn->query("UPDATE property_units SET
                    unit_name   = '$unit_name',
                    price       = '$unit_price',
                    deposit     = '$unit_deposit',
                    description = '$unit_description',
                    image_path  = '$unit_image_name'
                WHERE id = $unit_id AND boarding_id = $id");
            } else {
                // Update without touching image
                $conn->query("UPDATE property_units SET
                    unit_name   = '$unit_name',
                    price       = '$unit_price',
                    deposit     = '$unit_deposit',
                    description = '$unit_description'
                WHERE id = $unit_id AND boarding_id = $id");
            }
        } else {
            // New unit — INSERT
            $img_val = $unit_image_name ? "'$unit_image_name'" : "NULL";
            $conn->query("INSERT INTO property_units
                (boarding_id, unit_name, price, deposit, description, image_path, is_reserved)
                VALUES ($id, '$unit_name', '$unit_price', '$unit_deposit',
                        '$unit_description', $img_val, 0)");
        }
    }

    echo "success";

} catch (mysqli_sql_exception $e) {
    echo "error: " . $e->getMessage();
} finally {
    if (isset($conn)) $conn->close();
}
?>