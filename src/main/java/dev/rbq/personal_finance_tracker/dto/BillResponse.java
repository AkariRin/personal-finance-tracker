package dev.rbq.personal_finance_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单响应DTO
 */
public class BillResponse {

    private String uuid;
    private String type;
    private BigDecimal amount;
    private String method;
    private String counterparty;
    private LocalDateTime time;
    private String category;
    private String note;

    public BillResponse() {
    }

    public BillResponse(String uuid, String type, BigDecimal amount, String method,
                        String counterparty, LocalDateTime time, String category, String note) {
        this.uuid = uuid;
        this.type = type;
        this.amount = amount;
        this.method = method;
        this.counterparty = counterparty;
        this.time = time;
        this.category = category;
        this.note = note;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
}

