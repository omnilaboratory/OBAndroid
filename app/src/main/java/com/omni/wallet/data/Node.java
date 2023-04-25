package com.omni.wallet.data;

import com.omni.wallet.common.NetworkType;

public class Node {
    private String alias;
    private String spayUrl;
    private String nodeUrl;
    private NetworkType networkType;

    public Node(String alias, String spayUrl, String nodeUrl, NetworkType networkType) {
        this.alias = alias;
        this.spayUrl = spayUrl;
        this.nodeUrl = nodeUrl;
        this.networkType = networkType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSpayUrl() {
        return spayUrl;
    }

    public void setSpayUrl(String spayUrl) {
        this.spayUrl = spayUrl;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }
}
