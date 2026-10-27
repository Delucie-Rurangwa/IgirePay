package lab1;

public class SavingsAccount extends Account {
    private double withdrawalLimit;
    private double withdrawalFee;


    public SavingsAccount(String accountId, String customerId, double balance,
                           double withdrawalLimit, double withdrawalFee) {
    super(accountId, customerId, balance);
    this.withdrawalLimit = withdrawalLimit;
    this.withdrawalFee = withdrawalFee;
}

    // withdrawal limit and fee method

    // Savings applies a withdrawal limit and a fee
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal failed: amount must be greater than zero");
            return false;
        }
        if (amount > withdrawalLimit) {
            System.out.println("Withdrawal failed: savings withdrawal limit is " + withdrawalLimit);
            return false;
        }
        double total = amount + withdrawalFee;
        if (total > getBalance()) {
            System.out.println("Withdrawal failed: insufficient balance for amount + fee (" + total + ")");
            return false;
        }
        setBalance(getBalance() - total);
        return true;
    }

    public double getWithdrawalLimit() {
        return withdrawalLimit;
    }

    public void setWithdrawalLimit(double withdrawalLimit) {
        this.withdrawalLimit = withdrawalLimit;
    }

    public double getWithdrawalFee() {
        return withdrawalFee;
    }

    public void setWithdrawalFee(double withdrawalFee) {
        this.withdrawalFee = withdrawalFee;
    }

    @Override
    public String toString() {
        return super.toString() +
                " [withdrawalLimit=" + withdrawalLimit +
                ", withdrawalFee=" + withdrawalFee + "]";
    }
    }

