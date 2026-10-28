package lab2;

import lab1.Account;
import lab1.Transaction;

import java.sql.SQLException;
import java.util.List;

public class PaymentService {

    private AccountDAO accountDAO = new AccountDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();
    private ProcessedRequestDAO processedRequestDAO = new ProcessedRequestDAO();

    // DEPOSIT
    public boolean deposit(String accountId, double amount, String referenceId) throws SQLException {
        return processSingle(accountId, amount, referenceId, "DEPOSIT");
    }

    // WITHDRAW
    public boolean withdraw(String accountId, double amount, String referenceId) throws SQLException {
        return processSingle(accountId, amount, referenceId, "WITHDRAWAL");
    }

    // Shared steps for deposits and withdrawals
    private boolean processSingle(String accountId, double amount, String referenceId, String type)
            throws SQLException {

        // 1. Does the account exist?
        Account account = accountDAO.findById(accountId);
        if (account == null) {
            System.out.println("Rejected: account " + accountId + " not found");
            return false;
        }

        // 2. Claim the reference ID (duplicate check)
        if (!processedRequestDAO.create(referenceId)) {
            System.out.println("Duplicate request rejected: " + referenceId + " was already processed");
            return false;
        }

        // 3. Apply the Lab 1 rules (wallet or savings, through polymorphism)
        Transaction transaction = new Transaction("TXN-" + referenceId, referenceId, amount, type);
        boolean success = account.processTransaction(transaction);

        // 4. Rejected by the rules: release the reference ID so the customer can retry
        if (!success) {
            processedRequestDAO.delete(referenceId);
            return false;
        }

        // 5. Accepted: save the new balance and record the transaction
        accountDAO.updateBalance(accountId, account.getBalance());
        transactionDAO.create(accountId, transaction);
        return true;
    }

    // TRANSFER between two accounts
    public boolean transfer(String fromAccountId, String toAccountId, double amount, String referenceId)
            throws SQLException {

        // 1. Do both accounts exist?
        Account sender = accountDAO.findById(fromAccountId);
        Account receiver = accountDAO.findById(toAccountId);
        if (sender == null || receiver == null) {
            System.out.println("Rejected: account " + (sender == null ? fromAccountId : toAccountId) + " not found");
            return false;
        }

        // 2. Claim the reference ID (duplicate check)
        if (!processedRequestDAO.create(referenceId)) {
            System.out.println("Duplicate request rejected: " + referenceId + " was already processed");
            return false;
        }

        // 3. Sender: Lab 1 rules decide (wallet allows transfers, savings does not)
        Transaction outgoing = new Transaction("TXN-" + referenceId + "-OUT", referenceId, amount, "TRANSFER");
        if (!sender.processTransaction(outgoing)) {
            processedRequestDAO.delete(referenceId);   // 4. rejected: release the reference ID
            return false;
        }

        // Receiver: the money arrives as a deposit
        Transaction incoming = new Transaction("TXN-" + referenceId + "-IN", referenceId, amount, "DEPOSIT");
        receiver.processTransaction(incoming);

        // 5. Save both balances and record both transactions
        accountDAO.updateBalance(fromAccountId, sender.getBalance());
        accountDAO.updateBalance(toAccountId, receiver.getBalance());
        transactionDAO.create(fromAccountId, outgoing);
        transactionDAO.create(toAccountId, incoming);
        return true;
    }

    // VIEW TRANSACTION HISTORY
    public List<Transaction> getTransactionHistory(String accountId) throws SQLException {
        return transactionDAO.findByAccountId(accountId);
    }
}