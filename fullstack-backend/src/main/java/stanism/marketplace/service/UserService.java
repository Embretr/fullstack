package stanism.marketplace.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import stanism.marketplace.model.Role;
import stanism.marketplace.model.User;
import stanism.marketplace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for handling user-related operations.
 * Manages user registration, authentication, and profile updates.
 */
@Service
public class UserService {

  /** Logger instance for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

  /** Repository for user data access. */
  private final UserRepository userRepository;

  /** Password encoder for hashing passwords. */
  private final PasswordEncoder passwordEncoder;

  /**
   * Constructs a new UserService instance.
   *
   * @param userRepository
   *          The user repository to use
   * @param passwordEncoder
   *          The password encoder to use
   */
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Registers a new user with the provided credentials.
   *
   * @param username
   *          The username for the new user
   * @param email
   *          The email address for the new user
   * @param password
   *          The password for the new user
   * @return The newly registered user
   * @throws RuntimeException
   *           if username or email is already in use
   */
  @Transactional
  public User registerUser(String username, String email, String password) {
    if (userRepository.findByUsername(username).isPresent()) {
      throw new RuntimeException("Username already in use");
    }
    if (userRepository.findByEmail(email).isPresent()) {
      throw new RuntimeException("Email already in use");
    }
    User newUser = new User(username, email, passwordEncoder.encode(password), Role.USER);
    return userRepository.save(newUser);
  }

  /**
   * Registers a new admin user with the provided credentials.
   *
   * @param username
   *          The username for the new admin user
   * @param email
   *          The email address for the new admin user
   * @param password
   *          The password for the new admin user
   * @return The newly registered admin user
   */
  @Transactional
  public User registerAdmin(String username, String email, String password) {
    User user = new User(username, email, passwordEncoder.encode(password), Role.ADMIN);
    return userRepository.save(user);
  }

  /**
   * Finds a user by their username.
   *
   * @param username
   *          The username to search for
   * @return An Optional containing the user if found
   */
  public Optional<User> findUser(String username) {
    return userRepository.findByUsername(username);
  }

  /**
   * Finds a user by their email.
   *
   * @param email
   *          The email to search for
   * @return An Optional containing the user if found
   */
  public Optional<User> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  /**
   * Updates an existing user's information.
   *
   * @param user
   *          The user with updated information
   * @throws RuntimeException
   *           if the user is not found
   */
  @Transactional
  public void updateUser(User user) {
    Optional<User> existingUser = userRepository.findById(user.getId());
    if (existingUser.isPresent()) {
      User updatedUser = existingUser.get();
      updatedUser.setUsername(user.getUsername());
      updatedUser.setEmail(user.getEmail());
      // Only update password if it's different from the current one
      if (!user.getPassword().equals(updatedUser.getPassword())) {
        updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
      }
      userRepository.save(updatedUser);
    } else {
      throw new RuntimeException("User not found");
    }
  }

  /**
   * Verifies a user's credentials using email and password.
   *
   * @param email
   *          The email address to verify
   * @param password
   *          The password to verify
   * @return true if the credentials are valid, false otherwise
   */
  public boolean verifyUserByEmail(String email, String password) {
    Optional<User> user = userRepository.findByEmail(email);
    return user.isPresent() && passwordEncoder.matches(password, user.get().getPassword());
  }

  /**
   * Gets the currently authenticated user from Spring Security context.
   *
   * @return An Optional containing the authenticated user if present
   */
  public Optional<User> getCurrentUser() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    LOGGER.info("Getting current user with email: {}", email);
    return findUserByEmail(email);
  }

  /**
   * Gets a user by their ID.
   *
   * @param id
   *          The ID of the user to retrieve
   * @return An Optional containing the user if found
   */
  public Optional<User> getUserById(Long id) {
    LOGGER.info("Looking up user by ID: {}", id);
    Optional<User> user = userRepository.findById(id);
    if (user.isPresent()) {
      LOGGER.info("Found user with ID {}: {}", id, user.get().getEmail());
    } else {
      LOGGER.warn("No user found with ID: {}", id);
    }
    return user;
  }

  /**
   * Gets a user by their email.
   *
   * @param email
   *          The email of the user to retrieve
   * @return An Optional containing the user if found
   */
  public Optional<User> getUserByEmail(String email) {
    LOGGER.info("Looking up user by email: {}", email);
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isPresent()) {
      LOGGER.info("Found user with email {}: {}", email, user.get().getId());
    } else {
      LOGGER.warn("No user found with email: {}", email);
    }
    return user;
  }
}