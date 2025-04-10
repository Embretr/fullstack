package stanism.marketplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stanism.marketplace.model.User;
import stanism.marketplace.security.JwtUtil;
import stanism.marketplace.service.UserService;
import java.util.Optional;
import stanism.marketplace.model.dto.UserResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3173", allowCredentials = "true")
@Tag(name = "Authentication", description = "Endpoints for user authentication and session management")
public class AuthController {

        /** Service for handling user-related operations. */
        private final UserService userService;

        /** Utility for JWT token operations. */
        private final JwtUtil jwtUtil;

        /** Authentication manager for Spring Security. */
        private final AuthenticationManager authenticationManager;

        /** Development mode flag. */
        @Value("${app.development-mode:false}")
        private boolean developmentMode;

        public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
                this.userService = userService;
                this.jwtUtil = jwtUtil;
                this.authenticationManager = authenticationManager;
        }

        @PostMapping("/login")
        @Operation(summary = "User login",

                        description = "Authenticates a user based on email and password," +
                                        " sets a JWT token cookie upon success.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200",
                                        description = "Login successful",
                                        content = @Content(mediaType = "text/plain",
                                                        schema = @Schema(implementation = String.class),
                                                        examples = @ExampleObject("Login successful"))),
                        @ApiResponse(responseCode = "401",
                                        description = "Invalid credentials",
                                        content = @Content(mediaType = "text/plain",
                                                        schema = @Schema(implementation = String.class),
                                                        examples = @ExampleObject("Invalid credentials")))
        })
        public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        String token = jwtUtil.generateToken(user.getEmail());

                        // Set security headers
                        response.setHeader("X-Content-Type-Options", "nosniff");
                        response.setHeader("X-Frame-Options", "DENY");
                        response.setHeader("X-XSS-Protection", "1; mode=block");
                        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

                        // Create HTTP-only cookie with the JWT token
                        Cookie cookie = new Cookie("Authorization", token);
                        cookie.setHttpOnly(true);
                        cookie.setSecure(false); // Only send cookie over HTTPS unless in development mode
                        cookie.setPath("/");
                        cookie.setMaxAge((int) (jwtUtil.getExpirationTime() / 1000)); // Convert milliseconds to seconds
                        response.addCookie(cookie);

                        return ResponseEntity.ok("Login successful");
                } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
                }
        }

        @PostMapping("/logout")
        @Operation(summary = "User logout",
                        description = "Logs out the current user and clears the JWT token cookie.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200",
                                        description = "Logout successful",
                                        content = @Content(mediaType = "text/plain",
                                                        schema = @Schema(implementation = String.class),
                                                        examples = @ExampleObject("Logout successful")))
        })
        public ResponseEntity<?> logout(HttpServletResponse response) {
                // Set security headers
                response.setHeader("X-Content-Type-Options", "nosniff");
                response.setHeader("X-Frame-Options", "DENY");
                response.setHeader("X-XSS-Protection", "1; mode=block");
                response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

                // Clear the JWT cookie
                Cookie cookie = new Cookie("Authorization", "");
                cookie.setHttpOnly(true);
                cookie.setSecure(!developmentMode); // Only send cookie over HTTPS unless in development mode
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);

                // Clear the security context
                SecurityContextHolder.clearContext();

                return ResponseEntity.ok("Logout successful");
        }

        @GetMapping("/isLoggedIn")
        @Operation(summary = "Check login status",
                        description = "Checks if the current user is logged in.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200",
                                        description = "User is logged in",
                                        content = @Content(mediaType = "application/json",
                                                        schema = @Schema(implementation = User.class))),
                        @ApiResponse(responseCode = "401",
                                        description = "User is not logged in",
                                        content = @Content(mediaType = "text/plain",
                                                        schema = @Schema(implementation = String.class),
                                                        examples = @ExampleObject("Not logged in")))
        })
        public ResponseEntity<?> isLoggedIn() {
                Optional<User> currentUser = userService.getCurrentUser();
                if (currentUser.isPresent()) {
                        return ResponseEntity.ok(currentUser.get());
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        @PostMapping("/register")
        @Operation(summary = "User registration",
                        description = "Registers a new user with username, email, and password.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200",
                                        description = "User registered successfully",
                                        content = @Content(mediaType = "text/plain",
                                                        schema = @Schema(implementation = String.class),
                                                        examples = @ExampleObject("User has been registered" +
                                                                        " successfully."))),
                        @ApiResponse(responseCode = "400",
                                        description = "Registration failed (e.g., email already exists, invalid input)",
                                        content = @Content(mediaType = "text/plain",
                                                        schema = @Schema(implementation = String.class),
                                                        examples = @ExampleObject("Email already exists.")))
        })
        public ResponseEntity<String> register(@RequestBody User user) {
                try {
                        userService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
                        return ResponseEntity.ok("User has been registered successfully.");
                } catch (RuntimeException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                }
        }

        @GetMapping("/me")
        @Operation(summary = "Get current user information",
                        description = "Returns the currently logged-in user's information.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200",
                                        description = "User information retrieved successfully",
                                        content = @Content(mediaType = "application/json",
                                                        schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "401",
                                        description = "User is not authenticated",
                                        content = @Content(mediaType = "text/plain",
                                                        schema = @Schema(implementation = String.class),
                                                        examples = @ExampleObject("Not authenticated")))
        })
        public ResponseEntity<?> getMe() {
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
}