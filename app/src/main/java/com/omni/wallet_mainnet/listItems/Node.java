package com.omni.wallet_mainnet.listItems;

import java.util.List;

public class Node {
    private String nodeName;
    private List<String> tokenList;
    private double valueSum;

    public Node(String nodeName,List tokenList,double valueSum){
        this.nodeName = nodeName;
        this.tokenList = tokenList;
        this.valueSum = valueSum;
    }

    public String getNodeName() {
        return nodeName;
    }

    public double getValueSum() {
        return valueSum;
    }

    public List<String> getTokenList() {
        return tokenList;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setTokenList(List<String> tokenList) {
        this.tokenList = tokenList;
    }

    public void setValueSum(double valueSum) {
        this.valueSum = valueSum;
    }
}
