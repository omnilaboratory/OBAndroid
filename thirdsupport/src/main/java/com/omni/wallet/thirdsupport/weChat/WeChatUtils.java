package com.omni.wallet.thirdsupport.weChat;

import android.content.Context;

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.omni.wallet.baselibrary.common.Constants;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.thirdsupport.common.WeChatConfig;

/**
 * 微信相关工具类
 */

public class WeChatUtils {
    private static final String TAG = WeChatUtils.class.getSimpleName();

    /**
     * 启动微信小程序
     */
    public void launchWeChatApplet(Context context, String pagePath) {
        if (StringUtils.isEmpty(pagePath)) {
            LogUtils.e(TAG, "页面路径为空");
            return;
        }
        LogUtils.e(TAG, "小程序页面路径：" + pagePath);
        IWXAPI api = WXAPIFactory.createWXAPI(context, WeChatConfig.WE_CHAT_APP_ID);
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = WeChatConfig.WE_CHAT_APPLET_ID; // 填小程序原始id
        //拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.path = pagePath;
        if (Constants.isMinPre) {
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW;// 体验版
        } else if (Constants.isMinTest) {
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST;// 开发版
        } else {
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 正式版
        }
        api.sendReq(req);
    }
}
