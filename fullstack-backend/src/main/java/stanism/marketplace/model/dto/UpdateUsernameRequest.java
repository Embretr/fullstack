package stanism.marketplace.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request payload for updating username")
public class UpdateUsernameRequest {

    /**
     * The new desired username.
     */
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "The new desired username",
            example = "new_john_doe",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String newUsername;
}