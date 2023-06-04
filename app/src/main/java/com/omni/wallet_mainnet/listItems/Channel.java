package com.omni.wallet_mainnet.listItems;

public class Channel {
    int state = 0;
    String channelName;
    String tokenType;
    String pubkey;
    double localAmount;
    double remoteAmount;
    double totalAmount;

    public Channel(int state,String channelName,String tokenType,String pubkey,double localAmount,double remoteAmount){
        this.state = state;
        this.channelName = channelName;
        this.tokenType = tokenType;
        this.pubkey = pubkey;
        this.localAmount = localAmount;
        this.remoteAmount = remoteAmount;
        this.totalAmount = localAmount + remoteAmount;
    }

    public int getState(){
        return state;
    }

    public double getLocalAmount(){
        return localAmount;
    }
    public double getRemoteAmount(){
        return remoteAmount;
    }

    public double getTotalAmount(){
        return totalAmount;
    }

    public String getChannelName(){
        return channelName;
    }

    public String getTokenType(){
        return tokenType;
    }

    public String getPubkey(){
        return pubkey;
    }

    public void setState(int state){
        this.state = state;
    }

    public void  setLocalAmount(double localAmount){
        this.localAmount = localAmount;
        double remoteAmount = this.getRemoteAmount();
        this.totalAmount = localAmount + remoteAmount;
    }
    public void  setRemoteAmountAmount(double remoteAmount){
        this.remoteAmount = remoteAmount;
        double localAmount = this.getLocalAmount();
        this.totalAmount = localAmount + remoteAmount;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
