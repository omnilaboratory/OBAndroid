package com.omni.wallet.entity.js;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.framelibrary.view.dialog.ShareDialog;
import com.omni.wallet.framelibrary.view.dialog.WaitingDialog;
import com.omni.wallet.framelibrary.view.dialog.WeChatAppletShareDialog;
import com.omni.wallet.thirdsupport.umeng.share.common.Target;


/**
 * JS与安卓交互接口
 * Imp of JS and Android
 * author: fa
 * date: 2018/2/24 12:03.
 */
public class JavaScriptInterface extends BaseJavaScriptInterface {
    private static final String TAG = JavaScriptInterface.class.getSimpleName();
    private WeChatAppletShareDialog mWeChatAppletDialog;// 小程序分享弹窗
    private WaitingDialog mWaitingDialog;

    public JavaScriptInterface(Context context) {
        super(context);
    }

    /**
     * 活动页面分享小程序、网页
     *
     * @param actId 活动ID
     */
    @JavascriptInterface
    public void appShareWeChatApplet(String actId) {
        LogUtils.e(TAG, "=========H5调原生分享小程序========》");
        // 设置分享信息
        if (mWeChatAppletDialog == null) {
            mWeChatAppletDialog = new WeChatAppletShareDialog(mContext);
        }
        mWeChatAppletDialog.dialogCallback(new DialogCallback(actId));// Dialog的回调
        mWeChatAppletDialog.show();
    }

    /**
     * 活动分享弹窗的回调
     */
    private class DialogCallback implements ShareDialog.DialogCallback {
        private String mActId;

        DialogCallback(String actId) {
            this.mActId = actId;
        }

        @Override
        public boolean onClickItem(final Target target) {
            LogUtils.e(TAG, "接口获取分享信息");
            if (mWaitingDialog == null) {
                mWaitingDialog = new WaitingDialog(mContext);
            }
            mWaitingDialog.show();
            String shareType = WeChatAppletShareDialog.SHARE_TYPE_WX;
            if (target == Target.WX_CIRCLE) {
                shareType = WeChatAppletShareDialog.SHARE_TYPE_WX_CIRCLE;
            }
//            HttpRequestUtils.shareInfo828(mContext, mActId, shareType, new DefaultHttpCallback<ShareInfoEntity>() {
//
//                @Override
//                protected void onResponseSuccess(ShareInfoEntity result) {
//                    if (result != null) {
//                        mWeChatAppletDialog.setMinTitle(result.getTitle());
//                        mWeChatAppletDialog.shareTitle(result.getTitle());
//                        mWeChatAppletDialog.shareContent("");
//                        mWeChatAppletDialog.shareImage(result.getImageUrl());
//                        mWeChatAppletDialog.shareUrl(result.getLinkUrl());
//                        mWeChatAppletDialog.setPath(result.getWxUrl());
//                    }
//                    onResponseComplete(target);
//                }
//
//                @Override
//                protected void onResponseFail(Context context, String errorCode, String errorMsg) {
//                    super.onResponseFail(context, errorCode, errorMsg);
//                    onResponseComplete(target);
//                }
//
//                @Override
//                protected void onResponseError(Context context, String errorCode, String errorMsg) {
//                    super.onResponseError(context, errorCode, errorMsg);
//                    onResponseComplete(target);
//                }
//            });
            return false;
        }
    }

    /**
     * 接口返回成功
     */
    private void onResponseComplete(Target target) {
        if (mWaitingDialog != null) {
            mWaitingDialog.dismiss();
        }
        if (mWeChatAppletDialog == null) {
            return;
        }
        if (target == Target.WX) {
            mWeChatAppletDialog.shareWeChatApplet(target);
        } else {
            mWeChatAppletDialog.shareHtml(target);
        }
    }

    @Override
    public void release() {
        super.release();
        if (mWeChatAppletDialog != null) {
            mWeChatAppletDialog.release();
            mWeChatAppletDialog = null;
        }
        if (mWaitingDialog != null) {
            mWaitingDialog.dismiss();
            mWaitingDialog = null;
        }
    }
}
