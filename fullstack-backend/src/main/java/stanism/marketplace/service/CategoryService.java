package stanism.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stanism.marketplace.model.Category;
import stanism.marketplace.repository.CategoryRepository;

import java.util.List;

/**
 * Service class for managing categories in the marketplace.
 * Provides methods to retrieve and save categories.
 */
@Service
public class CategoryService {
    /** Repository for category data access. */
    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Retrieves all categories from the repository.
     *
     * @return a list of all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category to retrieve
     * @return the category with the specified ID, or null if not found
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    /**
     * Saves a category to the repository.
     *
     * @param category the category to save
     * @return the saved category
     */
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
}