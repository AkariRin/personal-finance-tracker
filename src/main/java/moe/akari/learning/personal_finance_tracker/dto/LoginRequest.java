package moe.akari.learning.personal_finance_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户登录请求DTO
 */
public class LoginRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 2, max = 16, message = "The username must be between 2 and 16 characters long.")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 4, max = 32, message = "The password must be between 4 and 32 characters long.")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

