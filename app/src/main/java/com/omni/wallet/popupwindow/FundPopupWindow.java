package com.omni.wallet.popupwindow;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

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


    public void show(View view) {
        if (mFundPopupWindow == null) {
            mFundPopupWindow = new BasePopWindow(mContext);
            View rootView = mFundPopupWindow.setContentView(R.layout.layout_popupwindow_fund);
            mFundPopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mFundPopupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mFundPopupWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mFundPopupWindow.setAnimationStyle(Gravity.CENTER);
            rootView.findViewById(R.id.layout_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFundPopupWindow.dismiss();
                }
            });
            rootView.findViewById(R.id.iv_copy_address_fund).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //接收需要复制到粘贴板的地址
                    //Get the address which will copy to clipboard
                    String toCopyAddress = "01234e*****bg453123";
                    System.out.println(toCopyAddress);

                    //将地址复制到粘贴板
                    //Copy the address to clipboard
                    ClipboardManager cm =(ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("text", toCopyAddress);
                    cm.setPrimaryClip(mClipData);
                    System.out.println(mClipData.toString());

                    //判断粘贴板中是否已经有字符串，如果有则弹出提示，已经将地址复制到粘贴板
                    //Check is there any string in the clipboard.If clipboard is not empty then give alert: Already copy the address to clipboard.
                    if(!mClipData.toString().isEmpty()){
                        String toastString = mContext.getResources().getString(R.string.toast_copy_address);
                        Toast copySuccessToast = Toast.makeText(mContext,toastString,Toast.LENGTH_LONG);
                        copySuccessToast.setGravity(Gravity.TOP,0,30);
                        copySuccessToast.show();
                    }
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
