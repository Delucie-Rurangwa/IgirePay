package lab3;

import lab1.Account;
import lab1.Customer;
import lab1.SavingsAccount;
import lab1.WalletAccount;
import lab2.AccountDAO;
import lab2.CustomerDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class lab3Main {

    private static Scanner scanner = new Scanner(System.in);
    private static CustomerDAO customerDAO = new CustomerDAO();
    private static AccountDAO accountDAO = new AccountDAO();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n===== IgirePay =====");
            System.out.println("1. Customer Management");
            System.out.println("2. Account Management");
            System.out.println("3. Transaction Management");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            try {
                if (choice.equals("1")) {
                    customerMenu();
                } else if (choice.equals("2")) {
                    accountMenu();
                } else if (choice.equals("3")) {
                    System.out.println("Transaction Management: coming next");
                } else if (choice.equals("0")) {
                    running = false;
                    System.out.println("Goodbye!");
                } else {
                    System.out.println("Invalid option, please try again");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, please enter digits only (for example 5000)");
            }
        }
    }

    // ===== CUSTOMER MANAGEMENT =====
    private static void customerMenu() throws SQLException {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n--- Customer Management ---");
            System.out.println("1. Register customer");
            System.out.println("2. Update customer information");
            System.out.println("3. View customer accounts");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                registerCustomer();
            } else if (choice.equals("2")) {
                updateCustomer();
            } else if (choice.equals("3")) {
                viewCustomerAccounts();
            } else if (choice.equals("0")) {
                inMenu = false;
            } else {
                System.out.println("Invalid option, please try again");
            }
        }
    }

    private static void registerCustomer() throws SQLException {
        System.out.print("Customer ID: ");
        String id = scanner.nextLine();

        if (customerDAO.findById(id) != null) {
            System.out.println("A customer with ID " + id + " already exists");
            return;
        }

        System.out.print("Full name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone number: ");
        String phone = scanner.nextLine();

        if (customerDAO.create(new Customer(id, name, email, phone))) {
            System.out.println("Customer registered successfully");
        } else {
            System.out.println("Registration failed");
        }
    }

    private static void updateCustomer() throws SQLException {
        System.out.print("Customer ID: ");
        String id = scanner.nextLine();

        Customer customer = customerDAO.findById(id);
        if (customer == null) {
            System.out.println("No customer found with ID " + id);
            return;
        }

        System.out.println("Current details: " + customer);
        System.out.println("Press Enter to keep the current value.");

        System.out.print("New full name: ");
        String name = scanner.nextLine();
        if (!name.isBlank()) {
            customer.setCustomerName(name);
        }

        System.out.print("New email: ");
        String email = scanner.nextLine();
        if (!email.isBlank()) {
            customer.setCustomerEmail(email);
        }

        System.out.print("New phone number: ");
        String phone = scanner.nextLine();
        if (!phone.isBlank()) {
            customer.setCustomerPhone(phone);
        }

        if (customerDAO.update(customer)) {
            System.out.println("Customer updated: " + customerDAO.findById(id));
        } else {
            System.out.println("Update failed");
        }
    }

    private static void viewCustomerAccounts() throws SQLException {
        System.out.print("Customer ID: ");
        String id = scanner.nextLine();

        List<Account> accounts = accountDAO.findByCustomerId(id);
        if (accounts.isEmpty()) {
            System.out.println("No accounts found for customer " + id);
        } else {
            for (Account account : accounts) {
                System.out.println(account);
            }
        }
    }

    // ===== ACCOUNT MANAGEMENT =====
    private static void accountMenu() throws SQLException {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n--- Account Management ---");
            System.out.println("1. Create wallet account");
            System.out.println("2. Create savings account");
            System.out.println("3. View account balance");
            System.out.println("4. Delete inactive account");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                createWallet();
            } else if (choice.equals("2")) {
                createSavings();
            } else if (choice.equals("3")) {
                viewBalance();
            } else if (choice.equals("4")) {
                deleteInactiveAccount();
            } else if (choice.equals("0")) {
                inMenu = false;
            } else {
                System.out.println("Invalid option, please try again");
            }
        }
    }

    private static void createWallet() throws SQLException {
        System.out.print("New account ID: ");
        String accountId = scanner.nextLine();
        if (accountDAO.findById(accountId) != null) {
            System.out.println("An account with ID " + accountId + " already exists");
            return;
        }

        System.out.print("Customer ID: ");
        String customerId = scanner.nextLine();
        if (customerDAO.findById(customerId) == null) {
            System.out.println("No customer found with ID " + customerId);
            return;
        }

        System.out.print("Opening balance: ");
        double balance = Double.parseDouble(scanner.nextLine());
        System.out.print("Transfer limit: ");
        double transferLimit = Double.parseDouble(scanner.nextLine());

        if (accountDAO.create(new WalletAccount(accountId, customerId, balance, transferLimit))) {
            System.out.println("Wallet account created: " + accountDAO.findById(accountId));
        } else {
            System.out.println("Account creation failed");
        }
    }

    private static void createSavings() throws SQLException {
        System.out.print("New account ID: ");
        String accountId = scanner.nextLine();
        if (accountDAO.findById(accountId) != null) {
            System.out.println("An account with ID " + accountId + " already exists");
            return;
        }

        System.out.print("Customer ID: ");
        String customerId = scanner.nextLine();
        if (customerDAO.findById(customerId) == null) {
            System.out.println("No customer found with ID " + customerId);
            return;
        }

        System.out.print("Opening balance: ");
        double balance = Double.parseDouble(scanner.nextLine());
        System.out.print("Withdrawal limit: ");
        double withdrawalLimit = Double.parseDouble(scanner.nextLine());
        System.out.print("Withdrawal fee: ");
        double withdrawalFee = Double.parseDouble(scanner.nextLine());

        if (accountDAO.create(new SavingsAccount(accountId, customerId, balance, withdrawalLimit, withdrawalFee))) {
            System.out.println("Savings account created: " + accountDAO.findById(accountId));
        } else {
            System.out.println("Account creation failed");
        }
    }

    private static void viewBalance() throws SQLException {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine();

        Account account = accountDAO.findById(accountId);
        if (account == null) {
            System.out.println("No account found with ID " + accountId);
        } else {
            System.out.println("Balance of " + accountId + ": " + account.getBalance());
        }
    }

    private static void deleteInactiveAccount() throws SQLException {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine();

        if (accountDAO.findById(accountId) == null) {
            System.out.println("No account found with ID " + accountId);
            return;
        }

        if (accountDAO.deleteInactive(accountId)) {
            System.out.println("Account " + accountId + " deleted");
        } else {
            System.out.println("Account " + accountId + " is still active (it has money or transaction history) "
                    + "and cannot be deleted");
        }
    }
}