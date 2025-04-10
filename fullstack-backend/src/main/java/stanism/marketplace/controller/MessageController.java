package stanism.marketplace.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stanism.marketplace.model.Message;
import stanism.marketplace.model.User;
import stanism.marketplace.model.Item;
import stanism.marketplace.service.MessageService;
import stanism.marketplace.service.UserService;
import stanism.marketplace.service.ItemService;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3173")
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

        @GetMapping("/conversation/{itemId}/{userId}")
        public ResponseEntity<List<Message>> getConversation(
                        @PathVariable Long itemId,
                        @PathVariable Long userId) {
                User currentUser = userService.getCurrentUser()
                                .orElseThrow(() -> new RuntimeException("User not authenticated"));
                User otherUser = userService.getUserById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Item item = itemService.getItemById(itemId)
                                .orElseThrow(() -> new RuntimeException("Item not found"));

                List<Message> messages = messageService.getMessagesBetweenUsers(currentUser, otherUser, item);
                return ResponseEntity.ok(messages);
        }

        @GetMapping("/user")
        public ResponseEntity<List<Message>> getUserMessages() {
                User currentUser = userService.getCurrentUser()
                                .orElseThrow(() -> new RuntimeException("User not authenticated"));
                List<Message> messages = messageService.getUserMessages(currentUser);
                return ResponseEntity.ok(messages);
        }
}