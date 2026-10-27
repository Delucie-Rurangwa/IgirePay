package lab1;

public class WalletAccount extends Account {
    private double transferLimit;

    public WalletAccount(String accountId, String customerId, double balance, double transferLimit) {
        super(accountId, customerId, balance);
        this.transferLimit = transferLimit;
    }

    // METHOD OVERIDE

    // Wallet allows instant transfers
    @Override   
    public boolean processTransaction(Transaction transaction) {
        if (transaction.getTransactionType().equals("TRANSFER")) {
            if (transaction.getAmount() > transferLimit) {
                System.out.println("Transfer failed: amount exceeds the transfer limit of " + transferLimit);
                return false;
            }
            return withdraw(transaction.getAmount());   // money leaves the wallet immediately
        }
        return super.processTransaction(transaction);   // DEPOSIT and WITHDRAWAL work as normal
    }

    public double getTransferLimit() {
        return transferLimit;
    }

    public void setTransferLimit(double transferLimit) {
        this.transferLimit = transferLimit;
    }

    @Override
    public String toString() {
        return super.toString() + " [transferLimit=" + transferLimit + "]";
    }
    }
