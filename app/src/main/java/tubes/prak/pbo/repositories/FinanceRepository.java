package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import tubes.prak.pbo.ENUM.JenisPos;
import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.FinanceLedger;
import tubes.prak.pbo.models.FinanceSummary;

public class FinanceRepository {

    // Ambil semua baris finance_ledger (dengan filter opsional bulan & tahun)
    public List<FinanceLedger> findAllLedger(Integer bulan, Integer tahun) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT
                fl.id_ledger,
                fl.id_po,
                fl.id_order,
                fl.jenis_pos,
                fl.nominal,
                fl.deskripsi,
                COALESCE(so.tanggal_pesan, fl.tanggal_transaksi) AS tanggal_aktual,
                COALESCE(so.id_user, fl.created_by) AS creator_id,
                u.nama AS creator_nama
            FROM public.finance_ledger fl
            LEFT JOIN public.sales_order so ON fl.id_order = so.id_order
            LEFT JOIN public.users u ON COALESCE(so.id_user, fl.created_by) = u.id_user
            WHERE 1 = 1
            """);

        List<Object> params = new ArrayList<>();
        if (bulan != null) {
            sql.append(" AND EXTRACT(MONTH FROM COALESCE(so.tanggal_pesan, fl.tanggal_transaksi)) = ? ");
            params.add(bulan);
        }
        if (tahun != null) {
            sql.append(" AND EXTRACT(YEAR FROM COALESCE(so.tanggal_pesan, fl.tanggal_transaksi)) = ? ");
            params.add(tahun);
        }

        sql.append(" ORDER BY COALESCE(so.tanggal_pesan, fl.tanggal_transaksi) DESC, fl.id_ledger DESC ");

        List<FinanceLedger> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int poVal = rs.getInt("id_po");
                    Integer idPo = rs.wasNull() ? null : poVal;

                    int orderVal = rs.getInt("id_order");
                    Integer idOrder = rs.wasNull() ? null : orderVal;

                    int creatorVal = rs.getInt("creator_id");
                    Integer createdBy = rs.wasNull() ? null : creatorVal;

                    list.add(new FinanceLedger(
                            rs.getInt("id_ledger"),
                            idPo,
                            idOrder,
                            JenisPos.fromString(rs.getString("jenis_pos")),
                            rs.getDouble("nominal"),
                            rs.getString("deskripsi"),
                            rs.getDate("tanggal_aktual"),
                            createdBy,
                            rs.getString("creator_nama")
                    ));
                }
            }
        }
        return list;
    }

    // Hitung metrik untuk 4 kartu ringkasan keuangan pada bulan & tahun tertentu
    public FinanceSummary getFinanceSummary(int bulan, int tahun) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            double totalCash = queryTotalCash(conn);
            double nilaiRawMaterial = queryNilaiRawMaterial(conn);
            double nilaiFinishedGoods = queryNilaiFinishedGoods(conn);

            double totalPembelianBulanan = queryTotalByJenisPos(conn, JenisPos.PEMBELIAN, bulan, tahun);
            double totalOpnameBulanan = queryTotalByJenisPos(conn, JenisPos.OPNAME, bulan, tahun);
            double totalRevenueBulanan = queryTotalByJenisPos(conn, JenisPos.PENJUALAN, bulan, tahun);
            double totalCogsBulanan = queryTotalCogsBulanan(conn, bulan, tahun);

            return new FinanceSummary(
                    totalCash,
                    nilaiRawMaterial,
                    nilaiFinishedGoods,
                    totalPembelianBulanan,
                    totalOpnameBulanan,
                    totalRevenueBulanan,
                    totalCogsBulanan
            );
        }
    }

    private double queryTotalCash(Connection conn) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(
                CASE
                    WHEN UPPER(jenis_pos) IN ('DEPOSIT', 'PENJUALAN') THEN nominal
                    WHEN UPPER(jenis_pos) = 'PEMBELIAN' THEN -nominal
                    ELSE 0
                END
            ), 0) AS total_cash
            FROM public.finance_ledger
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble("total_cash") : 0.0;
        }
    }

    private double queryNilaiRawMaterial(Connection conn) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(jumlah_stok * harga_rata2_bahan_baku), 0) AS total_rm
            FROM public.raw_material
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble("total_rm") : 0.0;
        }
    }

    private double queryNilaiFinishedGoods(Connection conn) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(jumlah_stok * harga_rata2_produk), 0) AS total_fg
            FROM public.produk
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble("total_fg") : 0.0;
        }
    }

    private double queryTotalByJenisPos(Connection conn, JenisPos jenisPos, int bulan, int tahun) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(fl.nominal), 0) AS total
            FROM public.finance_ledger fl
            LEFT JOIN public.sales_order so ON fl.id_order = so.id_order
            WHERE UPPER(fl.jenis_pos) = ?
              AND EXTRACT(MONTH FROM COALESCE(so.tanggal_pesan, fl.tanggal_transaksi)) = ?
              AND EXTRACT(YEAR FROM COALESCE(so.tanggal_pesan, fl.tanggal_transaksi)) = ?
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, jenisPos.name());
            stmt.setInt(2, bulan);
            stmt.setInt(3, tahun);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble("total") : 0.0;
            }
        }
    }

    private double queryTotalCogsBulanan(Connection conn, int bulan, int tahun) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(sod.jumlah_terjual * sod.hpp_aktual_saat_terjual), 0) AS total_cogs
            FROM public.finance_ledger fl
            JOIN public.sales_order so ON fl.id_order = so.id_order
            JOIN public.sales_order_details sod ON so.id_order = sod.id_order
            WHERE UPPER(fl.jenis_pos) = 'PENJUALAN'
              AND EXTRACT(MONTH FROM COALESCE(so.tanggal_pesan, fl.tanggal_transaksi)) = ?
              AND EXTRACT(YEAR FROM COALESCE(so.tanggal_pesan, fl.tanggal_transaksi)) = ?
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bulan);
            stmt.setInt(2, tahun);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble("total_cogs") : 0.0;
            }
        }
    }

    // Insert transaksi baru ke finance_ledger (Deposit Cash, Stock Opname, dll)
    public void insertLedger(Connection conn, Integer idPo, Integer idOrder, JenisPos jenisPos,
                             double nominal, String deskripsi, Integer createdBy) throws SQLException {
        String sql = """
            INSERT INTO public.finance_ledger (
                id_po, id_order, jenis_pos, nominal, deskripsi, tanggal_transaksi, created_by
            )
            VALUES (?, ?, ?, ?, ?, CURRENT_DATE, ?)
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (idPo != null) {
                stmt.setInt(1, idPo);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            if (idOrder != null) {
                stmt.setInt(2, idOrder);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, jenisPos.name());
            stmt.setDouble(4, nominal);
            stmt.setString(5, deskripsi);

            if (createdBy != null) {
                stmt.setInt(6, createdBy);
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.executeUpdate();
        }
    }

    public void insertDepositCash(double nominal, String deskripsi, Integer createdBy) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            insertLedger(conn, null, null, JenisPos.DEPOSIT, nominal, deskripsi, createdBy);
        }
    }

    public List<Integer> findAvailableYears() throws SQLException {
        String sql = """
            SELECT DISTINCT EXTRACT(YEAR FROM COALESCE(so.tanggal_pesan, fl.tanggal_transaksi))::int AS tahun
            FROM public.finance_ledger fl
            LEFT JOIN public.sales_order so ON fl.id_order = so.id_order
            WHERE COALESCE(so.tanggal_pesan, fl.tanggal_transaksi) IS NOT NULL
            ORDER BY tahun DESC
            """;

        List<Integer> years = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                years.add(rs.getInt("tahun"));
            }
        }
        return years;
    }
}