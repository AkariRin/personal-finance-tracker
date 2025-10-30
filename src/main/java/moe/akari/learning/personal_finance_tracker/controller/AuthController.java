package moe.akari.learning.personal_finance_tracker.controller;

import jakarta.validation.Valid;
import moe.akari.learning.personal_finance_tracker.dto.ApiResponse;
import moe.akari.learning.personal_finance_tracker.dto.AuthResponse;
import moe.akari.learning.personal_finance_tracker.dto.LoginRequest;
import moe.akari.learning.personal_finance_tracker.dto.RegisterRequest;
import moe.akari.learning.personal_finance_tracker.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @param bindingResult 验证结果
     * @return 注册响应
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Invalid request parameters";
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(errorMessage));
        }

        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success("User registration successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @param bindingResult 验证结果
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Invalid request parameters";
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(errorMessage));
        }

        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("User login successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 验证Token
     *
     * @param authorization Authorization请求头
     * @return 验证结果
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.ok(ApiResponse.success(false));
        }

        String token = authorization.substring(7);
        boolean isValid = authService.validateToken(token);

        return ResponseEntity.ok(ApiResponse.success(isValid));
    }
}

