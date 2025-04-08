package stanism.marketplace.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request payload for updating email")
public class UpdateEmailRequest {

    /**
     * The new desired email address.
     */
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(description = "The new desired email address",
            example = "new.john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String newEmail;
}