package moe.akari.learning.personal_finance_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户注册请求DTO
 */
public class RegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 16, message = "用户名长度必须在2-16个字符之间")
    private String username;

    @NotBlank(message = "显示名称不能为空")
    @Size(min = 1, max = 50, message = "显示名称长度必须在1-50个字符之间")
    private String name;

    @NotBlank(message = "密码不能为空")
    @Size(min = 4, max = 32, message = "密码长度必须在4-32个字符之间")
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

