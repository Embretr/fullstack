package stanism.marketplace.model.dto;

import stanism.marketplace.model.Item;
import stanism.marketplace.model.Image;

import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemResponseDTO toDTO(Item item) {
        if (item == null) {
            return null;
        }

        ItemResponseDTO dto = new ItemResponseDTO();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setBriefDescription(item.getBriefDescription());
        dto.setFullDescription(item.getFullDescription());
        dto.setPrice(item.getPrice());
        dto.setLatitude(item.getLatitude());
        dto.setLongitude(item.getLongitude());
        dto.setPublishDate(item.getPublishDate());
        dto.setStatus(item.getStatus());
        dto.setReservationDate(item.getReservationDate());

        // Convert user to UserResponse
        UserResponse userResponse = new UserResponse();
        userResponse.setId(item.getUser().getId());
        userResponse.setUsername(item.getUser().getUsername());
        userResponse.setEmail(item.getUser().getEmail());
        userResponse.setRole(item.getUser().getRole());
        dto.setOwner(userResponse);

        // Convert reservedBy to UserResponse if exists
        if (item.getReservedBy() != null) {
            UserResponse reservedByResponse = new UserResponse();
            reservedByResponse.setId(item.getReservedBy().getId());
            reservedByResponse.setUsername(item.getReservedBy().getUsername());
            reservedByResponse.setEmail(item.getReservedBy().getEmail());
            reservedByResponse.setRole(item.getReservedBy().getRole());
            dto.setReservedBy(reservedByResponse);
        }

        // Convert category to CategoryResponseDTO
        CategoryResponseDTO categoryDTO = new CategoryResponseDTO();
        categoryDTO.setId(item.getCategory().getId());
        categoryDTO.setName(item.getCategory().getName());
        dto.setCategory(categoryDTO);

        // Convert images to URLs
        if (item.getImages() != null) {
            dto.setImageUrls(item.getImages().stream()
                    .map(Image::getImageUrl)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }
}