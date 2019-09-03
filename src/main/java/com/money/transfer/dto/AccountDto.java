package com.money.transfer.dto;


public class AccountDto {

    private String id;
    private long balance;
    
    public AccountDto() {}
   
    public AccountDto(String id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public AccountDto(long balance) {
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public long getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                ", balance='" + balance + '\'' +
                '}';
    }
}
