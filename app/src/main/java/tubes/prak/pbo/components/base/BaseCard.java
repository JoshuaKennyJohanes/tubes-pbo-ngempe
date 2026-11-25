package tubes.prak.pbo.components.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public abstract class BaseCard extends JPanel {

    private static final int H_GAP = 16;
    private static final Insets DEFAULT_PADDING = new Insets(12, 16, 12, 16);

    private final int fixedHeight;
    private final int minWidth;
    private final Insets padding;

    protected BaseCard(int height, int minWidth) {
        this(height, minWidth, DEFAULT_PADDING);
    }

    protected BaseCard(int height, int minWidth, Insets padding) {
        this.fixedHeight = height;
        this.minWidth = minWidth;
        this.padding = padding;
    }

    // =========================================================================
    // TEMPLATE METHOD
    // =========================================================================

    protected final void build() {
        setLayout(new BorderLayout(H_GAP, 0));
        setBackground(Color.WHITE);
        setBorder(new CompoundBorder(
                new LineBorder(CardColors.BORDER, 1, true),
                new EmptyBorder(padding)
        ));

        int height = cardHeight();
        setPreferredSize(new Dimension(minWidth, height));
        setMinimumSize(new Dimension(minWidth, height));

        addZone(buildLeft(), BorderLayout.WEST);
        addZone(buildCenter(), BorderLayout.CENTER);
        addZone(buildRight(), BorderLayout.EAST);
    }

    protected JComponent buildLeft() {
        return null;
    }

    protected JComponent buildCenter() {
        return null;
    }

    protected JComponent buildRight() {
        return null;
    }

    protected int cardHeight() {
        return fixedHeight;
    }

    protected final int contentHeight() {
        return cardHeight() - padding.top - padding.bottom;
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, cardHeight());
    }

    private void addZone(JComponent zone, String position) {
        if (zone != null) {
            add(zone, position);
        }
    }

    // =========================================================================
    // HELPER PANEL
    // =========================================================================

    protected JPanel column() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        return panel;
    }

    protected JPanel column(int width) {
        JPanel panel = column();
        Dimension fixed = new Dimension(width, contentHeight());
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(fixed);
        return panel;
    }

    /** Baris horizontal stabil menggunakan BoxLayout X_AXIS agar tidak terkompresi di dalam column(). */
    protected static JPanel row() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    protected JPanel centered(JComponent component, int width) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        Dimension fixed = new Dimension(width, contentHeight());
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(fixed);
        panel.add(component, new GridBagConstraints());
        return panel;
    }

    // =========================================================================
    // HELPER KOMPONEN
    // =========================================================================

    protected static JLabel label(String text, int fontStyle, int fontSize, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", fontStyle, fontSize));
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    protected static JLabel labelRight(String text, int fontStyle, int fontSize, Color color) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(new Font("SansSerif", fontStyle, fontSize));
        label.setForeground(color);
        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
        return label;
    }

    protected static JLabel badge(String text, BadgeStyle style) {
        return badge(text, style.getForeground(), style.getBackground(), style.getBorder());
    }

    protected static JLabel badge(String text, Color foreground, Color background) {
        return badge(text, foreground, background, background.darker());
    }

    private static JLabel badge(String text, Color foreground, Color background, Color border) {
        JLabel badge = new JLabel(" " + text + " ");
        badge.setFont(new Font("SansSerif", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setForeground(foreground);
        badge.setBackground(background);
        badge.setBorder(new LineBorder(border, 1, true));
        badge.setAlignmentY(Component.CENTER_ALIGNMENT);
        // Cegah badge mengecil atau terpotong di BoxLayout X_AXIS
        Dimension pref = badge.getPreferredSize();
        badge.setMinimumSize(pref);
        badge.setMaximumSize(pref);
        return badge;
    }

    protected static JButton button(String text, Color background, Color foreground, Runnable onClick) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            if (onClick != null) {
                onClick.run();
            }
        });
        return button;
    }

    protected static <T> Runnable invoke(Consumer<T> handler, T value) {
        return () -> {
            if (handler != null) {
                handler.accept(value);
            }
        };
    }

    // =========================================================================
    // HELPER TABEL READ-ONLY (SalesOrderCard, BomProductCard)
    // =========================================================================

    protected static JScrollPane readOnlyTable(String[] columns, List<Object[]> rows,
                                               int rowHeight, Consumer<JTable> customizer) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Object[] row : rows) {
            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setRowHeight(rowHeight);
        table.setFillsViewportHeight(true);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 234, 240));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));

        if (customizer != null) {
            customizer.accept(table);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        return scrollPane;
    }

    protected static void columnWidth(JTable table, int column, int preferredWidth, int maxWidth) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        tableColumn.setPreferredWidth(preferredWidth);
        if (maxWidth > 0) {
            tableColumn.setMaxWidth(maxWidth);
        }
    }

    protected static void alignColumn(JTable table, int column, int alignment) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(alignment);
        table.getColumnModel().getColumn(column).setCellRenderer(renderer);
    }
}