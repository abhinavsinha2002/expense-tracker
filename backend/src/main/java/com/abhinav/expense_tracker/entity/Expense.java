package com.abhinav.expense_tracker.entity;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name="expenses")
public class Expense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private long id;
    @Column(nullable = false) private String description;
    @Column(nullable=false,precision = 12,scale=2) private BigDecimal amount;
    @Column(nullable=false) private LocalDate date = LocalDate.now();
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE}) private Category category;
    @ManyToOne private User owner;
    @ManyToOne private ExpenseGroup group;
    @OneToMany(mappedBy = "expense",cascade = CascadeType.ALL, orphanRemoval = true) private List<ExpenseSplit> splits=new ArrayList<>();

    public Expense(){

    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public ExpenseGroup getGroup() {
        return group;
    }

    public void setGroup(ExpenseGroup group) {
        this.group = group;
    }

    public List<ExpenseSplit> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseSplit> splits) {
        this.splits = splits;
    }}
