package com.omni.wallet.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.baselibrary.utils.DateUtils;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.baselibrary.utils.ToastUtils;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.entity.AddressEntity;
import com.omni.wallet.entity.ListAssetItemEntity;
import com.omni.wallet.entity.event.SendSuccessEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.ui.activity.ScanSendActivity;
import com.omni.wallet.utils.ShareUtil;
import com.omni.wallet.utils.ValidateBitcoinAddress;
import com.omni.wallet.utils.Wallet;
import com.omni.wallet.view.popupwindow.SelectAssetPopupWindow;
import com.omni.wallet.view.popupwindow.SelectSpeedPopupWindow;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

/**
 * 汉: 链上支付的弹窗
 * En: SendDialog
 * author: guoyalei
 * date: 2022/12/13
 */
public class SendDialog implements Wallet.ScanSendListener {
    private static final String TAG = SendDialog.class.getSimpleName();

    private Context mContext;
    private AlertDialog mAlertDialog;
    EditText searchEdit;
    TextView recentsAddressTv;
    TextView recentsAddressSecondTv;
    RecyclerView mRecyclerView;
    TextView assetsBalanceTv;
    private TextView sendFeeTv;
    private TextView sendFeeExchangeTv;
    private List<String> txidList;
    private List<AddressEntity> list;
    private List<AddressEntity> mAddressData = new ArrayList<>();
    private MyAdapter mAdapter;
    String selectAddress;
    int time = 1;
    long feeStr;
    long assetId = 0;
    String assetBalance = "0";
    String assetBalanceMax;
    private LoadingDialog mLoadingDialog;
    // 初始数据（Initial data）
    String toFriendName = "unname";
    SelectSpeedPopupWindow mSelectSpeedPopupWindow;
    SelectAssetPopupWindow mSelectAssetPopupWindow;
    SendSuccessDialog mSendSuccessDialog;

    public SendDialog(Context context) {
        this.mContext = context;
    }

    public void show(String payAddr) {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_popupwindow_send_stepone)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        Wallet.getInstance().registerScanSendListener(this);
        mLoadingDialog = new LoadingDialog(mContext);
        SharedPreferences sp = mContext.getSharedPreferences("SP_ADDR_LIST", Activity.MODE_PRIVATE);
        String addrListJson = sp.getString("addrListKey", "");
        if (!StringUtils.isEmpty(addrListJson)) {
            Gson gson = new Gson();
            mAddressData = gson.fromJson(addrListJson, new TypeToken<List<AddressEntity>>() {
            }.getType()); //将json字符串转换成List集合
            removeDuplicate(mAddressData);
            LogUtils.e(TAG, "========localaddress=====" + mAddressData.get(0).getName());
            LogUtils.e(TAG, "========localaddress=====" + addrListJson);
        }
        if (!StringUtils.isEmpty(payAddr)) {
            if (mAddressData.size() == 0) {
                toFriendName = "unname";
            } else {
                toFriendName = "unname";
                for (AddressEntity entity : mAddressData) {
                    if (entity.getAddress().equals(payAddr)) {
                        toFriendName = entity.getName();
                    }
                }
            }
            selectAddress = payAddr;
            mAlertDialog.findViewById(R.id.lv_step_one_content).setVisibility(View.GONE);
            mAlertDialog.findViewById(R.id.lv_step_two_content).setVisibility(View.VISIBLE);
            showStepTwo();
        } else {
            showStepOne();
        }
        /**
         * @备注： 点击cancel 按钮
         * @description: Click cancel button
         */
        mAlertDialog.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        if (mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog.show();
    }

