package stanism.marketplace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;

/**
 * Category class representing a category of items in the marketplace.
 * It contains fields for id, name, and a set of items associated with the category.
 */
@Entity
@Table(name = "categories")
public class Category {
    /** Unique identifier for the category. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** The id of the category. */
    private Long id;

    /** Name of the category. */
    private String name;

    /** Set of items associated with this category. */
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<Item> items;

    /**
     * Gets the id of the category.
     *
     * @return the id of the category
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of the category.
     *
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the category.
     *
     * @return the name of the category
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the category.
     *
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the set of items associated with the category.
     *
     * @return the set of items
     */
    public Set<Item> getItems() {
        return items;
    }

    /**
     * Sets the set of items associated with the category.
     *
     * @param items
     *            the set of items to set
     */
    public void setItems(Set<Item> items) {
        this.items = items;
    }

    /**
     * Returns a string representation of the category.
     *
     * @return a string representation of the category
     */
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}