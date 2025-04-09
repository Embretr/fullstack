package stanism.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stanism.marketplace.model.Order;
import stanism.marketplace.model.User;
import stanism.marketplace.model.Item;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByBuyerAndItem(User buyer, Item item);
} 