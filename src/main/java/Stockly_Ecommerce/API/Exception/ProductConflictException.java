package Stockly_Ecommerce.API.Exception;

public class ProductConflictException extends RuntimeException {

    public ProductConflictException(String message) {
        super(message);
    }
}
