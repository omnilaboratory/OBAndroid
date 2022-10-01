package com.omni.wallet.thirdsupport.umeng.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.umeng.socialize.media.UMWeb;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.thirdsupport.common.WeChatConfig;
import com.omni.wallet.thirdsupport.umeng.share.bean.BaseShareBean;
import com.omni.wallet.thirdsupport.umeng.share.bean.ImageShareBean;
import com.omni.wallet.thirdsupport.umeng.share.bean.ShareBuilder;
import com.omni.wallet.thirdsupport.umeng.share.bean.WeChatAppletShareBean;
import com.omni.wallet.thirdsupport.umeng.share.bean.WebShareBean;
import com.omni.wallet.thirdsupport.umeng.share.callback.ShareCallback;
import com.omni.wallet.thirdsupport.umeng.share.common.IShare;
import com.omni.wallet.thirdsupport.umeng.share.common.Target;

import java.io.File;


public class UMShare implements IShare {
    private static final String TAG = UMShare.class.getSimpleName();
    private boolean isRequestShare = false;


    // 初始化分享平台
    {
        PlatformConfig.setWeixin(WeChatConfig.WE_CHAT_APP_ID, WeChatConfig.WE_CHAT_APP_KEY);
//        PlatformConfig.setSinaWeibo("3071799711", "d3d14750583fc1e87043823e77d745c9", "https://sns.whalecloud.com/sina2/callback");
//        PlatformConfig.setQQZone("1107846536", "QQLjLSXH46zsSP73");
//        PlatformConfig.setQQZone("1106627812", "w9yCt48EwDG0Jqwc");
    }

    public UMShare() {
    }

    @Override
    public void share(ShareBuilder builder) {
        if (builder == null) {
            return;
        }
        // 检查APP是否安装
        if (!isInstall(builder.mActivity, builder.mShareTarget)) {
            ToastUtils.showToast(builder.mActivity, getUnInstallTips(builder.mShareTarget));
            return;
        }
        BaseShareBean shareBean = builder.mShareBean;
        // 分享文字
        if (shareBean == null) {
            String text = builder.mText;
            if (!StringUtils.isEmpty(text)) {
                shareText(builder, text);
            }
        }
        // 分享图片
        if (shareBean instanceof ImageShareBean) {
            shareImage(builder);
        }
        // 分享链接
        if (shareBean instanceof WebShareBean) {
            shareUrl(builder);
        }
        // 分享小程序
        if (shareBean instanceof WeChatAppletShareBean) {
            shareWeChatApplet(builder);
        }
        this.isRequestShare = true;
    }

