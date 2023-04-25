package com.omni.wallet.data;

import android.content.Context;

import com.omni.wallet.common.NetworkType;

import java.util.List;

public class NodeActions {
    public static void clearNodeList(Context context) {
        NodeDao nodeDao = new NodeDao(context);
        nodeDao.clearTable();
    }

    public static void addNodeToList(Context context, Node node) {
        String alias = node.getAlias();
        String spayUrl = node.getSpayUrl();
        String nodeUrl = node.getNodeUrl();
        NetworkType networkType = node.getNetworkType();
        NodeDao nodeDao = new NodeDao(context);
        nodeDao.insertNode(alias, spayUrl, nodeUrl, networkType);
    }

    public static void addMultipleNodes (Context context,List<Node> nodes){
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            addNodeToList(context,node);
        }
    }

    public static List<Node> getNodeList(Context context, NetworkType networkType){
        NodeDao nodeDao = new NodeDao(context);
        List<Node> nodeList = nodeDao.getNodeListByNetType(networkType);
        return nodeList;
    }

    public static void clearAndAddMultipleNodes(Context context,List<Node> nodes){
        clearNodeList(context);
        addMultipleNodes(context,nodes);
    }


}
