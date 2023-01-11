package com.omni.wallet.view.popupwindow;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.DisplayUtil;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.thirdsupport.zxing.util.CodeUtils;
import com.omni.wallet.utils.ShareUtil;

/**
 * Fund的弹窗
 */
public class FundPopupWindow {
    private static final String TAG = FundPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mFundPopupWindow;

    public FundPopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(View view, String address) {
        if (mFundPopupWindow == null) {
            mFundPopupWindow = new BasePopWindow(mContext);
            View rootView = mFundPopupWindow.setContentView(R.layout.layout_popupwindow_fund);
            mFundPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mFundPopupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mFundPopupWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mFundPopupWindow.setAnimationStyle(R.style.popup_anim_style);
            ImageView addressQRCodeIv = rootView.findViewById(R.id.iv_address_qrcode);
            Bitmap mQRBitmap = CodeUtils.createQRCode(address, DisplayUtil.dp2px(mContext, 128));
            addressQRCodeIv.setImageBitmap(mQRBitmap);
            TextView addressTv = rootView.findViewById(R.id.tv_address);
            addressTv.setText(address);
            rootView.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFundPopupWindow.dismiss();
                }
            });
            rootView.findViewById(R.id.iv_copy_address_fund).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get address which will copy to clipboard
                    //接收需要复制到粘贴板的地址
                    //Get the address which will copy to clipboard
                    String toCopyAddress = address;
                    System.out.println(toCopyAddress);
                    // copy the address to clipboard
                    //将地址复制到粘贴板
                    //Copy the address to clipboard
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("text", toCopyAddress);
                    cm.setPrimaryClip(mClipData);
                    System.out.println(mClipData.toString());

                    //判断粘贴板中是否已经有字符串，如果有则弹出提示，已经将地址复制到粘贴板
                    //Check is there any string in the clipboard.If clipboard is not empty then give alert: Already copy the address to clipboard.
                    if (!mClipData.toString().isEmpty()) {
                        String toastString = mContext.getResources().getString(R.string.toast_copy_address);
                        Toast copySuccessToast = Toast.makeText(mContext, toastString, Toast.LENGTH_LONG);
                        copySuccessToast.setGravity(Gravity.TOP, 0, 30);
                        copySuccessToast.show();
                    }
                }
            });
            RelativeLayout shareLayout = rootView.findViewById(R.id.layout_share);
            rootView.findViewById(R.id.layout_son).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.GONE);
                }
            });
            rootView.findViewById(R.id.layout_share_fund).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareLayout.setVisibility(View.VISIBLE);
                }
            });
            /**
             * @描述： 点击 facebook
             * @desc: click facebook button
             */
            rootView.findViewById(R.id.iv_facebook_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast(mContext, "Not yet open, please wait");
                    shareLayout.setVisibility(View.GONE);
                }
            });
            /**
             * @描述： 点击页 twitter
             * @desc: click twitter button
             */
            rootView.findViewById(R.id.iv_twitter_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(ShareUtil.getTwitterIntent(mContext, address));
                    shareLayout.setVisibility(View.GONE);
                }
            });
            if (mFundPopupWindow.isShowing()) {
                return;
            }
            mFundPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    public void release() {
        if (mFundPopupWindow != null) {
            mFundPopupWindow.dismiss();
            mFundPopupWindow = null;
        }
    }
}
