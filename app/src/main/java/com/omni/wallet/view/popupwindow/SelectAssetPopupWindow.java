package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.entity.ListAssetItemEntity;

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
        LightningOuterClass.NewAddressRequest asyncNewAddressRequest = LightningOuterClass.NewAddressRequest.newBuilder()
                .setTypeValue(2)
                .build();
        Obdmobile.newAddress(asyncNewAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------newAddressOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.NewAddressResponse addressResp = LightningOuterClass.NewAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------newAddressOnResponse-----------------" + addressResp.getAddress());
                    LightningOuterClass.WalletBalanceByAddressRequest walletBalanceByAddressRequest = LightningOuterClass.WalletBalanceByAddressRequest.newBuilder()
                            .setAddress(addressResp.getAddress())
                            .build();
                    Obdmobile.walletBalanceByAddress(walletBalanceByAddressRequest.toByteArray(), new Callback() {
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
                                entity.setAmount(resp.getTotalBalance());
                                entity.setPropertyid(0);
                                entity.setType(1);
                                blockData.add(entity);
                                allData.addAll(blockData);
                                mAdapter.notifyDataSetChanged();
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
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
     * request the interface of each asset balance list
     * 请求各资产余额列表的接口
     */
    public void fetchAssetsBalanceByAddress() {
        LightningOuterClass.AssetsBalanceByAddressRequest asyncAssetsBalanceRequest = LightningOuterClass.AssetsBalanceByAddressRequest.newBuilder()
                .setAddress("mz6keb5sJt4f7AxXTWuz4siYDD48oHUrSs")
                .build();
        Obdmobile.assetsBalanceByAddress(asyncAssetsBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG, "------------------assetsBalanceOnError------------------" + e.getMessage());
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.AssetsBalanceByAddressResponse resp = LightningOuterClass.AssetsBalanceByAddressResponse.parseFrom(bytes);
                    LogUtils.e(TAG, "------------------assetsBalanceOnResponse------------------" + resp.getListList().toString());
                    lightningData.clear();
                    for (int i = 0; i < resp.getListList().size(); i++) {
                        ListAssetItemEntity entity = new ListAssetItemEntity();
                        entity.setAmount(resp.getListList().get(i).getBalance());
                        entity.setPropertyid(resp.getListList().get(i).getPropertyid());
                        entity.setType(2);
                        lightningData.add(entity);
                        allData.addAll(lightningData);
//                        mAdapter.notifyDataSetChanged();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
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
            if (item.getPropertyid() == 0) {
                holder.setImageResource(R.id.iv_logo, R.mipmap.icon_btc_logo_small);
                holder.setText(R.id.tv_asset, "BTC");
            } else {
                holder.setImageResource(R.id.iv_logo, R.mipmap.icon_usdt_logo_small);
                holder.setText(R.id.tv_asset, "USDT");
            }
            holder.setText(R.id.tv_asset_amount, String.valueOf(item.getAmount()));
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
