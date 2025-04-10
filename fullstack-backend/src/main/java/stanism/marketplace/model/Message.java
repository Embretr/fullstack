package stanism.marketplace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

/**
 * Entity class representing a message between users in the marketplace.
 */
@Entity
@Table(name = "messages")
public class Message {
    /** Unique identifier for the message. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who sent the message. */
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonIgnoreProperties({ "sentMessages", "receivedMessages", "items", "favorites", "orders" })
    private User sender;

    /** The user who received the message. */
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    @JsonIgnoreProperties({ "sentMessages", "receivedMessages", "items", "favorites", "orders" })
    private User receiver;

    /** The item this message is related to. */
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnoreProperties({ "messages", "favorites", "orders" })
    private Item item;

    /** The content of the message. */
    private String content;

    /** The date and time when the message was sent. */
    private LocalDateTime timestamp;

    public Message() {
    }

    public Message(User sender, User receiver, Item item, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.item = item;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}