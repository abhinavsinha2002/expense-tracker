package com.abhinav.expense_tracker.dto;

import java.util.Set;

public class GroupCreateDto {
    private String name;
    private Set<String> members;

    public GroupCreateDto(){}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<String> getMembers() {
        return members;
    }
    public void setMembers(Set<String> members) {
        this.members = members;
    }
}
