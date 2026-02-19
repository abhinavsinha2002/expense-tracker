package com.abhinav.expense_tracker.dto;

import java.util.Set;

public class GroupResponseDto {
    private Long id;
    private String name;
    private UserDto owner;
    private String currency;
    private Set<UserDto> members;

    public GroupResponseDto(Long id,String name,UserDto owner, Set<UserDto> members,String currency){
        this.id=id;
        this.name=name;
        this.owner=owner;
        this.members=members;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public Set<UserDto> getMembers() {
        return members;
    }

    public void setMembers(Set<UserDto> members) {
        this.members = members;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
