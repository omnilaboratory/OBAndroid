package com.omni.testnet.utils;

import com.omni.testnet.view.dialog.LoadingDialog;

public class PublicUtils {
    public static void closeLoading(LoadingDialog loadingDialog){
        if(loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    public static void showLoading(LoadingDialog loadingDialog){
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
        }
    }
}
