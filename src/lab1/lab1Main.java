package lab1;

public class lab1Main {
    public static void main(String[] args) {

        PaymentProcessor processor = new PaymentProcessor();

        // Customers
        Customer miracle = new Customer("C1", "Miracle Uwase", "miracle@gmail.com", "0788000001");
        Customer kian = new Customer("C2", "Kian Byiza", "kian@gmail.com", "0788000002");
        processor.addCustomer(miracle);
        processor.addCustomer(kian);

        // Accounts
        Account miracleWallet = new WalletAccount("W1", "C1", 50000, 100000);
        Account miracleSavings = new SavingsAccount("S1", "C1", 200000, 50000, 500);
        Account kianWallet = new WalletAccount("W2", "C2", 10000, 100000);
        processor.addAccount(miracleWallet);
        processor.addAccount(miracleSavings);
        processor.addAccount(kianWallet);

        System.out.println("=== Customers ===");
        System.out.println(miracle);
        System.out.println(kian);

        System.out.println("\n=== 1. Deposit 20000 into W1 (REF-001) ===");
        processor.processTransaction("W1", new Transaction("TXN-1", "REF-001", 20000, "DEPOSIT"));
        System.out.println(miracleWallet);

        System.out.println("\n=== 2. Same request sent again after a timeout (REF-001) ===");
        processor.processTransaction("W1", new Transaction("TXN-2", "REF-001", 20000, "DEPOSIT"));
        System.out.println(miracleWallet);

        System.out.println("\n=== 3. Withdraw 10000 from savings S1 (fee applies) ===");
        processor.processTransaction("S1", new Transaction("TXN-3", "REF-002", 10000, "WITHDRAWAL"));
        System.out.println(miracleSavings);

        System.out.println("\n=== 4. Withdraw 60000 from savings S1 (above limit) ===");
        processor.processTransaction("S1", new Transaction("TXN-4", "REF-003", 60000, "WITHDRAWAL"));
        System.out.println(miracleSavings);

        System.out.println("\n=== 5. Instant transfer of 15000 from wallet W1 ===");
        processor.processTransaction("W1", new Transaction("TXN-5", "REF-004", 15000, "TRANSFER"));
        System.out.println(miracleWallet);

        System.out.println("\n=== 6. Transfer from savings S1 (not allowed) ===");
        processor.processTransaction("S1", new Transaction("TXN-6", "REF-005", 5000, "TRANSFER"));

        System.out.println("\n=== 7. Deposit into an account that doesn't exist ===");
        processor.processTransaction("W9", new Transaction("TXN-7", "REF-006", 1000, "DEPOSIT"));

        System.out.println("\n=== Transaction history of W1 ===");
        for (Transaction t : miracleWallet.getTransactionHistory()) {
            System.out.println(t);
        }

        System.out.println("\n=== Processed reference IDs ===");
        System.out.println(processor.getProcessedReferenceIds());

        System.out.println("\n=== Failed transaction logs ===");
        for (String log : processor.getFailedTransactionLogs()) {
            System.out.println(log);
        }
    }
}