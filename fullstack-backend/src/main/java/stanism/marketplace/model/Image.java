package stanism.marketplace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

/**
 * Entity class representing an image associated with an item in the marketplace.
 */
@Entity
@Table(name = "images")
public class Image {
    /** Unique identifier for the image. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The item this image belongs to. */
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /** URL of the image. */
    private String imageUrl;

    /** Alternative text description of the image. */
    private String altText;

    public Image() {
    }

    public Image(Item item, String imageUrl, String altText) {
        this.item = item;
        this.imageUrl = imageUrl;
        this.altText = altText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
} 