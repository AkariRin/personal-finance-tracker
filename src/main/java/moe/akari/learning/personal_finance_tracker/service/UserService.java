package moe.akari.learning.personal_finance_tracker.service;

import moe.akari.learning.personal_finance_tracker.dto.UpdateNameRequest;
import moe.akari.learning.personal_finance_tracker.dto.UpdatePasswordRequest;
import moe.akari.learning.personal_finance_tracker.entity.User;
import moe.akari.learning.personal_finance_tracker.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务
 * 处理用户信息管理相关功能
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BillService billService;

    public UserService(UserRepository userRepository, BillService billService) {
        this.userRepository = userRepository;
        this.billService = billService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 更新用户名称（name字段）
     *
     * @param username 用户名（主键）
     * @param request 更新名称请求
     * @return 更新后的用户名称
     */
    @Transactional
    public String updateName(String username, UpdateNameRequest request) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setName(request.getName());
        userRepository.save(user);

        return user.getName();
    }

    /**
     * 更新用户密码
     *
     * @param username 用户名（主键）
     * @param request 更新密码请求
     */
    @Transactional
    public void updatePassword(String username, UpdatePasswordRequest request) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证当前密码
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("当前密码错误");
        }

        // 使用bcrypt加密新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 删除用户账号
     *
     * @param username 用户名（主键）
     * @param confirmUsername 确认的用户名
     */
    @Transactional
    public void deleteAccount(String username, String confirmUsername) {
        // 验证用户名确认
        if (!username.equals(confirmUsername)) {
            throw new RuntimeException("用户名确认不匹配");
        }

        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 先删除用户的所有账单
        billService.deleteUserBills(username);

        // 再删除用户
        userRepository.delete(user);
    }
}

