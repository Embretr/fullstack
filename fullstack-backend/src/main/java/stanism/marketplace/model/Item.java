package stanism.marketplace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entity class representing an item in the marketplace.
 * This class models a product or service that can be listed, bought, and sold in the marketplace.
 * Each item has a title, description, price, location, and is associated with a user and category.
 *
 * @author Stanism Marketplace.
 * @version 1.0.
 */
@Entity
@Table(name = "items")
public class Item {
    /** Unique identifier for the item. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Title of the item. */
    private String title;

    /** Brief description of the item. */
    private String briefDescription;

    /** Full detailed description of the item. */
    private String fullDescription;

    /** Price of the item. */
    private Double price;

    /** Latitude coordinate of the item's location. */
    private Double latitude;

    /** Longitude coordinate of the item's location. */
    private Double longitude;

    /** Date and time when the item was published. */
    private LocalDateTime publishDate;

    /** Current status of the item (e.g., ACTIVE, SOLD). */
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    /** User who owns or listed the item. */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Category to which the item belongs. */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /** Set of images associated with the item. */
    @OneToMany(mappedBy = "item")
    private Set<Image> images;

    /** Set of users who have favorited this item. */
    @OneToMany(mappedBy = "item")
    private Set<Favorite> favorites;

    /** Set of messages related to this item. */
    @OneToMany(mappedBy = "item")
    private Set<Message> messages;

    /** Set of orders associated with this item. */
    @OneToMany(mappedBy = "item")
    private Set<Order> orders;

    /**
     * Default constructor for JPA.
     */
    public Item() {
    }

    /**
     * Private constructor for Builder pattern.
     *
     * @param builder the builder instance containing the item properties
     */
    private Item(Builder builder) {
        this.title = builder.title;
        this.briefDescription = builder.briefDescription;
        this.fullDescription = builder.fullDescription;
        this.price = builder.price;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.user = builder.user;
        this.category = builder.category;
        this.publishDate = LocalDateTime.now();
        this.status = ItemStatus.ACTIVE;
    }

    /**
     * Builder class for Item.
     */
    public static class Builder {
        /** The title of the item. */
        private String title;

        /** The brief description of the item. */
        private String briefDescription;

        /** The full description of the item. */
        private String fullDescription;

        /** The price of the item. */
        private Double price;

        /** The latitude coordinate of the item's location. */
        private Double latitude;

        /** The longitude coordinate of the item's location. */
        private Double longitude;

        /** The user who owns or listed the item. */
        private User user;

        /** The category to which the item belongs. */
        private Category category;

        /**
         * Sets the title of the item being built.
         *
         * @param title the title of the item
         * @return this builder instance for method chaining
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the brief description of the item being built.
         *
         * @param briefDescription the brief description of the item
         * @return this builder instance for method chaining
         */
        public Builder briefDescription(String briefDescription) {
            this.briefDescription = briefDescription;
            return this;
        }

        /**
         * Sets the full description of the item being built.
         *
         * @param fullDescription the full description of the item
         * @return this builder instance for method chaining
         */
        public Builder fullDescription(String fullDescription) {
            this.fullDescription = fullDescription;
            return this;
        }

        /**
         * Sets the price of the item being built.
         *
         * @param price the price of the item
         * @return this builder instance for method chaining
         */
        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        /**
         * Sets the latitude coordinate of the item's location.
         *
         * @param latitude the latitude coordinate
         * @return this builder instance for method chaining
         */
        public Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        /**
         * Sets the longitude coordinate of the item's location.
         *
         * @param longitude the longitude coordinate
         * @return this builder instance for method chaining
         */
        public Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        /**
         * Sets the user who owns or listed the item.
         *
         * @param user the user who owns the item
         * @return this builder instance for method chaining
         */
        public Builder user(User user) {
            this.user = user;
            return this;
        }

        /**
         * Sets the category to which the item belongs.
         *
         * @param category the category of the item
         * @return this builder instance for method chaining
         */
        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        /**
         * Builds and returns a new Item instance with the configured properties.
         *
         * @return a new Item instance with the configured properties.
         */
        public Item build() {
            return new Item(this);
        }
    }

    /**
     * Returns the unique identifier of this item.
     *
     * @return the item's ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this item.
     *
     * @param id the new ID for the item.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the title of this item.
     *
     * @return the item's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this item.
     *
     * @param title the new title for the item.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the brief description of this item.
     *
     * @return the item's brief description.
     */
    public String getBriefDescription() {
        return briefDescription;
    }

    /**
     * Sets the brief description of this item.
     *
     * @param briefDescription the new brief description for the item.
     */
    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    /**
     * Returns the full description of this item.
     *
     * @return the item's full description.
     */
    public String getFullDescription() {
        return fullDescription;
    }

    /**
     * Sets the full description of this item.
     *
     * @param fullDescription the new full description for the item.
     */
    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    /**
     * Returns the price of this item.
     *
     * @return the item's price.
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the price of this item.
     *
     * @param price the new price for the item.
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Returns the latitude coordinate of this item's location.
     *
     * @return the item's latitude.
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude coordinate of this item's location.
     *
     * @param latitude the new latitude for the item.
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the longitude coordinate of this item's location.
     *
     * @return the item's longitude.
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude coordinate of this item's location.
     *
     * @param longitude the new longitude for the item.
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the date and time when this item was published.
     *
     * @return the item's publish date.
     */
    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    /**
     * Sets the date and time when this item was published.
     *
     * @param publishDate the new publish date for the item.
     */
    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * Returns the current status of this item.
     *
     * @return the item's status.
     */
    public ItemStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of this item.
     *
     * @param status the new status for the item.
     */
    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    /**
     * Returns the user who owns or listed this item.
     *
     * @return the item's owner.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who owns or listed this item.
     *
     * @param user the new owner for the item.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the category to which this item belongs.
     *
     * @return the item's category.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the category to which this item belongs.
     *
     * @param category the new category for the item.
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Returns the set of images associated with this item.
     *
     * @return the item's images.
     */
    public Set<Image> getImages() {
        return images;
    }

    /**
     * Sets the set of images associated with this item.
     *
     * @param images the new set of images for the item.
     */
    public void setImages(Set<Image> images) {
        this.images = images;
    }

    /**
     * Returns the set of users who have favorited this item.
     *
     * @return the item's favorites.
     */
    public Set<Favorite> getFavorites() {
        return favorites;
    }

    /**
     * Sets the set of users who have favorited this item.
     *
     * @param favorites the new set of favorites for the item.
     */
    public void setFavorites(Set<Favorite> favorites) {
        this.favorites = favorites;
    }

    /**
     * Returns the set of messages related to this item.
     *
     * @return the item's messages.
     */
    public Set<Message> getMessages() {
        return messages;
    }

    /**
     * Sets the set of messages related to this item.
     *
     * @param messages the new set of messages for the item.
     */
    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    /**
     * Returns the set of orders associated with this item.
     *
     * @return the item's orders.
     */
    public Set<Order> getOrders() {
        return orders;
    }

    /**
     * Sets the set of orders associated with this item.
     *
     * @param orders the new set of orders for the item.
     */
    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }
}