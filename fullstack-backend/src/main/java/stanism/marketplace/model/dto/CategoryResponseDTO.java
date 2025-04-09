package stanism.marketplace.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) representing a category in the marketplace.
 * This class is used to transfer category information between the backend and
 * frontend.
 * 
 * @author Stanism Marketplace Team
 * @version 1.0
 */
@Schema(description = "Category information response")
public class CategoryResponseDTO {
    /**
     * The unique identifier of the category.
     */
    @Schema(description = "Category ID")
    private Long id;

    /**
     * The name of the category.
     */
    @Schema(description = "Category name")
    private String name;

    /**
     * Gets the unique identifier of the category.
     *
     * @return the category ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the category.
     *
     * @param id the category ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the category.
     *
     * @return the category name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the category.
     *
     * @param name the category name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}