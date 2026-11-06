<?php
/**
 * MONEYWISE API - Database Connection
 * File: db_connect.php
 * Deskripsi: Menghubungkan API PHP dengan Database MySQL XAMPP.
 * Dilengkapi fitur AUTO-MIGRATE jika database `db_moneywise` belum dibuat.
 */

// Header response format JSON dan CORS
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Nonaktifkan uncaught exception PHP 8.1+ agar error dapat ditangani secara aman
mysqli_report(MYSQLI_REPORT_OFF);

// Konfigurasi Database XAMPP
$db_host = "localhost";
$db_user = "root";
$db_pass = "";
$db_name = "db_moneywise";

// Coba koneksi langsung ke db_moneywise
$conn = @mysqli_connect($db_host, $db_user, $db_pass, $db_name);

// Jika database belum ada di MySQL (Error 1049: Unknown database), buat otomatis!
if (!$conn && mysqli_connect_errno() == 1049) {
    // 1. Hubungkan ke server MySQL tanpa memilih database
    $server_conn = @mysqli_connect($db_host, $db_user, $db_pass);
    
    if ($server_conn) {
        // 2. Buat database db_moneywise
        mysqli_query($server_conn, "CREATE DATABASE IF NOT EXISTS `db_moneywise` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        mysqli_select_db($server_conn, "db_moneywise");

        // 3. Buat tabel pengeluaran
        $sql_table = "CREATE TABLE IF NOT EXISTS `pengeluaran` (
            `id` INT AUTO_INCREMENT PRIMARY KEY,
            `uang_bulanan` DECIMAL(12,2) NOT NULL,
            `makan` DECIMAL(12,2) NOT NULL,
            `transportasi` DECIMAL(12,2) NOT NULL,
            `kuota` DECIMAL(12,2) NOT NULL,
            `kuliah` DECIMAL(12,2) NOT NULL,
            `total_pengeluaran` DECIMAL(12,2) NOT NULL,
            `sisa_saldo` DECIMAL(12,2) NOT NULL,
            `persentase` DECIMAL(5,2) NOT NULL,
            `status` VARCHAR(50) NOT NULL,
            `tanggal` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        mysqli_query($server_conn, $sql_table);

        // 4. Masukkan data sampel awal
        $check = mysqli_query($server_conn, "SELECT COUNT(*) as total FROM `pengeluaran`");
        if ($check) {
            $row = mysqli_fetch_assoc($check);
            if ($row['total'] == 0) {
                $sql_sample = "INSERT INTO `pengeluaran` 
                    (`uang_bulanan`, `makan`, `transportasi`, `kuota`, `kuliah`, `total_pengeluaran`, `sisa_saldo`, `persentase`, `status`) 
                    VALUES 
                    (2000000.00, 700000.00, 250000.00, 100000.00, 200000.00, 1250000.00, 750000.00, 62.50, 'Aman (Hemat)'),
                    (1800000.00, 850000.00, 300000.00, 150000.00, 350000.00, 1650000.00, 150000.00, 91.67, 'Waspada (Mendekati Batas)'),
                    (1500000.00, 800000.00, 300000.00, 120000.00, 400000.00, 1620000.00, -120000.00, 108.00, 'Defisit (Overbudget)')";
                mysqli_query($server_conn, $sql_sample);
            }
        }

        $conn = $server_conn;
    }
}

// Periksa kembali koneksi
if (!$conn) {
    http_response_code(500);
    echo json_encode([
        "status"  => "error",
        "message" => "Gagal terhubung ke database MySQL: " . mysqli_connect_error(),
        "hint"    => "Pastikan MySQL di XAMPP sudah di-START."
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit();
}

// Set karakter encoding ke UTF-8
mysqli_set_charset($conn, "utf8mb4");
?>
