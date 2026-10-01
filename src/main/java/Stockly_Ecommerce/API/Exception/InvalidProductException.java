package Stockly_Ecommerce.API.Exception;

public class InvalidProductException extends RuntimeException {

    public InvalidProductException(String message) {
        super(message);
    }
}
