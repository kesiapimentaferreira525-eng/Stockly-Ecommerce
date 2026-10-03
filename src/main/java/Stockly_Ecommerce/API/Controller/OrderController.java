package Stockly_Ecommerce.API.Controller;

import Stockly_Ecommerce.API.DTO.OrderRequestDto;
import Stockly_Ecommerce.API.DTO.OrderResponseDto;
import Stockly_Ecommerce.API.Service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/orders", "/orders"})
@Tag(name = "Pedidos", description = "Checkout, confirmação de pedido e baixa de estoque.")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(
            summary = "Finalizar compra",
            description = "Registra comprador, quantidade, pagamento e entrega ou retirada. Não processa cobrança externa."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compra confirmada."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou estoque insuficiente."),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado.")
    })
    public ResponseEntity<OrderResponseDto> create(@RequestBody OrderRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }
}
