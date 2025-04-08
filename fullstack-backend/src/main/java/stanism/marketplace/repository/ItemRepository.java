package stanism.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stanism.marketplace.model.Category;
import stanism.marketplace.model.Item;
import stanism.marketplace.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByTitle(String title);
    List<Item> findByBriefDescriptionContainingOrFullDescriptionContaining(String keyword, String keyword2);
    List<Item> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Item> findByUser(User user);
    List<Item> findByCategory(Category category);
}