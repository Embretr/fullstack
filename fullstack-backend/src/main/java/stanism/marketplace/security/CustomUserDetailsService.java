package stanism.marketplace.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import stanism.marketplace.repository.UserRepository;


/**
 * Service for loading user details from the database.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /** The repository for user data. */
    private final UserRepository userRepository;

    /**
     * Constructs a new CustomUserDetailsService with the given UserRepository.
     *
     * @param userRepository The repository for user data
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email address.
     *
     * @param email The email address of the user to load
     * @return The UserDetails for the user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
} 