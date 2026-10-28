package lab3;

public class InvalidAmountException extends PaymentException {
    public InvalidAmountException(String message) {
        super(message);
    }
}