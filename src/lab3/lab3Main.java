package lab3;

import lab1.Account;
import lab1.Customer;
import lab1.SavingsAccount;
import lab1.Transaction;
import lab1.WalletAccount;
import lab2.AccountDAO;
import lab2.CustomerDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class lab3Main {

    private static Scanner scanner = new Scanner(System.in);
    private static CustomerDAO customerDAO = new CustomerDAO();
    private static AccountDAO accountDAO = new AccountDAO();
    private static TransactionService transactionService = new TransactionService();
    private static ReportService reportService = new ReportService();
    private static PinService pinService = new PinService();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n===== IgirePay =====");
            System.out.println("1. Customer Management");
            System.out.println("2. Account Management");
            System.out.println("3. Transaction Management");
            System.out.println("4. Reports");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            try {
                if (choice.equals("1")) {
                    customerMenu();
                } else if (choice.equals("2")) {
                    accountMenu();
                } else if (choice.equals("3")) {
                    transactionMenu();
                } else if (choice.equals("4")) {
                    reportMenu();
                } else if (choice.equals("0")) {
                    running = false;
                    System.out.println("Goodbye!");
                } else {
                    System.out.println("Invalid option, please try again");
                }

            } catch (AuthenticationException e) {
                System.out.println("Authentication failed: " + e.getMessage());
            } catch (InvalidAmountException e) {
                System.out.println("Invalid amount: " + e.getMessage());
            } catch (InvalidAccountException e) {
                System.out.println("Invalid account: " + e.getMessage());
            } catch (DuplicateTransactionException e) {
                System.out.println("Duplicate request rejected: " + e.getMessage());
            } catch (InsufficientBalanceException e) {
                System.out.println("Insufficient balance: " + e.getMessage());
            } catch (PaymentException e) {
                System.out.println("Transaction failed: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, please enter digits only (for example 5000)");
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date, please use the format YYYY-MM-DD (for example 2026-10-28)");
            } catch (IOException e) {
                System.out.println("Could not write the file: " + e.getMessage());
            } catch (SQLException e) {
                if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
                    System.out.println("Cannot connect to the database. Make sure PostgreSQL is running.");
                } else {
                    System.out.println("Database error: " + e.getMessage());
                }
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
    private static void accountMenu() throws SQLException, AuthenticationException {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n--- Account Management ---");
            System.out.println("1. Create wallet account");
            System.out.println("2. Create savings account");
            System.out.println("3. View account balance");
            System.out.println("4. Delete inactive account");
            System.out.println("5. Create PIN");
            System.out.println("6. Change PIN");
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
            } else if (choice.equals("5")) {
                createPin();
            } else if (choice.equals("6")) {
                changePin();
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

    private static void createPin() throws SQLException, AuthenticationException {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine();
        System.out.print("New 4-digit PIN: ");
        String pin = scanner.nextLine();

        pinService.createPin(accountId, pin);
        System.out.println("PIN created for account " + accountId);
    }

    private static void changePin() throws SQLException, AuthenticationException {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine();
        System.out.print("Current PIN: ");
        String oldPin = scanner.nextLine();
        System.out.print("New 4-digit PIN: ");
        String newPin = scanner.nextLine();

        pinService.changePin(accountId, oldPin, newPin);
        System.out.println("PIN changed for account " + accountId);
    }

    // ===== TRANSACTION MANAGEMENT =====
    private static void transactionMenu() throws SQLException, PaymentException, AuthenticationException {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n--- Transaction Management ---");
            System.out.println("1. Deposit money");
            System.out.println("2. Withdraw money");
            System.out.println("3. Transfer money");
            System.out.println("4. View transaction history");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                deposit();
            } else if (choice.equals("2")) {
                withdraw();
            } else if (choice.equals("3")) {
                transfer();
            } else if (choice.equals("4")) {
                viewTransactionHistory();
            } else if (choice.equals("0")) {
                inMenu = false;
            } else {
                System.out.println("Invalid option, please try again");
            }
        }
    }

    private static void deposit() throws SQLException, PaymentException {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine();
        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Reference ID: ");
        String referenceId = scanner.nextLine();

        transactionService.deposit(accountId, amount, referenceId);
        System.out.println("Deposit successful. New balance: " + accountDAO.findById(accountId).getBalance());
    }

    private static void withdraw() throws SQLException, PaymentException, AuthenticationException {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine();
        System.out.print("PIN: ");
        String pin = scanner.nextLine();
        pinService.validatePin(accountId, pin);   // stops here if the PIN is wrong

        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Reference ID: ");
        String referenceId = scanner.nextLine();

        transactionService.withdraw(accountId, amount, referenceId);
        System.out.println("Withdrawal successful. New balance: " + accountDAO.findById(accountId).getBalance());
    }

    private static void transfer() throws SQLException, PaymentException, AuthenticationException {
        System.out.print("From account ID: ");
        String fromAccountId = scanner.nextLine();
        System.out.print("PIN: ");
        String pin = scanner.nextLine();
        pinService.validatePin(fromAccountId, pin);   // stops here if the PIN is wrong

        System.out.print("To account ID: ");
        String toAccountId = scanner.nextLine();
        System.out.print("Amount: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Reference ID: ");
        String referenceId = scanner.nextLine();

        transactionService.transfer(fromAccountId, toAccountId, amount, referenceId);
        System.out.println("Transfer successful.");
        System.out.println(fromAccountId + " balance: " + accountDAO.findById(fromAccountId).getBalance());
        System.out.println(toAccountId + " balance: " + accountDAO.findById(toAccountId).getBalance());
    }

    private static void viewTransactionHistory() throws SQLException, PaymentException {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine();

        List<Transaction> history = transactionService.getTransactionHistory(accountId);
        if (history.isEmpty()) {
            System.out.println("No transactions yet for " + accountId);
        } else {
            for (Transaction transaction : history) {
                System.out.println(transaction);
            }
        }
    }

    // ===== REPORTS =====
    private static void reportMenu() throws SQLException, PaymentException, IOException {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n--- Reports ---");
            System.out.println("1. Export transaction history to CSV");
            System.out.println("2. View daily transaction summary");
            System.out.println("3. Display customer transaction statement");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                exportToCsv();
            } else if (choice.equals("2")) {
                dailySummary();
            } else if (choice.equals("3")) {
                customerStatement();
            } else if (choice.equals("0")) {
                inMenu = false;
            } else {
                System.out.println("Invalid option, please try again");
            }
        }
    }

    private static void exportToCsv() throws SQLException, IOException, PaymentException {
        System.out.print("Account ID: ");
        String accountId = scanner.nextLine();

        String fileName = reportService.exportTransactionsToCsv(accountId);
        System.out.println("Transaction history exported to " + fileName);
    }

    private static void dailySummary() throws SQLException {
        System.out.print("Date (YYYY-MM-DD), or press Enter for today: ");
        String input = scanner.nextLine();

        LocalDate date;
        if (input.isBlank()) {
            date = LocalDate.now();
        } else {
            date = LocalDate.parse(input);
        }

        reportService.printDailySummary(date);
    }

    private static void customerStatement() throws SQLException {
        System.out.print("Customer ID: ");
        String customerId = scanner.nextLine();

        reportService.printCustomerStatement(customerId);
    }
}