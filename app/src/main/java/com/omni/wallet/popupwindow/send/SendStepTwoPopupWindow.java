package com.omni.wallet.popupwindow.send;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;

public class SendStepTwoPopupWindow {
    private static final String TAG = SendStepTwoPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    SendStepOnePopupWindow mSendStepOnePopupWindow;
    SendStepThreePopupWindow mSendStepThreePopupWindow;

    Double assetBalance = 500.00d;
    String toAddress = "1mn8382odjddwedqw323f3d32343f23fweg65er4345yge43t4534gy7";
    String toFriendName = "to_friend_name";

    public SendStepTwoPopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_send_steptwo);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            /**
             * @描述: 初始化页面初始数据包括：friendName,address、balance、default send amount
             * @Description: The initial data of the initialization page includes friend name, address, balance, and default send amount
             * @author: Tong ChangHui
             * @E-mail: tch081092@gmail.com
             */

            TextView toAddressView = rootView.findViewById(R.id.tv_to_address);
            toAddressView.setText(toAddress);
            TextView assetsBalanceView = rootView.findViewById(R.id.tv_asset_balance);
            assetsBalanceView.setText(assetBalance.toString());
            TextView toFriendNameView = rootView.findViewById(R.id.tv_to_friend_name);
            toFriendNameView.setText(toFriendName);

            /**
             * @描述: 增加MAX按钮的点击事件，点击将balance的值填入amount输入框中
             * @Description: Add the click event of MAX button, click to fill the balance value into the amount input box
             * @author: Tong ChangHui
             * @E-mail: tch081092@gmail.com
             */

            final EditText amountInputView = rootView.findViewById(R.id.etv_send_amount);
            TextView maxBtnView = rootView.findViewById(R.id.tv_btn_set_amount_max);
            maxBtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    amountInputView.setText(assetBalance.toString());
                }
            });

            // 点击back
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mSendStepOnePopupWindow = new SendStepOnePopupWindow(mContext);
                    mSendStepOnePopupWindow.show(view);
                }
            });
            // 点击next
            rootView.findViewById(R.id.layout_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mSendStepThreePopupWindow = new SendStepThreePopupWindow(mContext);
                    mSendStepThreePopupWindow.show(view);
                }
            });
            // 点击底部cancel
            rootView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
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
