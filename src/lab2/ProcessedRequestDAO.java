package lab2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProcessedRequestDAO {

    // CREATE: mark a reference ID as processed.
    // Returns false if it was already processed (duplicate).
    public boolean create(String referenceId) throws SQLException {
        String insertsql = "INSERT INTO processed_requests (reference_id) VALUES (?) "
                + "ON CONFLICT (reference_id) DO NOTHING";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertsql)) {

            statement.setString(1, referenceId);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted == 1;
        }
    }

    // READ / VALIDATE: has this reference ID already been processed?
    public boolean exists(String referenceId) throws SQLException {
        String selectsql = "SELECT 1 FROM processed_requests WHERE reference_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectsql)) {

            statement.setString(1, referenceId);

            ResultSet result = statement.executeQuery();
            return result.next();   // true if a row was found
        }
    }

    // READ: all processed reference IDs, oldest first
    public List<String> findAll() throws SQLException {
        String selectallsql = "SELECT reference_id FROM processed_requests ORDER BY processed_at";
        List<String> referenceIds = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectallsql)) {

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                referenceIds.add(result.getString("reference_id"));
            }
        }

        return referenceIds;
    }

    // DELETE: remove a processed reference ID (for cleaning up test data)
    public boolean delete(String referenceId) throws SQLException {
        String deletesql = "DELETE FROM processed_requests WHERE reference_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deletesql)) {

            statement.setString(1, referenceId);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted == 1;
        }
    }
}