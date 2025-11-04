package dev.rbq.personal_finance_tracker.service;

import dev.rbq.personal_finance_tracker.dto.AnalyticsResponse;
import dev.rbq.personal_finance_tracker.dto.CategoryStatsResponse;
import dev.rbq.personal_finance_tracker.dto.MonthlyStatsResponse;
import dev.rbq.personal_finance_tracker.dto.PaymentMethodStatsResponse;
import moe.akari.learning.personal_finance_tracker.dto.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 统计分析服务
 */
@Service
public class AnalyticsService {

    private final JdbcTemplate jdbcTemplate;

    public AnalyticsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 获取用户的综合统计分析数据
     *
     * @param username 用户名
     * @param months 统计最近几个月的数据（默认6个月）
     * @return 综合分析响应
     */
    public AnalyticsResponse getAnalytics(String username, Integer months) {
        if (months == null || months <= 0) {
            months = 6;
        }

        // 获取总收入
        BigDecimal totalIncome = getTotalIncome(username);

        // 获取总支出
        BigDecimal totalExpense = getTotalExpense(username);

        // 计算余额
        BigDecimal balance = totalIncome.subtract(totalExpense);

        // 获取总账单数
        Integer totalBills = getTotalBills(username);

        // 获取月度统计
        List<MonthlyStatsResponse> monthlyStats = getMonthlyStats(username, months);

        // 获取支付方式统计
        List<PaymentMethodStatsResponse> paymentMethodStats = getPaymentMethodStats(username);

        // 获取分类统计（仅支出）
        List<CategoryStatsResponse> categoryStats = getCategoryStats(username);

        return new AnalyticsResponse(
                totalIncome,
                totalExpense,
                balance,
                totalBills,
                monthlyStats,
                paymentMethodStats,
                categoryStats
        );
    }

    /**
     * 获取总收入
     */
    private BigDecimal getTotalIncome(String username) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM bill WHERE user_username = ? AND type = 'in'";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, username);
    }

    /**
     * 获取总支出
     */
    private BigDecimal getTotalExpense(String username) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM bill WHERE user_username = ? AND type = 'out'";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, username);
    }

    /**
     * 获取总账单数
     */
    private Integer getTotalBills(String username) {
        String sql = "SELECT COUNT(*) FROM bill WHERE user_username = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, username);
    }

    /**
     * 获取月度统计
     */
    private List<MonthlyStatsResponse> getMonthlyStats(String username, Integer months) {
        String sql = "SELECT " +
                "DATE_FORMAT(time, '%Y-%m') as month, " +
                "COALESCE(SUM(CASE WHEN type = 'in' THEN amount ELSE 0 END), 0) as income, " +
                "COALESCE(SUM(CASE WHEN type = 'out' THEN amount ELSE 0 END), 0) as expense, " +
                "COUNT(CASE WHEN type = 'in' THEN 1 END) as incomeCount, " +
                "COUNT(CASE WHEN type = 'out' THEN 1 END) as expenseCount " +
                "FROM bill " +
                "WHERE user_username = ? AND time >= DATE_SUB(CURDATE(), INTERVAL ? MONTH) " +
                "GROUP BY DATE_FORMAT(time, '%Y-%m') " +
                "ORDER BY month DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String month = rs.getString("month");
            BigDecimal income = rs.getBigDecimal("income");
            BigDecimal expense = rs.getBigDecimal("expense");
            BigDecimal balance = income.subtract(expense);
            Integer incomeCount = rs.getInt("incomeCount");
            Integer expenseCount = rs.getInt("expenseCount");

            return new MonthlyStatsResponse(month, income, expense, balance, incomeCount, expenseCount);
        }, username, months);
    }

    /**
     * 获取支付方式统计
     */
    private List<PaymentMethodStatsResponse> getPaymentMethodStats(String username) {
        // 先获取总金额用于计算百分比
        String totalSql = "SELECT COALESCE(SUM(amount), 0) FROM bill WHERE user_username = ?";
        BigDecimal total = jdbcTemplate.queryForObject(totalSql, BigDecimal.class, username);

        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return List.of();
        }

        String sql = "SELECT " +
                "method, " +
                "COALESCE(SUM(amount), 0) as totalAmount, " +
                "COUNT(*) as count " +
                "FROM bill " +
                "WHERE user_username = ? " +
                "GROUP BY method " +
                "ORDER BY totalAmount DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String method = rs.getString("method");
            BigDecimal totalAmount = rs.getBigDecimal("totalAmount");
            Integer count = rs.getInt("count");
            BigDecimal percentage = totalAmount.multiply(BigDecimal.valueOf(100))
                    .divide(total, 2, RoundingMode.HALF_UP);

            return new PaymentMethodStatsResponse(method, totalAmount, count, percentage);
        }, username);
    }

    /**
     * 获取分类统计（仅支出）
     */
    private List<CategoryStatsResponse> getCategoryStats(String username) {
        // 先获取总支出金额用于计算百分比
        String totalSql = "SELECT COALESCE(SUM(amount), 0) FROM bill WHERE user_username = ? AND type = 'out'";
        BigDecimal total = jdbcTemplate.queryForObject(totalSql, BigDecimal.class, username);

        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return List.of();
        }

        String sql = "SELECT " +
                "category, " +
                "COALESCE(SUM(amount), 0) as totalAmount, " +
                "COUNT(*) as count " +
                "FROM bill " +
                "WHERE user_username = ? AND type = 'out' " +
                "GROUP BY category " +
                "ORDER BY totalAmount DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String category = rs.getString("category");
            BigDecimal totalAmount = rs.getBigDecimal("totalAmount");
            Integer count = rs.getInt("count");
            BigDecimal percentage = totalAmount.multiply(BigDecimal.valueOf(100))
                    .divide(total, 2, RoundingMode.HALF_UP);

            return new CategoryStatsResponse(category, totalAmount, count, percentage);
        }, username);
    }
}

