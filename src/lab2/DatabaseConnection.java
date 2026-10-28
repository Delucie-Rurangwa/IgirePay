package lab2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class DatabaseConnection {

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/igirepay", "postgres", "d.rurangwa@alustudent.com");
        }
    }

