<?php
/**
 * MONEYWISE API - Save Expense Record
 * File: save_expense.php
 * Deskripsi: Menyimpan data perhitungan pengeluaran mahasiswa menggunakan Prepared Statements
 */

require_once __DIR__ . '/db_connect.php';

// Pastikan method adalah POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode([
        "status"  => "error",
        "message" => "Method tidak diizinkan. Gunakan POST."
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit();
}

// Ambil data dari JSON body atau x-www-form-urlencoded / $_POST
$raw_input = file_get_contents("php://input");
$json_data = json_decode($raw_input, true);

if (!empty($json_data) && is_array($json_data)) {
    $uang_bulanan = isset($json_data['uang_bulanan']) ? $json_data['uang_bulanan'] : null;
    $makan        = isset($json_data['makan']) ? $json_data['makan'] : null;
    $transportasi = isset($json_data['transportasi']) ? $json_data['transportasi'] : null;
    $kuota        = isset($json_data['kuota']) ? $json_data['kuota'] : null;
    $kuliah       = isset($json_data['kuliah']) ? $json_data['kuliah'] : null;
    $status_input = isset($json_data['status']) ? trim($json_data['status']) : null;
} else {
    $uang_bulanan = isset($_POST['uang_bulanan']) ? $_POST['uang_bulanan'] : null;
    $makan        = isset($_POST['makan']) ? $_POST['makan'] : null;
    $transportasi = isset($_POST['transportasi']) ? $_POST['transportasi'] : null;
    $kuota        = isset($_POST['kuota']) ? $_POST['kuota'] : null;
    $kuliah       = isset($_POST['kuliah']) ? $_POST['kuliah'] : null;
    $status_input = isset($_POST['status']) ? trim($_POST['status']) : null;
}

// Validasi keberadaan input
if ($uang_bulanan === null || $makan === null || $transportasi === null || $kuota === null || $kuliah === null) {
    http_response_code(400);
    echo json_encode([
        "status"  => "error",
        "message" => "Data tidak lengkap. Harap isi uang_bulanan, makan, transportasi, kuota, dan kuliah."
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit();
}

// Validasi tipe data numerik
if (!is_numeric($uang_bulanan) || !is_numeric($makan) || !is_numeric($transportasi) || !is_numeric($kuota) || !is_numeric($kuliah)) {
    http_response_code(400);
    echo json_encode([
        "status"  => "error",
        "message" => "Semua nilai pengeluaran harus berupa angka numerik valid."
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit();
}

$uang_bulanan = (float)$uang_bulanan;
$makan        = (float)$makan;
$transportasi = (float)$transportasi;
$kuota        = (float)$kuota;
$kuliah       = (float)$kuliah;

// Validasi uang bulanan harus > 0
if ($uang_bulanan <= 0) {
    http_response_code(400);
    echo json_encode([
        "status"  => "error",
        "message" => "Uang bulanan harus lebih besar dari Rp 0."
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit();
}

// Validasi tidak boleh negatif
if ($makan < 0 || $transportasi < 0 || $kuota < 0 || $kuliah < 0) {
    http_response_code(400);
    echo json_encode([
        "status"  => "error",
        "message" => "Komponen pengeluaran tidak boleh bernilai negatif."
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit();
}

// Hitung total pengeluaran, sisa saldo, dan persentase penggunaan
$total_pengeluaran = $makan + $transportasi + $kuota + $kuliah;
$sisa_saldo        = $uang_bulanan - $total_pengeluaran;
$persentase        = round(($total_pengeluaran / $uang_bulanan) * 100, 2);

// Tentukan status jika tidak dikirim dari Android
if (!empty($status_input)) {
    $status = $status_input;
} else {
    if ($persentase <= 70.0) {
        $status = "Aman (Hemat)";
    } elseif ($persentase <= 100.0) {
        $status = "Waspada (Mendekati Batas)";
    } else {
        $status = "Defisit (Overbudget)";
    }
}

// Query menggunakan Prepared Statement untuk keamanan SQL Injection
$query = "INSERT INTO pengeluaran (uang_bulanan, makan, transportasi, kuota, kuliah, total_pengeluaran, sisa_saldo, persentase, status) 
          VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

$stmt = mysqli_prepare($conn, $query);

if (!$stmt) {
    http_response_code(500);
    echo json_encode([
        "status"  => "error",
        "message" => "Prepared statement gagal: " . mysqli_error($conn)
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit();
}

// Bind parameter (d = double, s = string)
// 8 double/float dan 1 string
mysqli_stmt_bind_param(
    $stmt, 
    "dddddddds", 
    $uang_bulanan, 
    $makan, 
    $transportasi, 
    $kuota, 
    $kuliah, 
    $total_pengeluaran, 
    $sisa_saldo, 
    $persentase, 
    $status
);

if (mysqli_stmt_execute($stmt)) {
    $inserted_id = mysqli_insert_id($conn);
    http_response_code(201);
    echo json_encode([
        "status"  => "success",
        "message" => "Data pengeluaran berhasil disimpan ke database!",
        "data"    => [
            "id"                => $inserted_id,
            "uang_bulanan"      => $uang_bulanan,
            "makan"             => $makan,
            "transportasi"      => $transportasi,
            "kuota"             => $kuota,
            "kuliah"            => $kuliah,
            "total_pengeluaran" => $total_pengeluaran,
            "sisa_saldo"        => $sisa_saldo,
            "persentase"        => $persentase,
            "status"            => $status
        ]
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
} else {
    http_response_code(500);
    echo json_encode([
        "status"  => "error",
        "message" => "Gagal menyimpan data: " . mysqli_stmt_error($stmt)
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
}

mysqli_stmt_close($stmt);
mysqli_close($conn);
?>

