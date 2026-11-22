package tubes.prak.pbo.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import tubes.prak.pbo.config.DatabaseConfig;
import tubes.prak.pbo.models.Vendor;

public class VendorRepository {
    public static ArrayList<Vendor> getAllVendor() {
        String sql = "select * from vendor";
        ArrayList<Vendor> vendors = new ArrayList<>();

        try {
            Connection conn = DatabaseConfig.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                vendors.add(new Vendor(
                    resultSet.getInt("id_vendor"),
                    resultSet.getString("nama_vendor"),
                    resultSet.getString("no_telp"),
                    resultSet.getString("alamat_vendor")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vendors;
    }
}
