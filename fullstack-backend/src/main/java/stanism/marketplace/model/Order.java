package stanism.marketplace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.time.LocalDateTime;

/**
 * Entity class representing an order in the marketplace.
 */
@Entity
@Table(name = "orders")
public class Order {
    /** Unique identifier for the order. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who placed the order. */
    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    /** The item that was ordered. */
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /** The date and time when the order was placed. */
    private LocalDateTime orderDate;

    /** The current status of the order. */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /** The payment method used for the order. */
    private String paymentMethod;

    /** The transaction ID from the payment provider. */
    private String transactionId;

    public Order() {
    }

    public Order(User buyer, Item item, String paymentMethod) {
        this.buyer = buyer;
        this.item = item;
        this.paymentMethod = paymentMethod;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.RESERVED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
} 