package stanism.marketplace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity class representing a user in the marketplace.
 * This class maps to the 'users' table in the database and includes
 * validation constraints for user data.
 */
@Entity
@Table(name = "users")
public class User {

  /** Unique identifier for the user.
   * -- GETTER --
   *  Gets the unique identifier of the user.
   *
   *
   * -- SETTER --
   *  Sets the unique identifier of the user.
   *
   @return The user's ID
    * @param id The user's ID to set
   */
  @Setter
  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Username of the user. Must be unique and between 3-20 characters. */
  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
  @Column(unique = true, nullable = false)
  private String username;

  /** Email address of the user. Must be unique and valid. */
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Column(unique = true, nullable = false)
  private String email;

  /** Password of the user. Must be at least 6 characters. */
  @NotBlank(message = "Password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  private String password;

    /** Role of the user (e.g., ADMIN, USER). */
  @Enumerated(EnumType.STRING)
    private Role role;

    /** Set of items listed by this user. */
    @OneToMany(mappedBy = "user")
    private Set<Item> items;

    /** Set of items favorited by this user. */
    @OneToMany(mappedBy = "user")
    private Set<Favorite> favorites;

    /** Set of messages sent by this user. */
    @OneToMany(mappedBy = "sender")
    private Set<Message> sentMessages;

    /** Set of messages received by this user. */
    @OneToMany(mappedBy = "receiver")
    private Set<Message> receivedMessages;

    /** Set of orders placed by this user. */
    @OneToMany(mappedBy = "buyer")
    private Set<Order> orders;

  /**
   * Default constructor required by JPA.
   */
  public User() {
  }

  /**
   * Constructs a new User with the specified details.
   *
   * @param username The username of the user
   * @param email    The email address of the user
   * @param password The password of the user
   * @param role     The role of the user (e.g., ADMIN, USER)
   */
  public User(String username, String email, String password, Role role) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.role = role;
  }

    /**
   * Gets the username of the user.
   *
   * @return The user's username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets the username of the user.
   *
   * @param username The username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Gets the email address of the user.
   *
   * @return The user's email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email address of the user.
   *
   * @param email The email address to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the password of the user.
   *
   * @return The user's password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password of the user.
   *
   * @param password The password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

    /**
     * Gets the role of the user.
     *
     * @return The user's role
     */
    public Role getRole() {
        return role;
    }
    /**
     * Sets the role of the user.
     *
     * @param role The role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }

  /**
   * Returns a string representation of the user.
   * Note: Password is intentionally excluded from the string representation.
   *
   * @return A string representation of the user
   */
  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", email='" + email + '\'' +
        '}';
  }

  public Set<Item> getItems() {
    return items;
  }

  public void setItems(Set<Item> items) {
    this.items = items;
  }

  public Set<Favorite> getFavorites() {
    return favorites;
  }

  public void setFavorites(Set<Favorite> favorites) {
    this.favorites = favorites;
  }

  public Set<Message> getSentMessages() {
    return sentMessages;
  }

  public void setSentMessages(Set<Message> sentMessages) {
    this.sentMessages = sentMessages;
  }

  public Set<Message> getReceivedMessages() {
    return receivedMessages;
  }

  public void setReceivedMessages(Set<Message> receivedMessages) {
    this.receivedMessages = receivedMessages;
  }

  public Set<Order> getOrders() {
    return orders;
  }

  public void setOrders(Set<Order> orders) {
    this.orders = orders;
  }
}