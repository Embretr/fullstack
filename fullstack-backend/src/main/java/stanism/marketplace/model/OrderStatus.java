package stanism.marketplace.model;

/**
 * Enum representing the possible states of an order in the marketplace.
 */
public enum OrderStatus {
    /** The order has been placed but not yet completed. */
    RESERVED,

    /** The order has been successfully completed. */
    COMPLETED,

    /** The order has been cancelled. */
    CANCELLED
} 