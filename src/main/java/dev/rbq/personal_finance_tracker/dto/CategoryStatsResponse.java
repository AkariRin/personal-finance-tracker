package dev.rbq.personal_finance_tracker.dto;

import java.math.BigDecimal;

/**
 * 分类统计响应DTO
 */
public class CategoryStatsResponse {

    private String category;
    private BigDecimal totalAmount;
    private Integer count;
    private BigDecimal percentage;

    public CategoryStatsResponse() {
    }

    public CategoryStatsResponse(String category, BigDecimal totalAmount, Integer count, BigDecimal percentage) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.count = count;
        this.percentage = percentage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

