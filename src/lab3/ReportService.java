package lab3;

import lab1.Account;
import lab1.Customer;
import lab1.Transaction;
import lab2.AccountDAO;
import lab2.CustomerDAO;
import lab2.TransactionDAO;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReportService {

    private CustomerDAO customerDAO = new CustomerDAO();
    private AccountDAO accountDAO = new AccountDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

    // ===== EXPORT TRANSACTION HISTORY TO CSV =====
    public String exportTransactionsToCsv(String accountId)
            throws SQLException, IOException, InvalidAccountException {

        if (accountDAO.findById(accountId) == null) {
            throw new InvalidAccountException("No account found with ID " + accountId);
        }

        List<Transaction> transactions = transactionDAO.findByAccountId(accountId);
        String fileName = "transactions_" + accountId + ".csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("transaction_id,reference_id,transaction_type,amount,timestamp");
            for (Transaction t : transactions) {
                writer.println(t.getTransactionId() + ","
                        + t.getReferenceId() + ","
                        + t.getTransactionType() + ","
                        + t.getAmount() + ","
                        + t.getTimestamp());
            }
        }

        return fileName;
    }

    // ===== DAILY TRANSACTION SUMMARY =====
    public void printDailySummary(LocalDate date) throws SQLException {
        List<Transaction> transactions = transactionDAO.findByDate(date);

        int depositCount = 0;
        int withdrawalCount = 0;
        int transferCount = 0;
        double depositTotal = 0;
        double withdrawalTotal = 0;
        double transferTotal = 0;

        for (Transaction t : transactions) {
            if (t.getTransactionType().equals("DEPOSIT")) {
                depositCount++;
                depositTotal += t.getAmount();
            } else if (t.getTransactionType().equals("WITHDRAWAL")) {
                withdrawalCount++;
                withdrawalTotal += t.getAmount();
            } else if (t.getTransactionType().equals("TRANSFER")) {
                transferCount++;
                transferTotal += t.getAmount();
            }
        }

        System.out.println("\n===== Daily Summary for " + date + " =====");
        System.out.println("Deposits:    " + depositCount + " transactions, total " + depositTotal);
        System.out.println("Withdrawals: " + withdrawalCount + " transactions, total " + withdrawalTotal);
        System.out.println("Transfers:   " + transferCount + " transactions, total " + transferTotal);
        System.out.println("All:         " + transactions.size() + " transactions");
    }

    // ===== CUSTOMER TRANSACTION STATEMENT =====
    public void printCustomerStatement(String customerId) throws SQLException {
        Customer customer = customerDAO.findById(customerId);
        if (customer == null) {
            System.out.println("No customer found with ID " + customerId);
            return;
        }

        System.out.println("\n========== IgirePay Statement ==========");
        System.out.println("Customer: " + customer.getCustomerName() + " (" + customer.getCustomerId() + ")");
        System.out.println("Email:    " + customer.getCustomerEmail());
        System.out.println("Phone:    " + customer.getCustomerPhone());
        System.out.println("Date:     " + LocalDateTime.now());

        List<Account> accounts = accountDAO.findByCustomerId(customerId);
        if (accounts.isEmpty()) {
            System.out.println("\nThis customer has no accounts.");
            return;
        }

        for (Account account : accounts) {
            System.out.println("\n--- " + account.getClass().getSimpleName() + " " + account.getAccountId()
                    + " | Balance: " + account.getBalance() + " ---");

            List<Transaction> history = transactionDAO.findByAccountId(account.getAccountId());
            if (history.isEmpty()) {
                System.out.println("  No transactions");
            } else {
                for (Transaction t : history) {
                    System.out.println("  " + t.getTimestamp() + " | " + t.getTransactionType()
                            + " | " + t.getAmount() + " | ref " + t.getReferenceId());
                }
            }
        }
        System.out.println("========================================");
    }
}