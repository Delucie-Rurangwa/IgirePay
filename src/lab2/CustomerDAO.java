package lab2;

import lab1.Customer;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerDAO {

    // CREATE a new customer to the database
    public boolean create(Customer customer) throws SQLException {
        String insertsql = "INSERT INTO customers (id, full_name, email, phone_number) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertsql)) {

            statement.setString(1, customer.getCustomerId());
            statement.setString(2, customer.getCustomerName());
            statement.setString(3, customer.getCustomerEmail());
            statement.setString(4, customer.getCustomerPhone());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted == 1;

        }
    }

    // READ one customer by ID
    public Customer findById(String id) throws SQLException {
        String selectsql = "SELECT id, full_name, email, phone_number FROM customers WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectsql)) {

            statement.setString(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return new Customer(
                        result.getString("id"),
                        result.getString("full_name"),
                        result.getString("email"),
                        result.getString("phone_number"));
            }

            return null;
        }
    }

    // READ all customers
    public List<Customer> findAll() throws SQLException {
        String selectallsql = "SELECT id, full_name, email, phone_number FROM customers ORDER BY id";
        List<Customer> customers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectallsql)) {

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                customers.add(new Customer(
                        result.getString("id"),
                        result.getString("full_name"),
                        result.getString("email"),
                        result.getString("phone_number")));
            }
        }

        return customers;
    }

    // UPDATE a customer's details
    public boolean update(Customer customer) throws SQLException {
        String updatesql = "UPDATE customers SET full_name = ?, email = ?, phone_number = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updatesql)) {

            statement.setString(1, customer.getCustomerName());
            statement.setString(2, customer.getCustomerEmail());
            statement.setString(3, customer.getCustomerPhone());
            statement.setString(4, customer.getCustomerId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated == 1;
        }
    }

    // DELETE a customer by ID
    public boolean delete(String id) throws SQLException {
        String deletesql = "DELETE FROM customers WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deletesql)) {

            statement.setString(1, id);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted == 1;
        }
    }

    // CUSTOM QUERY: find a customer by phone number
    public Customer findByPhone(String phoneNumber) throws SQLException {
        String selectphonesql = "SELECT id, full_name, email, phone_number FROM customers WHERE phone_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectphonesql)) {

            statement.setString(1, phoneNumber);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return new Customer(
                        result.getString("id"),
                        result.getString("full_name"),
                        result.getString("email"),
                        result.getString("phone_number"));
            }

            return null;
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
    }
}