package com.example.styleconnect;

public class AccountId {
    private String account_id;

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    private static AccountId instance = null;

    public static synchronized AccountId getInstance() {
        if(null == instance) {
            instance = new AccountId();
        }
        return instance;
    }
}
