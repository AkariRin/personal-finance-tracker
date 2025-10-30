package moe.akari.learning.personal_finance_tracker.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单实体
 */
public class Bill {

    private String uuid;
    private BigDecimal amount;
    private String type; // in, out
    private String method; // cash, wx, alipay, card, btc, paypal, apple
    private String counterparty;
    private LocalDateTime time;
    private String category;
    private String note;
    private String userUsername;

    public Bill() {
    }

    public Bill(String uuid, BigDecimal amount, String type, String method,
                String counterparty, LocalDateTime time, String category,
                String note, String userUsername) {
        this.uuid = uuid;
        this.amount = amount;
        this.type = type;
        this.method = method;
        this.counterparty = counterparty;
        this.time = time;
        this.category = category;
        this.note = note;
        this.userUsername = userUsername;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @SuppressWarnings("unused")
    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }
}

