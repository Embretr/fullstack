package stanism.marketplace.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request payload for updating password")
public class UpdatePasswordRequest {
    /**
     * The current password for verification.
     */
    @NotBlank(message = "Current password cannot be blank")
    @Schema(description = "The current password for verification",
            example = "currentPassword123",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String currentPassword;

    /**
     * The new desired password.
     */
    @NotBlank(message = "New password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "The new desired password",
            example = "newSecurePassword123",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}