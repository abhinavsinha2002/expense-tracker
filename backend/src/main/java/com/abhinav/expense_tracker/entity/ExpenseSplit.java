package com.abhinav.expense_tracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name="expense_splits")
public class ExpenseSplit {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String memberIdentifier;
    @Column(nullable=false,precision = 12,scale=2) private BigDecimal amount;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="expense_id") private Expense expense;
    public ExpenseSplit(){
        
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMemberIdentifier() {
        return memberIdentifier;
    }
    public void setMemberIdentifier(String memberIdentifier) {
        this.memberIdentifier = memberIdentifier;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public Expense getExpense() {
        return expense;
    }
    public void setExpense(Expense expense) {
        this.expense = expense;
    }
    
}
