package dev.rbq.personal_finance_tracker.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 综合分析响应DTO
 */
public class AnalyticsResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private Integer totalBills;
    private List<MonthlyStatsResponse> monthlyStats;
    private List<PaymentMethodStatsResponse> paymentMethodStats;
    private List<CategoryStatsResponse> categoryStats;

    public AnalyticsResponse() {
    }

    public AnalyticsResponse(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal balance,
                             Integer totalBills, List<MonthlyStatsResponse> monthlyStats,
                             List<PaymentMethodStatsResponse> paymentMethodStats,
                             List<CategoryStatsResponse> categoryStats) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
        this.totalBills = totalBills;
        this.monthlyStats = monthlyStats;
        this.paymentMethodStats = paymentMethodStats;
        this.categoryStats = categoryStats;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getTotalBills() {
        return totalBills;
    }

    public void setTotalBills(Integer totalBills) {
        this.totalBills = totalBills;
    }

    public List<MonthlyStatsResponse> getMonthlyStats() {
        return monthlyStats;
    }

    public void setMonthlyStats(List<MonthlyStatsResponse> monthlyStats) {
        this.monthlyStats = monthlyStats;
    }

    public List<PaymentMethodStatsResponse> getPaymentMethodStats() {
        return paymentMethodStats;
    }

    public void setPaymentMethodStats(List<PaymentMethodStatsResponse> paymentMethodStats) {
        this.paymentMethodStats = paymentMethodStats;
    }

    public List<CategoryStatsResponse> getCategoryStats() {
        return categoryStats;
    }

    public void setCategoryStats(List<CategoryStatsResponse> categoryStats) {
        this.categoryStats = categoryStats;
    }
}

