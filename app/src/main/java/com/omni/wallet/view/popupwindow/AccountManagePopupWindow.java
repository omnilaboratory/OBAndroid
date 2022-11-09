package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.listItems.Account;

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
    private List<Account> accountData = new ArrayList<Account>();
    private List<LightningOuterClass.RecAddress> addressData = new ArrayList<>();
    private AccountAdapter accountAdapter;
    View accountView;

    public AccountManagePopupWindow(Context context) {
        this.accountContext = context;
    }

    public void initAccountData() {
        Account account_1 = new Account("1mn8382odjd.........34gy7", 1.78, "Home");
        account_1.setAvatarColor(1);
        Account account_2 = new Account("2nm8382odjd.........fe689", 10.00, "Wife");
        account_2.setAvatarColor(3);
        Account account_3 = new Account("2nmvfergeegd.........efecw", 30.00, "Gift");
        account_3.setAvatarColor(2);
        Account account_4 = new Account("1mn8382odjd.........34gy7", 125.00, "Salary");
        account_4.setAvatarColor(4);
        Account account_5 = new Account("2nmvfergeegd.........efecw", 125.00, "For Child");
        account_5.setAvatarColor(3);
        Account account_6 = new Account("2nm8382odjd.........fe689", 78.04, "Self Gift");
        account_6.setAvatarColor(4);
        Account account_7 = new Account("2nmvfergeegd.........efecw", 34.89, "Game");
        account_7.setAvatarColor(1);
        Account account_8 = new Account("1mn8382odjd.........34gy7", 1000.78, "Fee");
        account_8.setAvatarColor(2);
        accountData.add(account_1);
        accountData.add(account_2);
        accountData.add(account_3);
        accountData.add(account_4);
        accountData.add(account_5);
        accountData.add(account_6);
        accountData.add(account_7);
        accountData.add(account_8);
        LightningOuterClass.ListRecAddressRequest listRecAddressRequest = LightningOuterClass.ListRecAddressRequest.newBuilder()
                .build();
        Obdmobile.listRecAddress(listRecAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------listRecAddressOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                try {
                    LightningOuterClass.ListRecAddressResponse resp = LightningOuterClass.ListRecAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------listRecAddressOnResponse-----------------" + resp);
                    addressData.clear();
                    addressData.addAll(resp.getItemsList());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void show(final View view) {
        accountView = view;
        if (accountBasePopWindow == null) {
            initAccountData();
            accountBasePopWindow = new BasePopWindow(accountContext);
            View rootView = accountBasePopWindow.setContentView(R.layout.layout_popupwindow_account_manage);
            accountBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            accountBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            accountBasePopWindow.setAnimationStyle(R.style.popup_anim_style);
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
                    accountBasePopWindow.dismiss();
                }
            });

            rootView.findViewById(R.id.lv_account_recent_second).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accountBasePopWindow.dismiss();
                }
            });
            RecyclerView accountRecyclerView = rootView.findViewById(R.id.rv_account_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(accountContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            accountRecyclerView.setLayoutManager(layoutManager);
            accountAdapter = new AccountAdapter(accountContext, accountData, R.layout.layout_item_accounts_list);
            accountRecyclerView.setAdapter(accountAdapter);

            if (accountBasePopWindow.isShowing()) {
                return;
            }
            accountBasePopWindow.showAtLocation(accountView, Gravity.CENTER, 0, 0);
        }

    }

    /**
     * @description:
     * @描述:
     * @author: Tong ChangHui
     * @date: 2022-10-14
     */

    private class AccountAdapter extends CommonRecyclerAdapter<Account> {
        public AccountAdapter(Context context, List<Account> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, int position, Account item) {
            holder.setText(R.id.tv_account_nickname, item.getAccountName());
            holder.setText(R.id.tv_account_address, item.getAccountAddress());
            holder.setText(R.id.tv_account_value, item.getAccountBalance() + "");
            String avatarSrc = item.getAvatarSrc();
            TextView avatarText = holder.getView(R.id.tv_avatar_text);
            ImageView avatarImage = holder.getView(R.id.iv_avatar);
            if (avatarSrc != null) {
                avatarImage.setImageURI(Uri.parse(item.getAvatarSrc()));
                avatarImage.setVisibility(View.VISIBLE);
                avatarText.setVisibility(View.GONE);
            } else {
                switch (item.getAvatarColor()) {
                    default:
                        avatarText.setBackground(accountContext.getResources().getDrawable(R.drawable.bg_conner_24_black));
                        break;
                    case 1:
                        avatarText.setBackground(accountContext.getResources().getDrawable(R.drawable.bg_conner_24_green));
                        break;
                    case 2:
                        avatarText.setBackground(accountContext.getResources().getDrawable(R.drawable.bg_conner_24_orange));
                        break;
                    case 3:
                        avatarText.setBackground(accountContext.getResources().getDrawable(R.drawable.bg_conner_24_black));
                        break;
                    case 4:
                        avatarText.setBackground(accountContext.getResources().getDrawable(R.drawable.bg_conner_24_blue));
                        break;
                }
                avatarText.setText(item.getAvatarString());
                avatarText.setVisibility(View.VISIBLE);
                avatarImage.setVisibility(View.GONE);
            }
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
