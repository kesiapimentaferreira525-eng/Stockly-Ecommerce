package Stockly_Ecommerce.API.Service;

import Stockly_Ecommerce.API.DTO.ProductRequestDto;
import Stockly_Ecommerce.API.DTO.ProductResponseDto;
import Stockly_Ecommerce.API.Exception.InvalidProductException;
import Stockly_Ecommerce.API.Exception.ProductConflictException;
import Stockly_Ecommerce.API.Model.Category;
import Stockly_Ecommerce.API.Model.Product;
import Stockly_Ecommerce.API.Repository.CategoryRepository;
import Stockly_Ecommerce.API.Repository.OrderRepository;
import Stockly_Ecommerce.API.Repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            OrderRepository orderRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public List<ProductResponseDto> listAll() {
        return productRepository.findAll().stream()
                .map(product -> new ProductResponseDto(
                        product,
                        orderRepository.existsByProduct_Id(product.getId())
                ))
                .toList();
    }

    @Transactional
    public ProductResponseDto findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        return new ProductResponseDto(product, orderRepository.existsByProduct_Id(id));
    }

    @Transactional
    public ProductResponseDto create(ProductRequestDto request) {
        validate(request);
        String sku = request.sku().trim().toUpperCase(Locale.ROOT);
        if (productRepository.findBySkuIgnoreCase(sku).isPresent()) {
            throw new ProductConflictException("Já existe um produto cadastrado com este SKU.");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        Product product = new Product();
        product.setSku(sku);
        product.setName(request.name().trim());
        product.setDescription(request.description().trim());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);

        return new ProductResponseDto(productRepository.save(product), false);
    }

    @Transactional
    public void delete(UUID id) {
        Product product = productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        if (product.getStock() != 0) {
            throw new ProductConflictException("O produto precisa estar sem estoque para ser excluído.");
        }
        if (orderRepository.existsByProduct_Id(id)) {
            throw new ProductConflictException("O produto não pode ser excluído porque possui vendas registradas.");
        }

        productRepository.delete(product);
    }

    private void validate(ProductRequestDto request) {
        if (request == null
                || request.sku() == null
                || request.sku().isBlank()
                || request.name() == null
                || request.name().isBlank()
                || request.description() == null
                || request.description().isBlank()
                || request.price() == null
                || request.stock() == null
                || request.categoryId() == null) {
            throw new InvalidProductException("Preencha todos os campos obrigatórios do produto.");
        }
        if (request.sku().trim().length() > 40) {
            throw new InvalidProductException("O SKU deve ter no máximo 40 caracteres.");
        }
        if (request.name().trim().length() > 255) {
            throw new InvalidProductException("O nome deve ter no máximo 255 caracteres.");
        }
        if (request.description().trim().length() > 1000) {
            throw new InvalidProductException("A descrição deve ter no máximo 1000 caracteres.");
        }
        if (request.price().signum() <= 0) {
            throw new InvalidProductException("O preço deve ser maior que zero.");
        }
        if (request.stock() < 0) {
            throw new InvalidProductException("O estoque não pode ser negativo.");
        }
    }
}
