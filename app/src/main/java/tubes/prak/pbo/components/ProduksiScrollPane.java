package tubes.prak.pbo.components;

import tubes.prak.pbo.models.Produksi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class ProduksiScrollPane extends JScrollPane {

    private final ScrollableCardContainer listContainer;
    private Consumer<Produksi> onDetailClickListener;

    public ProduksiScrollPane() {
        listContainer = new ScrollableCardContainer();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(new Color(248, 250, 252));
        listContainer.setBorder(new EmptyBorder(12, 12, 12, 12));

        setViewportView(listContainer);
        setBorder(BorderFactory.createEmptyBorder());
        getVerticalScrollBar().setUnitIncrement(16);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    public void setOnDetailClickListener(Consumer<Produksi> listener) {
        this.onDetailClickListener = listener;
    }

    public void addCard(Produksi produksi) {
        if (listContainer.getComponentCount() > 0) {
            listContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        ProduksiCard card = new ProduksiCard(produksi, data -> {
            if (onDetailClickListener != null) {
                onDetailClickListener.accept(data);
            }
        });

        listContainer.add(card);
        refresh();
    }

    // Praktis untuk langsung menampilkan List<Produksi> dari repository
    public void setData(List<Produksi> daftarProduksi) {
        listContainer.removeAll();
        for (Produksi p : daftarProduksi) {
            addCard(p);
        }
        refresh();
    }

    public void clearCards() {
        listContainer.removeAll();
        refresh();
    }

    private void refresh() {
        listContainer.revalidate();
        listContainer.repaint();
    }

    private static class ScrollableCardContainer extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 64;
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