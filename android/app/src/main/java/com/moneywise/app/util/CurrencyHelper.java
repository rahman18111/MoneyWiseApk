package com.moneywise.app.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Helper untuk format mata uang Rupiah (IDR) dan format persentase.
 */
public class CurrencyHelper {

    /**
     * Format angka ke bentuk format Rupiah, contoh: "Rp 1.500.000"
     */
    public static String formatRupiah(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setCurrencySymbol("Rp ");
        symbols.setGroupingSeparator('.');
        symbols.setMonetaryDecimalSeparator(',');

        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("id", "ID"));
        decimalFormat.setDecimalFormatSymbols(symbols);
        decimalFormat.setMaximumFractionDigits(0);
        decimalFormat.setMinimumFractionDigits(0);

        if (amount < 0) {
            double positive = Math.abs(amount);
            return "-Rp " + decimalFormat.format(positive).replace("Rp", "").trim();
        }

        return decimalFormat.format(amount);
    }

    /**
     * Format angka ke persentase dengan 1 desimal, contoh: "62.5%"
     */
    public static String formatPercentage(double percentage) {
        return String.format(Locale.US, "%.1f%%", percentage);
    }
}

