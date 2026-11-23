package tubes.prak.pbo.ENUM;

public enum StatusPo {
    PAID,
    ARRIVED;

    public static StatusPo fromString(String value) {
        if (value == null || value.isBlank()) {
            return PAID;
        }
        return StatusPo.valueOf(value.trim().toUpperCase());
    }
}