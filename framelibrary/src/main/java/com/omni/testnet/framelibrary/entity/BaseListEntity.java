package com.omni.testnet.framelibrary.entity;

/**
 * RecyclerView的列表的实体基类
 */

public class BaseListEntity {
    protected int itemType;
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_ITEM_NO_DATA = 1;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
