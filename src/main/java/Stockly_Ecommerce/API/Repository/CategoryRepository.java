package Stockly_Ecommerce.API.Repository;

import Stockly_Ecommerce.API.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByNameIgnoreCase(String name);

    @Query("select distinct category from Product product join product.category category order by category.name")
    List<Category> findCategoriesWithProducts();
}
