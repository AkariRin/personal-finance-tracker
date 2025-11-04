package dev.rbq.personal_finance_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新用户名称请求
 */
public class UpdateNameRequest {

    @NotBlank(message = "The name cannot be empty")
    @Size(min = 1, max = 50, message = "The name must be between 1 and 50 characters long.")
    private String name;

    public UpdateNameRequest() {
    }

    public UpdateNameRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

