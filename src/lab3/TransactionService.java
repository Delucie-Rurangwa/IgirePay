package lab3;

import lab1.Account;
import lab1.SavingsAccount;
import lab1.Transaction;
import lab2.AccountDAO;
import lab2.PaymentService;
import lab2.ProcessedRequestDAO;

import java.sql.SQLException;
import java.util.List;

public class TransactionService {

    private AccountDAO accountDAO = new AccountDAO();
    private ProcessedRequestDAO processedRequestDAO = new ProcessedRequestDAO();
    private PaymentService paymentService = new PaymentService();

    public void deposit(String accountId, double amount, String referenceId)
            throws SQLException, PaymentException {
        validateAmount(amount);
        findAccount(accountId);
        checkNotDuplicate(referenceId);

        if (!paymentService.deposit(accountId, amount, referenceId)) {
            throw new TransactionFailedException("Deposit could not be completed");
        }
    }

    public void withdraw(String accountId, double amount, String referenceId)
            throws SQLException, PaymentException {
        validateAmount(amount);
        Account account = findAccount(accountId);
        checkNotDuplicate(referenceId);
        checkSufficientBalance(account, amount);

        if (!paymentService.withdraw(accountId, amount, referenceId)) {
            throw new TransactionFailedException("Withdrawal could not be completed (check the account's withdrawal limit)");
        }
    }

    public void transfer(String fromAccountId, String toAccountId, double amount, String referenceId)
            throws SQLException, PaymentException {
        validateAmount(amount);
        Account sender = findAccount(fromAccountId);
        findAccount(toAccountId);
        checkNotDuplicate(referenceId);

        if (sender instanceof SavingsAccount) {
            throw new TransactionFailedException("Savings accounts cannot make transfers");
        }
        checkSufficientBalance(sender, amount);

        if (!paymentService.transfer(fromAccountId, toAccountId, amount, referenceId)) {
            throw new TransactionFailedException("Transfer could not be completed (check the transfer limit)");
        }
    }

    public List<Transaction> getTransactionHistory(String accountId)
            throws SQLException, PaymentException {
        findAccount(accountId);
        return paymentService.getTransactionHistory(accountId);
    }

    // ===== CHECKS =====

    private void validateAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero, but was " + amount);
        }
    }

    private Account findAccount(String accountId) throws SQLException, InvalidAccountException {
        Account account = accountDAO.findById(accountId);
        if (account == null) {
            throw new InvalidAccountException("No account found with ID " + accountId);
        }
        return account;
    }

    private void checkNotDuplicate(String referenceId) throws SQLException, DuplicateTransactionException {
        if (processedRequestDAO.exists(referenceId)) {
            throw new DuplicateTransactionException("Reference ID " + referenceId + " was already processed");
        }
    }

    private void checkSufficientBalance(Account account, double amount) throws InsufficientBalanceException {
        double required = amount;
        if (account instanceof SavingsAccount) {
            required = amount + ((SavingsAccount) account).getWithdrawalFee();
        }
        if (required > account.getBalance()) {
            throw new InsufficientBalanceException("Available balance is " + account.getBalance()
                    + ", but " + required + " is required");
        }
    }
}