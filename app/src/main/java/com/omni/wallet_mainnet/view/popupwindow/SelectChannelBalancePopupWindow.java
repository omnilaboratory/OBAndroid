package com.omni.wallet_mainnet.view.popupwindow;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.baselibrary.utils.LogUtils;
import com.omni.wallet_mainnet.baselibrary.utils.image.ImageUtils;
import com.omni.wallet_mainnet.baselibrary.view.BasePopWindow;
import com.omni.wallet_mainnet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet_mainnet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet_mainnet.common.ConstantInOB;
import com.omni.wallet_mainnet.common.NetworkType;
import com.omni.wallet_mainnet.entity.AssetEntity;
import com.omni.wallet_mainnet.entity.ListAssetItemEntity;
import com.omni.wallet_mainnet.framelibrary.entity.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

/**
 * 汉: 选择通道余额的弹窗
 * En: SelectChannleBalancePopupWindow
 * author: guoyalei
 * date: 2022/11/27
 */
public class SelectChannelBalancePopupWindow {
    private static final String TAG = SelectChannelBalancePopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private ItemCleckListener mCallback;
    RecyclerView mRecyclerView;

    public List<ListAssetItemEntity> blockData = new ArrayList<>();
    public List<ListAssetItemEntity> lightningData = new ArrayList<>();
    public List<ListAssetItemEntity> allData = new ArrayList<>();
    private MyAdapter mAdapter;
    private List<AssetEntity> mAssetData = new ArrayList<>();

