package moe.akari.learning.personal_finance_tracker.service;

import moe.akari.learning.personal_finance_tracker.dto.AuthResponse;
import moe.akari.learning.personal_finance_tracker.dto.LoginRequest;
import moe.akari.learning.personal_finance_tracker.dto.RegisterRequest;
import moe.akari.learning.personal_finance_tracker.entity.User;
import moe.akari.learning.personal_finance_tracker.repository.UserRepository;
import moe.akari.learning.personal_finance_tracker.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 认证服务
 * 前端发送明文密码，后端负责bcrypt加密和验证
 * username作为唯一标识和主键
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 用户注册
     * 接收明文密码，使用bcrypt加密后存储
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsById(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户，使用bcrypt加密密码
        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        // 使用bcrypt加密明文密码（10轮）
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBalance(BigDecimal.ZERO);

        userRepository.save(user);

        // 生成Token（只使用username）
        String token = jwtUtil.generateToken(user.getUsername());

        return new AuthResponse(token, user.getUsername(), user.getName(), user.getBalance());
    }

    /**
     * 用户登录
     * 接收明文密码，使用bcrypt验证
     */
    public AuthResponse login(LoginRequest request) {
        // 查找用户
        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 使用bcrypt的matches方法验证明文密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成Token（只使用username）
        String token = jwtUtil.generateToken(user.getUsername());

        return new AuthResponse(token, user.getUsername(), user.getName(), user.getBalance());
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * 从Token获取用户名
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }
}

