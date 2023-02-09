package com.omni.testnet.view.popupwindow;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.testnet.R;
import com.omni.testnet.baselibrary.utils.LogUtils;
import com.omni.testnet.baselibrary.utils.StringUtils;
import com.omni.testnet.baselibrary.view.BasePopWindow;
import com.omni.testnet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.testnet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.testnet.entity.event.SelectAccountEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;


/**
 * @description: Account manage popup window
 * @描述: 用户管理弹窗
 * @author: Tong ChangHui
 * @date: 2022-10-14
 */

public class AccountManagePopupWindow {
    private static final String TAG = AccountManagePopupWindow.class.getSimpleName();

    private Context accountContext;
    private BasePopWindow accountBasePopWindow;
    private List<LightningOuterClass.RecAddress> addressData = new ArrayList<>();
    private List<LightningOuterClass.RecAddress> mDataSearch = new ArrayList<>();
    private AccountAdapter accountAdapter;
    View accountView;
    EditText searchEdit;
    TextView recentsAddressTv;
    RecyclerView accountRecyclerView;

    public AccountManagePopupWindow(Context context) {
        this.accountContext = context;
    }

    public void show(final View view) {
        accountView = view;
        if (accountBasePopWindow == null) {
            accountBasePopWindow = new BasePopWindow(accountContext);
            View rootView = accountBasePopWindow.setContentView(R.layout.layout_popupwindow_account_manage);
            accountBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            accountBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            accountBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            searchEdit = rootView.findViewById(R.id.edit_search);
            recentsAddressTv = rootView.findViewById(R.id.tv_recents_address);
            accountRecyclerView = rootView.findViewById(R.id.rv_account_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(accountContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            accountRecyclerView.setLayoutManager(layoutManager);
            getListRecAddress();
            // Search
            searchEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mDataSearch.clear();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String searchStr = String.valueOf(s);
                    if (s.length() > 0) {
                        for (LightningOuterClass.RecAddress recAddress: addressData) {
                            if (!StringUtils.isEmpty(recAddress.getAddre()) ) {
                                if ((recAddress.getAddre().contains(searchStr))) {
                                    mDataSearch.add(recAddress);
                                }
                            }
                        }
                        accountAdapter = new AccountAdapter(accountContext, mDataSearch, R.layout.layout_item_accounts_list);
                        accountRecyclerView.setAdapter(accountAdapter);
                        accountAdapter.notifyDataSetChanged();
                    } else if (s == null || s.length() == 0) {
                        mDataSearch.clear();
                        getListRecAddress();
                    }
                }
            });

            /**
             * @description: Click close button then close popup window
             * @描述: 点击 close 按钮关闭弹窗
             * @author: Tong ChangHui
             * @date: 2022-10-14
             */
            rootView.findViewById(R.id.lv_cancel_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountBasePopWindow.dismiss();
                }
            });

            rootView.findViewById(R.id.lv_account_recent_first).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectAccountEvent event = new SelectAccountEvent();
                    event.setAddress(addressData.get(0).getAddre());
                    EventBus.getDefault().post(event);
                    accountBasePopWindow.dismiss();
                }
            });

            rootView.findViewById(R.id.lv_account_recent_second).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountBasePopWindow.dismiss();
                }
            });

            if (accountBasePopWindow.isShowing()) {
                return;
            }
            accountBasePopWindow.showAtLocation(accountView, Gravity.CENTER, 0, 0);
        }

    }

    /**
     * @description: getListRecAddress
     * @描述： 获取多个钱包地址
     */
    private void getListRecAddress() {
        LightningOuterClass.ListRecAddressRequest listRecAddressRequest = LightningOuterClass.ListRecAddressRequest.newBuilder()
                .build();
        Obdmobile.oB_ListRecAddress(listRecAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------listRecAddressOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.ListRecAddressResponse resp = LightningOuterClass.ListRecAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------listRecAddressOnResponse-----------------" + resp);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            recentsAddressTv.setText(resp.getItems(0).getAddre());
                            addressData.clear();
                            addressData.addAll(resp.getItemsList());
                            accountAdapter = new AccountAdapter(accountContext, addressData, R.layout.layout_item_accounts_list);
                            accountRecyclerView.setAdapter(accountAdapter);
                            accountAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @description:
     * @描述:
     * @author: Tong ChangHui
     * @date: 2022-10-14
     */

    private class AccountAdapter extends CommonRecyclerAdapter<LightningOuterClass.RecAddress> {
        public AccountAdapter(Context context, List<LightningOuterClass.RecAddress> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, int position, LightningOuterClass.RecAddress item) {
//            holder.setText(R.id.tv_account_nickname, item.getAccountName());
//            holder.setText(R.id.tv_account_address, item.getAccountAddress());
//            holder.setText(R.id.tv_account_value, item.getAccountBalance() + "");
//            String avatarSrc = item.getAvatarSrc();
//            TextView avatarText = holder.getView(R.id.tv_avatar_text);
//            ImageView avatarImage = holder.getView(R.id.iv_avatar);
//            if (avatarSrc != null) {
//                avatarImage.setImageURI(Uri.parse(item.getAvatarSrc()));
//                avatarImage.setVisibility(View.VISIBLE);
//                avatarText.setVisibility(View.GONE);
//            } else {
//                switch (item.getAvatarColor()) {
//                    default:
//                        avatarText.setBackground(accountContext.getResources().getDrawable(R.drawable.bg_conner_24_black));
//                        break;
//                    case 1:
//                        avatarText.setBackground(accountContext.getResources().getDrawable(R.drawable.bg_conner_24_green));
//                        break;
//                    case 2:
//                        avatarText.setBackground(accountContext.getResources().getDrawable(R.drawable.bg_conner_24_orange));
//                        break;
//                    case 3:
//                        avatarText.setBackground(accountContext.getResources().getDrawable(R.drawable.bg_conner_24_black));
//                        break;
//                    case 4:
//                        avatarText.setBackground(accountContext.getResources().getDrawable(R.drawable.bg_conner_24_blue));
//                        break;
//                }
//                avatarText.setText(item.getAvatarString());
//                avatarText.setVisibility(View.VISIBLE);
//                avatarImage.setVisibility(View.GONE);
//            }
            holder.setText(R.id.tv_avatar_text, item.getAddre().substring(0, 1));
            holder.setText(R.id.tv_account_address, item.getAddre());
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectAccountEvent event = new SelectAccountEvent();
                    event.setAddress(item.getAddre());
                    EventBus.getDefault().post(event);
                    accountBasePopWindow.dismiss();
                }
            });
        }
    }

    public void release() {
        if (accountBasePopWindow != null) {
            accountBasePopWindow.dismiss();
            accountBasePopWindow = null;
        }
    }
}
