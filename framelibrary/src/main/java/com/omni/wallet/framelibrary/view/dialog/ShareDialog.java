package com.omni.wallet.framelibrary.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.AppUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.framelibrary.R;
import com.omni.wallet.thirdsupport.umeng.UMUtils;
import com.omni.wallet.thirdsupport.umeng.share.bean.ImageShareBean;
import com.omni.wallet.thirdsupport.umeng.share.bean.ShareBuilder;
import com.omni.wallet.thirdsupport.umeng.share.bean.WeChatAppletShareBean;
import com.omni.wallet.thirdsupport.umeng.share.bean.WebShareBean;
import com.omni.wallet.thirdsupport.umeng.share.callback.ShareCallback;
import com.omni.wallet.thirdsupport.umeng.share.common.Target;


/**
 * 详情页分享的dialog
 */

public class ShareDialog implements View.OnClickListener {
    private static final String TAG = ShareDialog.class.getSimpleName();
    protected Context mContext;
    AlertDialog mDialog;
    //    private UMUtils mUMUtils;
    // 分享的Url
    String mShareUrl;
    // 分享的图片Url
    Object mShareImage;
    // 分享的标题
    private String mShareTitle;
    // 分享的描述
    String mShareContent;
    // 小程序的页面路径
    private String mAppletPath;
    // 分享回调
    ShareCallback mShareCallback = new MyShareCallback();
    // Dialog事件回调
    DialogCallback mDialogCallback;
    // 标题
    private String mDialogTitle;
    private boolean mShowDialogTitle;

    ShareDialog(Context context) {
        this.mContext = context;
//        this.mUMUtils = UMUtils.with(mContext);
    }

    public static ShareDialog with(Context context) {
        return new ShareDialog(context);
    }

    public ShareDialog shareUrl(String shareUrl) {
        this.mShareUrl = shareUrl;
        return this;
    }

    public ShareDialog shareTitle(String shareTitle) {
        this.mShareTitle = StringUtils.isEmpty(shareTitle) ? AppUtils.getAppName(mContext) : shareTitle;
        return this;
    }

    public ShareDialog shareImage(Object shareImage) {
        this.mShareImage = shareImage;
        return this;
    }

    public ShareDialog shareContent(String shareContent) {
        this.mShareContent = shareContent;
        return this;
    }

    public ShareDialog sharePath(String path) {
        this.mAppletPath = path;
        return this;
    }

    public ShareDialog shareCallback(ShareCallback callback) {
        if (mShareCallback != null) {
            this.mShareCallback = callback;
        }
        return this;
    }

    public ShareDialog dialogCallback(DialogCallback callback) {
        if (mShareCallback != null) {
            this.mDialogCallback = callback;
        }
        return this;
    }

