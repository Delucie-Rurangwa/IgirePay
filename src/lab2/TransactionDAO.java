package lab2;

import lab1.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TransactionDAO {

    // CREATE: record a transaction for an account
    public boolean create(String accountId, Transaction transaction) throws SQLException {
        String insertsql = "INSERT INTO transactions (id, account_id, reference_id, transaction_type, amount, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertsql)) {

            statement.setString(1, transaction.getTransactionId());
            statement.setString(2, accountId);
            statement.setString(3, transaction.getReferenceId());
            statement.setString(4, transaction.getTransactionType());
            statement.setDouble(5, transaction.getAmount());
            statement.setTimestamp(6, Timestamp.valueOf(transaction.getTimestamp()));

            int rowsInserted = statement.executeUpdate();
            return rowsInserted == 1;
        }
    }
}