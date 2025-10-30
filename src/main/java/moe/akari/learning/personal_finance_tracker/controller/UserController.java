package moe.akari.learning.personal_finance_tracker.controller;

import jakarta.validation.Valid;
import moe.akari.learning.personal_finance_tracker.dto.ApiResponse;
import moe.akari.learning.personal_finance_tracker.dto.DeleteAccountRequest;
import moe.akari.learning.personal_finance_tracker.dto.UpdateNameRequest;
import moe.akari.learning.personal_finance_tracker.dto.UpdatePasswordRequest;
import moe.akari.learning.personal_finance_tracker.service.AuthService;
import moe.akari.learning.personal_finance_tracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 处理用户信息管理相关接口，需要JWT认证
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * 更新用户名称（name字段）
     *
     * @param authorization Authorization请求头
     * @param request 更新名称请求
     * @param bindingResult 验证结果
     * @return 更新后的名称
     */
    @PutMapping("/name")
    public ResponseEntity<ApiResponse<String>> updateName(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UpdateNameRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "请求参数错误";
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(errorMessage));
        }

        try {
            // 从Token获取用户名
            String token = authorization.substring(7);
            String username = authService.getUsernameFromToken(token);

            String newName = userService.updateName(username, request);
            return ResponseEntity.ok(ApiResponse.success("名称更新成功", newName));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新用户密码
     *
     * @param authorization Authorization请求头
     * @param request 更新密码请求
     * @param bindingResult 验证结果
     * @return 操作结果
     */
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UpdatePasswordRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "请求参数错误";
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(errorMessage));
        }

        try {
            // 从Token获取用户名
            String token = authorization.substring(7);
            String username = authService.getUsernameFromToken(token);

            userService.updatePassword(username, request);
            return ResponseEntity.ok(ApiResponse.success("密码更新成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除用户账号
     *
     * @param authorization Authorization请求头
     * @param request 删除账号请求
     * @param bindingResult 验证结果
     * @return 操作结果
     */
    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody DeleteAccountRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "请求参数错误";
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(errorMessage));
        }

        try {
            // 从Token获取用户名
            String token = authorization.substring(7);
            String username = authService.getUsernameFromToken(token);

            userService.deleteAccount(username, request.getUsername());
            return ResponseEntity.ok(ApiResponse.success("账号删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

