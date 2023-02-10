package com.omni.wallet.utils;

import android.content.Context;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.data.AssetsDB;
import com.omni.wallet.data.AssetsDao;
import com.omni.wallet.entity.event.BtcAndUsdtEvent;
import com.omni.wallet.framelibrary.entity.User;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lnrpc.LightningOuterClass;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class WalletServiceUtil {

    public interface GetAssetListCallback {
        void callback(List<LightningOuterClass.Asset> assetsList);
    }

    public interface GetBtcBalanceCallback {
        void callback(double btcBalance);
    }

    public interface GetAssetsBalanceCallback {
        void callback(List<LightningOuterClass.AssetBalanceByAddressResponse> assetsList);
    }

    public interface GetUsingAssetsPriceCallback{
        void callback(Context context, JSONArray priceList,Map<String,Object> propertyMap);
    }

    public interface GetAssetChannelBalanceCallback{
        void callback(Context context,String propertyId,long channelAmountLong);
    }

    private static String TAG = WalletServiceUtil.class.getSimpleName();

    public static void getAssetsList(GetAssetListCallback callback){
        Obdmobile.oB_ListAsset(null, new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null){
                    return;
                }
                try {
                    LightningOuterClass.ListAssetResponse listAssetResponse = LightningOuterClass.ListAssetResponse.parseFrom(bytes);
                    List<LightningOuterClass.Asset> list = listAssetResponse.getListList();
                    callback.callback(list);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getBtcBalance(Context context,GetBtcBalanceCallback callback){
        LightningOuterClass.WalletBalanceByAddressRequest walletBalanceByAddressRequest = LightningOuterClass.WalletBalanceByAddressRequest.newBuilder()
                .setAddress(User.getInstance().getWalletAddress(context))
                .build();
        Obdmobile.oB_WalletBalanceByAddress(walletBalanceByAddressRequest.toByteArray(),new Callback(){

            @Override
            public void onError(Exception e) {
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                double balance = 0.0;
                if (bytes == null){
                    callback.callback(balance);
                    return;
                }
                try {
                    LightningOuterClass.WalletBalanceByAddressResponse resp = LightningOuterClass.WalletBalanceByAddressResponse.parseFrom(bytes);
                    long confirmedBalance = resp.getConfirmedBalance();
                    balance = UtilFunctions.parseAmount(confirmedBalance,8);
                    callback.callback(balance);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getAssetsBalance(Context context,GetAssetsBalanceCallback callback){
        LightningOuterClass.AssetsBalanceByAddressRequest asyncAssetsBalanceRequest = LightningOuterClass.AssetsBalanceByAddressRequest.newBuilder()
                .setAddress(User.getInstance().getWalletAddress(context))
                .build();
        Obdmobile.oB_AssetsBalanceByAddress(asyncAssetsBalanceRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null){
                    return;
                }
                try {
                    LightningOuterClass.AssetsBalanceByAddressResponse resp = LightningOuterClass.AssetsBalanceByAddressResponse.parseFrom(bytes);
                    List<LightningOuterClass.AssetBalanceByAddressResponse> assetsBalanceList =  resp.getListList();
                    callback.callback(assetsBalanceList);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getUsingAssetsPrice(Context context,GetUsingAssetsPriceCallback callback){
        AssetsDao assetsDao = new AssetsDao(context);
        List<Map<String,Object>> usingAssetsList = assetsDao.getUsingAssetsList();
        String assetsIds = "";
        Map<String,Object> propertyMap = new HashMap<>();
        for (int i = 0; i < usingAssetsList.size(); i++) {
            if (i == 0){
                assetsIds = assetsIds + usingAssetsList.get(i).get("token_name");
            }else{
                assetsIds = assetsIds + "," + usingAssetsList.get(i).get("token_name");
            }
            propertyMap.put((String) usingAssetsList.get(i).get("token_name"),usingAssetsList.get(i).get("property_id"));
        }
//        String reqString = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids="+assetsIds+"&order=market_cap_desc&per_page="+usingAssetsList.size()+"&page=1&sparkline=false";
        String reqString = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=bitcoin,tether,usd-coin&order=market_cap_desc&per_page=100&page=1&sparkline=false";

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
                        Log.e(TAG,"getPriceError:"+ errorMsg);
                        /*try {
                            DollarData dollarData = new DollarData(mContext);
                            if(dollarData.checkDataIsEmpty()){
                                dollarData.insert(0,dollarData.getLastPrice());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/
                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        LogUtils.e(TAG, "---------------Price---------------------" + result.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            callback.callback(context,jsonArray,propertyMap);
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

    public static void getUsingAssetsChannelBalance(Context context,GetAssetChannelBalanceCallback callback){
        AssetsDao assetsDao = new AssetsDao(context);
        List<Map<String,Object>> assetsList =  assetsDao.getUsingAssetsList();
        for (int i = 0; i < assetsList.size(); i++) {
            String propertyId = (String) assetsList.get(i).get("property_id");
            LightningOuterClass.ChannelBalanceRequest channelBalanceRequest = LightningOuterClass.ChannelBalanceRequest.newBuilder()
                    .setAssetId(Integer.parseInt(propertyId))
                    .build();
            Obdmobile.channelBalance(channelBalanceRequest.toByteArray(), new Callback() {
                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(byte[] bytes) {
                    if (bytes == null){
                        return;
                    }
                    try {
                        LightningOuterClass.ChannelBalanceResponse resp = LightningOuterClass.ChannelBalanceResponse.parseFrom(bytes);
                        long balance = resp.getLocalBalance().getMsat();
                        callback.callback(context,propertyId,balance);

                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

}
