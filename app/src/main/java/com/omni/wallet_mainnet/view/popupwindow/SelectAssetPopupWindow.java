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
 * 汉: 选择资产类型的弹窗
 * En: SelectAssetPopupWindow
 * author: guoyalei
 * date: 2022/10/21
 */
public class SelectAssetPopupWindow {
    private static final String TAG = SelectAssetPopupWindow.class.getSimpleName();

    private Context mContext;
    private BasePopWindow mBasePopWindow;
    private ItemCleckListener mCallback;
    RecyclerView mRecyclerView;

    public List<ListAssetItemEntity> blockData = new ArrayList<>();
    public List<ListAssetItemEntity> lightningData = new ArrayList<>();
    public List<ListAssetItemEntity> allData = new ArrayList<>();
    private MyAdapter mAdapter;
    private List<AssetEntity> mAssetData = new ArrayList<>();

    public SelectAssetPopupWindow(Context context) {
        this.mContext = context;
    }

    public void show(final View view) {
        if (mBasePopWindow == null) {
            fetchWalletBalance();
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
                    blockData.clear();
                    ListAssetItemEntity entity = new ListAssetItemEntity();
                    entity.setAmount(resp.getConfirmedBalance());
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
                        List list = new ArrayList();
                        for (lnrpc.LightningOuterClass.AssetBalanceByAddressResponse response : resp.getListList()) {
                            list.add(response.getPropertyid());
                            if (!list.contains(Long.parseLong("2147485160"))) {
                                setDefaultData();
                            }
                        }
                    } else if (ConstantInOB.networkType == NetworkType.REG) {
                        List list = new ArrayList();
                        for (lnrpc.LightningOuterClass.AssetBalanceByAddressResponse response : resp.getListList()) {
                            list.add(response.getPropertyid());
                            if (!list.contains(Long.parseLong("2147483651"))) {
                                setDefaultData();
                            }
                        }
                    } else { //mainnet
                        List list = new ArrayList();
                        for (lnrpc.LightningOuterClass.AssetBalanceByAddressResponse response : resp.getListList()) {
                            list.add(response.getPropertyid());
                            if (!list.contains(Long.parseLong("31"))) {
                                setDefaultData();
                            }
                        }
                    }
                    lightningData.clear();
                    for (int i = 0; i < resp.getListList().size(); i++) {
                        ListAssetItemEntity entity = new ListAssetItemEntity();
                        entity.setAmount(resp.getListList().get(i).getBalance());
                        entity.setPropertyid(resp.getListList().get(i).getPropertyid());
                        entity.setType(2);
                        lightningData.add(entity);
                    }
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
        lightningData.clear();
        ListAssetItemEntity entity = new ListAssetItemEntity();
        entity.setAmount(0);
        if (User.getInstance().getNetwork(mContext).equals("testnet")) {
            entity.setPropertyid(Long.parseLong("2147485160"));
        } else if (User.getInstance().getNetwork(mContext).equals("regtest")) {
            entity.setPropertyid(Long.parseLong("2147483651"));
        } else { //mainnet
            entity.setPropertyid(Long.parseLong("31"));
        }
        entity.setType(2);
        lightningData.add(entity);
        allData.addAll(lightningData);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
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
