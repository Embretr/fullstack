package stanism.marketplace.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final UserDetailsService userDetailsService;

    public WebSocketAuthInterceptor(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                System.out.println("Processing CONNECT command");
                System.out.println("Message headers: " + accessor.getMessageHeaders());

                Authentication authentication = (Authentication) accessor.getUser();
                if (authentication != null && authentication.isAuthenticated()) {
                    System.out.println("User already authenticated: " + authentication.getName());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    System.out.println("No valid authentication found");
                    throw new RuntimeException("User not authenticated");
                }
            } else if (StompCommand.SEND.equals(accessor.getCommand())) {
                Authentication authentication = (Authentication) accessor.getUser();
                if (authentication == null || !authentication.isAuthenticated()) {
                    throw new RuntimeException("User not authenticated");
                }
            }
        }

        return message;
    }
}