    public void show() {
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(mContext)
                    .setContentView(R.layout.layout_dialog_share)
                    .setOnClickListener(R.id.main_tv_share_dialog_we_chat, this)
                    .setOnClickListener(R.id.main_tv_share_dialog_we_friend, this)
                    .setOnClickListener(R.id.main_tv_share_dialog_qq, this)
                    .setOnClickListener(R.id.main_tv_share_dialog_qq_zone, this)
                    .setOnClickListener(R.id.main_tv_share_dialog_sina, this)
                    .setOnClickListener(R.id.main_tv_share_dialog_cancel, this)
                    .fullWidth()
                    .fromBottom(true)
                    .create();
        }
        // 初始化标题控件
        TextView titleView = mDialog.getViewById(R.id.tv_dialog_share_title);
        titleView.setText(mDialogTitle);
        // 标题布局
        LinearLayout titleLayout = mDialog.getViewById(R.id.layout_dialog_share_title);
        if (mShowDialogTitle) {
            titleLayout.setVisibility(View.VISIBLE);
        } else {
            titleLayout.setVisibility(View.GONE);
        }
        // dismiss
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog.show();
    }

    public void setTitle(String title) {
        this.mDialogTitle = title;
    }

    public void showTitle(boolean showTitle) {
        this.mShowDialogTitle = showTitle;
    }

    @Override
    public void onClick(View v) {
        Target target = null;
        int id = v.getId();
        if (id == R.id.main_tv_share_dialog_cancel) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            return;
        }
        if (id == R.id.main_tv_share_dialog_we_chat) {
            LogUtils.e(TAG, "分享到微信");
            target = Target.WX;
        } else if (id == R.id.main_tv_share_dialog_we_friend) {
            LogUtils.e(TAG, "分享到微信朋友圈");
            target = Target.WX_CIRCLE;
        } else if (id == R.id.main_tv_share_dialog_qq) {
            LogUtils.e(TAG, "分享到QQ");
            target = Target.QQ;
        } else if (id == R.id.main_tv_share_dialog_qq_zone) {
            LogUtils.e(TAG, "分享到QQ空间");
            target = Target.QZONE;
        } else if (id == R.id.main_tv_share_dialog_sina) {
            LogUtils.e(TAG, "分享到微博");
            target = Target.SINA;
        }
        // 是否立即拉起分享
        boolean shareNow = true;
        if (mDialogCallback != null) {
            shareNow = mDialogCallback.onClickItem(target);
        }
        // 接口返回true的时候才自动拉起分享
        if (target != null && shareNow) {
            if (target == Target.WX) {
                if (StringUtils.isEmpty(mShareUrl)) {
                    shareImage(target);
                } else {
                    // 反了微信的Path就分享到微信小程序，否则分享到网页
                    if (StringUtils.isEmpty(mAppletPath)) {
                        shareHtml(target);
                    } else {
                        shareWeChatApplet(target);
                    }
                }
            } else {
                if (StringUtils.isEmpty(mShareUrl)) {
                    shareImage(target);
                } else {
                    shareHtml(target);
                }
            }
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /**
     * 分享图片
     */
    public void sharePicture(Target target) {
        ImageShareBean imageShareBean = ImageShareBean.makeImageShareBean(mShareImage);
        imageShareBean.setThumb(imageShareBean);
        new ShareBuilder(mContext)
                .target(target)
                .shareBean(imageShareBean)
                .callback(mShareCallback)
                .create()
                .share();
    }

    /**
     * 分享
     */
    public void shareHtml(Target target) {
        WebShareBean web = new WebShareBean();
        web.setTitle(mShareTitle);
        web.setDesc(mShareContent);
        web.setUrl(mShareUrl);
        web.setThumb(ImageShareBean.makeImageShareBean(mShareImage));
        new ShareBuilder(mContext)
                .target(target)
                .shareBean(web)
                .callback(mShareCallback)
                .create()
                .share();
    }

    /**
     * 分享小程序
     */
    public void shareWeChatApplet(Target target) {
        WeChatAppletShareBean web = new WeChatAppletShareBean();
        web.setTitle(mShareTitle);
        web.setDesc(mShareContent);
        web.setUrl(mShareUrl);
        web.setPath(mAppletPath);
        web.setThumb(ImageShareBean.makeImageShareBean(mShareImage));
        new ShareBuilder(mContext)
                .target(target)
                .shareBean(web)
                .callback(mShareCallback)
                .create()
                .share();
    }

//    /**
//     * 创建分享图片的实体
//     */
//    protected ImageShareBean makeImageShareBean() {
//        ImageShareBean shareImage = new ImageShareBean();
//        if (mShareImage == null) {
//            shareImage.setImageRes(R.drawable.ic_launcher_share);
//            return shareImage;
//        }
//        if (mShareImage instanceof Integer) {
//            shareImage.setImageRes((Integer) mShareImage);
//        } else if (mShareImage instanceof Bitmap) {
//            shareImage.setImageBitmap((Bitmap) mShareImage);
//        } else if (mShareImage instanceof byte[]) {
//            shareImage.setImageByte((byte[]) mShareImage);
//        } else if (mShareImage instanceof String) {
//            shareImage.setImageUrl((String) mShareImage);
//        } else if (mShareImage instanceof File) {
//            shareImage.setImageFile((File) mShareImage);
//        }
//        return shareImage;
//    }


    /**
     * 分享的回调
     */
    private class MyShareCallback implements ShareCallback {
        @Override
        public void onStartShare(Target target) {
        }

        @Override
        public void onShareResult(Target target) {
//            ToastUtils.showToast(mContext, "分享成功");
        }

        @Override
        public void onShareCancel(Target target) {
        }

        @Override
        public void onShareError(Target target, Throwable throwable) {
            ToastUtils.showToast(mContext, "分享出错");
        }
    }

    /**
     * 是否调用了分享
     */
    public boolean isRequestShare() {
        return UMUtils.isRequestShare();
    }


    public interface DialogCallback {
        boolean onClickItem(Target target);
    }


    public void release() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        UMUtils.release((Activity) mContext);
    }
}
