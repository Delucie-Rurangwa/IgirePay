package lab3;

public class InvalidAccountException extends PaymentException {
    public InvalidAccountException(String message) {
        super(message);
    }
}