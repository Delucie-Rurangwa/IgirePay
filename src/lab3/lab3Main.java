package lab3;

import lab1.Account;
import lab1.Customer;
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
                    System.out.println("Account Management: coming next");
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
}