package com.omni.wallet.ui.activity.createwallet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.view.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class CreateWalletStepOneActivity extends AppBaseActivity {
    private static final String TAG = CreateWalletStepOneActivity.class.getSimpleName();
    List <String> seedArray = new ArrayList();
    String seedsString = "";
    Context ctx = CreateWalletStepOneActivity.this;
    LoadingDialog mLoadingDialog;

    @BindView(R.id.seed_content)
    LinearLayout lvSeedContent;

    SeedsAdapter seedsAdapter;

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_create_wallet_setp_one;
    }


    @Override
    protected void initView() {
        GridView seedGridView = (GridView) findViewById(R.id.seed_grid_view);
        EventBus.getDefault().register(this);
        mLoadingDialog = new LoadingDialog(mContext);
        seedGridView.setAdapter(seedsAdapter = new SeedsAdapter(this,seedArray));
    }

    @Override
    protected void initData() {
        createSeeds();
    }


    class SeedsAdapter extends BaseAdapter {
        Context mContext;
        LayoutInflater mInflater;
        List <String> mDatas;
        public SeedsAdapter(Context context,List <String> seedArray) {
            mInflater = LayoutInflater.from(context);
            this.mContext = context;
            this.mDatas = seedArray;
        }

        @Override
        public int getCount() {
            return mDatas != null ? mDatas.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.layout_item_seed, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String indexString = position + 1>=10?String.valueOf(position + 1):"0" + (position + 1);
            viewHolder.setText(R.id.seed_idx, indexString + ".");
            TextView textView = viewHolder.getView(R.id.seed_text);
            textView.setText(mDatas.get(position));
            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void createSeeds() {
        mLoadingDialog.show();
        Walletunlocker.GenSeedRequest genSeedRequest = Walletunlocker.GenSeedRequest.newBuilder().build();
        Obdmobile.genSeed(genSeedRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e("create error:",e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingDialog.dismiss();
                    }
                });

            }

            @Override
            public void onResponse(byte[] bytes) {
                try{
                    Walletunlocker.GenSeedResponse genSeedResponse = Walletunlocker.GenSeedResponse.parseFrom(bytes);
                    List genSeedResponseList =  genSeedResponse.getCipherSeedMnemonicList();
                    Log.d(TAG, "onResponse: " + genSeedResponseList);
                    for (int i = 0; i<genSeedResponseList.size();i++) {
                        seedArray.add(genSeedResponseList.get(i).toString());
                    }
                    Log.d(TAG, "onResponse seedArray: " + seedArray);
                    for (int idx = 0;idx<seedArray.size();idx++) {
                        seedsString = seedsString + seedArray.get(idx)+ " ";
                    }
                    Log.d(TAG, "onResponse seedsString: " + seedsString);
                    User.getInstance().setSeedString(mContext,seedsString);
                    runOnUiThread(() -> {
                        seedsAdapter.notifyDataSetChanged();
                        mLoadingDialog.dismiss();
                    });

                }catch (InvalidProtocolBufferException e){
                    e.printStackTrace();
                    mLoadingDialog.dismiss();
                }
            }
        });
    }

    /**
     * 点击Back
     * click back button
     */
    @OnClick(R.id.btn_back)
    public void clickBack() {
        finish();
    }

    /**
     * 点击Forward
     * click forward button
     */
    @OnClick(R.id.btn_forward)
    public void clickForward() {
        User.getInstance().setInitWalletType(mContext,"createStepOne");
        switchActivity(CreateWalletStepTwoActivity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
        public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
            finish();
        }
}
