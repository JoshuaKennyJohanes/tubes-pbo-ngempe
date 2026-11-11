package tubes.prak.pbo.components;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import tubes.prak.pbo.models.Item;

public class RowItem {
    public static <T extends Item> DefaultTableModel createTableModel(List<T> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return new DefaultTableModel();
        }

        // Ambil nama kolom dari elemen pertama (karena polymorphic)
        List<String> columnNames = itemList.get(0).getTableHeader();
        String[] columns = columnNames.toArray(new String[0]);

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Isi baris tabel secara otomatis
        for (T item : itemList) {
            model.addRow(item.toTableRow());
        }

        return model;
    }
}
