package lab2;
import java.sql.ResultSet;

import lab1.Account;
import lab1.SavingsAccount;
import lab1.WalletAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class AccountDAO {

    // CREATE a new account (wallet or savings)
    public boolean create(Account account) throws SQLException {
        String insertsql = "INSERT INTO accounts (id, customer_id, account_type, balance, "
                + "transfer_limit, withdrawal_limit, withdrawal_fee) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertsql)) {

            // Columns every account has
            statement.setString(1, account.getAccountId());
            statement.setString(2, account.getCustomerId());
            statement.setDouble(4, account.getBalance());

            // Columns that depend on the type of account
            if (account instanceof WalletAccount) {
                WalletAccount wallet = (WalletAccount) account;
                statement.setString(3, "WALLET");
                statement.setDouble(5, wallet.getTransferLimit());
                statement.setNull(6, Types.NUMERIC);
                statement.setNull(7, Types.NUMERIC);
            } else if (account instanceof SavingsAccount) {
                SavingsAccount savings = (SavingsAccount) account;
                statement.setString(3, "SAVINGS");
                statement.setNull(5, Types.NUMERIC);
                statement.setDouble(6, savings.getWithdrawalLimit());
                statement.setDouble(7, savings.getWithdrawalFee());
            } else {
                return false;   // only wallet and savings accounts can be saved
            }

            int rowsInserted = statement.executeUpdate();
            return rowsInserted == 1;

        }
    }

    // READ one account by ID
    public Account findById(String id) throws SQLException {
        String selectsql = "SELECT id, customer_id, account_type, balance, "
                + "transfer_limit, withdrawal_limit, withdrawal_fee FROM accounts WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectsql)) {

            statement.setString(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                String accountType = result.getString("account_type");

                if (accountType.equals("WALLET")) {
                    return new WalletAccount(
                            result.getString("id"),
                            result.getString("customer_id"),
                            result.getDouble("balance"),
                            result.getDouble("transfer_limit"));
                } else {
                    return new SavingsAccount(
                            result.getString("id"),
                            result.getString("customer_id"),
                            result.getDouble("balance"),
                            result.getDouble("withdrawal_limit"),
                            result.getDouble("withdrawal_fee"));
                }
            }

            return null;   // no account with this ID
        }
    }
}