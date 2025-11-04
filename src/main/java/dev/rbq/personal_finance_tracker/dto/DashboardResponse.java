package dev.rbq.personal_finance_tracker.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 仪表板响应DTO
 */
public class DashboardResponse {

    private BigDecimal totalAssets;
    private BigDecimal todayIncome;
    private BigDecimal todayExpense;
    private List<BillResponse> recentBills;

    public DashboardResponse() {
    }

    public DashboardResponse(BigDecimal totalAssets, BigDecimal todayIncome,
                           BigDecimal todayExpense, List<BillResponse> recentBills) {
        this.totalAssets = totalAssets;
        this.todayIncome = todayIncome;
        this.todayExpense = todayExpense;
        this.recentBills = recentBills;
    }

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }

    public BigDecimal getTodayIncome() {
        return todayIncome;
    }

    public void setTodayIncome(BigDecimal todayIncome) {
        this.todayIncome = todayIncome;
    }

    public BigDecimal getTodayExpense() {
        return todayExpense;
    }

    public void setTodayExpense(BigDecimal todayExpense) {
        this.todayExpense = todayExpense;
    }

    public List<BillResponse> getRecentBills() {
        return recentBills;
    }

    public void setRecentBills(List<BillResponse> recentBills) {
        this.recentBills = recentBills;
    }
}

