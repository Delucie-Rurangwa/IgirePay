package lab3;

import lab2.AccountDAO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class PinService {

    private AccountDAO accountDAO = new AccountDAO();
    private PinDAO pinDAO = new PinDAO();

    // PIN CREATION
    public void createPin(String accountId, String pin) throws SQLException, AuthenticationException {
        checkAccountExists(accountId);

        if (pinDAO.findPinHash(accountId) != null) {
            throw new AuthenticationException("Account " + accountId + " already has a PIN. Use Change PIN instead.");
        }

        checkFormat(pin);
        pinDAO.savePinHash(accountId, hash(accountId, pin));
    }

    // PIN VALIDATION
    public void validatePin(String accountId, String pin) throws SQLException, AuthenticationException {
        checkAccountExists(accountId);

        String storedHash = pinDAO.findPinHash(accountId);
        if (storedHash == null) {
            throw new AuthenticationException("Account " + accountId
                    + " has no PIN yet. Create one in Account Management first.");
        }

        if (!storedHash.equals(hash(accountId, pin))) {
            throw new AuthenticationException("Incorrect PIN");
        }
    }

    // PIN CHANGE
    public void changePin(String accountId, String oldPin, String newPin)
            throws SQLException, AuthenticationException {
        validatePin(accountId, oldPin);   // must know the current PIN first
        checkFormat(newPin);

        if (oldPin.equals(newPin)) {
            throw new AuthenticationException("The new PIN must be different from the old PIN");
        }

        pinDAO.savePinHash(accountId, hash(accountId, newPin));
    }

    // ===== HELPERS =====

    private void checkAccountExists(String accountId) throws SQLException, AuthenticationException {
        if (accountDAO.findById(accountId) == null) {
            throw new AuthenticationException("No account found with ID " + accountId);
        }
    }

    private void checkFormat(String pin) throws AuthenticationException {
        if (!pin.matches("\\d{4}")) {
            throw new AuthenticationException("PIN must be exactly 4 digits");
        }
    }

    // Turns "accountId:pin" into a SHA-256 hash (64 hexadecimal characters)
    private String hash(String accountId, String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest((accountId + ":" + pin).getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 is not available", e);
        }
    }
}