package com.abhinav.expense_tracker.entity;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name="expense_groups")
public class ExpenseGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @ManyToOne private User owner;
    @ManyToMany
    @JoinTable(name="group_members",
        joinColumns = @JoinColumn(name="group_id"),
        inverseJoinColumns = @JoinColumn(name="user_id")
    )
    private Set<User> members=new HashSet<>();
    @OneToMany(mappedBy="group",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Expense> expenses=new ArrayList<>();

    @Column(unique = true)
    private String inviteToken;

    private String currency;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonIgnore // Prevent infinite loops! (See explanation below)
    private List<GroupActivity> activities;

    public String getInviteToken() {
        return inviteToken;
    }
    public void setInviteToken(String inviteToken) {
        this.inviteToken = inviteToken;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public List<GroupActivity> getActivities() {
        return activities;
    }
    public void setActivities(List<GroupActivity> activities) {
        this.activities = activities;
    }
    @PrePersist
    public void generateToken(){
        if(this.inviteToken==null){
            this.inviteToken = java.util.UUID.randomUUID().toString();
        }
    }
    public ExpenseGroup(){}
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
    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }
    public Set<User> getMembers() {
        return members;
    }
    public void setMembers(Set<User> members) {
        this.members = members;
    }
    public List<Expense> getExpenses() {
        return expenses;
    }
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
    
}
