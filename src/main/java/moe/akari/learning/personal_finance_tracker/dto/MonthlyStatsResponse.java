package moe.akari.learning.personal_finance_tracker.dto;

import java.math.BigDecimal;

/**
 * 月度统计响应DTO
 */
public class MonthlyStatsResponse {

    private String month; // 格式: YYYY-MM
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal balance;
    private Integer incomeCount;
    private Integer expenseCount;

    public MonthlyStatsResponse() {
    }

    public MonthlyStatsResponse(String month, BigDecimal income, BigDecimal expense,
                                BigDecimal balance, Integer incomeCount, Integer expenseCount) {
        this.month = month;
        this.income = income;
        this.expense = expense;
        this.balance = balance;
        this.incomeCount = incomeCount;
        this.expenseCount = expenseCount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getExpense() {
        return expense;
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getIncomeCount() {
        return incomeCount;
    }

    public void setIncomeCount(Integer incomeCount) {
        this.incomeCount = incomeCount;
    }

    public Integer getExpenseCount() {
        return expenseCount;
    }

    public void setExpenseCount(Integer expenseCount) {
        this.expenseCount = expenseCount;
    }
}

