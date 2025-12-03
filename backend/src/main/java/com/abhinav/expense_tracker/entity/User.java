package com.abhinav.expense_tracker.entity;

import java.util.*;
import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String username;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String password;
    private boolean enabled = false;

    @OneToMany(mappedBy = "owner",cascade = CascadeType.ALL)
    private List<Expense> expenses=new ArrayList<>();

    @ManyToMany(mappedBy="members")
    private Set<ExpenseGroup> groups = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public Set<ExpenseGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<ExpenseGroup> groups) {
        this.groups = groups;
    };
    
}
