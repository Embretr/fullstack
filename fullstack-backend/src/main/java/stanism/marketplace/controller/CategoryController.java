package stanism.marketplace.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import stanism.marketplace.model.Category;
import stanism.marketplace.model.Item;
import stanism.marketplace.service.CategoryService;
import stanism.marketplace.service.ItemService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3173")
@Tag(name = "Category Management", description = "Endpoints for managing item categories")
public class CategoryController {

    /** Service for handling category-related operations. */
    private final CategoryService categoryService;
    
    /** Service for handling item-related operations. */
    private final ItemService itemService;

    public CategoryController(CategoryService categoryService, ItemService itemService) {
        this.categoryService = categoryService;
        this.itemService = itemService;
    }

    @PostMapping
    @Operation(summary = "Create category", 
              description = "Creates a new category",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @GetMapping
    @Operation(summary = "Get all categories", 
              description = "Retrieves a list of all categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/{itemId}/categories")
    @Operation(summary = "Add category to item", 
              description = "Adds a category to an existing item",
              security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> addCategoriesToItem(@PathVariable Long itemId, @RequestBody Long categoryId) {
        Optional<Item> optionalItem = itemService.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }

        Optional<Category> optionalCategory = Optional.ofNullable(categoryService.getCategoryById(categoryId));
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }

        Item item = optionalItem.get();
        Category category = optionalCategory.get();
        item.setCategory(category);
        itemService.saveItem(item);

        return ResponseEntity.ok(item);
    }
} 