    public SelectChannelBalancePopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view) {
        if (mBasePopWindow == null) {
            getBtcChannelBalance();
            fetchAssetsBalanceByAddress();
            mBasePopWindow = new BasePopWindow(mContext);
            View rootView = mBasePopWindow.setContentView(R.layout.layout_popupwindow_select_asset);
            mBasePopWindow.setWidth(view.getWidth());
            mBasePopWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            mBasePopWindow.setOutsideTouchable(true);
            mBasePopWindow.setFocusable(false);
            mRecyclerView = rootView.findViewById(R.id.recycler_asset_list);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mAdapter = new MyAdapter(mContext, allData, R.layout.layout_item_asset_list);
            mRecyclerView.setAdapter(mAdapter);
            if (mBasePopWindow.isShowing()) {
                return;
            }
            mBasePopWindow.showAsDropDown(view);
        }
    }

    /**
     * get BTC Channel Balance
     * 查询btc通道余额
     */
    public void getBtcChannelBalance() {
        LightningOuterClass.ChannelBalanceRequest channelBalanceRequest = LightningOuterClass.ChannelBalanceRequest.newBuilder()
                .setAssetId((int) 0)
                .build();
        Obdmobile.channelBalance(channelBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    blockData = null;
                    allData.addAll(blockData);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    return;
                }
                try {
                    LightningOuterClass.ChannelBalanceResponse resp = LightningOuterClass.ChannelBalanceResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------channelBalanceOnResponse------------------" + resp.toString());
                    blockData.clear();
                    ListAssetItemEntity entity = new ListAssetItemEntity();
                    entity.setAmount(resp.getLocalBalance().getMsat() / 1000 + resp.getRemoteBalance().getMsat() / 1000);
                    entity.setPropertyid(0);
                    entity.setType(1);
                    blockData.add(entity);
                    allData.addAll(blockData);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * request the interface of each asset balance list
     * 请求各资产余额列表的接口
     */
    public void fetchAssetsBalanceByAddress() {
        LightningOuterClass.AssetsBalanceByAddressRequest asyncAssetsBalanceRequest = LightningOuterClass.AssetsBalanceByAddressRequest.newBuilder()
                .setAddress(User.getInstance().getWalletAddress(mContext))
                .build();
        Obdmobile.oB_AssetsBalanceByAddress(asyncAssetsBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------assetsBalanceOnError------------------" + e.getMessage());
                setDefaultData();
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    setDefaultData();
                    return;
                }
                try {
                    LightningOuterClass.AssetsBalanceByAddressResponse resp = LightningOuterClass.AssetsBalanceByAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------assetsBalanceOnResponse------------------" + resp.getListList().toString());
                    if (ConstantInOB.networkType == NetworkType.TEST) {
                        for (int i = 0; i < resp.getListList().size(); i++) {
                            if (resp.getListList().get(i).getPropertyid() != Long.parseLong("2147485160")) {
                                setDefaultData();
                            }
                        }
                    } else if (ConstantInOB.networkType == NetworkType.REG) {
                        for (int i = 0; i < resp.getListList().size(); i++) {
                            if (resp.getListList().get(i).getPropertyid() != Long.parseLong("2147483651")) {
                                setDefaultData();
                            }
                        }
                    } else { //mainnet
                        for (int i = 0; i < resp.getListList().size(); i++) {
                            if (resp.getListList().get(i).getPropertyid() != Long.parseLong("31")) {
                                setDefaultData();
                            }
                        }
                    }
                    for (int i = 0; i < resp.getListList().size(); i++) {
                        getChannelBalance(resp.getListList().get(i).getPropertyid());
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * get Channel Balance
     * 查询通道余额
     *
     * @param propertyid
     */
    private void getChannelBalance(long propertyid) {
        LightningOuterClass.ChannelBalanceRequest channelBalanceRequest = LightningOuterClass.ChannelBalanceRequest.newBuilder()
                .setAssetId((int) propertyid)
                .build();
        Obdmobile.channelBalance(channelBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    lightningData = null;
                    allData.addAll(lightningData);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                    return;
                }
                try {
                    LightningOuterClass.ChannelBalanceResponse resp = LightningOuterClass.ChannelBalanceResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------channelBalanceOnResponse------------------" + resp.toString());
                    lightningData.clear();
                    ListAssetItemEntity entity = new ListAssetItemEntity();
                    entity.setAmount(resp.getLocalBalance().getMsat() + resp.getRemoteBalance().getMsat());
                    entity.setPropertyid(propertyid);
                    entity.setType(2);
                    lightningData.add(entity);
                    allData.addAll(lightningData);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setDefaultData() {
        if (User.getInstance().getNetwork(mContext).equals("testnet")) {
            getChannelBalance(Long.parseLong("2147485160"));
        } else if (User.getInstance().getNetwork(mContext).equals("regtest")) {
            getChannelBalance(Long.parseLong("2147483651"));
        } else { //mainnet
            getChannelBalance(Long.parseLong("31"));
        }
    }

    /**
     * the adapter of asset list
     * 资产列表适配器
     */
    private class MyAdapter extends CommonRecyclerAdapter<ListAssetItemEntity> {

        public MyAdapter(Context context, List<ListAssetItemEntity> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final int position, final ListAssetItemEntity item) {
            ImageView imageView = holder.getView(R.id.iv_logo);
            mAssetData.clear();
            Gson gson = new Gson();
            mAssetData = gson.fromJson(User.getInstance().getAssetListString(mContext), new TypeToken<List<AssetEntity>>() {
            }.getType());
            for (AssetEntity entity : mAssetData) {
                if (Long.parseLong(entity.getAssetId()) == item.getPropertyid()) {
                    ImageUtils.showImage(mContext, entity.getImgUrl(), imageView);
                    holder.setText(R.id.tv_asset, entity.getName());
                }
            }
            if (item.getAmount() == 0) {
                DecimalFormat df = new DecimalFormat("0.00");
                holder.setText(R.id.tv_asset_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
            } else {
                DecimalFormat df = new DecimalFormat("0.00######");
                holder.setText(R.id.tv_asset_amount, df.format(Double.parseDouble(String.valueOf(item.getAmount())) / 100000000));
            }
            holder.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mCallback) {
                        mCallback.onItemClick(v, item);
                    }
                    mBasePopWindow.dismiss();
                }
            });
        }
    }

    public void setOnItemClickCallback(ItemCleckListener itemCleckListener) {
        this.mCallback = itemCleckListener;
    }

    public interface ItemCleckListener {
        void onItemClick(View view, ListAssetItemEntity item);
    }

    public void release() {
        if (mBasePopWindow != null) {
            mBasePopWindow.dismiss();
            mBasePopWindow = null;
        }
    }
}
