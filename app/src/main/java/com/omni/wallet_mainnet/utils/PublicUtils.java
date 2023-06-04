package com.omni.wallet_mainnet.utils;

import com.omni.wallet_mainnet.view.dialog.LoadingDialog;
import com.omni.wallet_mainnet.view.dialog.LoginLoadingDialog;

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
    public static void closeLoading(LoginLoadingDialog loadingDialog){
        if(loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    public static void showLoading(LoginLoadingDialog loadingDialog){
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
        }
    }
}
