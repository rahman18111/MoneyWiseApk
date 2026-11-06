<?php
/**
 * MONEYWISE API - Server Diagnostic & Connection Tester
 * File: test_api.php
 * Akses: http://localhost/MoneyWiseApk/api/test_api.php
 */

header("Content-Type: application/json; charset=UTF-8");

$db_host = "localhost";
$db_user = "root";
$db_pass = "";
$db_name = "db_moneywise";

$response = [
    "app_name"     => "MONEYWISE API",
    "version"      => "1.0.0",
    "php_version"  => phpversion(),
    "server_time"  => date("Y-m-d H:i:s"),
    "endpoints"    => [
        "get_history"  => "http://localhost/MoneyWiseApk/api/get_history.php",
        "save_expense" => "http://localhost/MoneyWiseApk/api/save_expense.php"
    ]
];

$conn = @mysqli_connect($db_host, $db_user, $db_pass, $db_name);

if ($conn) {
    $response["database_status"] = "CONNECTED (Sukses terhubung ke database `db_moneywise`)";
    
    // Hitung jumlah record
    $res = mysqli_query($conn, "SELECT COUNT(*) as total FROM pengeluaran");
    if ($res) {
        $row = mysqli_fetch_assoc($res);
        $response["total_records"] = (int)$row['total'];
    }
    mysqli_close($conn);
} else {
    $response["database_status"] = "DISCONNECTED: " . mysqli_connect_error();
    $response["hint"] = "Pastikan Apache & MySQL di XAMPP sudah di-START dan database `db_moneywise` sudah di-import melalui phpMyAdmin.";
}

echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
?>

