package com.omni.wallet.listItems;

import java.util.List;

public class NodeGroup {
    public String groupName;
    public List<Node> nodeList;

    public NodeGroup(List<Node> nodeList,String groupName ){
        this.nodeList = nodeList;
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }
}
