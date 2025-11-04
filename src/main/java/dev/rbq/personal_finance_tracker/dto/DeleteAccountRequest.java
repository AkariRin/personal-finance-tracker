package dev.rbq.personal_finance_tracker.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 删除账号请求
 */
public class DeleteAccountRequest {

    @NotBlank(message = "Username confirmation cannot be empty")
    private String username;

    public DeleteAccountRequest() {
    }

    public DeleteAccountRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

