package com.abhinav.expense_tracker.dto;

import java.util.Set;

public class GroupCreateDto {
    private String name;
    private String currency;

    public GroupCreateDto(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}
