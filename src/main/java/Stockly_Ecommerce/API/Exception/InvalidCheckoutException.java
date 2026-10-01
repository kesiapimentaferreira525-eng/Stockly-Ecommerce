package Stockly_Ecommerce.API.Exception;

public class InvalidCheckoutException extends RuntimeException {

    public InvalidCheckoutException(String message) {
        super(message);
    }
}
