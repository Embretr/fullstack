package stanism.marketplace.model;

/**
 * Enum representing the possible states of an item in the marketplace.
 */
public enum ItemStatus {
    /** The item is currently available for sale. */
    ACTIVE,

    /** The item has been archived and is no longer available. */
    ARCHIVED,

    /** The item has been sold. */
    SOLD,

    /** The item has been temporarily reserved. */
    RESERVED
}