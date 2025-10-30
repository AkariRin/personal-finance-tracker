package moe.akari.learning.personal_finance_tracker.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 创建账单请求DTO
 */
public class CreateBillRequest {

    @NotBlank(message = "账单类型不能为空")
    @Pattern(regexp = "^(in|out)$", message = "账单类型必须是 in 或 out")
    private String type;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Digits(integer = 8, fraction = 2, message = "金额最多8位整数和2位小数")
    private BigDecimal amount;

    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "^(cash|wx|alipay|card|btc|paypal|apple)$",
             message = "支付方式必须是 cash, wx, alipay, card, btc, paypal, apple 之一")
    private String method;

    @NotBlank(message = "交易对方不能为空")
    @Size(max = 100, message = "交易对方名称不能超过100个字符")
    private String counterparty;

    @Size(max = 50, message = "分类不能超过50个字符")
    private String category;

    private String time; // ISO 8601 格式的时间字符串，可选

    @Size(max = 500, message = "备注不能超过500个字符")
    private String note;

    public CreateBillRequest() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