    @Override
    public void release(Activity activity) {
        UMShareAPI.get(activity).release();
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean isRequestShare() {
        return isRequestShare;
    }

    /**
     * 分享文字
     */
    private void shareText(ShareBuilder builder, String text) {
        // QQ不支持分享纯文本
        if (builder.mShareTarget == Target.QQ) {
            builder.mShareCallback.onShareError(Target.QQ, new Exception("unSupport share type：text"));
            return;
        }
        new ShareAction(builder.mActivity)
                .setPlatform(getShareMedia(builder.mShareTarget))
                .withText(text)
                .setCallback(new MyShareCallback(builder))
                .share();
    }


    /**
     * 分享图片
     */
    private void shareImage(ShareBuilder builder) {
        UMImage shareImage = createUMImage(builder, (ImageShareBean) builder.mShareBean);
        //大小压缩，默认为大小压缩，适合普通很大的图
        shareImage.compressStyle = UMImage.CompressStyle.SCALE;
//        //质量压缩，适合长图的分享
//        shareImage.compressStyle = UMImage.CompressStyle.QUALITY;
        // 压缩格式设置
        //用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
        shareImage.compressFormat = Bitmap.CompressFormat.PNG;
        shareImage.setThumb(createShareImage(builder));// 设置缩略图
        new ShareAction(builder.mActivity)
                .setPlatform(getShareMedia(builder.mShareTarget))
                .withMedia(shareImage)
                .setCallback(new MyShareCallback(builder))
                .share();
    }

    /**
     * 分享链接
     */
    private void shareUrl(ShareBuilder builder) {
        // 获取分享对象
        BaseShareBean shareBean = builder.mShareBean;
        if (shareBean == null) {
            return;
        }
        // 缩略图
        UMImage thumb = createShareImage(builder);
        // 构建友盟Web对象
        UMWeb web = new UMWeb(shareBean.getUrl());
        //标题
        web.setTitle(shareBean.getTitle());
        //缩略图
        web.setThumb(thumb);
        //描述
        web.setDescription(shareBean.getDesc());
        //分享
        new ShareAction(builder.mActivity)
                .setPlatform(getShareMedia(builder.mShareTarget))
                .withMedia(web)
                .setCallback(new MyShareCallback(builder))
                .share();
    }

    /**
     * 分享小程序
     */
    private void shareWeChatApplet(ShareBuilder builder) {
        // 获取分享对象
        if (builder.mShareBean != null && builder.mShareBean instanceof WeChatAppletShareBean) {
            WeChatAppletShareBean shareBean = (WeChatAppletShareBean) builder.mShareBean;
            UMMin umMin = new UMMin(shareBean.getUrl());// 兼容低版本的网页链接
            umMin.setThumb(createShareImage(builder));// 小程序消息封面图片
            umMin.setTitle(shareBean.getTitle());// 小程序消息title
            umMin.setDescription(shareBean.getDesc());// 小程序消息描述
            umMin.setPath(shareBean.getPath());// 小程序页面路径
            umMin.setUserName(WeChatConfig.WE_CHAT_APPLET_ID);// 小程序原始id,在微信平台查询
            new ShareAction(builder.mActivity)
                    .withMedia(umMin)
                    .setPlatform(SHARE_MEDIA.WEIXIN)
                    .setCallback(new MyShareCallback(builder))
                    .share();
        }
    }


    /**
     * 构建分享的图片
     */
    private UMImage createShareImage(ShareBuilder builder) {
        BaseShareBean shareBean = builder.mShareBean;
        if (shareBean == null) {
            return null;
        }
        ImageShareBean shareThumb = shareBean.getThumb();
        if (shareThumb != null) {
            return createUMImage(builder, shareThumb);
        }
        return null;
    }

    /**
     * 创建友盟分享的图片对象
     */
    private UMImage createUMImage(ShareBuilder builder, ImageShareBean shareImage) {
        if (shareImage == null) {
            return null;
        }
        UMImage resultImage = null;
        String imageUrl = shareImage.getImageUrl();
        if (!StringUtils.isEmpty(imageUrl)) {
            resultImage = new UMImage(builder.mActivity, imageUrl);
        }
        File imageFile = shareImage.getImageFile();
        if (imageFile != null) {
            resultImage = new UMImage(builder.mActivity, imageFile);
        }
        int imageResId = shareImage.getImageRes();
        if (imageResId != 0) {
            resultImage = new UMImage(builder.mActivity, imageResId);
        }
        Bitmap imageBitmap = shareImage.getImageBitmap();
        if (imageBitmap != null) {
            resultImage = new UMImage(builder.mActivity, imageBitmap);
        }
        byte[] imageByte = shareImage.getImageByte();
        if (imageByte != null) {
            resultImage = new UMImage(builder.mActivity, imageByte);
        }
        return resultImage;
    }

    /**
     * 分享监听
     */
    private class MyShareCallback implements UMShareListener {
        private ShareCallback mCallback;

        MyShareCallback(ShareBuilder builder) {
            this.mCallback = builder.mShareCallback;
        }

        @Override
        public void onStart(SHARE_MEDIA share_media) {
            isRequestShare = true;
            LogUtils.e(TAG, "=======onStart========>");
            if (mCallback != null) {
                mCallback.onStartShare(getShareTarget(share_media));
            }
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            isRequestShare = false;
            LogUtils.e(TAG, "=======onResult========>");
            if (mCallback != null) {
                mCallback.onShareResult(getShareTarget(share_media));
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            isRequestShare = false;
            LogUtils.e(TAG, "=======onError========>");
            if (mCallback != null) {
                mCallback.onShareError(getShareTarget(share_media), throwable);
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            isRequestShare = false;
            LogUtils.e(TAG, "============onCancel==========>");
            if (mCallback != null) {
                mCallback.onShareCancel(getShareTarget(share_media));
            }
        }
    }


    /**
     * 获取对应的友盟的分享渠道类型
     */
    private SHARE_MEDIA getShareMedia(Target target) {
        if (target == Target.QQ) {
            return SHARE_MEDIA.QQ;
        }
        if (target == Target.QZONE) {
            return SHARE_MEDIA.QZONE;
        }
        if (target == Target.WX) {
            return SHARE_MEDIA.WEIXIN;
        }
        if (target == Target.WX_CIRCLE) {
            return SHARE_MEDIA.WEIXIN_CIRCLE;
        }
        if (target == Target.SINA) {
            return SHARE_MEDIA.SINA;
        }
        return null;
    }

    /**
     * 获取自己定义的分享渠道
     */
    private Target getShareTarget(SHARE_MEDIA shareMedia) {
        if (shareMedia == SHARE_MEDIA.QQ) {
            return Target.QQ;
        }
        if (shareMedia == SHARE_MEDIA.QZONE) {
            return Target.QZONE;
        }
        if (shareMedia == SHARE_MEDIA.WEIXIN) {
            return Target.WX;
        }
        if (shareMedia == SHARE_MEDIA.WEIXIN_CIRCLE) {
            return Target.WX_CIRCLE;
        }
        if (shareMedia == SHARE_MEDIA.SINA) {
            return Target.SINA;
        }
        return null;
    }

    /**
     * 判断APP是否安装
     */
    private boolean isInstall(Activity activity, Target target) {
        // 这里在判断QQZone的APP是否安装的时候默认判断QQ即可
        if (target == Target.QZONE) {
            target = Target.QQ;
        }
        return UMShareAPI.get(activity).isInstall(activity, getShareMedia(target));
    }

    /**
     * 获取不同类型的提示语
     */
    private String getUnInstallTips(Target target) {
        if (target == Target.QQ || target == Target.QZONE) {
            return "您手机暂未安装QQ";
        }
        if (target == Target.WX || target == Target.WX_CIRCLE) {
            return "您手机暂未安装微信";
        }
        if (target == Target.SINA) {
            return "您手机暂未安装微博";
        }
        return "应用未安装";
    }
}
