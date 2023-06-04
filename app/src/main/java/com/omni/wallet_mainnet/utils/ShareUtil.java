package com.omni.wallet_mainnet.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 汉: 分享的工具类
 * En: ShareUtil
 * author: guoyalei
 * date: 2023/1/11
 */
public class ShareUtil {
    private static final String TAG = ShareUtil.class.getSimpleName();

    // TODO: 2023/1/11 待完善
    public static Intent getTwitterIntent(Context ctx, String shareText) {
        Intent shareIntent;
//        if (checkInstalled(ctx, "com.twitter.android")) {
//            LogUtils.e(TAG, "========11111=======");
//            shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setClassName("com.twitter.android",
//                    "com.twitter.android.PostActivity");
//            shareIntent.setType("text/*");
//            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
//            return shareIntent;
//        } else {
            String tweetUrl = "https://twitter.com/intent/tweet?text=" + shareText;
            Uri uri = Uri.parse(tweetUrl);
            shareIntent = new Intent(Intent.ACTION_VIEW, uri);
            return shareIntent;
//        }
    }

    public static boolean checkInstalled(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        try {
            context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (Exception x) {
            return false;
        }
        return true;
    }
}
