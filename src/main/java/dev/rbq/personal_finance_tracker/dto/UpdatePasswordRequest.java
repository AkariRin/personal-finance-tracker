package dev.rbq.personal_finance_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新密码请求
 */
public class UpdatePasswordRequest {

    @NotBlank(message = "The current password cannot be empty")
    private String currentPassword;

    @NotBlank(message = "The new password cannot be empty")
    @Size(min = 4, max = 32, message = "The new password must be between 4 and 32 characters long.")
    private String newPassword;

    public UpdatePasswordRequest() {
    }

    public UpdatePasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

