package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.utils.CopyUtil;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

/**
 * 汉: 代币详情的弹窗
 * En: TokenInfoPopupWindow
 * author: guoyalei
 * date: 2022/11/20
 */
public class TokenInfoPopupWindow {
    private static final String TAG = TokenInfoPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;

    public TokenInfoPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view, String address, long assetId, long balanceAccount) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_token_info);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            TextView tokenPubkeyTv = rootView.findViewById(R.id.tv_token_pubkey);
            ImageView tokenLogoIv = rootView.findViewById(R.id.iv_token_logo);
            TextView tokenTypeTv = rootView.findViewById(R.id.tv_token_type);
            TextView tokenIdTv = rootView.findViewById(R.id.tv_token_id);
            TextView tokenAmountTv = rootView.findViewById(R.id.tv_token_amount);
            TextView categoryTv = rootView.findViewById(R.id.tv_token_category);
            TextView dateTv = rootView.findViewById(R.id.tv_token_date);
            TextView tokenUrlTv = rootView.findViewById(R.id.tv_token_url);
            TextView divisibleTv = rootView.findViewById(R.id.tv_token_divisible);
            if (assetId == 0) {
                tokenLogoIv.setImageResource(R.mipmap.icon_btc_logo_small);
                tokenTypeTv.setText("BTC");
            } else {
                tokenLogoIv.setImageResource(R.mipmap.icon_usdt_logo_small);
                tokenTypeTv.setText("USDT");
            }
            tokenIdTv.setText(assetId + "");

            LightningOuterClass.GetAssetInfoRequest getAssetInfoRequest = LightningOuterClass.GetAssetInfoRequest.newBuilder()
                    .setAssetId(assetId)
                    .build();
            Obdmobile.getAssetInfo(getAssetInfoRequest.toByteArray(), new Callback() {
                @Override
                public void onError(Exception e) {
                    LogUtils.e(TAG, "------------------getAssetInfoOnError------------------" + e.getMessage());
                }

                @Override
                public void onResponse(byte[] bytes) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LightningOuterClass.GetAssetInfoResponse resp = LightningOuterClass.GetAssetInfoResponse.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------getAssetInfoOnResponse-----------------" + resp);
                                tokenPubkeyTv.setText(resp.getIssuer());
                                tokenAmountTv.setText(resp.getTotaltokens());
                                categoryTv.setText(resp.getCategory());
                                dateTv.setText("");
                                tokenUrlTv.setText(resp.getUrl());
                                divisibleTv.setText(resp.getDivisible() + "");
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            RelativeLayout shareLayout = rootView.findViewById(R.id.layout_share);
            rootView.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.GONE);
                }
            });
            // 点击copy
            rootView.findViewById(R.id.layout_copy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //接收需要复制到粘贴板的地址
                    //Get the address which will copy to clipboard
                    String toCopyAddress = tokenPubkeyTv.getText().toString();
                    //接收需要复制成功的提示语
                    //Get the notice when you copy success
                    String toastString = mContext.getResources().getString(R.string.toast_copy_address);
                    CopyUtil.SelfCopy(mContext, toCopyAddress, toastString);
                    mBasePopWindow.dismiss();
                }
            });
            // 点击share to
            rootView.findViewById(R.id.layout_share_to).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.VISIBLE);
                }
            });
            // 点击facebook
            rootView.findViewById(R.id.iv_facebook_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    shareLayout.setVisibility(View.GONE);
                }
            });
            // 点击twitter
            rootView.findViewById(R.id.iv_twitter_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    shareLayout.setVisibility(View.GONE);
                }
            });
            // 点击底部close
            rootView.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                }
            });
            if (mBasePopWindow.isShowing()) {
                return;
            }
            mBasePopWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
