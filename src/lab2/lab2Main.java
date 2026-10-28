package lab2;

import lab1.Customer;
import lab1.SavingsAccount;
import lab1.Transaction;
import lab1.WalletAccount;

import java.sql.SQLException;

public class lab2Main {
    public static void main(String[] args) throws SQLException {

        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();
        PaymentService paymentService = new PaymentService();

        // ===== SETUP: customers and accounts (created only if missing) =====
        if (customerDAO.findById("C1") == null) {
            customerDAO.create(new Customer("C1", "Miracle Uwase", "miracle@gmail.com", "0788000001"));
        }
        if (customerDAO.findById("C2") == null) {
            customerDAO.create(new Customer("C2", "Kian Byiza", "kian@gmail.com", "0788000002"));
        }
        if (accountDAO.findById("W1") == null) {
            accountDAO.create(new WalletAccount("W1", "C1", 50000, 100000));
        }
        if (accountDAO.findById("S1") == null) {
            accountDAO.create(new SavingsAccount("S1", "C1", 200000, 50000, 500));
        }
        if (accountDAO.findById("W2") == null) {
            accountDAO.create(new WalletAccount("W2", "C2", 10000, 100000));
        }

        // Reset balances so every run gives the same results
        accountDAO.updateBalance("W1", 50000);
        accountDAO.updateBalance("S1", 200000);
        accountDAO.updateBalance("W2", 10000);

        // Reference IDs must be new each run; the time in milliseconds makes them unique
        String run = String.valueOf(System.currentTimeMillis());

        System.out.println("=== 1. Deposit 20000 into W1 ===");
        paymentService.deposit("W1", 20000, "REF-" + run + "-1");
        System.out.println(accountDAO.findById("W1"));

        System.out.println("\n=== 2. Same request retried after a timeout ===");
        paymentService.deposit("W1", 20000, "REF-" + run + "-1");
        System.out.println(accountDAO.findById("W1"));

        System.out.println("\n=== 3. Withdraw 10000 from savings S1 (fee applies) ===");
        paymentService.withdraw("S1", 10000, "REF-" + run + "-2");
        System.out.println(accountDAO.findById("S1"));

        System.out.println("\n=== 4. Withdraw 60000 from savings S1 (above limit) ===");
        paymentService.withdraw("S1", 60000, "REF-" + run + "-3");
        System.out.println(accountDAO.findById("S1"));

        System.out.println("\n=== 5. Transfer 15000 from W1 to W2 ===");
        paymentService.transfer("W1", "W2", 15000, "REF-" + run + "-4");
        System.out.println(accountDAO.findById("W1"));
        System.out.println(accountDAO.findById("W2"));

        System.out.println("\n=== 6. Transfer from savings S1 (not allowed) ===");
        paymentService.transfer("S1", "W2", 5000, "REF-" + run + "-5");

        System.out.println("\n=== 7. Deposit into an account that doesn't exist ===");
        paymentService.deposit("W99", 1000, "REF-" + run + "-6");

        System.out.println("\n=== Transaction history of W2 ===");
        for (Transaction t : paymentService.getTransactionHistory("W2")) {
            System.out.println(t);
        }
    }
}