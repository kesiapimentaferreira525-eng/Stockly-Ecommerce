package Stockly_Ecommerce.API.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record DeliveryAddressDto(
        @Schema(description = "CEP no formato 00000-000.", example = "01001-000")
        String postalCode,
        @Schema(description = "Rua ou avenida.", example = "Praça da Sé")
        String street,
        @Schema(description = "Número do endereço.", example = "100")
        String number,
        @Schema(description = "Complemento opcional.", example = "Apto 12")
        String complement,
        @Schema(description = "Bairro.", example = "Sé")
        String neighborhood,
        @Schema(description = "Cidade.", example = "São Paulo")
        String city,
        @Schema(description = "Sigla do estado.", example = "SP")
        String state
) {
}
