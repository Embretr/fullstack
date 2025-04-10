package stanism.marketplace.model.dto;

/**
 * DTO class for updating a user's password.
 * This class contains the current and new password fields.
 */
public class UpdatePasswordRequest {
        /**
         * The current password of the user.
         */
    private String currentPassword;
        /**
         * The new password that the user wants to set.
         */
    private String newPassword;

        /**
         * Gets the current password of the user.
         *
         * @return The current password.
         */
    public String getCurrentPassword() {
        return currentPassword;
    }

        /**
         * Sets the current password of the user.
         *
         * @param currentPassword The current password to set.
         */
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

        /**
         * Gets the new password that the user wants to set.
         *
         * @return The new password.
         */
    public String getNewPassword() {
        return newPassword;
    }

        /**
         * Sets the new password that the user wants to set.
         *
         * @param newPassword The new password to set.
         */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}