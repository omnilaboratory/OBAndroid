package com.omni.wallet.framelibrary.view.dialog;

import android.content.Context;
import android.view.View;

import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.framelibrary.R;
import com.omni.wallet.thirdsupport.umeng.UMUtils;
import com.omni.wallet.thirdsupport.umeng.share.bean.ImageShareBean;
import com.omni.wallet.thirdsupport.umeng.share.bean.WeChatAppletShareBean;
import com.omni.wallet.thirdsupport.umeng.share.common.Target;

/**
 * 小程序分享的弹窗
 */

public class WeChatAppletShareDialog extends ShareDialog implements View.OnClickListener {
    private static final String TAG = WeChatAppletShareDialog.class.getSimpleName();

    private String mPath;// 小程序的页面路径
    private String mMinTitle;// 小程序的标题
    private boolean mShowWXCircle;// 是否显示朋友圈

    public static final String SHARE_TYPE_WX = "1";// 微信
    public static final String SHARE_TYPE_WX_CIRCLE = "2";// 微信朋友圈


    public void setPath(String path) {
        this.mPath = path;
    }

    public void setMinTitle(String content) {
        this.mMinTitle = content;
    }

    public WeChatAppletShareDialog(Context context) {
        super(context);
    }

    public void setShowWXCircle(boolean showWXCircle) {
        this.mShowWXCircle = showWXCircle;
    }

    @Override
    public void show() {
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(mContext)
                    .setContentView(R.layout.layout_dialog_we_chat_applet_share)
                    .setOnClickListener(R.id.tv_dialog_share_we_chat_applet_we_chat, this)
                    .setOnClickListener(R.id.tv_dialog_share_we_chat_applet_we_friend, this)
                    .setOnClickListener(R.id.tv_dialog_share_we_chat_applet_cancel, this)
                    .fullWidth()
                    .fromBottom(true)
                    .create();
        }
        if (mShowWXCircle) {
            mDialog.getViewById(R.id.tv_dialog_share_we_chat_applet_we_friend).setVisibility(View.VISIBLE);
        } else {
            mDialog.getViewById(R.id.tv_dialog_share_we_chat_applet_we_friend).setVisibility(View.GONE);
        }
        // dismiss
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        if (mDialog == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.tv_dialog_share_we_chat_applet_cancel) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            return;
        }
        Target target = null;
        if (id == R.id.tv_dialog_share_we_chat_applet_we_chat) {
            LogUtils.e(TAG, "分享小程序到微信");
            target = Target.WX;
            shareWeChatApplet(target);
        } else if (id == R.id.tv_dialog_share_we_chat_applet_we_friend) {
            LogUtils.e(TAG, "分享到微信朋友圈");
            target = Target.WX_CIRCLE;
            shareHtml(target);
        }
        // 是否立即拉起分享
        boolean shareNow = true;
        if (mDialogCallback != null) {
            shareNow = mDialogCallback.onClickItem(target);
        }
        // 接口返回true的时候才自动拉起分享
        if (target != null && shareNow) {
            shareHtml(target);
        }
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    /**
     * 分享小程序
     */
    public void shareWeChatApplet(Target target) {
        WeChatAppletShareBean web = new WeChatAppletShareBean();
        web.setTitle(mMinTitle);
        web.setDesc(mShareContent);
        web.setUrl(mShareUrl);
        web.setPath(mPath);
        web.setThumb(ImageShareBean.makeImageShareBean(mShareImage));
        UMUtils.with(mContext)
                .target(target)
                .shareBean(web)
                .callback(mShareCallback)
                .share();
    }
}
