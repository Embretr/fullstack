package stanism.marketplace.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import stanism.marketplace.model.Message;
import stanism.marketplace.model.User;
import stanism.marketplace.model.Item;
import stanism.marketplace.service.MessageService;
import stanism.marketplace.service.UserService;
import stanism.marketplace.service.ItemService;

@Controller
public class WebSocketController {

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

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(MessageService messageService, UserService userService, ItemService itemService,
            SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.userService = userService;
        this.itemService = itemService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Message chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        Authentication authentication = (Authentication) headerAccessor.getUser();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        // Get sender from security context
        User sender = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Get receiver and item from the message
        User receiver = userService.getUserById(chatMessage.getReceiver().getId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        Item item = itemService.getItemById(chatMessage.getItem().getId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Create and save the message
        Message message = new Message(sender, receiver, item, chatMessage.getContent());
        Message savedMessage = messageService.saveMessage(message);

        // Send the message to both users
        String topic = "/topic/chat/" + item.getId() + "/" + receiver.getId();
        messagingTemplate.convertAndSend(topic, savedMessage);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Message addUser(@Payload Message chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender().getUsername());
        return chatMessage;
    }
}