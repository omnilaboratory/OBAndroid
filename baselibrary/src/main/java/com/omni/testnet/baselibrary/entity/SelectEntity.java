package com.omni.testnet.baselibrary.entity;

/**
 * 选择控件对应的通用实体
 */

public class SelectEntity {
    private String id;
    private String title;
    private boolean selected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
