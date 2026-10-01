package Stockly_Ecommerce.API.Service;

import Stockly_Ecommerce.API.DTO.DeliveryAddressDto;
import Stockly_Ecommerce.API.DTO.OrderRequestDto;
import Stockly_Ecommerce.API.DTO.OrderResponseDto;
import Stockly_Ecommerce.API.Exception.InvalidCheckoutException;
import Stockly_Ecommerce.API.Exception.InsufficientStockException;
import Stockly_Ecommerce.API.Model.DeliveryAddress;
import Stockly_Ecommerce.API.Model.FulfillmentType;
import Stockly_Ecommerce.API.Model.Order;
import Stockly_Ecommerce.API.Model.Product;
import Stockly_Ecommerce.API.Repository.OrderRepository;
import Stockly_Ecommerce.API.Repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderService {

    private static final String POSTAL_CODE_PATTERN = "\\d{5}-?\\d{3}";

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final String pickupLocation;

    public OrderService(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            @Value("${STORE_PICKUP_LOCATION:Endereço da loja a configurar}") String pickupLocation
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.pickupLocation = pickupLocation;
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {
        validate(request);
        UUID productId = request.getProductId();
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (product.getStock() < request.getQuantity()) {
            throw new InsufficientStockException("Estoque insuficiente para o produto: " + product.getName());
        }

        DeliveryAddress deliveryAddress = request.getFulfillmentType() == FulfillmentType.DELIVERY
                ? toModel(request.getDeliveryAddress())
                : null;
        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        Order order = Order.builder()
                .product(product)
                .quantity(request.getQuantity())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                .buyerName(request.getBuyerName().trim())
                .fulfillmentType(request.getFulfillmentType())
                .paymentMethod(request.getPaymentMethod())
                .deliveryAddress(deliveryAddress)
                .status("CONFIRMED")
                .build();

        orderRepository.save(order);

        return new OrderResponseDto(order, pickupLocation);
    }

    private void validate(OrderRequestDto request) {
        if (request == null
                || request.getProductId() == null
                || request.getQuantity() == null
                || request.getBuyerName() == null
                || request.getBuyerName().isBlank()
                || request.getFulfillmentType() == null
                || request.getPaymentMethod() == null) {
            throw new InvalidCheckoutException("Informe produto, quantidade, comprador, forma de pagamento e tipo de entrega.");
        }
        if (request.getQuantity() < 1) {
            throw new InvalidCheckoutException("A quantidade do pedido deve ser maior que zero.");
        }
        if (request.getBuyerName().trim().length() > 120) {
            throw new InvalidCheckoutException("O nome do comprador deve ter no máximo 120 caracteres.");
        }
        if (request.getFulfillmentType() == FulfillmentType.DELIVERY) {
            validateDeliveryAddress(request.getDeliveryAddress());
        }
    }

    private void validateDeliveryAddress(DeliveryAddressDto address) {
        if (address == null
                || isBlank(address.postalCode())
                || isBlank(address.street())
                || isBlank(address.number())
                || isBlank(address.neighborhood())
                || isBlank(address.city())
                || isBlank(address.state())) {
            throw new InvalidCheckoutException("Preencha CEP, rua, número, bairro, cidade e estado para entrega.");
        }
        if (!address.postalCode().trim().matches(POSTAL_CODE_PATTERN)) {
            throw new InvalidCheckoutException("Informe um CEP válido no formato 00000-000.");
        }
        if (!address.state().trim().matches("[a-zA-Z]{2}")) {
            throw new InvalidCheckoutException("Informe a sigla de estado com duas letras.");
        }
        if (address.street().trim().length() > 255
                || address.number().trim().length() > 30
                || (!isBlank(address.complement()) && address.complement().trim().length() > 120)
                || address.neighborhood().trim().length() > 120
                || address.city().trim().length() > 120) {
            throw new InvalidCheckoutException("Revise o tamanho dos campos do endereço de entrega.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private DeliveryAddress toModel(DeliveryAddressDto address) {
        return new DeliveryAddress(
                address.postalCode().trim(),
                address.street().trim(),
                address.number().trim(),
                isBlank(address.complement()) ? null : address.complement().trim(),
                address.neighborhood().trim(),
                address.city().trim(),
                address.state().trim().toUpperCase(java.util.Locale.ROOT)
        );
    }
}