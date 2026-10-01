package Stockly_Ecommerce.API.DTO;

import Stockly_Ecommerce.API.Model.Category;
import Stockly_Ecommerce.API.Model.Product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDto(
        UUID id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Category category,
        boolean hasSales
) {
    public ProductResponseDto(Product product, boolean hasSales) {
        this(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                hasSales
        );
    }
}
