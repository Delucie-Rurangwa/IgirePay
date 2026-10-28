package lab2;

import lab1.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    // READ: transaction history of one account, oldest first
    public List<Transaction> findByAccountId(String accountId) throws SQLException {
        String selectsql = "SELECT id, reference_id, transaction_type, amount, created_at "
                + "FROM transactions WHERE account_id = ? ORDER BY created_at";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectsql)) {

            statement.setString(1, accountId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Transaction transaction = new Transaction(
                        result.getString("id"),
                        result.getString("reference_id"),
                        result.getDouble("amount"),
                        result.getString("transaction_type"));

                // The constructor sets the time to "now", so replace it with the real time from the database
                transaction.setTimestamp(result.getTimestamp("created_at").toLocalDateTime());

                transactions.add(transaction);
            }
        }

        return transactions;
    }
    // CUSTOM QUERY: find all transactions created by one request (reference ID)
    public List<Transaction> findByReferenceId(String referenceId) throws SQLException {
        String selectsql = "SELECT id, reference_id, transaction_type, amount, created_at "
                + "FROM transactions WHERE reference_id = ? ORDER BY created_at";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectsql)) {

            statement.setString(1, referenceId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Transaction transaction = new Transaction(
                        result.getString("id"),
                        result.getString("reference_id"),
                        result.getDouble("amount"),
                        result.getString("transaction_type"));
                transaction.setTimestamp(result.getTimestamp("created_at").toLocalDateTime());
                transactions.add(transaction);
            }
        }

        return transactions;
    }

    // UPDATE: correct a transaction's details (for fixing test data, not normal payments)
    public boolean update(Transaction transaction) throws SQLException {
        String updatesql = "UPDATE transactions SET reference_id = ?, transaction_type = ?, amount = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updatesql)) {

            statement.setString(1, transaction.getReferenceId());
            statement.setString(2, transaction.getTransactionType());
            statement.setDouble(3, transaction.getAmount());
            statement.setString(4, transaction.getTransactionId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated == 1;
        }
    }

    // DELETE: remove a transaction by ID (for cleaning up test data)
    public boolean delete(String transactionId) throws SQLException {
        String deletesql = "DELETE FROM transactions WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deletesql)) {

            statement.setString(1, transactionId);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted == 1;
        }
    }
}