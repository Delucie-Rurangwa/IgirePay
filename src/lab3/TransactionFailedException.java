package lab3;

public class TransactionFailedException extends PaymentException {
    public TransactionFailedException(String message) {
        super(message);
    }
}