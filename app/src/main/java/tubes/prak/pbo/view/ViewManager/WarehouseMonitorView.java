package tubes.prak.pbo.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.components.RowItem;
import tubes.prak.pbo.models.Item;
import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.models.RawMaterial;
import tubes.prak.pbo.repositories.ProdukRepository;
import tubes.prak.pbo.repositories.RawMaterialRepository;
public class WarehouseMonitorView extends JPanel {

    // 1. Layer Data / Repository
    private ProdukRepository produkRepo = new ProdukRepository();
    private RawMaterialRepository materialRepo = new RawMaterialRepository();

    // 2. Data Caching
    private List<Produk> listProduk;
    private List<RawMaterial> listRawMaterial;
    private List<? extends Item> currentItemList; // Menampung list yang sedang aktif di tabel

    // 3. Komponen GUI
    private JTable tableItem;
    private JScrollPane scrollPane;
    private JComboBox<String> cbKategori; // Pengendali jenis data yang ditampilkan

    public WarehouseMonitorView() {
        // Layout utama JPanel
        setLayout(new BorderLayout(10, 10));

        // Load data awal dari DB/Repository
        loadDataFromRepositories();

        // Inisialisasi Komponen GUI
        initHeaderPanel();
        initTableComponents();

        // Tampilkan data default pertama kali (misal: Produk)
        renderTableData(listProduk);
    }

    /**
     * Memuat data dari repositori ke memori local
     */
    private void loadDataFromRepositories() {
        this.listProduk = produkRepo.findAllProduk();
        this.listRawMaterial = materialRepo.findAllRawMaterial();
    }

    /**
     * Panel atas untuk Filter/Switch Kategori
     */
    private void initHeaderPanel() {
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.add(new JLabel("Pilih Tipe Item:"));

        cbKategori = new JComboBox<>(new String[]{"Produk", "Raw Material"});
        
        // Listener saat ComboBox diganti oleh user
        cbKategori.addActionListener(e -> {
            String selected = (String) cbKategori.getSelectedItem();
            if ("Produk".equals(selected)) {
                renderTableData(listProduk);
            } else if ("Raw Material".equals(selected)) {
                renderTableData(listRawMaterial);
            }
        });

        panelTop.add(cbKategori);
        add(panelTop, BorderLayout.NORTH);
    }

    /**
     * Inisialisasi JTable & Mouse Listener
     */
    private void initTableComponents() {
        tableItem = new JTable();
        scrollPane = new JScrollPane(tableItem);
        add(scrollPane, BorderLayout.CENTER);

        // Interaksi klik baris di dalam tabel
        tableItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableItem.getSelectedRow();
                if (selectedRow != -1 && currentItemList != null) {
                    int modelRow = tableItem.convertRowIndexToModel(selectedRow);
                    
                    // Mengambil Objek Java asli bertipe Item
                    Item itemTerpilih = currentItemList.get(modelRow);
                    
                    // Aksi yang dilakukan saat baris diklik
                    handleItemSelection(itemTerpilih);
                }
            }
        });
    }

    /**
     * Method internal untuk merender ulang data tabel
     */
    public void renderTableData(List<? extends Item> itemList) {
        this.currentItemList = itemList;
        DefaultTableModel model = RowItem.createTableModel(itemList);
        tableItem.setModel(model);
    }

    /**
     * Method penyegaran data (dipanggil jika ada perubahan/tambah data di DB)
     */
    public void refreshData() {
        loadDataFromRepositories();
        if (cbKategori.getSelectedIndex() == 0) {
            renderTableData(listProduk);
        } else {
            renderTableData(listRawMaterial);
        }
    }

    /**
     * Event penanganan saat baris item diklik
     */
    private void handleItemSelection(Item item) {
        if (item instanceof Produk) {
            Produk p = (Produk) item;
            System.out.println("Memilih Produk: " + p.getNama() + " (ID: " + p.getId() + ")");
        } else if (item instanceof RawMaterial) {
            RawMaterial rm = (RawMaterial) item;
            System.out.println("Memilih Raw Material: " + rm.getNama() + " (ID: " + rm.getId() + ")");
        }
    }
}

