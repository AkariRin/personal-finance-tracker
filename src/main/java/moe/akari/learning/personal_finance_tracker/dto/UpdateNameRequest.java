package moe.akari.learning.personal_finance_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新用户名称请求
 */
public class UpdateNameRequest {

    @NotBlank(message = "名称不能为空")
    @Size(min = 1, max = 50, message = "名称长度必须在1-50个字符之间")
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

