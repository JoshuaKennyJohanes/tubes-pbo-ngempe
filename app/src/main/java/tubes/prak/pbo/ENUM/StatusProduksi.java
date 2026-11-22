package tubes.prak.pbo.ENUM;

import java.awt.Color;

public enum StatusProduksi {
    PLANNED(new Color(180, 83, 9), new Color(254, 243, 199)),   // Kuning (Amber gelap agar terbaca jelas)
    ONGOING(new Color(29, 78, 216), new Color(219, 234, 254)),  // Biru
    FINISHED(new Color(21, 128, 61), new Color(220, 252, 231)); // Hijau

    private final Color textColor;
    private final Color badgeColor;

    StatusProduksi(Color textColor, Color badgeColor) {
        this.textColor = textColor;
        this.badgeColor = badgeColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getBadgeColor() {
        return badgeColor;
    }
}