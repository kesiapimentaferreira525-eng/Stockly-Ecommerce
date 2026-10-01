package Stockly_Ecommerce.API.DTO;

import Stockly_Ecommerce.API.Model.Order;
import Stockly_Ecommerce.API.Model.DeliveryAddress;
import Stockly_Ecommerce.API.Model.FulfillmentType;
import Stockly_Ecommerce.API.Model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private UUID orderId;
    private UUID productId;
    private Integer quantity;
    private BigDecimal total;
    private Integer remainingStock;
    private String productName;
    private String buyerName;
    private FulfillmentType fulfillmentType;
    private PaymentMethod paymentMethod;
    private DeliveryAddressDto deliveryAddress;
    private String pickupLocation;
    private String status;
    private String message;

    public OrderResponseDto(Order order, String pickupLocation) {
        this.orderId = order.getId();
        this.productId = order.getProduct().getId();
        this.quantity = order.getQuantity();
        this.total = order.getTotalPrice();
        this.remainingStock = order.getProduct().getStock();
        this.productName = order.getProduct().getName();
        this.buyerName = order.getBuyerName();
        this.fulfillmentType = order.getFulfillmentType();
        this.paymentMethod = order.getPaymentMethod();
        this.deliveryAddress = toDto(order.getDeliveryAddress());
        this.pickupLocation = order.getFulfillmentType() == FulfillmentType.STORE_PICKUP
                ? pickupLocation
                : null;
        this.status = order.getStatus();
        this.message = "Compra finalizada e pedido confirmado.";
    }

    private DeliveryAddressDto toDto(DeliveryAddress address) {
        if (address == null) {
            return null;
        }
        return new DeliveryAddressDto(
                address.getPostalCode(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState()
        );
    }
}
