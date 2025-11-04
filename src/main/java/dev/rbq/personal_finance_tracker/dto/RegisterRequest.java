package dev.rbq.personal_finance_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户注册请求DTO
 */
public class RegisterRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 2, max = 16, message = "The username must be between 2 and 16 characters long.")
    private String username;

    @NotBlank(message = "The name cannot be empty")
    @Size(min = 1, max = 50, message = "The name must be between 1 and 50 characters long.")
    private String name;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 4, max = 32, message = "The password must be between 4 and 32 characters long.")
    private String password;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

