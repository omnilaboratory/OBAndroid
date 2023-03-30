package com.omni.wallet.data;

public class AssetsItem {
    private String property_id;
    private int has_balance;
    private String token_name;
    static final int ASSET_USING = 1;
    static final int ASSET_UNUSED = 0;

    AssetsItem(String property_id, String token_name, int has_balance){
        this.property_id = property_id;
        this.token_name = token_name;
        this.has_balance = has_balance;
    }

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }

    public int getHas_balance() {
        return has_balance;
    }

    public void setHas_balance(int has_balance) {
        this.has_balance = has_balance;
    }

    public String getToken_name() {
        return token_name;
    }

    public void setToken_name(String token_name) {
        this.token_name = token_name;
    }
}
