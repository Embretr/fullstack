package stanism.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stanism.marketplace.model.Favorite;
import stanism.marketplace.model.Item;
import stanism.marketplace.model.User;
import stanism.marketplace.repository.FavoriteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {
    /** Repository for managing favorite relationships between users and items. */
    private final FavoriteRepository favoriteRepository;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public Favorite addFavorite(User user, Item item) {
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserAndItem(user, item);
        if (existingFavorite.isPresent()) {
            return existingFavorite.get();
        }

        Favorite favorite = new Favorite(user, item);
        return favoriteRepository.save(favorite);
    }

    public void removeFavorite(User user, Item item) {
        Optional<Favorite> favorite = favoriteRepository.findByUserAndItem(user, item);
        favorite.ifPresent(favoriteRepository::delete);
    }

    public List<Favorite> getUserFavorites(User user) {
        return favoriteRepository.findByUser(user);
    }

    public boolean isItemFavorited(User user, Item item) {
        return favoriteRepository.findByUserAndItem(user, item).isPresent();
    }
}