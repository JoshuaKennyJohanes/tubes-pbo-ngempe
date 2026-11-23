package tubes.prak.pbo.ENUM;

public enum JenisPos {
    PENJUALAN,
    PEMBELIAN,
    OPNAME,
    DEPOSIT;

    public static JenisPos fromString(String value) {
        if (value == null || value.isBlank()) {
            return OPNAME;
        }

        try {
            return JenisPos.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OPNAME;
        }

    }
}