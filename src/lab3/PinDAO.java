package lab3;

import lab2.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PinDAO {

    // Save (or replace) the hashed PIN of an account
    public boolean savePinHash(String accountId, String pinHash) throws SQLException {
        String updatesql = "UPDATE accounts SET pin_hash = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updatesql)) {

            statement.setString(1, pinHash);
            statement.setString(2, accountId);

            return statement.executeUpdate() == 1;
        }
    }

    // Get the stored PIN hash, or null if the account has no PIN yet
    public String findPinHash(String accountId) throws SQLException {
        String selectsql = "SELECT pin_hash FROM accounts WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectsql)) {

            statement.setString(1, accountId);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("pin_hash");
            }
            return null;
        }
    }
}