package stanism.marketplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stanism.marketplace.model.User;
import stanism.marketplace.model.dto.UpdateEmailRequest;
import stanism.marketplace.model.dto.UpdatePasswordRequest;
import stanism.marketplace.model.dto.UpdateUsernameRequest;
import stanism.marketplace.model.dto.UserResponse;
import stanism.marketplace.security.JwtUtil;
import stanism.marketplace.service.UserService;

import java.util.Optional;

/**
 * Controller for managing user profile information.
 */
@RestController
@RequestMapping("/api/userinfo")
@CrossOrigin(origins = "http://localhost:3173", allowCredentials = "true")
@Tag(name = "User Profile", description = "Endpoints for managing user profile information")
public class UserProfileController {

        /**
         * Service for user-related operations.
         */
    private final UserService userService;
        /**
         * Utility class for handling JWT operations.
         */
    private final JwtUtil jwtUtil;

        /**
         * Constructor for UserProfileController.
         *
         * @param userService Service for user-related operations.
         * @param jwtUtil Utility class for handling JWT operations.
         */
    public UserProfileController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Retrieves the profile information of the currently authenticated user.
     *
     * @return ResponseEntity containing user profile or an error message.
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user profile",
               description = "Retrieves the profile information of the currently authenticated user.")
    public ResponseEntity<?> getCurrentUserProfile() {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            UserResponse response = new UserResponse();
            response.setId(currentUser.get().getId());
            response.setUsername(currentUser.get().getUsername());
            response.setEmail(currentUser.get().getEmail());
            response.setRole(currentUser.get().getRole());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
    }

    /**
     * Updates the username of the currently authenticated user.
     *
     * @param request UpdateUsernameRequest containing the new username.
     * @return ResponseEntity with the update status.
     */
    @PutMapping("/username")
    @Operation(summary = "Update username",
               description = "Updates the username of the currently authenticated user.")
    public ResponseEntity<?> updateUsername(@RequestBody UpdateUsernameRequest request) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        User user = currentUser.get();
        if (userService.findUser(request.getNewUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken");
        }

        user.setUsername(request.getNewUsername());
        userService.updateUser(user);
        return ResponseEntity.ok("Username updated successfully");
    }

    /**
     * Updates the email address of the currently authenticated user.
     *
     * @param request UpdateEmailRequest containing the new email.
     * @return ResponseEntity with the update status.
     */
    @PutMapping("/email")
    @Operation(summary = "Update email",
               description = "Updates the email address of the currently authenticated user.")
    public ResponseEntity<?> updateEmail(@RequestBody UpdateEmailRequest request) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        User user = currentUser.get();
        if (userService.findUserByEmail(request.getNewEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already in use");
        }

        user.setEmail(request.getNewEmail());
        userService.updateUser(user);
        return ResponseEntity.ok("Email updated successfully");
    }

    /**
     * Updates the password of the currently authenticated user.
     *
     * @param request UpdatePasswordRequest containing the current and new passwords.
     * @return ResponseEntity with the update status.
     */
    @PutMapping("/password")
    @Operation(summary = "Update password",
               description = "Updates the password of the currently authenticated user.")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        User user = currentUser.get();
        if (!userService.verifyUserByEmail(user.getEmail(), request.getCurrentPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect");
        }

        user.setPassword(request.getNewPassword());
        userService.updateUser(user);
        return ResponseEntity.ok("Password updated successfully");
    }

    @GetMapping("/email")
    @Operation(summary = "Get user email",
                    description = "Retrieves the email address of the currently authenticated user.",
                    security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200",
                                    description = "User email retrieved successfully",
                                    content = @Content(mediaType = "text/plain",
                                                    schema = @Schema(implementation = String.class),
                                                    examples = @ExampleObject("john.doe@example.com"))),
                    @ApiResponse(responseCode = "401",
                                    description = "Unauthorized - No token, invalid token, or user mismatch",
                                    content = @Content(mediaType = "text/plain",
                                                    schema = @Schema(implementation = String.class),
                                                    examples = @ExampleObject("Invalid token")))
    })
    public ResponseEntity<?> getUserEmail(
                    @RequestHeader(value = "Authorization", required = false) String token) {

            if (!jwtUtil.validateToken(token)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
            String email = jwtUtil.extractUsername(token);
            Optional<User> currentUser = userService.getCurrentUser();
            if (currentUser.isPresent() && currentUser.get().getEmail().equals(email)) {
                    return ResponseEntity.ok(email);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }

    @GetMapping("/name")
    @Operation(summary = "Get username",
                    description = "Retrieves the username of the currently authenticated user.",
                    security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200",
                                    description = "Username retrieved successfully",
                                    content = @Content(mediaType = "text/plain",
                                                    schema = @Schema(implementation = String.class),
                                                    examples = @ExampleObject("john_doe"))),
                    @ApiResponse(responseCode = "401",
                                    description = "Unauthorized - No token, invalid token, or user mismatch",
                                    content = @Content(mediaType = "text/plain",
                                                    schema = @Schema(implementation = String.class),
                                                    examples = @ExampleObject("Invalid token")))
    })
    public ResponseEntity<?> getUserName(
                    @RequestHeader(value = "Authorization", required = false) String token) {
            if (token == null || token.trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token provided");
            }

            if (!jwtUtil.validateToken(token)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
            String email = jwtUtil.extractUsername(token);
            Optional<User> currentUser = userService.getCurrentUser();
            if (currentUser.isPresent() && currentUser.get().getEmail().equals(email)) {
                    return ResponseEntity.ok(currentUser.get().getUsername());
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }
}