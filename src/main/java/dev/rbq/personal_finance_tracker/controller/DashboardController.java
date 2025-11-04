package dev.rbq.personal_finance_tracker.controller;

import dev.rbq.personal_finance_tracker.dto.ApiResponse;
import dev.rbq.personal_finance_tracker.dto.BillResponse;
import dev.rbq.personal_finance_tracker.dto.DashboardResponse;
import dev.rbq.personal_finance_tracker.entity.User;
import dev.rbq.personal_finance_tracker.repository.UserRepository;
import dev.rbq.personal_finance_tracker.service.AuthService;
import dev.rbq.personal_finance_tracker.service.BillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 仪表板控制器
 * 提供仪表板页面所需的统计数据
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final BillService billService;
    private final AuthService authService;
    private final UserRepository userRepository;

    public DashboardController(BillService billService, AuthService authService, UserRepository userRepository) {
        this.billService = billService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * 获取仪表板数据
     *
     * @param authorization Authorization请求头
     * @return 仪表板数据
     */
    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardData(
            @RequestHeader("Authorization") String authorization) {

        try {
            // 从Token获取用户名
            String token = authorization.substring(7);
            String username = authService.getUsernameFromToken(token);

            // 获取用户信息（总资产）
            Optional<User> userOpt = userRepository.findById(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("User does not exist"));
            }
            User user = userOpt.get();
            BigDecimal totalAssets = user.getBalance();

            // 获取今日收入
            BigDecimal todayIncome = billService.getTodayIncome(username);

            // 获取今日支出
            BigDecimal todayExpense = billService.getTodayExpense(username);

            // 获取最近5条账单
            List<BillResponse> recentBills = billService.getRecentBills(username, 5);

            // 构造响应
            DashboardResponse response = new DashboardResponse(
                    totalAssets,
                    todayIncome,
                    todayExpense,
                    recentBills
            );

            return ResponseEntity.ok(ApiResponse.success("Successfully retrieved dashboard data", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

