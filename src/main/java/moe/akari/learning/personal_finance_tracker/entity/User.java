package moe.akari.learning.personal_finance_tracker.entity;

import java.math.BigDecimal;

/**
 * 用户实体
 * username作为主键（唯一标识）
 * name字段作为用户显示名称
 * 密码字段存储bcrypt哈希值（60个字符）
 * 前端发送明文密码，后端负责bcrypt加密和验证
 */
public class User {

    private String username;
    private String name;
    private String password;
    private BigDecimal balance;

    public User() {
    }

    public User(String username, String name, String password, BigDecimal balance) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

