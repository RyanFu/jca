package libs;

import play.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author wujinliang
 * @since 5/24/12
 */
public class IOUtils extends org.apache.commons.io.IOUtils {
    public static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                Logger.error(e, e.getMessage());
            }
        }
    }
    public static void closeQuietly(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                Logger.error(e, e.getMessage());
            }
        }
    }
    public static void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                Logger.error(e, e.getMessage());
            }
        }
    }
}