    /**
     * send step one
     */
    private void showStepOne() {
        searchEdit = mAlertDialog.findViewById(R.id.edit_search);
        recentsAddressTv = mAlertDialog.findViewById(R.id.tv_recents_address);
        recentsAddressSecondTv = mAlertDialog.findViewById(R.id.tv_recents_address_second);
        if (mAddressData.size() == 0) {
            recentsAddressTv.setVisibility(View.GONE);
            recentsAddressSecondTv.setVisibility(View.GONE);
        } else {
            if (mAddressData.size() == 1) {
                recentsAddressTv.setVisibility(View.VISIBLE);
                recentsAddressSecondTv.setVisibility(View.GONE);
                recentsAddressTv.setText(mAddressData.get(0).getAddress());
            } else {
                recentsAddressTv.setVisibility(View.VISIBLE);
                recentsAddressSecondTv.setVisibility(View.VISIBLE);
                recentsAddressTv.setText(mAddressData.get(0).getAddress());
                recentsAddressSecondTv.setText(mAddressData.get(1).getAddress());
            }
        }
        recentsAddressTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFriendName = mAddressData.get(0).getName();
                selectAddress = recentsAddressTv.getText().toString();
                mAlertDialog.findViewById(R.id.lv_step_one_content).setVisibility(View.GONE);
                mAlertDialog.findViewById(R.id.lv_step_two_content).setVisibility(View.VISIBLE);
                showStepTwo();
            }
        });
        recentsAddressSecondTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFriendName = mAddressData.get(1).getName();
                selectAddress = recentsAddressSecondTv.getText().toString();
                mAlertDialog.findViewById(R.id.lv_step_one_content).setVisibility(View.GONE);
                mAlertDialog.findViewById(R.id.lv_step_two_content).setVisibility(View.VISIBLE);
                showStepTwo();
            }
        });
        /**
         * @description: RecyclerView for send list
         * @描述： send list 的 RecyclerView
         */
        mRecyclerView = mAlertDialog.findViewById(R.id.recycler_send_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(mContext, mAddressData, R.layout.layout_item_send_list);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        // Search
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (ValidateBitcoinAddress.validateBitcoinAddress(s.toString())) {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                if (mAddressData.size() == 0) {
                                    toFriendName = "unname";
                                } else {
                                    toFriendName = "unname";
                                    for (AddressEntity entity : mAddressData) {
                                        if (entity.getAddress().equals(s.toString())) {
                                            toFriendName = entity.getName();
                                        }
                                    }
                                }
                                selectAddress = s.toString();
                                mAlertDialog.findViewById(R.id.lv_step_one_content).setVisibility(View.GONE);
                                mAlertDialog.findViewById(R.id.lv_step_two_content).setVisibility(View.VISIBLE);
                                showStepTwo();
                            }
                        }, 1000);
                    } else {
                        ToastUtils.showToast(mContext, mContext.getString(R.string.wallet_address_is_invalid));
                    }
                }
            }
        });

        /**
         * @description: click scan icon
         * @描述： 点击scan
         */
        mAlertDialog.findViewById(R.id.iv_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.launchCamera((Activity) mContext, new PermissionUtils.PermissionCallback() {
                    @Override
                    public void onRequestPermissionSuccess() {
//                        mBasePopWindow.dismiss();
                        Intent intent = new Intent(mContext, ScanSendActivity.class);
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onRequestPermissionFailure(List<String> permissions) {
                        LogUtils.e(TAG, "扫码页面摄像头权限拒绝");
                    }

                    @Override
                    public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                        LogUtils.e(TAG, "扫码页面摄像头权限拒绝并且勾选不再提示");
                    }
                });
            }
        });
    }

    /**
     * send step two
     */
    private void showStepTwo() {
        TextView toAddressView = mAlertDialog.findViewById(R.id.tv_to_address);
        toAddressView.setText(selectAddress);
        TextView toFriendNameView = mAlertDialog.findViewById(R.id.tv_to_friend_name);
        toFriendNameView.setText(toFriendName);
        ImageView assetTypeIv = mAlertDialog.findViewById(R.id.iv_asset_type);
        TextView assetTypeTv = mAlertDialog.findViewById(R.id.tv_asset_type);
        TextView amountTypeTv = mAlertDialog.findViewById(R.id.tv_amount_type);
        assetsBalanceTv = mAlertDialog.findViewById(R.id.tv_asset_balance);
        sendFeeTv = mAlertDialog.findViewById(R.id.tv_send_fee);
        TextView sendFeeUnitTv = mAlertDialog.findViewById(R.id.tv_send_fee_unit);
        sendFeeExchangeTv = mAlertDialog.findViewById(R.id.tv_send_fee_exchange);
        if (assetId == 0) {
            assetTypeIv.setImageResource(R.mipmap.icon_btc_logo_small);
            assetTypeTv.setText("BTC");
            amountTypeTv.setText("BTC");
            sendFeeUnitTv.setText("satoshis");
        } else {
            assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            assetTypeTv.setText("dollar");
            amountTypeTv.setText("dollar");
            sendFeeUnitTv.setText("satoshis");
        }
        fetchWalletBalance();
        final EditText amountInputView = mAlertDialog.findViewById(R.id.etv_send_amount);
        amountInputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                estimateOnChainFee(count, time);
            }

            @Override
            public void afterTextChanged(Editable s) {
                assetBalance = s.toString();
                if (!StringUtils.isEmpty(s.toString())) {
                    estimateOnChainFee((long) (Double.parseDouble(assetBalance) * 100000000), time);
                } else {
                    estimateOnChainFee(0, time);
                }
            }
        });
        RelativeLayout assetLayout = mAlertDialog.findViewById(R.id.layout_asset);
        assetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectAssetPopupWindow = new SelectAssetPopupWindow(mContext);
                mSelectAssetPopupWindow.setOnItemClickCallback(new SelectAssetPopupWindow.ItemCleckListener() {
                    @Override
                    public void onItemClick(View view, ListAssetItemEntity item) {
                        if (item.getPropertyid() == 0) {
                            assetTypeIv.setImageResource(R.mipmap.icon_btc_logo_small);
                            assetTypeTv.setText("BTC");
                            amountTypeTv.setText("BTC");
                            sendFeeUnitTv.setText("satoshis");
                        } else {
                            assetTypeIv.setImageResource(R.mipmap.icon_usdt_logo_small);
                            assetTypeTv.setText("dollar");
                            amountTypeTv.setText("dollar");
                            sendFeeUnitTv.setText("satoshis");
                        }
                        assetId = item.getPropertyid();
                        if (item.getAmount() == 0) {
                            DecimalFormat df = new DecimalFormat("0.00");
                            assetBalanceMax = df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000);
                        } else {
                            DecimalFormat df = new DecimalFormat("0.00######");
                            assetBalanceMax = df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000);
                        }
                        assetsBalanceTv.setText(assetBalanceMax);
                        if (!StringUtils.isEmpty(amountInputView.getText().toString())) {
                            estimateOnChainFee((long) (Double.parseDouble(amountInputView.getText().toString()) * 100000000), time);
                        }
                    }
                });
                mSelectAssetPopupWindow.show(v);
            }
        });
        /**
         * @描述: 增加MAX按钮的点击事件，点击将balance的值填入amount输入框中
         * @Description: Add the click event of MAX button, click to fill the balance value into the amount input box
         * @author: Tong ChangHui
         * @E-mail: tch081092@gmail.com
         */
        TextView maxBtnView = mAlertDialog.findViewById(R.id.tv_btn_set_amount_max);
        maxBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountInputView.setText(assetBalanceMax);
            }
        });
        Button speedButton = mAlertDialog.findViewById(R.id.btn_speed);
        speedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectSpeedPopupWindow = new SelectSpeedPopupWindow(mContext);
                mSelectSpeedPopupWindow.setOnItemClickCallback(new SelectSpeedPopupWindow.ItemCleckListener() {
                    @Override
                    public void onItemClick(View view) {
                        switch (view.getId()) {
                            case R.id.tv_slow:
                                speedButton.setText(R.string.slow);
                                time = 1; // 10 Minutes
                                if (!StringUtils.isEmpty(amountInputView.getText().toString())) {
                                    estimateOnChainFee((long) (Double.parseDouble(amountInputView.getText().toString()) * 100000000), time);
                                }
                                break;
                            case R.id.tv_medium:
                                speedButton.setText(R.string.medium);
                                time = 6 * 6; // 6 Hours
                                if (!StringUtils.isEmpty(amountInputView.getText().toString())) {
                                    estimateOnChainFee((long) (Double.parseDouble(amountInputView.getText().toString()) * 100000000), time);
                                }
                                break;
                            case R.id.tv_fast:
                                speedButton.setText(R.string.fast);
                                time = 6 * 24; // 24 Hours
                                if (!StringUtils.isEmpty(amountInputView.getText().toString())) {
                                    estimateOnChainFee((long) (Double.parseDouble(amountInputView.getText().toString()) * 100000000), time);
                                }
                                break;
                        }
                    }
                });
                mSelectSpeedPopupWindow.show(v);
            }
        });
        /**
         * @desc: Click back button then back to step one
         * @描述： 点击back显示step one
         */
        mAlertDialog.findViewById(R.id.layout_back_to_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdit.setText("");
                mAlertDialog.findViewById(R.id.lv_step_one_content).setVisibility(View.VISIBLE);
                mAlertDialog.findViewById(R.id.lv_step_two_content).setVisibility(View.GONE);
                showStepOne();
            }
        });
        /**
         * @desc: Click next button then forward to step three
         * @描述： 点击next跳转到step three
         */
        mAlertDialog.findViewById(R.id.layout_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (assetId != 0) {
                    if (User.getInstance().getNetwork(mContext).equals("testnet")) {
                        if (!toAddressView.getText().toString().startsWith("m") & !toAddressView.getText().toString().startsWith("n")) {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.wallet_address_is_invalid));
                            return;
                        }
                    } else if (User.getInstance().getNetwork(mContext).equals("regtest")) {
                        if (!toAddressView.getText().toString().startsWith("m") & !toAddressView.getText().toString().startsWith("n")) {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.wallet_address_is_invalid));
                            return;
                        }
                    } else { //mainnet
                        if (!toAddressView.getText().toString().startsWith("1")) {
                            ToastUtils.showToast(mContext, mContext.getString(R.string.wallet_address_is_invalid));
                            return;
                        }
                    }
                }
                if (StringUtils.isEmpty(assetBalance)) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.create_invoice_amount));
                    return;
                }
                if (assetBalance.equals("0")) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.amount_greater_than_0));
                    return;
                }
                if ((Double.parseDouble(assetBalance) * 100000000) - (Double.parseDouble(assetBalanceMax) * 100000000) > 0) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.credit_is_running_low));
                    return;
                }
                mAlertDialog.findViewById(R.id.lv_step_two_content).setVisibility(View.GONE);
                mAlertDialog.findViewById(R.id.lv_step_three_content).setVisibility(View.VISIBLE);
                showStepThree();
            }
        });
    }

    /**
     * send step three
     */
    private void showStepThree() {
        /**
         * @描述: 初始化页面初始数据包括：friendName、address、send amount、send value、gas fee、fee value、total used value
         * @Description: The initial data of the initialization page includes friend name, address, send value, gas fee, fee value, total used value
         * @author: Tong ChangHui
         * @E-mail: tch081092@gmail.com
         */
        TextView friendNameView = mAlertDialog.findViewById(R.id.tv_send_friend_name);
        friendNameView.setText(toFriendName);
        TextView friendAddressView = mAlertDialog.findViewById(R.id.tv_send_address);
        friendAddressView.setText(selectAddress);
        ImageView tokenImage = mAlertDialog.findViewById(R.id.iv_send_token_image);
        TextView tokenTypeView = mAlertDialog.findViewById(R.id.tv_send_token_type);
        TextView tokenTypeView2 = mAlertDialog.findViewById(R.id.tv_send_token_type_2);
        TextView sendAmountView = mAlertDialog.findViewById(R.id.tv_send_amount);
        sendAmountView.setText(assetBalance);
        TextView sendAmountValueView = mAlertDialog.findViewById(R.id.tv_send_amount_value);
        TextView feeAmountView = mAlertDialog.findViewById(R.id.tv_send_gas_fee_amount);
        feeAmountView.setText(feeStr + "");
        TextView feeUnitView = mAlertDialog.findViewById(R.id.tv_send_gas_fee_unit);
        TextView feeAmountValueView = mAlertDialog.findViewById(R.id.tv_send_gas_fee_amount_value);
        TextView sendUsedValueView = mAlertDialog.findViewById(R.id.tv_send_used_value);
        if (assetId == 0) {
            DecimalFormat df = new DecimalFormat("0.00");
            tokenImage.setImageResource(R.mipmap.icon_btc_logo_small);
            tokenTypeView.setText("BTC");
            tokenTypeView2.setText("BTC");
            feeUnitView.setText("satoshis");
            sendAmountValueView.setText(df.format(Double.parseDouble(assetBalance) * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
            feeAmountValueView.setText(df.format(Double.parseDouble(String.valueOf(feeStr)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
            String sendUsedValue = (long) (Double.parseDouble(assetBalance) * 100000000) + feeStr + "";
            sendUsedValueView.setText(df.format(Double.parseDouble(sendUsedValue) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
        } else {
            DecimalFormat df = new DecimalFormat("0.00");
            tokenImage.setImageResource(R.mipmap.icon_usdt_logo_small);
            tokenTypeView.setText("dollar");
            tokenTypeView2.setText("dollar");
            feeUnitView.setText("satoshis");
            sendAmountValueView.setText(df.format(Double.parseDouble(assetBalance) * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
            feeAmountValueView.setText(df.format(Double.parseDouble(String.valueOf(feeStr)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
            String sendUsedValue = (long) (Double.parseDouble(assetBalance) * 100000000) + feeStr + "";
            sendUsedValueView.setText(df.format(Double.parseDouble(sendUsedValue) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
        }
        /**
         * @desc: Click back button then back to step two
         * @描述： 点击back显示step two
         */
        mAlertDialog.findViewById(R.id.layout_back_to_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.findViewById(R.id.lv_step_two_content).setVisibility(View.VISIBLE);
                mAlertDialog.findViewById(R.id.lv_step_three_content).setVisibility(View.GONE);
                showStepTwo();
            }
        });
        /**
         * @desc: Click confirm button then forward to succeed or failed step
         * @描述： 点击 confirm button 显示成功或者失败的页面
         */
        mAlertDialog.findViewById(R.id.layout_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e(TAG, selectAddress);
                LogUtils.e(TAG, assetBalance);
                LogUtils.e(TAG, String.valueOf(time));
                LogUtils.e(TAG, String.valueOf(assetId));
                mLoadingDialog.show();
                if (assetId == 0) {
                    LightningOuterClass.SendCoinsFromRequest sendRequest = LightningOuterClass.SendCoinsFromRequest.newBuilder()
                            .setAddr(selectAddress)
                            .setFrom(User.getInstance().getWalletAddress(mContext))
                            .setAmount((long) (Double.parseDouble(assetBalance) * 100000000))
                            .setTargetConf(time)
                            .build();
                    Obdmobile.oB_SendCoinsFrom(sendRequest.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            mLoadingDialog.dismiss();
                                            mAlertDialog.findViewById(R.id.lv_step_failed_content).setVisibility(View.VISIBLE);
                                            mAlertDialog.findViewById(R.id.lv_step_three_content).setVisibility(View.GONE);
                                            showStepFailed(e.getMessage());
                                        }
                                    }, 2000);
                                }
                            });
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            try {
                                LightningOuterClass.SendCoinsResponse resp = LightningOuterClass.SendCoinsResponse.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------sendCoinsFromOnResponse-----------------" + resp);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferences sp = mContext.getSharedPreferences("SP_ADDR_LIST", Activity.MODE_PRIVATE);
                                        String addrListJson = sp.getString("addrListKey", "");
                                        if (StringUtils.isEmpty(addrListJson)) {
                                            list = new ArrayList<>();
                                            AddressEntity entity = new AddressEntity();
                                            entity.setName("unname");
                                            entity.setAddress(selectAddress);
                                            list.add(entity);
                                            Gson gson = new Gson();
                                            String jsonStr = gson.toJson(list);
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("addrListKey", jsonStr);
                                            editor.commit();
                                        } else {
                                            Gson gson = new Gson();
                                            list = gson.fromJson(addrListJson, new TypeToken<List<AddressEntity>>() {
                                            }.getType());
                                            AddressEntity entity = new AddressEntity();
                                            entity.setName("unname");
                                            entity.setAddress(selectAddress);
                                            list.add(entity);
                                            String jsonStr = gson.toJson(list);
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("addrListKey", jsonStr);
                                            editor.commit();
                                        }
                                        // 存储txid
                                        SharedPreferences txidSp = mContext.getSharedPreferences("SP_TXID_LIST", Activity.MODE_PRIVATE);
                                        String txidListJson = txidSp.getString("txidListKey", "");
                                        if (StringUtils.isEmpty(txidListJson)) {
                                            txidList = new ArrayList<>();
                                            txidList.add(resp.getTxid());
                                            Gson gson = new Gson();
                                            String jsonStr = gson.toJson(txidList);
                                            SharedPreferences.Editor editor = txidSp.edit();
                                            editor.putString("txidListKey", jsonStr);
                                            editor.commit();
                                        } else {
                                            Gson gson = new Gson();
                                            txidList = gson.fromJson(txidListJson, new TypeToken<List<String>>() {
                                            }.getType());
                                            txidList.add(resp.getTxid());
                                            String jsonStr = gson.toJson(txidList);
                                            SharedPreferences.Editor editor = txidSp.edit();
                                            editor.putString("txidListKey", jsonStr);
                                            editor.commit();
                                        }
                                        EventBus.getDefault().post(new SendSuccessEvent());
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                mLoadingDialog.dismiss();
//                                                mAlertDialog.dismiss();
//                                                mSendSuccessDialog = new SendSuccessDialog(mContext);
//                                                mSendSuccessDialog.show("success");
                                                mAlertDialog.findViewById(R.id.lv_step_three_content).setVisibility(View.GONE);
                                                mAlertDialog.findViewById(R.id.lv_step_success_content).setVisibility(View.VISIBLE);
                                                showStepSuccess(resp.getTxid());
                                            }
                                        }, 2000);
                                    }
                                });
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    LightningOuterClass.SendCoinsFromRequest sendRequest = LightningOuterClass.SendCoinsFromRequest.newBuilder()
                            .setAssetId((int) assetId)
                            .setAddr(selectAddress)
                            .setFrom(User.getInstance().getWalletAddress(mContext))
                            .setAssetAmount((long) (Double.parseDouble(assetBalance) * 100000000))
                            .setTargetConf(time)
                            .build();
                    Obdmobile.oB_SendCoinsFrom(sendRequest.toByteArray(), new Callback() {
                        @Override
                        public void onError(Exception e) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            mLoadingDialog.dismiss();
                                            mAlertDialog.findViewById(R.id.lv_step_failed_content).setVisibility(View.VISIBLE);
                                            mAlertDialog.findViewById(R.id.lv_step_three_content).setVisibility(View.GONE);
                                            showStepFailed(e.getMessage());
                                        }
                                    }, 2000);
                                }
                            });
                        }

                        @Override
                        public void onResponse(byte[] bytes) {
                            try {
                                LightningOuterClass.SendCoinsResponse resp = LightningOuterClass.SendCoinsResponse.parseFrom(bytes);
                                LogUtils.e(TAG, "------------------sendCoinsFromOnResponse-----------------" + resp);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SharedPreferences sp = mContext.getSharedPreferences("SP_ADDR_LIST", Activity.MODE_PRIVATE);
                                        String addrListJson = sp.getString("addrListKey", "");
                                        if (StringUtils.isEmpty(addrListJson)) {
                                            list = new ArrayList<>();
                                            AddressEntity entity = new AddressEntity();
                                            entity.setName("unname");
                                            entity.setAddress(selectAddress);
                                            list.add(entity);
                                            Gson gson = new Gson();
                                            String jsonStr = gson.toJson(list);
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("addrListKey", jsonStr);
                                            editor.commit();
                                        } else {
                                            Gson gson = new Gson();
                                            list = gson.fromJson(addrListJson, new TypeToken<List<AddressEntity>>() {
                                            }.getType());
                                            AddressEntity entity = new AddressEntity();
                                            entity.setName("unname");
                                            entity.setAddress(selectAddress);
                                            list.add(entity);
                                            String jsonStr = gson.toJson(list);
                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("addrListKey", jsonStr);
                                            editor.commit();
                                        }
                                        // 存储txid
                                        SharedPreferences txidSp = mContext.getSharedPreferences("SP_TXID_LIST", Activity.MODE_PRIVATE);
                                        String txidListJson = txidSp.getString("txidListKey", "");
                                        if (StringUtils.isEmpty(txidListJson)) {
                                            txidList = new ArrayList<>();
                                            txidList.add(resp.getTxid());
                                            Gson gson = new Gson();
                                            String jsonStr = gson.toJson(txidList);
                                            SharedPreferences.Editor editor = txidSp.edit();
                                            editor.putString("txidListKey", jsonStr);
                                            editor.commit();
                                        } else {
                                            Gson gson = new Gson();
                                            txidList = gson.fromJson(txidListJson, new TypeToken<List<String>>() {
                                            }.getType());
                                            txidList.add(resp.getTxid());
                                            String jsonStr = gson.toJson(txidList);
                                            SharedPreferences.Editor editor = txidSp.edit();
                                            editor.putString("txidListKey", jsonStr);
                                            editor.commit();
                                        }
                                        EventBus.getDefault().post(new SendSuccessEvent());
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                mLoadingDialog.dismiss();
//                                                mAlertDialog.dismiss();
//                                                mSendSuccessDialog = new SendSuccessDialog(mContext);
//                                                mSendSuccessDialog.show("success");
                                                mAlertDialog.findViewById(R.id.lv_step_three_content).setVisibility(View.GONE);
                                                mAlertDialog.findViewById(R.id.lv_step_success_content).setVisibility(View.VISIBLE);
                                                showStepSuccess(resp.getTxid());
                                            }
                                        }, 2000);
                                    }
                                });
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * send step success
     */
    private void showStepSuccess(String transactionHash) {
        TextView transactionHashTv = mAlertDialog.findViewById(R.id.tv_send_success_transaction_hash);
        TextView successAddressTv = mAlertDialog.findViewById(R.id.tv_send_success_address);
        TextView successExecutedTv = mAlertDialog.findViewById(R.id.tv_send_success_executed);
        ImageView successTokenImageIv = mAlertDialog.findViewById(R.id.iv_send_success_token_image);
        TextView successTokenTypeTv = mAlertDialog.findViewById(R.id.tv_send_success_token_type);
        TextView successAmountTv = mAlertDialog.findViewById(R.id.tv_send_success_amount);
        TextView successAmountUnitTv = mAlertDialog.findViewById(R.id.tv_send_success_amount_unit);
        TextView successAmountValueTv = mAlertDialog.findViewById(R.id.tv_send_success_amount_value);
        TextView gasFeeAmountTv = mAlertDialog.findViewById(R.id.tv_send_success_gas_fee_amount);
        TextView gasFeeAmountUnitTv = mAlertDialog.findViewById(R.id.tv_send_success_gas_fee_unit);
        TextView successTotalValueTv = mAlertDialog.findViewById(R.id.tv_send_success_total_value);
        transactionHashTv.setText(transactionHash);
        successExecutedTv.setText(DateUtils.formatCurrentTime());
        successAddressTv.setText(selectAddress);
        successAmountTv.setText(assetBalance);
        gasFeeAmountTv.setText(feeStr + "");
        if (assetId == 0) {
            DecimalFormat df = new DecimalFormat("0.00");
            successTokenImageIv.setImageResource(R.mipmap.icon_btc_logo_small);
            successTokenTypeTv.setText("BTC");
            successAmountUnitTv.setText("BTC");
            gasFeeAmountUnitTv.setText("satoshis");
            successAmountValueTv.setText(df.format(Double.parseDouble(assetBalance) * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
            String sendUsedValue = (long) (Double.parseDouble(assetBalance) * 100000000) + feeStr + "";
            successTotalValueTv.setText(df.format(Double.parseDouble(sendUsedValue) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
        } else {
            DecimalFormat df = new DecimalFormat("0.00");
            successTokenImageIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            successTokenTypeTv.setText("dollar");
            successAmountUnitTv.setText("dollar");
            gasFeeAmountUnitTv.setText("satoshis");
            successAmountValueTv.setText(df.format(Double.parseDouble(assetBalance) * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
            String sendUsedValue = (long) (Double.parseDouble(assetBalance) * 100000000) + feeStr + "";
            successTotalValueTv.setText(df.format(Double.parseDouble(sendUsedValue) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
        }
        mAlertDialog.findViewById(R.id.layout_add_to_addressbook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.findViewById(R.id.layout_step_add_to_addressbook).setVisibility(View.VISIBLE);
                mAlertDialog.findViewById(R.id.lv_step_success_content).setVisibility(View.GONE);
                showStepAddAddressBook();
            }
        });
    }

    /**
     * send step add to addressbook
     */
    private void showStepAddAddressBook() {
        TextView addressTv = mAlertDialog.findViewById(R.id.tv_address);
        TextView nicknameEdit = mAlertDialog.findViewById(R.id.edit_nickname);
        addressTv.setText(selectAddress);
        mAlertDialog.findViewById(R.id.layout_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = nicknameEdit.getText().toString();
                SharedPreferences sp = mContext.getSharedPreferences("SP_ADDR_LIST", Activity.MODE_PRIVATE);
                String addrListJson = sp.getString("addrListKey", "");
                if (StringUtils.isEmpty(addrListJson)) {
                    list = new ArrayList<>();
                    AddressEntity entity = new AddressEntity();
                    if (StringUtils.isEmpty(nickname)) {
                        entity.setName("unname");
                    } else {
                        entity.setName(nickname);
                    }
                    entity.setName(nickname);
                    entity.setAddress(selectAddress);
                    list.add(entity);
                    Gson gson = new Gson();
                    String jsonStr = gson.toJson(list);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("addrListKey", jsonStr);
                    editor.commit();
                } else {
                    Gson gson = new Gson();
                    list = gson.fromJson(addrListJson, new TypeToken<List<AddressEntity>>() {
                    }.getType());
                    AddressEntity entity = new AddressEntity();
                    if (StringUtils.isEmpty(nickname)) {
                        entity.setName("unname");
                    } else {
                        entity.setName(nickname);
                    }
                    entity.setAddress(selectAddress);
                    list.add(entity);
                    String jsonStr = gson.toJson(list);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("addrListKey", jsonStr);
                    editor.commit();
                }
                mAlertDialog.dismiss();
            }
        });
    }

    /**
     * send step failed
     */
    private void showStepFailed(String message) {
        TextView failedNameTcv = mAlertDialog.findViewById(R.id.tv_failed_name);
        failedNameTcv.setText(toFriendName);
        TextView friendAddressTv = mAlertDialog.findViewById(R.id.tv_failed_address);
        friendAddressTv.setText(selectAddress);
        ImageView assetLogoIv = mAlertDialog.findViewById(R.id.iv_asset_logo);
        TextView assetUnitTv = mAlertDialog.findViewById(R.id.tv_asset_unit);
        TextView failedAmountUnitTv = mAlertDialog.findViewById(R.id.tv_failed_amount_unit);
        TextView failedAmountTv = mAlertDialog.findViewById(R.id.tv_failed_amount);
        failedAmountTv.setText(assetBalance);
        TextView failedGasFeeTv = mAlertDialog.findViewById(R.id.tv_failed_gas_fee);
        failedGasFeeTv.setText(feeStr + "");
        TextView failedGasFeeUnitTv = mAlertDialog.findViewById(R.id.tv_failed_gas_fee_unit);
        TextView failedTotalValueTv = mAlertDialog.findViewById(R.id.tv_failed_total_value);
        TextView failedMessageTv = mAlertDialog.findViewById(R.id.tv_failed_message);
        failedMessageTv.setText(message);
        if (assetId == 0) {
            assetLogoIv.setImageResource(R.mipmap.icon_btc_logo_small);
            assetUnitTv.setText("BTC");
            failedAmountUnitTv.setText("BTC");
            failedGasFeeUnitTv.setText("satoshis");
            DecimalFormat df = new DecimalFormat("0.00");
            String totalValue = (long) (Double.parseDouble(assetBalance) * 100000000) + feeStr + "";
            failedTotalValueTv.setText(df.format(Double.parseDouble(totalValue) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
        } else {
            assetLogoIv.setImageResource(R.mipmap.icon_usdt_logo_small);
            assetUnitTv.setText("dollar");
            failedAmountUnitTv.setText("dollar");
            failedGasFeeUnitTv.setText("satoshis");
            DecimalFormat df = new DecimalFormat("0.00");
            String totalValue = (long) (Double.parseDouble(assetBalance) * 100000000) + feeStr + "";
            failedTotalValueTv.setText(df.format(Double.parseDouble(totalValue) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
        }
        // 点击back
        mAlertDialog.findViewById(R.id.layout_back_to_three).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.findViewById(R.id.lv_step_failed_content).setVisibility(View.GONE);
                mAlertDialog.findViewById(R.id.lv_step_three_content).setVisibility(View.VISIBLE);
                showStepThree();
            }
        });
        RelativeLayout shareLayout = mAlertDialog.findViewById(R.id.layout_share);
        mAlertDialog.findViewById(R.id.layout_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLayout.setVisibility(View.GONE);
            }
        });
        // 点击explorer
        mAlertDialog.findViewById(R.id.layout_explorer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLayout.setVisibility(View.VISIBLE);
            }
        });
        /**
         * @描述： 点击 facebook
         * @desc: click facebook button
         */
        mAlertDialog.findViewById(R.id.iv_facebook_share).setOnClickListener(new View.OnClickListener() {
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
        mAlertDialog.findViewById(R.id.iv_twitter_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(ShareUtil.getTwitterIntent(mContext, selectAddress));
                shareLayout.setVisibility(View.GONE);
            }
        });
    }

    /**
     * @描述： send list列表适配器
     * @desc: Adapter for send list
     */
    private class MyAdapter extends CommonRecyclerAdapter<AddressEntity> {

        public MyAdapter(Context context, List<AddressEntity> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final AddressEntity item) {
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toFriendName = item.getName();
                    selectAddress = item.getAddress();
                    mAlertDialog.findViewById(R.id.lv_step_one_content).setVisibility(View.GONE);
                    mAlertDialog.findViewById(R.id.lv_step_two_content).setVisibility(View.VISIBLE);
                    showStepTwo();
                }
            });
            holder.setText(R.id.tv_group_name, item.getName().substring(0, 1));
            holder.setText(R.id.tv_send_list_name, item.getName());
            holder.setText(R.id.tv_send_list_address, item.getAddress());
        }
    }

    /**
     * calculate fee
     */
    private void estimateOnChainFee(long amount, int targetConf) {
        // let LND estimate fee
        LightningOuterClass.ObEstimateFeeRequest asyncEstimateFeeRequest;
        if (assetId == 0) {
            asyncEstimateFeeRequest = LightningOuterClass.ObEstimateFeeRequest.newBuilder()
                    .setAddr(selectAddress)
                    .setFrom(User.getInstance().getWalletAddress(mContext))
                    .setAmount(amount)
                    .setTargetConf(targetConf)
                    .build();
        } else {
            asyncEstimateFeeRequest = LightningOuterClass.ObEstimateFeeRequest.newBuilder()
                    .setAssetId((int) assetId)
                    .setAddr(selectAddress)
                    .setFrom(User.getInstance().getWalletAddress(mContext))
                    .setAssetAmount(amount)
                    .setTargetConf(targetConf)
                    .build();
        }
        Obdmobile.oB_EstimateFee(asyncEstimateFeeRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------asyncEstimateFeeOnError------------------" + e.getMessage());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        feeStr = 0;
                        sendFeeTv.setText(feeStr + "");
                        sendFeeExchangeTv.setText("0");
                    }
                });
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.EstimateFeeResponse resp = LightningOuterClass.EstimateFeeResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------asyncEstimateFeeOnResponse-----------------" + resp);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            feeStr = resp.getFeeSat();
                            sendFeeTv.setText(feeStr + "");
                            DecimalFormat df = new DecimalFormat("0.00");
                            if (assetId == 0) {
                                sendFeeExchangeTv.setText(df.format(Double.parseDouble(String.valueOf(feeStr)) / 100000000 * Double.parseDouble(User.getInstance().getBtcPrice(mContext))));
                            } else {
                                sendFeeExchangeTv.setText(df.format(Double.parseDouble(String.valueOf(feeStr)) / 100000000 * Double.parseDouble(User.getInstance().getUsdtPrice(mContext))));
                            }
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create a new wallet address first, and then request the interface of each asset balance list
     * 先创建新的钱包地址后再去请求各资产余额列表的接口
     */
    public void fetchWalletBalance() {
        LightningOuterClass.WalletBalanceByAddressRequest walletBalanceByAddressRequest = LightningOuterClass.WalletBalanceByAddressRequest.newBuilder()
                .setAddress(User.getInstance().getWalletAddress(mContext))
                .build();
        Obdmobile.oB_WalletBalanceByAddress(walletBalanceByAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------walletBalanceByAddressOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.WalletBalanceByAddressResponse resp = LightningOuterClass.WalletBalanceByAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------walletBalanceByAddressOnResponse-----------------" + resp);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (resp.getConfirmedBalance() == 0) {
                                DecimalFormat df = new DecimalFormat("0.00");
                                assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getConfirmedBalance())) / 100000000);
                            } else {
                                DecimalFormat df = new DecimalFormat("0.00######");
                                assetBalanceMax = df.format(Double.parseDouble(String.valueOf(resp.getConfirmedBalance())) / 100000000);
                            }
                            assetsBalanceTv.setText(assetBalanceMax);
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onScanSendUpdated(String result) {
        if (mAddressData.size() == 0) {
            toFriendName = "unname";
        } else {
            toFriendName = "unname";
            for (AddressEntity entity : mAddressData) {
                if (entity.getAddress().equals(result)) {
                    toFriendName = entity.getName();
                }
            }
        }
        selectAddress = result;
        mAlertDialog.findViewById(R.id.lv_step_one_content).setVisibility(View.GONE);
        mAlertDialog.findViewById(R.id.lv_step_two_content).setVisibility(View.VISIBLE);
        showStepTwo();
    }

    // 循环重复数据
    public static void removeDuplicate(List<AddressEntity> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getAddress().equals(list.get(i).getAddress())) {
                    if (list.get(j).getName().equals("unname") & !list.get(i).getName().equals("unname")) {
                        list.remove(j);
                    } else if (!list.get(j).getName().equals("unname") & list.get(i).getName().equals("unname")) {
                        list.remove(i);
                    } else if (!list.get(j).getName().equals("unname") & !list.get(i).getName().equals("unname")) {
                        list.remove(i);
                    } else if (list.get(j).getName().equals("unname") & list.get(i).getName().equals("unname")) {
                        list.remove(i);
                    }
                }
            }
        }
        System.out.println(list);
    }

    public void release() {
        Wallet.getInstance().unregisterScanSendListener(this);
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }
}
