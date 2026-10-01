package Stockly_Ecommerce.API.Repository;

import Stockly_Ecommerce.API.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    boolean existsByProduct_Id(UUID productId);
}
