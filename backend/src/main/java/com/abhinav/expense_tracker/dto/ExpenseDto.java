package com.abhinav.expense_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ExpenseDto {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private String category;
    private Long groupId;
    private List<ExpenseSplitDto> splits;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<ExpenseSplitDto> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseSplitDto> splits) {
        this.splits = splits;
    }

    public ExpenseDto(){

    }
    
}
