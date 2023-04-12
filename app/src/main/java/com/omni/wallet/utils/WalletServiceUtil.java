package com.omni.wallet.utils;

import android.content.Context;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.data.AssetsDao;
import com.omni.wallet.data.AssetsItem;
import com.omni.wallet.framelibrary.entity.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class WalletServiceUtil {

    private static String TAG = WalletServiceUtil.class.getSimpleName();

    public interface GetAssetListCallback {
        void callback(Context context, List<LightningOuterClass.Asset> assetsList);
    }

    public interface GetAssetListErrorCallback {
        void callback(Context context, Exception e);
    }

    public interface GetBtcBalanceCallback {
        void callback(double btcBalance);
    }

    public interface GetBtcBalanceErrorCallback {
        void callback(Exception e, Context mContext);
    }

    public interface GetAssetsBalanceCallback {
        void callback(List<LightningOuterClass.AssetBalanceByAddressResponse> assetsList);
    }

    public interface GetAssetsBalanceErrorCallback {
        void callback(Context mContext, Exception e);
    }

    public interface GetUsingAssetsPriceCallback {
        void callback(Context context, JSONArray priceList, Map<String, Object> propertyMap);
    }

    public interface GetUsingAssetsPriceErrorCallback {
        void callback(Context context, String errorCode, String errorMsg, List<AssetsItem> usingAssetsList, Map<String, Object> propertyMap);
    }

    public interface GetAssetChannelBalanceCallback {
        void callback(Context context, String propertyId, long channelAmountLong, int index, int usingCount);
    }

    public interface GetAssetChannelBalanceErrorCallback {
        void callback(Context context, Exception e);
    }

    public static void getAssetsList(Context context, GetAssetListCallback callback, GetAssetListErrorCallback error) {
        Obdmobile.oB_ListAsset(null, new Callback() {
            @Override
            public void onError(Exception e) {
                error.callback(context, e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.ListAssetResponse listAssetResponse = LightningOuterClass.ListAssetResponse.parseFrom(bytes);
                    List<LightningOuterClass.Asset> list = listAssetResponse.getListList();
                    callback.callback(context, list);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getBtcBalance(Context context, GetBtcBalanceCallback callback, GetBtcBalanceErrorCallback error) {
        LightningOuterClass.WalletBalanceByAddressRequest walletBalanceByAddressRequest = LightningOuterClass.WalletBalanceByAddressRequest.newBuilder()
                .setAddress(User.getInstance().getWalletAddress(context))
                .build();
        Obdmobile.oB_WalletBalanceByAddress(walletBalanceByAddressRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                error.callback(e, context);
            }

            @Override
            public void onResponse(byte[] bytes) {
                double balance = 0.0;
                if (bytes == null) {
                    callback.callback(balance);
                    return;
                }
                try {
                    LightningOuterClass.WalletBalanceByAddressResponse resp = LightningOuterClass.WalletBalanceByAddressResponse.parseFrom(bytes);
                    long confirmedBalance = resp.getConfirmedBalance();
                    balance = UtilFunctions.parseAmount(confirmedBalance, 8);
                    callback.callback(balance);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getAssetsBalance(Context context, GetAssetsBalanceCallback callback, GetAssetsBalanceErrorCallback error) {
        LightningOuterClass.AssetsBalanceByAddressRequest asyncAssetsBalanceRequest = LightningOuterClass.AssetsBalanceByAddressRequest.newBuilder()
                .setAddress(User.getInstance().getWalletAddress(context))
                .build();
        Obdmobile.oB_AssetsBalanceByAddress(asyncAssetsBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                error.callback(context, e);
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null) {
                    return;
                }
                try {
                    LightningOuterClass.AssetsBalanceByAddressResponse resp = LightningOuterClass.AssetsBalanceByAddressResponse.parseFrom(bytes);
                    List<LightningOuterClass.AssetBalanceByAddressResponse> assetsBalanceList = resp.getListList();
                    callback.callback(assetsBalanceList);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getUsingAssetsPrice(Context context, GetUsingAssetsPriceCallback callback, GetUsingAssetsPriceErrorCallback error) {
        AssetsDao assetsDao = new AssetsDao(context);
        List<AssetsItem> usingAssetsList = assetsDao.getUsingAssetsList();
        String assetsIds = "";
        Map<String, Object> propertyMap = new HashMap<>();
        for (int i = 0; i < usingAssetsList.size(); i++) {
            String tokenName = usingAssetsList.get(i).getToken_name();
            if (tokenName.equals("TetherUS")){
                tokenName = "Tether";
            }else if(tokenName.equals("btc")){
                tokenName = "bitcoin";
            }
            String usingName = tokenName.toLowerCase();
            if (i == 0) {
                assetsIds = assetsIds + usingName;
            } else {
                assetsIds = assetsIds + "," + usingName;
            }
            propertyMap.put(usingAssetsList.get(i).getToken_name(), usingAssetsList.get(i).getProperty_id());
        }
        String reqString = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids="+assetsIds+"&order=market_cap_desc&per_page="+usingAssetsList.size()+"&page=1&sparkline=false";
//        String reqString = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=bitcoin,tether,usd-coin&order=market_cap_desc&per_page=100&page=1&sparkline=false";

        HttpUtils.with(context)
                .get()
                .url(reqString)
                .execute(new EngineCallback() {
                    @Override
                    public void onPreExecute(Context context, Map<String, Object> params) {

                    }

                    @Override
                    public void onCancel(Context context) {

                    }

                    @Override
                    public void onError(Context context, String errorCode, String errorMsg) {
                        error.callback(context, errorCode, errorMsg, usingAssetsList, propertyMap);
                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        LogUtils.e(TAG, "---------------Price---------------------" + result.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            callback.callback(context, jsonArray, propertyMap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(Context context, byte[] result) {

                    }

                    @Override
                    public void onProgressInThread(Context context, Progress progress) {

                    }

                    @Override
                    public void onFileSuccess(Context context, String filePath) {

                    }
                });

    }

    public static void getUsingAssetsChannelBalance(Context context, GetAssetChannelBalanceCallback callback, GetAssetChannelBalanceErrorCallback error) {
        AssetsDao assetsDao = new AssetsDao(context);
        List<AssetsItem> assetsList = assetsDao.getUsingAssetsList();
        for (int i = 0; i < assetsList.size(); i++) {
            int index = i;
            String propertyId = assetsList.get(i).getProperty_id();
            LightningOuterClass.ChannelBalanceRequest channelBalanceRequest = LightningOuterClass.ChannelBalanceRequest.newBuilder()
                    .setAssetId((int) Long.parseLong(propertyId))
                    .build();
            Obdmobile.channelBalance(channelBalanceRequest.toByteArray(), new Callback() {
                @Override
                public void onError(Exception e) {
                    error.callback(context, e);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(byte[] bytes) {
                    if (bytes == null) {
                        return;
                    }
                    try {
                        LightningOuterClass.ChannelBalanceResponse resp = LightningOuterClass.ChannelBalanceResponse.parseFrom(bytes);
                        long balance = resp.getLocalBalance().getMsat();
                        callback.callback(context, propertyId, balance, index, assetsList.size());

                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

}
