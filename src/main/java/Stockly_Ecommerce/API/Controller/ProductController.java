package Stockly_Ecommerce.API.Controller;

import Stockly_Ecommerce.API.DTO.ProductRequestDto;
import Stockly_Ecommerce.API.DTO.ProductResponseDto;
import Stockly_Ecommerce.API.Service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/products", "/products"})
@Tag(name = "Produtos", description = "Cadastro, consulta e exclusão de produtos.")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Lista os produtos e informa se cada um possui vendas.")
    public List<ProductResponseDto> listAll() {
        return productService.listAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar produto", description = "Busca um produto pelo seu UUID.")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar produto", description = "Cadastra um produto em uma categoria existente.")
    public ResponseEntity<ProductResponseDto> create(@RequestBody ProductRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir produto",
            description = "Exclui somente se o estoque for zero e não houver vendas registradas."
    )
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
