package tubes.prak.pbo.components.base;

import java.awt.Color;

/**
 * Palet badge status yang dipakai bersama oleh semua card.
 * Enum status domain (StatusPo, StatusPembayaran, JenisPos, dst.) cukup dipetakan ke salah satu nilai di sini.
 */
public enum BadgeStyle {

    SUCCESS(new Color(22, 101, 52), new Color(220, 252, 231), new Color(134, 239, 172)),
    WARNING(new Color(146, 64, 14), new Color(254, 243, 199), new Color(252, 211, 77)),
    DANGER(new Color(185, 28, 28), new Color(254, 226, 226), new Color(252, 165, 165)),
    INFO(new Color(29, 78, 216), new Color(219, 234, 254), new Color(147, 197, 253)),
    NEUTRAL(new Color(71, 85, 105), new Color(241, 245, 249), new Color(203, 213, 225));

    private final Color foreground;
    private final Color background;
    private final Color border;

    BadgeStyle(Color foreground, Color background, Color border) {
        this.foreground = foreground;
        this.background = background;
        this.border = border;
    }

    public Color getForeground() {
        return foreground;
    }

    public Color getBackground() {
        return background;
    }

    public Color getBorder() {
        return border;
    }
}