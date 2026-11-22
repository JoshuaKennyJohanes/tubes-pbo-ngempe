package tubes.prak.pbo.controller;

import java.sql.SQLException;
import java.util.List;

import tubes.prak.pbo.models.FinanceLedger;
import tubes.prak.pbo.models.FinanceSummary;
import tubes.prak.pbo.repositories.FinanceRepository;
import tubes.prak.pbo.util.Session;

public class FinanceController {

    private final FinanceRepository financeRepository = new FinanceRepository();

    public FinanceSummary getSummaryByMonthYear(int bulan, int tahun) throws SQLException {
        return financeRepository.getFinanceSummary(bulan, tahun);
    }

    public List<FinanceLedger> getLedgerByMonthYear(Integer bulan, Integer tahun) throws SQLException {
        return financeRepository.findAllLedger(bulan, tahun);
    }

    public List<Integer> getAvailableYears() throws SQLException {
        return financeRepository.findAvailableYears();
    }

    public void depositCash(double nominal, String keterangan) throws Exception {
        if (nominal <= 0) {
            throw new Exception("Nominal deposit cash harus lebih dari Rp 0!");
        }

        String desc = (keterangan != null && !keterangan.isBlank())
                ? "DEPOSIT - " + keterangan.trim()
                : "DEPOSIT - Setoran Modal Kas";

        Integer userId = (Session.getSession() != null) ? Session.getSession().getId() : null;
        financeRepository.insertDepositCash(nominal, desc, userId);
    }
}