package stanism.marketplace.model.dto;

import stanism.marketplace.model.Message;

public class MessageMapper {
    public static MessageResponseDTO toDTO(Message message) {
        if (message == null) {
            return null;
        }

        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());

        // Convert sender to UserResponse
        UserResponse senderResponse = new UserResponse();
        senderResponse.setId(message.getSender().getId());
        senderResponse.setUsername(message.getSender().getUsername());
        senderResponse.setEmail(message.getSender().getEmail());
        senderResponse.setRole(message.getSender().getRole());
        dto.setSender(senderResponse);

        // Convert receiver to UserResponse
        UserResponse receiverResponse = new UserResponse();
        receiverResponse.setId(message.getReceiver().getId());
        receiverResponse.setUsername(message.getReceiver().getUsername());
        receiverResponse.setEmail(message.getReceiver().getEmail());
        receiverResponse.setRole(message.getReceiver().getRole());
        dto.setReceiver(receiverResponse);

        // Convert item to ItemResponseDTO
        dto.setItem(ItemMapper.toDTO(message.getItem()));

        return dto;
    }
}