package com.omni.wallet_mainnet.listItems;

public class Friend {
    String friendName;
    String address;

    public Friend(String friendName,String address){
        this.friendName = friendName;
        this.address = address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFriendName(String name) {
        this.friendName = name;
    }

    public String getAddress() {
        return address;
    }

    public String getFriendName() {
        return friendName;
    }
}
