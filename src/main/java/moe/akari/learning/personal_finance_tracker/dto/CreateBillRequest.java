package moe.akari.learning.personal_finance_tracker.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 创建账单请求DTO
 */
public class CreateBillRequest {

    @NotBlank(message = "The bill type cannot be empty")
    @Pattern(regexp = "^(in|out)$", message = "The bill type must be either income or expense.")
    private String type;

    @NotNull(message = "The amount cannot be empty")
    @DecimalMin(value = "0.01", message = "The amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Amount up to 8 digits in integer part and 2 digits in decimal part")
    private BigDecimal amount;

    @NotBlank(message = "Payment method cannot be empty")
    @Pattern(regexp = "^(cash|wx|alipay|card|btc|paypal|apple)$",
             message = "The payment method must be one of Cash, Weixin, Alipay, Card, BTC, PayPal, or Apple Pay.")
    private String method;

    @NotBlank(message = "The counterparty cannot be empty")
    @Size(max = 100, message = "The counterparty name cannot exceed 100 characters.")
    private String counterparty;

    @Size(max = 50, message = "Categories cannot exceed 50 characters")
    private String category;

    private String time; // ISO 8601 格式的时间字符串，可选

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
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

