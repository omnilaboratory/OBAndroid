package com.omni.testnet.listItems;

import java.util.List;

public class FriendGroup {
    String groupName;
    List<Friend> groupFriend;

    public FriendGroup(String groupName,List groupFriend){
        this.groupFriend = groupFriend;
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Friend> getGroupFriend() {
        return groupFriend;
    }
}
