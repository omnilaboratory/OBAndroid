package com.omni.wallet_mainnet.listItems;

public class Account {
    String avatarString;
    String accountAddress;
    double accountBalance;
    String accountName;
    String avatarSrc;
    int avatarColor;

    public Account(String accountAddress,double accountBalance,String accountName){
        this.accountAddress = accountAddress;
        this.accountBalance = accountBalance;
        this.accountName = accountName;
        this.avatarString = accountName.substring(0,1);
    }
    public Account(String accountAddress,double accountBalance,String accountName,String avatarSrc){
        this.accountAddress = accountAddress;
        this.accountBalance = accountBalance;
        this.accountName = accountName;
        this.avatarString = accountName.substring(0,1);
        this.avatarSrc = avatarSrc;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAvatarSrc() {
        return avatarSrc;
    }


    public String getAvatarString() {
        return avatarString;
    }

    public int getAvatarColor() {
        return avatarColor;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
        this.avatarString = accountName.substring(0,1);
    }

    public void setAvatarSrc(String avatarSrc) {
        this.avatarSrc = avatarSrc;
    }

    public void setAvatarColor(int avatarColor) {
        this.avatarColor = avatarColor;
    }
}
