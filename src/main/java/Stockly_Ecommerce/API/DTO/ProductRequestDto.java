package Stockly_Ecommerce.API.DTO;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequestDto(
        String sku,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        UUID categoryId
) {
}
