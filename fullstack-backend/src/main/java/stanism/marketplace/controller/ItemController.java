package stanism.marketplace.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import stanism.marketplace.model.Category;
import stanism.marketplace.model.Favorite;
import stanism.marketplace.model.Image;
import stanism.marketplace.model.Item;
import stanism.marketplace.model.User;
import stanism.marketplace.model.dto.CreateItemRequestDTO;
import stanism.marketplace.model.dto.ItemResponseDTO;
import stanism.marketplace.model.dto.ItemMapper;
import stanism.marketplace.security.JwtUtil;
import stanism.marketplace.service.CategoryService;
import stanism.marketplace.service.ItemService;
import stanism.marketplace.service.UserService;
import stanism.marketplace.service.FavoriteService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:3173")
@Tag(name = "Item Management", description = "Endpoints for managing marketplace items")
public class ItemController {

    /** Service for handling item-related operations. */
    private final ItemService itemService;

    /** Service for handling user-related operations. */
    private final UserService userService;

    /** Service for handling category-related operations. */
    private final CategoryService categoryService;

    /** Utility for JWT token operations. */
    private final JwtUtil jwtUtil;

    /** Service for handling favorite-related operations. */
    private final FavoriteService favoriteService;

    public ItemController(ItemService itemService, UserService userService,
            CategoryService categoryService, JwtUtil jwtUtil,
            FavoriteService favoriteService) {
        this.itemService = itemService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.jwtUtil = jwtUtil;
        this.favoriteService = favoriteService;
    }

    @GetMapping
    @Operation(summary = "Get all items", description = "Retrieves a list of all items in the marketplace")
    public ResponseEntity<List<ItemResponseDTO>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        List<ItemResponseDTO> itemDTOs = items.stream()
                .map(ItemMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Get item by ID", description = "Retrieves a single item by its ID")
    public ResponseEntity<?> getItemById(@PathVariable Long itemId) {
        Optional<Item> optionalItem = itemService.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }
        return ResponseEntity.ok(ItemMapper.toDTO(optionalItem.get()));
    }

    @GetMapping("/user")
    @Operation(summary = "Get user's items",
            description = "Retrieves all items belonging to the currently logged-in user")
    public ResponseEntity<?> getUserItems(
            @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token provided");
        }

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String email = jwtUtil.extractUsername(token);
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent() && currentUser.get().getEmail().equals(email)) {
            List<Item> userItems = itemService.getItemsByUser(currentUser.get());
            List<ItemResponseDTO> itemDTOs = userItems.stream()
                    .map(ItemMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(itemDTOs);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get items by category", description = "Retrieves all items belonging to a specific category")
    public ResponseEntity<List<ItemResponseDTO>> getItemsByCategory(@PathVariable Long categoryId) {
        Optional<Category> optionalCategory = Optional.ofNullable(categoryService.getCategoryById(categoryId));
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }

        Category category = optionalCategory.get();
        List<Item> items = itemService.getItemsByCategory(category);
        List<ItemResponseDTO> itemDTOs = items.stream()
                .map(ItemMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @PostMapping
    @Operation(summary = "Create new item", description = "Creates a new item with associated images")
    public ResponseEntity<?> createItem(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam("images") MultipartFile[] imageFiles,
            @RequestParam("itemData") String itemDataJson) {
        if (!validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String email = jwtUtil.extractUsername(token);
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty() || !currentUser.get().getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            return processItemCreation(imageFiles, itemDataJson, currentUser.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating item: " + e.getMessage());
        }
    }

    private boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        return jwtUtil.validateToken(token);
    }

    private ResponseEntity<?> processItemCreation(
            MultipartFile[] imageFiles,
            String itemDataJson,
            User currentUser) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CreateItemRequestDTO itemData = objectMapper.readValue(itemDataJson, CreateItemRequestDTO.class);

        Optional<Category> category = Optional.ofNullable(
                categoryService.getCategoryById(itemData.getCategoryId()));
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Category not found");
        }

        Item item = new Item.Builder()
                .title(itemData.getTitle())
                .briefDescription(itemData.getBriefDescription())
                .fullDescription(itemData.getFullDescription())
                .price(itemData.getPrice())
                .latitude(itemData.getLatitude())
                .longitude(itemData.getLongitude())
                .user(currentUser)
                .category(category.get())
                .build();

        // Save the item first to get its ID
        Item createdItem = itemService.saveItem(item);

        // Save and associate images with the item
        for (MultipartFile imageFile : imageFiles) {
            Image image = saveImage(imageFile, createdItem);
            createdItem.getImages().add(image);
        }

        // Update the item with the images
        createdItem = itemService.saveItem(createdItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    private Image saveImage(MultipartFile imageFile, Item item) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
        Path filePath = Paths.get("uploads", fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, imageFile.getBytes());

        String imageUrl = "/uploads/" + fileName;
        return new Image(item, imageUrl, imageFile.getOriginalFilename());
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Delete item", description = "Deletes an item by its ID")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        Optional<Item> optionalItem = itemService.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }

        itemService.deleteItem(itemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/images/{filename:.+}")
    @Operation(summary = "Get item image", description = "Retrieves an image file for an item")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get("uploads/" + filename);
            UrlResource image = new UrlResource(imagePath.toUri());
            if (image.exists() && image.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(image);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{itemId}/favorite")
    @Operation(summary = "Add item to favorites", description = "Adds an item to the user's favorites")
    public ResponseEntity<?> addToFavorites(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long itemId) {

        Optional<Item> optionalItem = itemService.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }

        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        Favorite favorite = favoriteService.addFavorite(currentUser.get(), optionalItem.get());
        return ResponseEntity.ok(favorite);
    }

    @DeleteMapping("/{itemId}/favorite")
    @Operation(summary = "Remove item from favorites", description = "Removes an item from the user's favorites")
    public ResponseEntity<?> removeFromFavorites(
            @PathVariable Long itemId) {

        Optional<Item> optionalItem = itemService.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }

        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        favoriteService.removeFavorite(currentUser.get(), optionalItem.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    @Operation(summary = "Get user's favorites", description = "Retrieves all items favorited by the current user")
    public ResponseEntity<?> getUserFavorites() {

        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        List<Favorite> favorites = favoriteService.getUserFavorites(currentUser.get());
        List<ItemResponseDTO> itemDTOs = favorites.stream()
                .map(favorite -> ItemMapper.toDTO(favorite.getItem()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(itemDTOs);
    }

    @GetMapping("/{itemId}/is-favorite")
    @Operation(summary = "Check if item is favorited",
            description = "Checks if the current user has favorited the item")
    public ResponseEntity<?> isItemFavorited(
            @PathVariable Long itemId) {

        Optional<Item> optionalItem = itemService.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }

        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        boolean isFavorited = favoriteService.isItemFavorited(currentUser.get(), optionalItem.get());
        return ResponseEntity.ok(isFavorited);
    }
}