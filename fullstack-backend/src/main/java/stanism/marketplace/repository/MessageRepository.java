package stanism.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stanism.marketplace.model.Message;
import stanism.marketplace.model.User;
import stanism.marketplace.model.Item;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverAndItemOrReceiverAndSenderAndItemOrderByTimestampAsc(
            User sender, User receiver, Item item, User receiver2, User sender2, Item item2);

    List<Message> findBySenderOrReceiverOrderByTimestampDesc(User sender, User receiver);
}