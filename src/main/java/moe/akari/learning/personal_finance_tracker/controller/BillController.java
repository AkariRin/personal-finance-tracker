package moe.akari.learning.personal_finance_tracker.controller;

import jakarta.validation.Valid;
import moe.akari.learning.personal_finance_tracker.dto.ApiResponse;
import moe.akari.learning.personal_finance_tracker.dto.BillResponse;
import moe.akari.learning.personal_finance_tracker.dto.CreateBillRequest;
import moe.akari.learning.personal_finance_tracker.service.AuthService;
import moe.akari.learning.personal_finance_tracker.service.BillService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账单控制器
 * 处理账单相关接口，需要JWT认证
 */
@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;
    private final AuthService authService;

    public BillController(BillService billService, AuthService authService) {
        this.billService = billService;
        this.authService = authService;
    }

    /**
     * 创建新账单
     *
     * @param authorization Authorization请求头
     * @param request 创建账单请求
     * @param bindingResult 验证结果
     * @return 创建的账单信息
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BillResponse>> createBill(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateBillRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Invalid request parameters";
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(errorMessage));
        }

        try {
            // 从Token获取用户名
            String token = authorization.substring(7);
            String username = authService.getUsernameFromToken(token);

            BillResponse billResponse = billService.createBill(username, request);
            return ResponseEntity.ok(ApiResponse.success("Bill created successfully", billResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取当前用户的所有账单
     *
     * @param authorization Authorization请求头
     * @return 账单列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BillResponse>>> getUserBills(
            @RequestHeader("Authorization") String authorization) {

        try {
            // 从Token获取用户名
            String token = authorization.substring(7);
            String username = authService.getUsernameFromToken(token);

            List<BillResponse> bills = billService.getUserBills(username);
            return ResponseEntity.ok(ApiResponse.success("Successfully retrieved the bill list", bills));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据类型获取当前用户的账单
     *
     * @param authorization Authorization请求头
     * @param type 账单类型（in/out）
     * @return 账单列表
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<BillResponse>>> getUserBillsByType(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String type) {

        try {
            // 从Token获取用户名
            String token = authorization.substring(7);
            String username = authService.getUsernameFromToken(token);

            List<BillResponse> bills = billService.getUserBillsByType(username, type);
            return ResponseEntity.ok(ApiResponse.success("Successfully retrieved the bill list", bills));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

