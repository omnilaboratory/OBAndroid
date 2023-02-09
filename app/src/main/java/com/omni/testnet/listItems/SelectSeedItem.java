package com.omni.testnet.listItems;

public class SelectSeedItem {
    private String seed;
    private boolean selected;
    private int selectIndex;

    public SelectSeedItem(String seed,boolean selected,int selectIndex){
        this.seed = seed;
        this.selected = selected;
        this.selectIndex = selectIndex;
    }

    public String getSeed(){
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
