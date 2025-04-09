package stanism.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stanism.marketplace.model.Favorite;
import stanism.marketplace.model.User;
import stanism.marketplace.model.Item;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndItem(User user, Item item);

    List<Favorite> findByUser(User user);
}