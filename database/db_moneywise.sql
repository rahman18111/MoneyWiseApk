-- ========================================================
-- Database: db_moneywise
-- Aplikasi: MONEYWISE - Student Monthly Expense Calculator
-- Engine: MySQL / MariaDB (XAMPP)
-- ========================================================

CREATE DATABASE IF NOT EXISTS `db_moneywise` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `db_moneywise`;

-- --------------------------------------------------------
-- Struktur Tabel `pengeluaran`
-- --------------------------------------------------------
DROP TABLE IF EXISTS `pengeluaran`;

CREATE TABLE `pengeluaran` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Data Sampel Pengeluaran Mahasiswa (Opsional untuk testing)
-- --------------------------------------------------------
INSERT INTO `pengeluaran` 
(`uang_bulanan`, `makan`, `transportasi`, `kuota`, `kuliah`, `total_pengeluaran`, `sisa_saldo`, `persentase`, `status`, `tanggal`) 
VALUES 
(2000000.00, 700000.00, 250000.00, 100000.00, 200000.00, 1250000.00, 750000.00, 62.50, 'Aman (Hemat)', NOW() - INTERVAL 5 DAY),
(1800000.00, 850000.00, 300000.00, 150000.00, 350000.00, 1650000.00, 150000.00, 91.67, 'Waspada (Mendekati Batas)', NOW() - INTERVAL 2 DAY),
(1500000.00, 800000.00, 300000.00, 120000.00, 400000.00, 1620000.00, -120000.00, 108.00, 'Defisit (Overbudget)', NOW());

