package Stockly_Ecommerce.API;

import Stockly_Ecommerce.API.DTO.OrderRequestDto;
import Stockly_Ecommerce.API.DTO.DeliveryAddressDto;
import Stockly_Ecommerce.API.DTO.ProductRequestDto;
import Stockly_Ecommerce.API.Exception.ProductConflictException;
import Stockly_Ecommerce.API.Exception.InvalidCheckoutException;
import Stockly_Ecommerce.API.Model.FulfillmentType;
import Stockly_Ecommerce.API.Model.PaymentMethod;
import Stockly_Ecommerce.API.Repository.CategoryRepository;
import Stockly_Ecommerce.API.Repository.OrderRepository;
import Stockly_Ecommerce.API.Repository.ProductRepository;
import Stockly_Ecommerce.API.Service.OrderService;
import Stockly_Ecommerce.API.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ApiApplicationTests {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ProductService productService;

	@Test
	void contextLoads() {
	}

	@Test
	void seedsCatalogForFrontend() {
		var product = productRepository.findBySkuIgnoreCase("AUR-CAF-001").orElseThrow();
		Set<String> categoryNames = productRepository.findAll().stream()
				.map(item -> item.getCategory().getName())
				.collect(Collectors.toSet());

		assertEquals("Cafeteira italiana 6 xícaras", product.getName());
		assertNotNull(product.getDescription());
		assertTrue(productRepository.count() >= 18);
		assertEquals(Set.of("Acessórios", "Cafés Especiais", "Cápsulas & Kits", "Métodos"), categoryNames);
	}

	@Test
	void createsOrderWithFrontendResponseFields() {
		var product = productRepository.findBySkuIgnoreCase("AUR-CAF-001").orElseThrow();

		var deliveryAddress = new DeliveryAddressDto(
				"01001-000",
				"Praça da Sé",
				"100",
				"Apto 12",
				"Sé",
				"São Paulo",
				"SP"
		);
		var response = orderService.createOrder(new OrderRequestDto(
				product.getId(),
				2,
				"Maria da Silva",
				FulfillmentType.DELIVERY,
				PaymentMethod.PIX,
				deliveryAddress
		));

		assertNotNull(response.getOrderId());
		assertEquals(product.getId(), response.getProductId());
		assertEquals("Maria da Silva", response.getBuyerName());
		assertEquals(2, response.getQuantity());
		assertEquals(new BigDecimal("319.80"), response.getTotal());
		assertEquals(10, response.getRemainingStock());
		assertEquals(FulfillmentType.DELIVERY, response.getFulfillmentType());
		assertEquals(PaymentMethod.PIX, response.getPaymentMethod());
		assertEquals("01001-000", response.getDeliveryAddress().postalCode());
		assertEquals("São Paulo", response.getDeliveryAddress().city());
		assertEquals("CONFIRMED", response.getStatus());
		assertEquals("Compra finalizada e pedido confirmado.", response.getMessage());
		var savedOrder = orderRepository.findById(response.getOrderId()).orElseThrow();
		assertEquals("Maria da Silva", savedOrder.getBuyerName());
		assertEquals("Sé", savedOrder.getDeliveryAddress().getNeighborhood());
	}

	@Test
	void supportsStorePickupWithoutDeliveryAddress() {
		var product = productRepository.findBySkuIgnoreCase("AUR-CAF-001").orElseThrow();
		var response = orderService.createOrder(new OrderRequestDto(
				product.getId(),
				1,
				"João da Silva",
				FulfillmentType.STORE_PICKUP,
				PaymentMethod.CASH,
				null
		));

		assertEquals(FulfillmentType.STORE_PICKUP, response.getFulfillmentType());
		assertEquals(PaymentMethod.CASH, response.getPaymentMethod());
		assertNull(response.getDeliveryAddress());
		assertNotNull(response.getPickupLocation());
	}

	@Test
	void requiresDeliveryAddressForDeliveryOrders() {
		var product = productRepository.findBySkuIgnoreCase("AUR-CAF-001").orElseThrow();
		var request = new OrderRequestDto(
				product.getId(),
				1,
				"João da Silva",
				FulfillmentType.DELIVERY,
				PaymentMethod.PIX,
				null
		);

		assertThrows(InvalidCheckoutException.class, () -> orderService.createOrder(request));
	}

	@Test
	void createsAndDeletesProductWithoutStockOrSales() {
		var category = categoryRepository.findByNameIgnoreCase("Métodos").orElseThrow();
		var created = productService.create(new ProductRequestDto(
				"TEST-UNSOLD-001",
				"Produto temporário",
				"Produto criado pelo teste.",
				new BigDecimal("25.00"),
				0,
				category.getId()
		));

		assertFalse(created.hasSales());
		productService.delete(created.id());

		assertFalse(productRepository.existsById(created.id()));
	}

	@Test
	void refusesDeletingProductsWithStockOrSales() {
		var category = categoryRepository.findByNameIgnoreCase("Métodos").orElseThrow();
		var created = productService.create(new ProductRequestDto(
				"TEST-SOLD-001",
				"Produto com venda",
				"Produto temporário para validar as regras de exclusão.",
				new BigDecimal("25.00"),
				1,
				category.getId()
		));

		assertThrows(ProductConflictException.class, () -> productService.delete(created.id()));
		orderService.createOrder(new OrderRequestDto(
				created.id(),
				1,
				"João da Silva",
				FulfillmentType.STORE_PICKUP,
				PaymentMethod.CASH,
				null
		));
		assertThrows(ProductConflictException.class, () -> productService.delete(created.id()));
	}

}
