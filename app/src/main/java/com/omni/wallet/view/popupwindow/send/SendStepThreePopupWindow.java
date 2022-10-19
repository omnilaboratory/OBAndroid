package com.omni.wallet.view.popupwindow.send;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.utils.GetResourceUtil;
import com.omni.wallet.view.dialog.LoadingDialog;

public class SendStepThreePopupWindow {
    private static final String TAG = SendStepThreePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    SendStepTwoPopupWindow mSendStepTwoPopupWindow;
    LoadingDialog mLoadingDialog;

    // 初始数据（Initial data）
    String sendFriendName = "Alpha";
    String sendAddress = "1mn8382odjddwedqw323f3d32343f23fweg65er4345yge43t4534gy7";
    String type = "USDT";
    Double sendAmount = 100.00d;
    Double sendValue = 710.23d;
    Double gasFeeAmount = 100.00d;
    Double gasFeeValue = 5.06d;
    Double totalValue = 715.29d;


    public SendStepThreePopupWindow(Context context) {
        this.mContext = context;
    }


    public void show(final View view) {
        if (mBasePopWindow == null) {
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_send_stepthree);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            /**
             * @描述: 初始化页面初始数据包括：friendName、address、send amount、send value、gas fee、fee value、total used value
             * @Description: The initial data of the initialization page includes friend name, address, send value, gas fee, fee value, total used value
             * @author: Tong ChangHui
             * @E-mail: tch081092@gmail.com
             */

            TextView friendNameView = rootView.findViewById(R.id.tv_send_friend_name);
            friendNameView.setText(sendFriendName);
            TextView friendAddressView = rootView.findViewById(R.id.tv_send_address);
            friendAddressView.setText(sendAddress);
            ImageView tokenImage = rootView.findViewById(R.id.iv_send_token_image);
            tokenImage.setImageDrawable(mContext.getResources().getDrawable(GetResourceUtil.getTokenImageId(mContext, type)));
            TextView tokenTypeView = rootView.findViewById(R.id.tv_send_token_type);
            tokenTypeView.setText(type);
            TextView tokenTypeView2 = rootView.findViewById(R.id.tv_send_token_type_2);
            tokenTypeView2.setText(type);
            TextView sendAmountView = rootView.findViewById(R.id.tv_send_amount);
            sendAmountView.setText(sendAmount.toString());
            TextView sendAmountValueView = rootView.findViewById(R.id.tv_send_amount_value);
            sendAmountValueView.setText(sendValue.toString());
            TextView feeAmountView = rootView.findViewById(R.id.tv_send_gas_fee_amount);
            feeAmountView.setText(gasFeeAmount.toString());
            TextView feeAmountValueView = rootView.findViewById(R.id.tv_send_gas_fee_amount_value);
            feeAmountValueView.setText(gasFeeValue.toString());
            TextView sendUsedValueView = rootView.findViewById(R.id.tv_send_used_value);
            sendUsedValueView.setText(totalValue.toString());


            // 点击back
            rootView.findViewById(R.id.layout_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBasePopWindow.dismiss();
                    mSendStepTwoPopupWindow = new SendStepTwoPopupWindow(mContext);
                    mSendStepTwoPopupWindow.show(view);
                }
            });
            // 点击confirm
            rootView.findViewById(R.id.layout_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLoadingDialog = new LoadingDialog(mContext);
                    mLoadingDialog.show();
                    mBasePopWindow.dismiss();
                    mLoadingDialog.dismiss();
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
