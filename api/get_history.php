<?php
/**
 * MONEYWISE API - Get Expense History
 * File: get_history.php
 * Deskripsi: Mengambil seluruh riwayat data pengeluaran mahasiswa secara urut waktu (terbaru ke terlama)
 */

require_once __DIR__ . '/db_connect.php';

// Method GET atau POST diterima
$query = "SELECT id, uang_bulanan, makan, transportasi, kuota, kuliah, 
                 total_pengeluaran, sisa_saldo, persentase, status, tanggal 
          FROM pengeluaran 
          ORDER BY tanggal DESC, id DESC";

$stmt = mysqli_prepare($conn, $query);

if (!$stmt) {
    http_response_code(500);
    echo json_encode([
        "status"  => "error",
        "message" => "Gagal mempersiapkan query: " . mysqli_error($conn)
    ], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit();
}

mysqli_stmt_execute($stmt);
$result = mysqli_stmt_get_result($stmt);

$history = [];

if ($result) {
    while ($row = mysqli_fetch_assoc($result)) {
        $history[] = [
            "id"                => (int)$row['id'],
            "uang_bulanan"      => (float)$row['uang_bulanan'],
            "makan"             => (float)$row['makan'],
            "transportasi"      => (float)$row['transportasi'],
            "kuota"             => (float)$row['kuota'],
            "kuliah"            => (float)$row['kuliah'],
            "total_pengeluaran" => (float)$row['total_pengeluaran'],
            "sisa_saldo"        => (float)$row['sisa_saldo'],
            "persentase"        => (float)$row['persentase'],
            "status"            => $row['status'],
            "tanggal"           => $row['tanggal']
        ];
    }
}

http_response_code(200);
echo json_encode([
    "status"  => "success",
    "count"   => count($history),
    "message" => count($history) > 0 ? "Data riwayat berhasil diambil" : "Belum ada riwayat pengeluaran tersimpan",
    "data"    => $history
], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);

mysqli_stmt_close($stmt);
mysqli_close($conn);
?>

