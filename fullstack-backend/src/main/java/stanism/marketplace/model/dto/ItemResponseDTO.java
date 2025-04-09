package stanism.marketplace.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import stanism.marketplace.model.ItemStatus;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object (DTO) representing an item in the marketplace.
 * This class contains all the necessary information about an item that needs to
 * be
 * transferred between the backend and frontend.
 *
 * @author Stanism Marketplace Team
 * @version 1.0
 */
@Schema(description = "Item information response")
public class ItemResponseDTO {
    /**
     * The unique identifier of the item.
     */
    @Schema(description = "Item ID")
    private Long id;

    /**
     * The title of the item.
     */
    @Schema(description = "Item title")
    private String title;

    /**
     * A brief description of the item.
     */
    @Schema(description = "Brief description")
    private String briefDescription;

    /**
     * A detailed description of the item.
     */
    @Schema(description = "Full description")
    private String fullDescription;

    /**
     * The price of the item.
     */
    @Schema(description = "Item price")
    private Double price;

    /**
     * The latitude coordinate of the item's location.
     */
    @Schema(description = "Latitude coordinate")
    private Double latitude;

    /**
     * The longitude coordinate of the item's location.
     */
    @Schema(description = "Longitude coordinate")
    private Double longitude;

    /**
     * The date and time when the item was published.
     */
    @Schema(description = "Publish date")
    private LocalDateTime publishDate;

    /**
     * The current status of the item.
     */
    @Schema(description = "Item status")
    private ItemStatus status;

    /**
     * The user who owns the item.
     */
    @Schema(description = "User who owns the item")
    private UserResponse owner;

    /**
     * The category to which the item belongs.
     */
    @Schema(description = "Category of the item")
    private CategoryResponseDTO category;

    /**
     * Set of URLs pointing to the item's images.
     */
    @Schema(description = "Image URLs")
    private Set<String> imageUrls;

    /**
     * Gets the unique identifier of the item.
     *
     * @return the item ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the item.
     *
     * @param id the item ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the title of the item.
     *
     * @return the item title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the item.
     *
     * @param title the item title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the brief description of the item.
     *
     * @return the brief description
     */
    public String getBriefDescription() {
        return briefDescription;
    }

    /**
     * Sets the brief description of the item.
     *
     * @param briefDescription the brief description to set
     */
    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    /**
     * Gets the full description of the item.
     *
     * @return the full description
     */
    public String getFullDescription() {
        return fullDescription;
    }

    /**
     * Sets the full description of the item.
     *
     * @param fullDescription the full description to set
     */
    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    /**
     * Gets the price of the item.
     *
     * @return the item price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the price of the item.
     *
     * @param price the item price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Gets the latitude coordinate of the item's location.
     *
     * @return the latitude coordinate
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude coordinate of the item's location.
     *
     * @param latitude the latitude coordinate to set
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude coordinate of the item's location.
     *
     * @return the longitude coordinate
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude coordinate of the item's location.
     *
     * @param longitude the longitude coordinate to set
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the publish date of the item.
     *
     * @return the publish date
     */
    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    /**
     * Sets the publish date of the item.
     *
     * @param publishDate the publish date to set
     */
    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * Gets the current status of the item.
     *
     * @return the item status
     */
    public ItemStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the item.
     *
     * @param status the item status to set
     */
    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    /**
     * Gets the owner of the item.
     *
     * @return the item owner
     */
    public UserResponse getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the item.
     *
     * @param owner the item owner to set
     */
    public void setOwner(UserResponse owner) {
        this.owner = owner;
    }

    /**
     * Gets the category of the item.
     *
     * @return the item category
     */
    public CategoryResponseDTO getCategory() {
        return category;
    }

    /**
     * Sets the category of the item.
     *
     * @param category the item category to set
     */
    public void setCategory(CategoryResponseDTO category) {
        this.category = category;
    }

    /**
     * Gets the set of image URLs for the item.
     *
     * @return the set of image URLs
     */
    public Set<String> getImageUrls() {
        return imageUrls;
    }

    /**
     * Sets the set of image URLs for the item.
     *
     * @param imageUrls the set of image URLs to set
     */
    public void setImageUrls(Set<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}