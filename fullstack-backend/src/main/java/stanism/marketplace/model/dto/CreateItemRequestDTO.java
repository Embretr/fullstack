package stanism.marketplace.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for creating a new item")
public class CreateItemRequestDTO {
    /** Item title. */
    @Schema(description = "Item title", required = true)
    private String title;

    /** Brief description of the item. */
    @Schema(description = "Brief description of the item", required = true)
    private String briefDescription;

    /** Full description of the item. */
    @Schema(description = "Full description of the item", required = true)
    private String fullDescription;

    /** Item price. */
    @Schema(description = "Item price", required = true)
    private Double price;

    /** Category ID. */
    @Schema(description = "Category ID", required = true)
    private Long categoryId;

    /** Latitude coordinate. */
    @Schema(description = "Latitude coordinate", required = true)
    private Double latitude;

    /** Longitude coordinate. */
    @Schema(description = "Longitude coordinate", required = true)
    private Double longitude;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
} 