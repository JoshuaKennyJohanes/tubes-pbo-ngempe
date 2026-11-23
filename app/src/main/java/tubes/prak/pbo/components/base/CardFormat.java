package tubes.prak.pbo.components.base;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Format angka dan tanggal bersama. DecimalFormat/SimpleDateFormat tidak thread-safe,
 * jadi disimpan per-thread supaya aman dipakai dari mana saja.
 */
public final class CardFormat {

    private static final ThreadLocal<DecimalFormat> CURRENCY =
            ThreadLocal.withInitial(() -> new DecimalFormat("#,##0"));
    private static final ThreadLocal<DecimalFormat> QTY =
            ThreadLocal.withInitial(() -> new DecimalFormat("#,##0.##"));
    private static final ThreadLocal<SimpleDateFormat> DATE =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("dd MMM yyyy"));

    private CardFormat() {
    }

    /** 1500000 -> "1,500,000" (tanpa prefix) */
    public static String currency(double value) {
        return CURRENCY.get().format(value);
    }

    /** 1500000 -> "Rp 1,500,000" */
    public static String rupiah(double value) {
        return "Rp " + currency(value);
    }

    /** 2.5 -> "2.5", 3.0 -> "3" */
    public static String qty(double value) {
        return QTY.get().format(value);
    }

    /** null -> "-" */
    public static String date(Date date) {
        return date(date, "-");
    }

    public static String date(Date date, String fallbackIfNull) {
        return (date != null) ? DATE.get().format(date) : fallbackIfNull;
    }
}