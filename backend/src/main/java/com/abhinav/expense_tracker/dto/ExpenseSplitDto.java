package com.abhinav.expense_tracker.dto;

import java.math.BigDecimal;

public class ExpenseSplitDto {
    private String member;
    private BigDecimal amount;
    public String getMember() {
        return member;
    }
    public void setMember(String member) {
        this.member = member;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public ExpenseSplitDto(){}
}
