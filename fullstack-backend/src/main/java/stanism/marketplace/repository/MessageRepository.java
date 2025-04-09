package stanism.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stanism.marketplace.model.Message;
import stanism.marketplace.model.User;
import stanism.marketplace.model.Item;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findBySenderAndReceiverAndItemAndContent(User sender, User receiver, Item item, String content);
} 