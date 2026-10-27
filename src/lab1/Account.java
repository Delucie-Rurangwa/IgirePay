package lab1;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private String accountId;
    private String customerId;
    private double balance;


    private List<Transaction> transactionHistory = new ArrayList<>();

    public Account(String accountId, String customerId, double balance) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = balance;
    }

    //METHODS

    public boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit failed: amount must be greater than zero");
            return false;
        }
        balance = balance + amount;
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal failed: amount must be greater than zero");
            return false;
        }
        if (amount > balance) {
            System.out.println("Withdrawal failed: insufficient balance. Available: " + balance);
            return false;
        }
        balance = balance - amount;
        return true;
    }

    public boolean processTransaction(Transaction transaction) {
        if (transaction.getTransactionType().equals("DEPOSIT")) {
            return deposit(transaction.getAmount());
        } else if (transaction.getTransactionType().equals("WITHDRAWAL")) {
            return withdraw(transaction.getAmount());
        } else {
            System.out.println("Transaction failed: " + transaction.getTransactionType()
                    + " is not allowed on this account");
            return false;
        }

    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    // list

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(List<Transaction> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }




    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "accountId='" + accountId + '\'' +
                ", balance=" + balance +
                ", customerId='" + customerId + '\'' +
                '}';

    }
}
