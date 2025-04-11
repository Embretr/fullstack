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
import stanism.marketplace.model.ItemStatus;
import stanism.marketplace.model.User;
import stanism.marketplace.model.dto.CreateItemRequestDTO;
import stanism.marketplace.model.dto.ItemResponseDTO;
import stanism.marketplace.model.dto.ItemMapper;
import stanism.marketplace.security.JwtUtil;
import stanism.marketplace.service.CategoryService;
import stanism.marketplace.service.ItemService;
import stanism.marketplace.service.UserService;
import stanism.marketplace.service.FavoriteService;
import stanism.marketplace.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:3173")
@Tag(name = "Item Management", description = "Endpoints for managing marketplace items")
public class ItemController {
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    /** Service for handling item-related operations. */
    private final ItemService itemService;

    /** Service for handling user-related operations. */
    private final UserService userService;

    /** Service for handling category-related operations. */
    private final CategoryService categoryService;

    /** Service for handling image-related operations. */
    private final ImageService imageService;

    /** Utility for JWT token operations. */
    private final JwtUtil jwtUtil;

    /** Service for handling favorite-related operations. */
    private final FavoriteService favoriteService;

    public ItemController(ItemService itemService, UserService userService,
            CategoryService categoryService, JwtUtil jwtUtil,
            FavoriteService favoriteService, ImageService imageService) {
        this.itemService = itemService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.jwtUtil = jwtUtil;
        this.favoriteService = favoriteService;
        this.imageService = imageService;
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
            @RequestParam("images") MultipartFile[] imageFiles,
            @RequestParam("itemData") String itemDataJson) {
        LOGGER.info("Received create item request with {} images", imageFiles.length);
        LOGGER.debug("Item data JSON: {}", itemDataJson);

        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            LOGGER.warn("Create item request failed: User not logged in");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            return processItemCreation(imageFiles, itemDataJson, currentUser.get());
        } catch (Exception e) {
            LOGGER.error("Error creating item: {}", e.getMessage(), e);
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
        LOGGER.info("Processing item creation for user: {}", currentUser.getEmail());

        CreateItemRequestDTO itemData = parseItemData(itemDataJson);
        Optional<Category> category = validateCategory(itemData.getCategoryId());
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Category not found");
        }

        validateImages(imageFiles);

        Item item = createItem(itemData, currentUser, category.get());
        Item createdItem = saveItemWithImages(item, imageFiles);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    private CreateItemRequestDTO parseItemData(String itemDataJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CreateItemRequestDTO itemData = objectMapper.readValue(itemDataJson, CreateItemRequestDTO.class);
        LOGGER.debug("Parsed item data: {}", itemData);
        return itemData;
    }

    private Optional<Category> validateCategory(Long categoryId) {
        Optional<Category> category = Optional.ofNullable(
                categoryService.getCategoryById(categoryId));
        if (category.isEmpty()) {
            LOGGER.warn("Category not found for ID: {}", categoryId);
        }
        return category;
    }

    private void validateImages(MultipartFile[] imageFiles) {
        for (MultipartFile imageFile : imageFiles) {
            LOGGER.debug("Validating image: {} ({} bytes)",
                    imageFile.getOriginalFilename(),
                    imageFile.getSize());

            if (imageFile.isEmpty()) {
                LOGGER.warn("Empty image file received");
                throw new IllegalArgumentException("One or more image files are empty");
            }
            if (!imageFile.getContentType().startsWith("image/")) {
                LOGGER.warn("Invalid file type received: {}", imageFile.getContentType());
                throw new IllegalArgumentException("One or more files are not images");
            }
        }
    }

    private Item createItem(CreateItemRequestDTO itemData, User currentUser, Category category) {
        Item item = new Item.Builder()
                .title(itemData.getTitle())
                .briefDescription(itemData.getBriefDescription())
                .fullDescription(itemData.getFullDescription())
                .price(itemData.getPrice())
                .latitude(itemData.getLatitude())
                .longitude(itemData.getLongitude())
                .user(currentUser)
                .category(category)
                .build();

        LOGGER.info("Created item object: {}", item);
        return item;
    }

    private Item saveItemWithImages(Item item, MultipartFile[] imageFiles) throws IOException {
        Item createdItem = itemService.saveItem(item);
        LOGGER.info("Saved item with ID: {}", createdItem.getId());

        for (MultipartFile imageFile : imageFiles) {
            try {
                LOGGER.debug("Saving image: {}", imageFile.getOriginalFilename());
                Image image = saveImage(imageFile, createdItem);
                image = imageService.saveImage(image);
                createdItem.getImages().add(image);
                LOGGER.info("Successfully saved image: {}", image.getImageUrl());
            } catch (IOException e) {
                LOGGER.error("Failed to save image: {}", e.getMessage(), e);
                itemService.deleteItem(createdItem.getId());
                throw e;
            }
        }

        createdItem = itemService.saveItem(createdItem);
        LOGGER.info("Successfully created item with {} images", createdItem.getImages().size());
        return createdItem;
    }

    private Image saveImage(MultipartFile imageFile, Item item) throws IOException {
        LOGGER.debug("Saving image file: {}", imageFile.getOriginalFilename());

        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        LOGGER.debug("Generated filename: {}", fileName);

        Path filePath = Paths.get("uploads", fileName);
        LOGGER.debug("Saving to path: {}", filePath);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, imageFile.getBytes());
        LOGGER.info("Successfully saved image file to: {}", filePath);

        String imageUrl = "/uploads/" + fileName;
        LOGGER.debug("Created image URL: {}", imageUrl);

        return new Image(item, imageUrl, originalFilename);
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

    @PostMapping("/{itemId}/reserve")
    @Operation(summary = "Reserve item", description = "Reserves an item for one hour")
    public ResponseEntity<?> reserveItem(
            @PathVariable Long itemId) {

        Optional<Item> optionalItem = itemService.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }

        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        Item item = optionalItem.get();
        User user = currentUser.get();

        // Check if item is already reserved
        if (item.getStatus() == ItemStatus.RESERVED) {
            // Check if reservation has expired (1 hour)
            if (item.getReservationDate() != null &&
                    item.getReservationDate().plusHours(1).isAfter(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Item is already reserved");
            }
        }

        // Reserve the item
        item.setStatus(ItemStatus.RESERVED);
        item.setReservationDate(LocalDateTime.now());
        item.setReservedBy(user);
        itemService.saveItem(item);

        return ResponseEntity.ok(ItemMapper.toDTO(item));
    }

    @DeleteMapping("/{itemId}/reserve")
    @Operation(summary = "Cancel reservation", description = "Cancels the reservation of an item")
    public ResponseEntity<?> cancelReservation(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long itemId) {

        if (!validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Optional<Item> optionalItem = itemService.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        }

        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        Item item = optionalItem.get();
        User user = currentUser.get();

        // Check if user is the one who reserved the item
        if (item.getReservedBy() == null || !item.getReservedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the reserver of this item");
        }

        // Cancel the reservation
        item.setStatus(ItemStatus.ACTIVE);
        item.setReservationDate(null);
        item.setReservedBy(null);
        itemService.saveItem(item);

        return ResponseEntity.ok(ItemMapper.toDTO(item));
    }
}