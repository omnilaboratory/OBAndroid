package com.omni.wallet.thirdsupport.umeng.share.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.omni.wallet.thirdsupport.umeng.share.bean.ShareBuilder;


public interface IShare {

    void share(ShareBuilder builder);

    void release(Activity activity);

    void onActivityResult(Context context, int requestCode, int resultCode, Intent data);

    boolean isRequestShare();
}
