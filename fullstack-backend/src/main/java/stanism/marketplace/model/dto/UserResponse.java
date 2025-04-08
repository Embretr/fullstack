package stanism.marketplace.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import stanism.marketplace.model.Role;

/**
 * Data transfer object representing user information in API responses.
 * This class is used to structure user data when sending responses from authentication endpoints.
 */
@Schema(description = "User information response")
public class UserResponse {
    /** The unique identifier of the user. */
    @Schema(description = "User ID")
    private Long id;
    
    /** The display name of the user. */
    @Schema(description = "Username")
    private String username;
    
    /** The email address associated with the user's account. */
    @Schema(description = "Email address")
    private String email;
    
    /** The security role assigned to the user. */
    @Schema(description = "User role")
    private Role role;

    /**
     * Gets the user's unique identifier.
     * @return The user's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user's unique identifier.
     * @param id The user's ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user's display name.
     * @return The user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's display name.
     * @param username The username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the user's email address.
     * @return The user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     * @param email The email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's security role.
     * @return The user's role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the user's security role.
     * @param role The role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }
} 