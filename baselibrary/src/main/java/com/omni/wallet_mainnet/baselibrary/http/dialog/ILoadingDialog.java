package com.omni.wallet_mainnet.baselibrary.http.dialog;

import android.content.DialogInterface;

/**
 * 网络加载的Dialog
 */

public interface ILoadingDialog {
    void setOnDismissListener(DialogInterface.OnDismissListener listener);
}