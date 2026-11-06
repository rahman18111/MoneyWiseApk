package com.moneywise.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moneywise.app.model.ExpenseRecord;
import com.moneywise.app.network.ApiConfig;
import com.moneywise.app.network.HttpHelper;
import com.moneywise.app.util.CurrencyHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * MONEYWISE - Halaman Riwayat Pengeluaran
 * Menampilkan histori perhitungan pengeluaran mahasiswa dari Database MySQL XAMPP
 * menggunakan layout ScrollView dan LinearLayout.
 */
public class HistoryActivity extends AppCompatActivity {

    private TextView tvHistoryCount;
    private Button btnRefreshHistory;
    private ProgressBar pbLoadingHistory;
    private TextView tvEmptyHistory;
    private LinearLayout layoutHistoryList;

    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        inflater = LayoutInflater.from(this);
        initViews();
        setupListeners();
        fetchHistoryData();
    }

    private void initViews() {
        tvHistoryCount     = findViewById(R.id.tvHistoryCount);
        btnRefreshHistory  = findViewById(R.id.btnRefreshHistory);
        pbLoadingHistory   = findViewById(R.id.pbLoadingHistory);
        tvEmptyHistory     = findViewById(R.id.tvEmptyHistory);
        layoutHistoryList  = findViewById(R.id.layoutHistoryList);
    }

    private void setupListeners() {
        btnRefreshHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchHistoryData();
            }
        });
    }

    /**
     * Mengambil data riwayat dari MySQL via API get_history.php menggunakan background thread
     */
    private void fetchHistoryData() {
        pbLoadingHistory.setVisibility(View.VISIBLE);
        tvEmptyHistory.setVisibility(View.GONE);
        layoutHistoryList.removeAllViews();
        tvHistoryCount.setText("Menghubungkan ke database...");

        HttpHelper.get(ApiConfig.URL_GET_HISTORY, new HttpHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                pbLoadingHistory.setVisibility(View.GONE);
                try {
                    JSONObject resObj = new JSONObject(response);
                    String status = resObj.optString("status", "");

                    if ("success".equalsIgnoreCase(status)) {
                        JSONArray dataArray = resObj.optJSONArray("data");
                        List<ExpenseRecord> recordList = new ArrayList<>();

                        if (dataArray != null) {
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject itemObj = dataArray.getJSONObject(i);
                                recordList.add(ExpenseRecord.fromJson(itemObj));
                            }
                        }

                        renderHistoryList(recordList);
                    } else {
                        String msg = resObj.optString("message", "Gagal memuat data riwayat.");
                        tvEmptyHistory.setText(msg);
                        tvEmptyHistory.setVisibility(View.VISIBLE);
                        tvHistoryCount.setText("0 Catatan");
                    }

                } catch (Exception e) {
                    tvEmptyHistory.setText("Format data server tidak valid: " + e.getMessage());
                    tvEmptyHistory.setVisibility(View.VISIBLE);
                    tvHistoryCount.setText("Error data");
                }
            }

            @Override
            public void onError(String errorMessage) {
                pbLoadingHistory.setVisibility(View.GONE);
                tvEmptyHistory.setText("Gagal terhubung ke MySQL XAMPP.\n" + errorMessage + "\n\nPastikan Apache & MySQL aktif dan URL IP sudah sesuai.");
                tvEmptyHistory.setVisibility(View.VISIBLE);
                tvHistoryCount.setText("Koneksi gagal");
                Toast.makeText(HistoryActivity.this, "Koneksi database gagal!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Membangun tampilan kartu item riwayat secara dinamis ke dalam LinearLayout
     */
    private void renderHistoryList(List<ExpenseRecord> recordList) {
        layoutHistoryList.removeAllViews();

        if (recordList == null || recordList.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            tvHistoryCount.setText("Total: 0 Catatan");
            return;
        }

        tvEmptyHistory.setVisibility(View.GONE);
        tvHistoryCount.setText("Total: " + recordList.size() + " Catatan Tersimpan");

        for (ExpenseRecord item : recordList) {
            View cardView = inflater.inflate(R.layout.item_history_card, layoutHistoryList, false);

            TextView tvItemDate       = cardView.findViewById(R.id.tvItemDate);
            TextView tvItemStatus     = cardView.findViewById(R.id.tvItemStatus);
            TextView tvItemTotal      = cardView.findViewById(R.id.tvItemTotal);
            TextView tvItemRemaining  = cardView.findViewById(R.id.tvItemRemaining);
            TextView tvItemBudgetInfo = cardView.findViewById(R.id.tvItemBudgetInfo);
            TextView tvItemBreakdown  = cardView.findViewById(R.id.tvItemBreakdown);

            // Set Tanggal & Jam
            tvItemDate.setText(item.getTanggal());

            // Set Total & Sisa Saldo
            tvItemTotal.setText(CurrencyHelper.formatRupiah(item.getTotalPengeluaran()));
            tvItemRemaining.setText(CurrencyHelper.formatRupiah(item.getSisaSaldo()));

            // Set Info Anggaran & Persentase
            String budgetInfo = "Anggaran: " + CurrencyHelper.formatRupiah(item.getUangBulanan())
                    + " (" + CurrencyHelper.formatPercentage(item.getPersentase()) + " terpakai)";
            tvItemBudgetInfo.setText(budgetInfo);

            // Set Rincian Komponen Pengeluaran
            String breakdown = "Makan: " + CurrencyHelper.formatRupiah(item.getMakan())
                    + " | Transport: " + CurrencyHelper.formatRupiah(item.getTransportasi())
                    + "\nKuota: " + CurrencyHelper.formatRupiah(item.getKuota())
                    + " | Kuliah: " + CurrencyHelper.formatRupiah(item.getKuliah());
            tvItemBreakdown.setText(breakdown);

            // Styling Badge Berdasarkan Status
            String itemStatus = item.getStatus();
            if (itemStatus == null) itemStatus = "";

            if (itemStatus.contains("Aman")) {
                tvItemStatus.setText("Aman (Hemat)");
                tvItemStatus.setBackgroundResource(R.drawable.bg_badge_safe);
                tvItemStatus.setTextColor(ContextCompat.getColor(this, R.color.safe_badge_text));
                tvItemRemaining.setTextColor(ContextCompat.getColor(this, R.color.safe_badge_text));
            } else if (itemStatus.contains("Waspada")) {
                tvItemStatus.setText("Waspada");
                tvItemStatus.setBackgroundResource(R.drawable.bg_badge_warning);
                tvItemStatus.setTextColor(ContextCompat.getColor(this, R.color.warning_badge_text));
                tvItemRemaining.setTextColor(ContextCompat.getColor(this, R.color.warning_badge_text));
            } else {
                tvItemStatus.setText("Defisit");
                tvItemStatus.setBackgroundResource(R.drawable.bg_badge_danger);
                tvItemStatus.setTextColor(ContextCompat.getColor(this, R.color.danger_badge_text));
                tvItemRemaining.setTextColor(ContextCompat.getColor(this, R.color.danger_badge_text));
            }

            // Tambahkan kartu ke dalam ScrollView LinearLayout
            layoutHistoryList.addView(cardView);
        }
    }
}

