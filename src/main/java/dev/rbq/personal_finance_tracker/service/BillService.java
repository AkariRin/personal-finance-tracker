package dev.rbq.personal_finance_tracker.service;

import dev.rbq.personal_finance_tracker.dto.BillResponse;
import dev.rbq.personal_finance_tracker.dto.CreateBillRequest;
import dev.rbq.personal_finance_tracker.entity.Bill;
import dev.rbq.personal_finance_tracker.repository.BillRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 账单服务
 */
@Service
public class BillService {

    private final BillRepository billRepository;
    private final JdbcTemplate jdbcTemplate;

    public BillService(BillRepository billRepository, JdbcTemplate jdbcTemplate) {
        this.billRepository = billRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 创建账单 - 完全使用原生 SQL，不涉及 Spring Data JDBC
     *
     * @param username 用户名
     * @param request 创建账单请求
     * @return 账单响应
     */
    @Transactional
    public BillResponse createBill(String username, CreateBillRequest request) {
        // 生成UUID
        String uuid = UUID.randomUUID().toString();

        // 解析时间，如果未提供则使用当前时间
        LocalDateTime billTime;
        if (request.getTime() != null && !request.getTime().isEmpty()) {
            try {
                // 尝试解析 ISO 8601 格式，包括带 'T' 的格式
                billTime = LocalDateTime.parse(request.getTime(),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Time format error, please use ISO 8601 format (e.g.: 2024-01-01T12:00:00)");
            }
        } else {
            billTime = LocalDateTime.now();
        }

        // 完全使用原生 SQL 插入，不创建 Bill 实体
        String sql = "INSERT INTO bill (uuid, amount, type, method, counterparty, time, category, note, user_username) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            int rows = jdbcTemplate.update(sql,
                uuid,
                request.getAmount(),
                request.getType(),
                request.getMethod(),
                request.getCounterparty(),
                billTime,
                request.getCategory(),
                request.getNote(),
                username
            );

            if (rows != 1) {
                throw new RuntimeException("Failed to insert bill, affected rows: " + rows);
            }

            // 直接构造响应对象返回，不创建 Bill 实体
            return new BillResponse(
                    uuid,
                    request.getType(),
                    request.getAmount(),
                    request.getMethod(),
                    request.getCounterparty(),
                    billTime,
                    request.getCategory(),
                    request.getNote()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bill: " + e.getMessage(), e);
        }
    }

    /**
     * 获取用户的所有账单
     *
     * @param username 用户名
     * @return 账单列表
     */
    public List<BillResponse> getUserBills(String username) {
        List<Bill> bills = billRepository.findByUserUsernameOrderByTimeDesc(username);
        return bills.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据类型获取用户的账单
     *
     * @param username 用户名
     * @param type 账单类型（in/out）
     * @return 账单列表
     */
    public List<BillResponse> getUserBillsByType(String username, String type) {
        if (!type.equals("in") && !type.equals("out")) {
            throw new RuntimeException("Bill type must be in or out");
        }
        List<Bill> bills = billRepository.findByUserUsernameAndTypeOrderByTimeDesc(username, type);
        return bills.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 删除用户的所有账单（当用户删除账号时调用）
     *
     * @param username 用户名
     */
    @Transactional
    public void deleteUserBills(String username) {
        billRepository.deleteByUserUsername(username);
    }

    /**
     * 获取用户最近的N条账单
     *
     * @param username 用户名
     * @param limit 数量限制
     * @return 账单列表
     */
    public List<BillResponse> getRecentBills(String username, int limit) {
        List<Bill> bills = billRepository.findByUserUsernameOrderByTimeDesc(username);
        return bills.stream()
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 计算今日收入
     *
     * @param username 用户名
     * @return 今日收入总额
     */
    public java.math.BigDecimal getTodayIncome(String username) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<Bill> bills = billRepository.findByUserUsernameAndTypeAndTimeBetween(
                username, "in", startOfDay, endOfDay);

        return bills.stream()
                .map(Bill::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * 计算今日支出
     *
     * @param username 用户名
     * @return 今日支出总额
     */
    public java.math.BigDecimal getTodayExpense(String username) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<Bill> bills = billRepository.findByUserUsernameAndTypeAndTimeBetween(
                username, "out", startOfDay, endOfDay);

        return bills.stream()
                .map(Bill::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * 将Bill实体转换为BillResponse
     *
     * @param bill 账单实体
     * @return 账单响应DTO
     */
    private BillResponse convertToResponse(Bill bill) {
        return new BillResponse(
                bill.getUuid(),
                bill.getType(),
                bill.getAmount(),
                bill.getMethod(),
                bill.getCounterparty(),
                bill.getTime(),
                bill.getCategory(),
                bill.getNote()
        );
    }
}

