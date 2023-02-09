package com.omni.testnet.baselibrary.entity;

import java.util.List;

/**
 * 自定义筛选菜单父类
 */

public class BaseFilterMenuEntity {
    private String title;
    private String id;
    private String showTitle;
    private List<BaseFilterMenuEntity> childList;
    private boolean isSelected;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public List<BaseFilterMenuEntity> getChildList() {
        return childList;
    }

    public void setChildList(List<BaseFilterMenuEntity> childList) {
        this.childList = childList;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
