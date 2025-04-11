package stanism.marketplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import stanism.marketplace.model.Message;
import stanism.marketplace.model.User;
import stanism.marketplace.model.Item;
import stanism.marketplace.model.dto.MessageResponseDTO;
import stanism.marketplace.service.MessageService;
import stanism.marketplace.service.UserService;
import stanism.marketplace.service.ItemService;
import stanism.marketplace.model.dto.MessageMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "Message management APIs")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

        /**
         * Service for handling message-related operations.
         */
        private final MessageService messageService;

        /**
         * Service for handling user-related operations.
         */
        private final UserService userService;

        /**
         * Service for handling item-related operations.
         */
        private final ItemService itemService;

        public MessageController(MessageService messageService, UserService userService, ItemService itemService) {
                this.messageService = messageService;
                this.userService = userService;
                this.itemService = itemService;
        }

        @GetMapping("/conversations")
        @Operation(summary = "Get all conversations for the authenticated user")
        public ResponseEntity<List<MessageResponseDTO>> getUserConversations(@AuthenticationPrincipal User user) {
                List<Message> messages = messageService.getUserMessages(user);
                List<MessageResponseDTO> dtos = messages.stream()
                                .map(MessageMapper::toDTO)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        @GetMapping("/conversation/{itemId}/{userId}")
        @Operation(summary = "Get conversation between two users for a specific item")
        public ResponseEntity<List<MessageResponseDTO>> getConversation(
                        @PathVariable Long userId,
                        @PathVariable Long itemId) {
                // Get current user from SecurityContextHolder
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                System.out.println("Current user email from SecurityContext: " + email);

                User currentUser = userService.getUserByEmail(email)
                                .orElseThrow(() -> {
                                        System.out.println("Failed to find user with email: " + email);
                                        return new RuntimeException("Current user not found with email: " + email);
                                });

                System.out.println("Found current user: " + currentUser.getId() + " - " + currentUser.getEmail());

                User otherUser = userService.getUserById(userId)
                                .orElseThrow(() -> {
                                        System.out.println("Failed to find other user with ID: " + userId);
                                        return new RuntimeException("Other user not found with ID: " + userId);
                                });

                System.out.println("Found other user: " + otherUser.getId() + " - " + otherUser.getEmail());

                Item item = itemService.getItemById(itemId)
                                .orElseThrow(() -> {
                                        System.out.println("Failed to find item with ID: " + itemId);
                                        return new RuntimeException("Item not found with ID: " + itemId);
                                });

                System.out.println("Found item: " + item.getId() + " - " + item.getTitle());

                List<Message> messages = messageService.getMessagesBetweenUsers(currentUser, otherUser, item);
                System.out.println("Found " + messages.size() + " messages");

                List<MessageResponseDTO> dtos = messages.stream()
                                .map(MessageMapper::toDTO)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }
}