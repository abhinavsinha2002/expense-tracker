package com.abhinav.expense_tracker.dto;

import java.util.List;
import java.util.Set;

public class GroupResponseDto {
    private Long id;
    private String name;
    private String description;
    private UserDto owner;
    private String currency;
    private Set<UserDto> members;
    private String inviteToken;

    public GroupResponseDto(Long id,String name,UserDto owner, Set<UserDto> members,String currency,String inviteToken){
        this.id=id;
        this.name=name;
        this.owner=owner;
        this.members=members;
        this.currency = currency;
        this.inviteToken = inviteToken;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInviteToken() {
        return inviteToken;
    }

    public void setInviteToken(String inviteToken) {
        this.inviteToken = inviteToken;
    }
}
