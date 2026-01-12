package com.abhinav.expense_tracker.dto;

public class SettleUpTransactionDto {
    private String fromUser;
    private String toUser;
    private double amount;

    public SettleUpTransactionDto(){}
    public SettleUpTransactionDto(String from,String to,double amount){
        this.fromUser=from;
        this.toUser=to;
        this.amount=amount;
    }
    public String getFromUser() {
        return fromUser;
    }
    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }
    public String getToUser() {
        return toUser;
    }
    public void setToUser(String toUser) {
        this.toUser = toUser;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

}
