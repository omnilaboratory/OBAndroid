package com.omni.wallet.thirdsupport.umeng.share.callback;


import com.omni.wallet.thirdsupport.umeng.share.common.Target;

/**
 * 分享的回调
 */

public interface ShareCallback {

    void onStartShare(Target target);

    void onShareResult(Target target);

    void onShareCancel(Target target);

    void onShareError(Target target, Throwable throwable);

}
