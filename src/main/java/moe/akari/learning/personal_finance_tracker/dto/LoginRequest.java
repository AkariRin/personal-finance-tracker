package moe.akari.learning.personal_finance_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户登录请求DTO
 */
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 16, message = "用户名长度必须在2-16个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 4, max = 32, message = "密码长度必须在4-32个字符之间")
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

