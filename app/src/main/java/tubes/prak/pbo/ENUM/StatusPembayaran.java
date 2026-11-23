package tubes.prak.pbo.ENUM;

public enum StatusPembayaran {
    PENDING,
    PAID,
    CANCELLED;

    public static StatusPembayaran fromString(String value) {
        if (value == null || value.isBlank()) {
            return PENDING;
        }
        return StatusPembayaran.valueOf(value.trim().toUpperCase());
    }
}