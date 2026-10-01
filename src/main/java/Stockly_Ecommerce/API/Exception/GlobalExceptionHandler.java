package Stockly_Ecommerce.API.Exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientStock(InsufficientStockException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Erro de Estoque");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Recurso não encontrado");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ProductConflictException.class)
    public ResponseEntity<Map<String, String>> handleProductConflict(ProductConflictException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Operação não permitida");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidProductException.class)
    public ResponseEntity<Map<String, String>> handleInvalidProduct(InvalidProductException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Produto inválido");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidCheckoutException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCheckout(InvalidCheckoutException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Dados da compra inválidos");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
