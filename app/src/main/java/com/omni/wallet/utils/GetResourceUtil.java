package com.omni.wallet.utils;

import android.content.Context;

import com.omni.wallet.R;

public class GetResourceUtil {
    static public int getTokenImageId(Context context,String type){
        switch (type){
            default:
                return R.mipmap.icon_usdt_logo_small;
            case "Doallar":
                return R.mipmap.icon_usdt_logo_small;
            case "BTC":
                return R.mipmap.icon_btc_logo_small;
        }
    }

    static public int getColorId(Context context,int id){
        return context.getResources().getColor(id);
    }

    static public String getStringText(Context context,int id){
        return  context.getResources().getString(id);
    }
}
