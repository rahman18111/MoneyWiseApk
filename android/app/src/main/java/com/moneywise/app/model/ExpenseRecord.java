package com.moneywise.app.model;

import org.json.JSONObject;

/**
 * Model data entitas pengeluaran bulanan mahasiswa
 */
public class ExpenseRecord {

    private int id;
    private double uangBulanan;
    private double makan;
    private double transportasi;
    private double kuota;
    private double kuliah;
    private double totalPengeluaran;
    private double sisaSaldo;
    private double persentase;
    private String status;
    private String tanggal;

    public ExpenseRecord() {
    }

    public ExpenseRecord(int id, double uangBulanan, double makan, double transportasi,
                         double kuota, double kuliah, double totalPengeluaran,
                         double sisaSaldo, double persentase, String status, String tanggal) {
        this.id = id;
        this.uangBulanan = uangBulanan;
        this.makan = makan;
        this.transportasi = transportasi;
        this.kuota = kuota;
        this.kuliah = kuliah;
        this.totalPengeluaran = totalPengeluaran;
        this.sisaSaldo = sisaSaldo;
        this.persentase = persentase;
        this.status = status;
        this.tanggal = tanggal;
    }

    /**
     * Parsing JSONObject dari API get_history.php menjadi ExpenseRecord
     */
    public static ExpenseRecord fromJson(JSONObject json) {
        ExpenseRecord record = new ExpenseRecord();
        try {
            record.setId(json.optInt("id", 0));
            record.setUangBulanan(json.optDouble("uang_bulanan", 0.0));
            record.setMakan(json.optDouble("makan", 0.0));
            record.setTransportasi(json.optDouble("transportasi", 0.0));
            record.setKuota(json.optDouble("kuota", 0.0));
            record.setKuliah(json.optDouble("kuliah", 0.0));
            record.setTotalPengeluaran(json.optDouble("total_pengeluaran", 0.0));
            record.setSisaSaldo(json.optDouble("sisa_saldo", 0.0));
            record.setPersentase(json.optDouble("persentase", 0.0));
            record.setStatus(json.optString("status", "-"));
            record.setTanggal(json.optString("tanggal", "-"));
        } catch (Exception ignored) {
        }
        return record;
    }

    /**
     * Konversi object ke JSONObject untuk dikirim via save_expense.php
     */
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("uang_bulanan", uangBulanan);
            json.put("makan", makan);
            json.put("transportasi", transportasi);
            json.put("kuota", kuota);
            json.put("kuliah", kuliah);
            json.put("total_pengeluaran", totalPengeluaran);
            json.put("sisa_saldo", sisaSaldo);
            json.put("persentase", persentase);
            json.put("status", status);
        } catch (Exception ignored) {
        }
        return json;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getUangBulanan() { return uangBulanan; }
    public void setUangBulanan(double uangBulanan) { this.uangBulanan = uangBulanan; }

    public double getMakan() { return makan; }
    public void setMakan(double makan) { this.makan = makan; }

    public double getTransportasi() { return transportasi; }
    public void setTransportasi(double transportasi) { this.transportasi = transportasi; }

    public double getKuota() { return kuota; }
    public void setKuota(double kuota) { this.kuota = kuota; }

    public double getKuliah() { return kuliah; }
    public void setKuliah(double kuliah) { this.kuliah = kuliah; }

    public double getTotalPengeluaran() { return totalPengeluaran; }
    public void setTotalPengeluaran(double totalPengeluaran) { this.totalPengeluaran = totalPengeluaran; }

    public double getSisaSaldo() { return sisaSaldo; }
    public void setSisaSaldo(double sisaSaldo) { this.sisaSaldo = sisaSaldo; }

    public double getPersentase() { return persentase; }
    public void setPersentase(double persentase) { this.persentase = persentase; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
}

