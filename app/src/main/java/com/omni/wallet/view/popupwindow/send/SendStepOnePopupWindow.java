package com.omni.wallet.view.popupwindow.send;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.PermissionUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.listItems.Friend;
import com.omni.wallet.listItems.FriendGroup;
import com.omni.wallet.ui.activity.ScanActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * SendStepOne的弹窗
 */
public class SendStepOnePopupWindow {
    private static final String TAG = SendStepOnePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private List<String> mData = new ArrayList<>();
    private MyAdapter mAdapter;
    SendStepTwoPopupWindow mSendStepTwoPopupWindow;
    View mView;
    private List<FriendGroup> friendGroups  = new ArrayList<FriendGroup>();

    public SendStepOnePopupWindow(Context context) {
        this.mContext = context;
    }

//    测试使用数据
    public void friendGroupsData(){
        Friend alice = new Friend("Alice","1mn8382odjd.........34gy7");
        Friend abbe = new Friend("Abbe","2nm8382odjd.........dfe689");
        List<Friend> groupA = new ArrayList<Friend>();
        groupA.add(alice);
        groupA.add(abbe);
        FriendGroup friendGroupA =  new FriendGroup("A",groupA);

        Friend bob = new Friend("Bob","1mn8382odjd.........34gy7");
        Friend bill = new Friend("Bill","2nm8382odjd.........dfe689");
        Friend boss = new Friend("Boss","2nm8382odjd.........dfe689");
        List<Friend> groupB = new ArrayList<Friend>();
        groupB.add(bob);
        groupB.add(bill);
        groupB.add(boss);
        groupB.add(bob);
        groupB.add(bill);
        groupB.add(boss);
        FriendGroup friendGroupB =  new FriendGroup("B",groupB);

        Friend charli = new Friend("Charli","1mn8382odjd.........34gy7");
        List<Friend> groupC = new ArrayList<Friend>();
        groupC.add(charli);
        FriendGroup friendGroupC =  new FriendGroup("C",groupC);

        friendGroups.add(friendGroupA);
        friendGroups.add(friendGroupB);
        friendGroups.add(friendGroupC);
    }


    public void show(final View view) {
        if (mBasePopWindow == null) {
            mView = view;
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_send_stepone);
            mBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
//            mBasePopWindow.setBackgroundDrawable(new ColorDrawable(0xD1123A50));
            mBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            friendGroupsData();
            // send list RecyclerView
            RecyclerView mRecyclerView = rootView.findViewById(R.id.recycler_send_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mAdapter = new MyAdapter(mContext, friendGroups, R.layout.layout_item_send_list);
            mRecyclerView.setAdapter(mAdapter);

            // 点击scan
            rootView.findViewById(R.id.iv_scan).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PermissionUtils.launchCamera((Activity) mContext, new PermissionUtils.PermissionCallback() {
                        @Override
                        public void onRequestPermissionSuccess() {
                            mBasePopWindow.dismiss();
                            Intent intent = new Intent(mContext, ScanActivity.class);
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
            mBasePopWindow.showAtLocation(mView, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * send list列表适配器
     */
    private class MyAdapter extends CommonRecyclerAdapter<FriendGroup> {

        public MyAdapter(Context context, List<FriendGroup> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final FriendGroup item) {
            holder.setText(R.id.tv_group_name,item.getGroupName());
            holder.getView(R.id.v_deliver).setVisibility(View.INVISIBLE);

            LinearLayout ListContentView = holder.getView(R.id.lv_friend_item_list);

            List<Friend> friendListInGroup = item.getGroupFriend();

            for (int i = 0 ; i<friendListInGroup.size();i++){
                String friendName = friendListInGroup.get(i).getFriendName();
                String address = friendListInGroup.get(i).getAddress();

                RelativeLayout friendItemContain = new RelativeLayout(mContext);
                RelativeLayout.LayoutParams friendItemContainParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                friendItemContain.setLayoutParams(friendItemContainParams);

                LinearLayout friendItemContent = new LinearLayout(mContext);
                LinearLayout.LayoutParams friendItemContentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                friendItemContent.setPadding(0,20,0,20);
                friendItemContent.setOrientation(LinearLayout.HORIZONTAL);
                friendItemContent.setLayoutParams(friendItemContentParams);

                TextView friendNameView = new TextView(mContext);
                LinearLayout.LayoutParams friendNameViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                friendNameView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                friendNameView.setGravity(Gravity.CENTER);
                friendNameView.setTextSize(16);
                friendNameView.setTextColor(mContext.getResources().getColor(R.color.color_99_transparent));
                friendNameView.setText(friendName);
                friendNameView.setLayoutParams(friendNameViewParams);

                TextView friendAddressView = new TextView(mContext);
                LinearLayout.LayoutParams friendAddressViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
                friendAddressView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                friendAddressView.setGravity(Gravity.CENTER);
                friendAddressView.setTextSize(16);
                friendAddressView.setTextColor(mContext.getResources().getColor(R.color.color_99_transparent));
                friendAddressView.setText(address);
                friendAddressView.setLayoutParams(friendAddressViewParams);

                friendItemContent.addView(friendNameView);
                friendItemContent.addView(friendAddressView);
                friendItemContain.addView(friendItemContent);
                friendItemContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBasePopWindow.dismiss();
                        mSendStepTwoPopupWindow = new SendStepTwoPopupWindow(mContext);
                        mSendStepTwoPopupWindow.show(mView);
                    }
                });
                ListContentView.addView(friendItemContain);
            }
        }
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
