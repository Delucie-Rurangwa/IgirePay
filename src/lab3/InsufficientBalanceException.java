package lab3;

public class InsufficientBalanceException extends PaymentException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}