package com.omni.wallet.utils;

import com.omni.wallet.view.dialog.LoadingDialog;
import com.omni.wallet.view.dialog.LoginLoadingDialog;

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
