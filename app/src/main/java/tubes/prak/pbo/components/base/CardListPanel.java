package tubes.prak.pbo.components.base;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

/**
 * Daftar card vertikal yang scrollable dan lebarnya selalu mengikuti viewport.
 * Menggantikan semua ScrollableCardListPanel / ProduksiScrollPane di tiap view.
 *
 * <pre>
 * private final CardListPanel&lt;SalesOrder&gt; list = new CardListPanel&lt;&gt;(SalesOrderCard::new);
 *
 * list.setEmptyMessage("Belum ada data.");
 * list.setItems(controller.getAll());   // render ulang seluruh card
 * </pre>
 *
 * @param <T> tipe data yang direpresentasikan satu card
 */
public class CardListPanel<T> extends JScrollPane {

    private static final int UNIT_INCREMENT = 16;
    private static final int BLOCK_INCREMENT = 60;

    private final ScrollableContainer container = new ScrollableContainer();
    private final Function<? super T, ? extends JComponent> cardFactory;

    private String emptyMessage = "Tidak ada data.";
    private int gap = 10;

    public CardListPanel(Function<? super T, ? extends JComponent> cardFactory) {
        this.cardFactory = Objects.requireNonNull(cardFactory, "cardFactory");

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(4, 2, 4, 2));

        setViewportView(container);
        setBorder(BorderFactory.createEmptyBorder());
        getVerticalScrollBar().setUnitIncrement(UNIT_INCREMENT);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    // =========================================================================
    // DATA
    // =========================================================================

    /** Mengganti seluruh isi daftar. Collection kosong/null menampilkan pesan kosong. */
    public void setItems(Collection<? extends T> items) {
        container.removeAll();

        if (items == null || items.isEmpty()) {
            container.add(createEmptyLabel());
        } else {
            boolean first = true;
            for (T item : items) {
                if (!first) {
                    container.add(Box.createVerticalStrut(gap));
                }
                container.add(cardFactory.apply(item));
                first = false;
            }
        }

        refresh();
    }

    /** Mengosongkan daftar tanpa pesan (mis. saat terjadi error). */
    public void clear() {
        container.removeAll();
        refresh();
    }

    // =========================================================================
    // KONFIGURASI
    // =========================================================================

    /** Dipanggil sebelum setItems() jika pesan kosong bergantung pada filter. */
    public void setEmptyMessage(String emptyMessage) {
        this.emptyMessage = emptyMessage;
    }

    /** Jarak vertikal antar card (default 10). Berlaku pada setItems() berikutnya. */
    public void setGap(int gap) {
        this.gap = gap;
    }

    public void setContentInsets(Insets insets) {
        container.setBorder(new EmptyBorder(insets));
    }

    public void setListBackground(Color color) {
        container.setBackground(color);
    }

    // =========================================================================
    // INTERNAL
    // =========================================================================

    private JLabel createEmptyLabel() {
        JLabel label = new JLabel(emptyMessage);
        label.setFont(new Font("SansSerif", Font.ITALIC, 13));
        label.setForeground(Color.GRAY);
        label.setBorder(new EmptyBorder(24, 10, 24, 10));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void refresh() {
        container.revalidate();
        container.repaint();
    }

    /** Memastikan card selalu melebar mengikuti lebar JScrollPane. */
    private static class ScrollableContainer extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return UNIT_INCREMENT;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return BLOCK_INCREMENT;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}