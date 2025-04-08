package stanism.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stanism.marketplace.model.Category;
import stanism.marketplace.model.Item;
import stanism.marketplace.model.User;
import stanism.marketplace.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing items in the marketplace.
 * Provides methods to retrieve, save, and delete items.
 */
@Service
public class ItemService {

    /** Repository for item data access. */
    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Saves an item to the repository.
     *
     * @param item the item to save
     * @return the saved item
     */
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    /**
     * Retrieves an item by its ID.
     *
     * @param id the ID of the item to retrieve
     * @return an Optional containing the item if found, or empty if not found
     */
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    /**
     * Retrieves all items from the repository.
     *
     * @return a list of all items
     */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Deletes an item by its ID.
     *
     * @param id the ID of the item to delete
     */
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    /**
     * Retrieves an item by its title.
     *
     * @param title the title of the item to retrieve
     * @return an Optional containing the item if found, or empty if not found
     */
    public Optional<Item> getItemByTitle(String title) {
        return itemRepository.findByTitle(title);
    }

    /**
     * Searches for items by a keyword in their descriptions.
     *
     * @param keyword the keyword to search for
     * @return a list of items containing the keyword in their brief or full description
     */
    public List<Item> searchItemsByDescription(String keyword) {
        return itemRepository.findByBriefDescriptionContainingOrFullDescriptionContaining(keyword, keyword);
    }

    /**
     * Retrieves items within a specified price range.
     *
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return a list of items within the specified price range
     */
    public List<Item> getItemsByPriceRange(Double minPrice, Double maxPrice) {
        return itemRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * Retrieves items associated with a specific category.
     *
     * @param category the category to filter by
     * @return a list of items in the specified category
     */
    public List<Item> getItemsByCategory(Category category) {
        return itemRepository.findByCategory(category);
    }

    /**
     * Retrieves items associated with a specific user.
     *
     * @param user the user to filter by
     * @return a list of items owned by the specified user
     */
    public List<Item> getItemsByUser(User user) {
        return itemRepository.findByUser(user);
    }
}