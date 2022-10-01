package com.omni.wallet.wxapi;

import android.content.Intent;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.thirdsupport.common.WeChatConfig;
import com.omni.wallet.thirdsupport.weChat.entity.WeChatEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 微信分享相关的Activity
 */

public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {
    private static final String TAG = WXEntryActivity.class.getSimpleName();

    @Override
    protected void onResume() {
        super.onResume();
        IWXAPI api = WXAPIFactory.createWXAPI(this, WeChatConfig.WE_CHAT_APP_ID, true);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.e(TAG, "=============onReq===================>");
    }

    /**
     * 小程序回到App的回调
     */
    @Override
    public void onResp(BaseResp resp) {
        LogUtils.e(TAG, "=============onResp===================>" + (resp == null ? "空" : resp.getType()));
        if (resp != null && resp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
            String extraData = launchMiniProResp.extMsg; //对应小程序组件 <button open-type="launchApp"> 中的 app-parameter 属性
            LogUtils.e(TAG, "=======小程序回调App，参数：==========>" + extraData);
            // 发通知
            WeChatEvent event = new WeChatEvent();
            event.setExtraData(extraData);
            EventBus.getDefault().post(event);
            //
            finish();
        }
    }

}
