package lab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PaymentProcessor {

    private Map<String, Customer> customers = new HashMap<>();
    private Map<String, Account> accounts = new HashMap<>();
    private Set<String> processedReferenceIds = new HashSet<>();
    private List<String> failedTransactionLogs = new ArrayList<>();

    public void addCustomer(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    public boolean addAccount(Account account) {
        Customer owner = customers.get(account.getCustomerId());
        if (owner == null) {
            System.out.println("Cannot add account: customer " + account.getCustomerId() + " not found");
            return false;
        }
        owner.addAccount(account);
        accounts.put(account.getAccountId(), account);
        return true;
    }

    public boolean processTransaction(String accountId, Transaction transaction) {
        String referenceId = transaction.getReferenceId();

        // 1. Duplicate check: has this reference ID already been processed?

        if (processedReferenceIds.contains(referenceId)) {
            System.out.println("Duplicate request rejected: " + referenceId + " was already processed");
            failedTransactionLogs.add(referenceId + " | " + accountId + " | Duplicate request");
            return false;
        }

        // 2. Does the account exist?

        Account account = accounts.get(accountId);
        if (account == null) {
            System.out.println("Transaction failed: account " + accountId + " not found");
            failedTransactionLogs.add(referenceId + " | " + accountId + " | Account not found");
            return false;
        }

        // 3. Let the account process it (polymorphism: wallet or savings rules apply)
        boolean success = account.processTransaction(transaction);

        if (success) {
            processedReferenceIds.add(referenceId);
            account.addTransaction(transaction);
            return true;
        } else {
            failedTransactionLogs.add(referenceId + " | " + accountId + " | "
                    + transaction.getTransactionType() + " of " + transaction.getAmount() + " rejected");
            return false;
        }
    }


    //getters

    public Map<String, Customer> getCustomers() {
        return customers;
    }

    public Map<String, Account> getAccounts() {
        return accounts;
    }

    public Set<String> getProcessedReferenceIds() {
        return processedReferenceIds;
    }

    public List<String> getFailedTransactionLogs() {
        return failedTransactionLogs;
    }
}