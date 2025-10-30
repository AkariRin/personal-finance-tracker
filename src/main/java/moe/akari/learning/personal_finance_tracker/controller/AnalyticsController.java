package moe.akari.learning.personal_finance_tracker.controller;

import moe.akari.learning.personal_finance_tracker.dto.AnalyticsResponse;
import moe.akari.learning.personal_finance_tracker.dto.ApiResponse;
import moe.akari.learning.personal_finance_tracker.service.AnalyticsService;
import moe.akari.learning.personal_finance_tracker.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 统计分析控制器
 * 提供账单统计分析接口
 */
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AuthService authService;

    public AnalyticsController(AnalyticsService analyticsService, AuthService authService) {
        this.analyticsService = analyticsService;
        this.authService = authService;
    }

    /**
     * 获取统计分析数据
     *
     * @param authorization Authorization请求头
     * @param months 统计最近几个月的数据（可选，默认6个月）
     * @return 统计分析数据
     */
    @GetMapping
    public ResponseEntity<ApiResponse<AnalyticsResponse>> getAnalytics(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(required = false, defaultValue = "6") Integer months) {

        try {
            // 从Token获取用户名
            String token = authorization.substring(7);
            String username = authService.getUsernameFromToken(token);

            AnalyticsResponse analytics = analyticsService.getAnalytics(username, months);
            return ResponseEntity.ok(ApiResponse.success("获取统计数据成功", analytics));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

