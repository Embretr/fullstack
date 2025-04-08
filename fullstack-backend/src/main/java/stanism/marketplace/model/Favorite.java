package stanism.marketplace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;

/**
 * Entity class representing a user's favorite item in the marketplace.
 */
@Entity
@Table(name = "favorites")
public class Favorite {
    /** Unique identifier for the favorite. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who favorited the item. */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The item that was favorited. */
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /** The date and time when the item was favorited. */
    private LocalDateTime dateAdded;

    public Favorite() {
    }

    public Favorite(User user, Item item) {
        this.user = user;
        this.item = item;
        this.dateAdded = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }
} 