package dev.rbq.personal_finance_tracker.dto;

import java.math.BigDecimal;

/**
 * 支付方式统计响应DTO
 */
public class PaymentMethodStatsResponse {

    private String method;
    private BigDecimal totalAmount;
    private Integer count;
    private BigDecimal percentage;

    public PaymentMethodStatsResponse() {
    }

    public PaymentMethodStatsResponse(String method, BigDecimal totalAmount, Integer count, BigDecimal percentage) {
        this.method = method;
        this.totalAmount = totalAmount;
        this.count = count;
        this.percentage = percentage;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}

