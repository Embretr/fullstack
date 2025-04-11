package stanism.marketplace.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a message response in the marketplace system.
 * This class encapsulates all the necessary information about a message including
 * sender, receiver, related item, content, and timestamp.
 */
@Schema(description = "Message information response")
public class MessageResponseDTO {
    /**
     * Unique identifier for the message.
     */
    @Schema(description = "Message ID")
    private Long id;

    /**
     * User who sent the message.
     */
    @Schema(description = "Sender information")
    private UserResponse sender;

    /**
     * User who received the message.
     */
    @Schema(description = "Receiver information")
    private UserResponse receiver;

    /**
     * Item related to the message conversation.
     */
    @Schema(description = "Related item information")
    private ItemResponseDTO item;

    /**
     * The actual content of the message.
     */
    @Schema(description = "Message content")
    private String content;

    /**
     * Date and time when the message was sent.
     */
    @Schema(description = "Message timestamp")
    private LocalDateTime timestamp;

    /**
     * Gets the unique identifier of the message.
     *
     * @return the message ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the message.
     *
     * @param id
     *            the message ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the sender of the message.
     *
     * @return the sender information
     */
    public UserResponse getSender() {
        return sender;
    }

    /**
     * Sets the sender of the message.
     *
     * @param sender
     *            the sender information to set
     */
    public void setSender(UserResponse sender) {
        this.sender = sender;
    }

    /**
     * Gets the receiver of the message.
     *
     * @return the receiver information
     */
    public UserResponse getReceiver() {
        return receiver;
    }

    /**
     * Sets the receiver of the message.
     *
     * @param receiver
     *            the receiver information to set
     */
    public void setReceiver(UserResponse receiver) {
        this.receiver = receiver;
    }

    /**
     * Gets the item related to the message conversation.
     *
     * @return the related item information
     */
    public ItemResponseDTO getItem() {
        return item;
    }

    /**
     * Sets the item related to the message conversation.
     *
     * @param item
     *            the item information to set
     */
    public void setItem(ItemResponseDTO item) {
        this.item = item;
    }

    /**
     * Gets the content of the message.
     *
     * @return the message content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the message.
     *
     * @param content
     *            the message content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the timestamp when the message was sent.
     *
     * @return the message timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when the message was sent.
     *
     * @param timestamp
     *            the message timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}