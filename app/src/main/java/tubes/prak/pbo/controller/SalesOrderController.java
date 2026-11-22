package tubes.prak.pbo.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tubes.prak.pbo.models.Produk;
import tubes.prak.pbo.models.SalesOrder;
import tubes.prak.pbo.models.SalesOrderDetail;
import tubes.prak.pbo.repositories.ProdukRepository;
import tubes.prak.pbo.repositories.SalesOrderRespository;

public class SalesOrderController {

    private final SalesOrderRespository salesOrderRepository = new SalesOrderRespository();

    public List<SalesOrder> getAllSalesOrders() throws SQLException {
        return getSalesOrdersByFilter(null, null);
    }

    public List<SalesOrder> getSalesOrdersByFilter(Integer bulan, Integer tahun) throws SQLException {
        List<SalesOrder> orders = salesOrderRepository.findAllWithDetails(bulan, tahun);
        List<Produk> allProduk = ProdukRepository.findAllProduk();

        Map<Integer, String> namaProdukMap = new HashMap<>();
        for (Produk produk : allProduk) {
            namaProdukMap.put(produk.getIdProduk(), produk.getNamaProduk());
        }

        for (SalesOrder order : orders) {
            for (SalesOrderDetail detail : order.getDetails()) {
                String namaProduk = namaProdukMap.getOrDefault(
                        detail.getIdProduk(),
                        "Produk #" + detail.getIdProduk()
                );
                detail.setNamaProduk(namaProduk);
            }
        }

        return orders;
    }

    public List<Integer> getAvailableYears() throws SQLException {
        return salesOrderRepository.findAvailableYears();
    }
}