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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stanism.marketplace.model.User;
import stanism.marketplace.model.Role;
import stanism.marketplace.repository.UserRepository;
import stanism.marketplace.security.JwtUtil;
import stanism.marketplace.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3173")
@Tag(name = "User Management", description = "Endpoints for user profile management")
public class UserController {

    /** Service for handling user-related operations. */
    private final UserService userService;
    
    /** Utility for JWT token operations. */
    private final JwtUtil jwtUtil;
    
    /** Repository for user data access. */
    private final UserRepository userRepository;

    public UserController(UserService userService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @GetMapping("/{email}")
    @Operation(summary = "Get user by email",
            description = "Retrieves the user details for the specified email.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request (e.g., user not found)",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject("User not found"))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - No token, invalid token, or user mismatch",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject("Invalid token")))
    })
    public ResponseEntity<?> getUserByEmail(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable String email) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token provided");
        }
        
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Optional<User> userOptional = userService.findUserByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        return ResponseEntity.ok(userOptional.get());
    }

    @PutMapping("/{email}/admin")
    @Operation(summary = "Make user admin",
            description = "Updates the role of the user with the specified email to ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> makeUserAdmin(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable String email) {
       
        String jwtToken = token.substring(7);
        if (!jwtUtil.validateToken(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Optional<User> userOptional = userRepository.findByEmail(email.toLowerCase().trim());    
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User with email " + email + " not found");
        }

        User user = userOptional.get();
        user.setRole(Role.ADMIN);
        userService.updateUser(user);

        return ResponseEntity.ok("User role updated to ADMIN successfully");
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Delete user by email",
            description = "Deletes the user with the specified email.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteUserByEmail(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable String email) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token provided");
        }
        
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Optional<User> userOptional = userRepository.findByEmail(email.toLowerCase().trim());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        userRepository.delete(userOptional.get());
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/{email}/admin/remove")
    @Operation(summary = "Remove admin role from user",
            description = "Removes the ADMIN role from the user with the specified email.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> removeAdminRole(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable String email) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token provided");
        }
       
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Optional<User> userOptional = userRepository.findByEmail(email.toLowerCase().trim());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        User user = userOptional.get();
        if (user.getRole() == Role.ADMIN) {
            user.setRole(Role.USER);
            userService.updateUser(user);
            return ResponseEntity.ok("Admin role removed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not an admin");
        }
    }
} 