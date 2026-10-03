package Stockly_Ecommerce.API.Controller;

import Stockly_Ecommerce.API.Model.Category;
import Stockly_Ecommerce.API.Repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/categories", "/categories"})
@Tag(name = "Categorias", description = "Categorias disponíveis para cadastro de produtos.")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    @Operation(summary = "Listar categorias", description = "Retorna categorias que possuem produtos cadastrados.")
    public List<Category> listInUse() {
        return categoryRepository.findCategoriesWithProducts();
    }
}
