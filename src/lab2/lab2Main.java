package lab2;

import lab1.Account;
import lab1.Customer;
import lab1.SavingsAccount;
import lab1.WalletAccount;
import lab1.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class lab2Main {
    public static void main(String[] args) throws SQLException {

        CustomerDAO customerDAO = new CustomerDAO();

        // ===== CREATE =====
        System.out.println("=== CREATE ===");
        if (customerDAO.findById("C1") == null) {
            customerDAO.create(new Customer("C1", "Miracle Uwase", "miracle@gmail.com", "0788000001"));
            System.out.println("Miracle saved");
        } else {
            System.out.println("Miracle already exists");
        }
        if (customerDAO.findById("C2") == null) {
            customerDAO.create(new Customer("C2", "Kian Byiza", "kian@gmail.com", "0788000002"));
            System.out.println("Kian saved");
        } else {
            System.out.println("Kian already exists");
        }

        // ===== READ one =====
        System.out.println("\n=== READ: find C1 ===");
        System.out.println(customerDAO.findById("C1"));

        System.out.println("\n=== READ: find C99 (doesn't exist) ===");
        System.out.println(customerDAO.findById("C99"));

        // ===== READ all =====
        System.out.println("\n=== READ: all customers ===");
        for (Customer customer : customerDAO.findAll()) {
            System.out.println(customer);
        }

        // ===== UPDATE =====
        System.out.println("\n=== UPDATE Kian's email ===");
        Customer kian = customerDAO.findById("C2");
        kian.setCustomerEmail("kian.byiza@gmail.com");
        System.out.println("Updated? " + customerDAO.update(kian));
        System.out.println(customerDAO.findById("C2"));

        System.out.println("\n=== UPDATE a customer that doesn't exist ===");
        Customer ghost = new Customer("C99", "Nobody", "nobody@gmail.com", "0700000000");
        System.out.println("Updated? " + customerDAO.update(ghost));

        // ===== DELETE =====
        System.out.println("\n=== DELETE a temporary customer ===");
        customerDAO.create(new Customer("C3", "Test User", "test@gmail.com", "0788000003"));
        System.out.println("Before delete: " + customerDAO.findById("C3"));
        System.out.println("Deleted? " + customerDAO.delete("C3"));
        System.out.println("After delete: " + customerDAO.findById("C3"));

        System.out.println("\n=== DELETE a customer that doesn't exist ===");
        System.out.println("Deleted? " + customerDAO.delete("C99"));

        // ===== CUSTOM QUERY =====
        System.out.println("\n=== CUSTOM: find by phone number ===");
        System.out.println(customerDAO.findByPhone("0788000001"));

        System.out.println("\n=== CUSTOM: phone number not registered ===");
        System.out.println(customerDAO.findByPhone("0799999999"));


        // ===== ACCOUNT DAO =====
        AccountDAO accountDAO = new AccountDAO();

        System.out.println("\n=== CREATE accounts ===");
        System.out.println("W1 saved? " + accountDAO.create(new WalletAccount("W1", "C1", 50000, 100000)));
        System.out.println("S1 saved? " + accountDAO.create(new SavingsAccount("S1", "C1", 200000, 50000, 500)));
        System.out.println("W2 saved? " + accountDAO.create(new WalletAccount("W2", "C2", 10000, 100000)));

        // ===== ACCOUNT DAO =====
        AccountDAO accountdao = new AccountDAO();

        System.out.println("\n=== CREATE accounts ===");
        if (accountDAO.findById("W1") == null) {
            accountDAO.create(new WalletAccount("W1", "C1", 50000, 100000));
        }
        if (accountDAO.findById("S1") == null) {
            accountDAO.create(new SavingsAccount("S1", "C1", 200000, 50000, 500));
        }
        if (accountDAO.findById("W2") == null) {
            accountDAO.create(new WalletAccount("W2", "C2", 10000, 100000));
        }
        System.out.println("Accounts ready");

        System.out.println("\n=== READ: find accounts by ID ===");
        System.out.println(accountDAO.findById("W1"));
        System.out.println(accountDAO.findById("S1"));
        System.out.println(accountDAO.findById("W99"));


        System.out.println("\n=== CUSTOM: Miracle's accounts (C1) ===");
        for (Account account : accountDAO.findByCustomerId("C1")) {
            System.out.println(account);
        }

        System.out.println("\n=== CUSTOM: Kian's accounts (C2) ===");
        for (Account account : accountDAO.findByCustomerId("C2")) {
            System.out.println(account);



            System.out.println("\n=== UPDATE: deposit 20000 into W1 ===");
            Account w1 = accountDAO.findById("W1");                    // 1. load from the database
            System.out.println("Before: " + w1.getBalance());
            w1.deposit(20000);                                          // 2. Lab 1 rule changes it in memory
            accountDAO.updateBalance("W1", w1.getBalance());            // 3. save the new balance
            System.out.println("After:  " + accountDAO.findById("W1").getBalance());   // 4. reload to confirm

            System.out.println("\n=== UPDATE: account that doesn't exist ===");
            System.out.println("Updated? " + accountDAO.updateBalance("W99", 1000));

            // Reset W1 so this test gives the same result every time
            accountDAO.updateBalance("W1", 50000);

        }
           }

    // DELETE: remove an account, but only if it is inactive
    // (zero balance and no transactions)
    public boolean deleteInactive(String accountId) throws SQLException {
        String deletesql = "DELETE FROM accounts "
                + "WHERE id = ? AND balance = 0 "
                + "AND NOT EXISTS (SELECT 1 FROM transactions WHERE account_id = accounts.id)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deletesql)) {

            statement.setString(1, accountId);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted == 1;
        }
        // ===== TRANSACTION DAO =====
        TransactionDAO transactionDAO = new TransactionDAO();

        System.out.println("\n=== CREATE: record a transaction for W1 ===");
        Transaction tx = new Transaction("TXN-" + System.currentTimeMillis(), "REF-TEST", 5000, "DEPOSIT");
        System.out.println("Saved? " + transactionDAO.create("W1", tx));
    }
    }


