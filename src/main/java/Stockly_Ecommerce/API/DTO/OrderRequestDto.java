package Stockly_Ecommerce.API.DTO;

import Stockly_Ecommerce.API.Model.FulfillmentType;
import Stockly_Ecommerce.API.Model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    @Schema(description = "UUID do produto comprado.")
    private UUID productId;

    @Schema(description = "Quantidade desejada.", example = "2", minimum = "1")
    private Integer quantity;

    @Schema(description = "Nome do comprador.", example = "Maria da Silva")
    private String buyerName;

    @Schema(description = "Forma de recebimento: DELIVERY ou STORE_PICKUP.")
    private FulfillmentType fulfillmentType;

    @Schema(description = "Forma de pagamento escolhida: PIX, CREDIT_CARD, DEBIT_CARD ou CASH.")
    private PaymentMethod paymentMethod;

    @Schema(description = "Obrigatório para DELIVERY; omitido ou nulo para STORE_PICKUP.")
    private DeliveryAddressDto deliveryAddress;
}
