package stanism.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stanism.marketplace.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}