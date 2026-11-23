package tubes.prak.pbo.Interfaces;

import tubes.prak.pbo.models.users.UserBuyer;
import tubes.prak.pbo.models.users.UserStaff;

import java.sql.SQLException;

public interface UserPanelVisitor {
    void buildForBuyer(UserBuyer buyer) throws SQLException;
    void buildForStaff(UserStaff staff) throws SQLException;
}