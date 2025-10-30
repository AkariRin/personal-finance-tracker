package moe.akari.learning.personal_finance_tracker.dto;

import java.math.BigDecimal;

/**
 * 认证响应DTO
 */
public class AuthResponse {

    private String token;
    private String username;
    private String name;
    private BigDecimal balance;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String name, BigDecimal balance) {
        this.token = token;
        this.username = username;
        this.name = name;
        this.balance = balance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

