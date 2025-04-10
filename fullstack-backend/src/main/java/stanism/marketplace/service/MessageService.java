package stanism.marketplace.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stanism.marketplace.model.Message;
import stanism.marketplace.model.User;
import stanism.marketplace.model.Item;
import stanism.marketplace.repository.MessageRepository;
import java.util.List;

@Service
public class MessageService {
    /**
     * Repository for accessing and managing message data in the database.
     */
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesBetweenUsers(User user1, User user2, Item item) {
        return messageRepository.findBySenderAndReceiverAndItemOrReceiverAndSenderAndItemOrderByTimestampAsc(
                user1, user2, item, user1, user2, item);
    }

    @Transactional(readOnly = true)
    public List<Message> getUserMessages(User user) {
        return messageRepository.findBySenderOrReceiverOrderByTimestampDesc(user, user);
    }
}