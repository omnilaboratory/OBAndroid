package com.omni.wallet.data;

import android.content.Context;
import android.util.Log;

import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.utils.TimeFormatUtil;
import com.omni.wallet.utils.UtilFunctions;
import com.omni.wallet.utils.WalletServiceUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import lnrpc.LightningOuterClass;

public class AssetsActions {

    private static final String TAG = AssetsActions.class.getSimpleName();

    public static void updateAssetsValueDataValueLast(Context context){
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            List<Map<String,Object>> dataList = assetsDataDao.queryAllAssetsDataByDate(date);
            double value = 0.0;
            for (int i = 0; i < dataList.size(); i++) {
                Map<String,Object> item = dataList.get(i);
                double price = (double) item.get("price");
                double amount = (double) item.get("amount");
                double channel_amount = (double) item.get("channel_amount");
                value = (channel_amount + amount) * price;
            }
            AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
            assetsValueDataDao.updateAssetValueData(value,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void updateAssetsPrice(Context context, String propertyId, double price){
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.updateAssetDataPrice(propertyId,price);
    }

    public static void updateAssetsAmount(Context context, String propertyId, double amount){
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.updateAssetDataAmount(propertyId,amount);
    }

    public static void updateAssetChannelsAmount(Context context, String propertyId, double amount){
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.updateAssetDataChannelAmount(propertyId,amount);
    }

    public static void updateAssetsPriceS(Context context, String propertyId, double price){
        updateAssetsPrice(context,propertyId,price);
        updateAssetsValueDataValueLast(context);
    }

    public static void updateAssetsAmountS(Context context, String propertyId, double amount){
        updateAssetsAmount(context,propertyId,amount);
        updateAssetsValueDataValueLast(context);
        AssetsDao assetsDao = new AssetsDao(context);
        assetsDao.changeAssetIsUse(propertyId,1);
    }

    public static void updateAssetChannelsAmountS(Context context, String propertyId, double amount){
        updateAssetChannelsAmount(context,propertyId,amount);
        updateAssetsValueDataValueLast(context);
        AssetsDao assetsDao = new AssetsDao(context);
        assetsDao.changeAssetIsUse(propertyId,1);
    }

    public static void insertAssetData(Context context, String propertyId,double price,double amount,double channelAmount){
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData(propertyId,price,amount,channelAmount);
    }

    public static void insertAssetDataAndValueData(Context context, String propertyId,double price,double amount,double channelAmount){
        insertAssetData(context,propertyId,price,amount,channelAmount);
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            double value = (amount + channelAmount) * price;
            assetsValueDataDao.insertAssetValueData(value,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void insertAssetDataAndUpdateValueData(Context context, String propertyId,double price,double amount,double channelAmount){
        insertAssetData(context,propertyId,price,amount,channelAmount);
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        try {
            long  date = TimeFormatUtil.getCurrentDayMills();
            double value = (amount + channelAmount) * price;
            assetsValueDataDao.updateAssetValueData(value,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void getAssetListAndSave(Context context){
        WalletServiceUtil.GetAssetListCallback getAssetListCallback = assetsList -> {
            AssetsDao assetsDao = new AssetsDao(context);
            int assetsCount = User.getInstance().getAssetsCount(context);
            int getAssetsCount = assetsList.size();
            if(getAssetsCount>assetsCount){
                assetsDao.checkAndInsertAsset("0","btc");
                for (int i = 0; i<getAssetsCount; i++){
                    LightningOuterClass.Asset assets = assetsList.get(i);
                    String propertyId = Long.toString(assets.getPropertyid());
                    String tokenName = assets.getName();
                    assetsDao.checkAndInsertAsset(propertyId,tokenName);
                }
                User.getInstance().setAssetsCount(context,getAssetsCount);
            }
            Log.e(TAG,"update assets complete!");
        };

        WalletServiceUtil.getAssetsList(getAssetListCallback);
    }

    public static void insertAssetsDataToday(Context context){
        AssetsDao assetsDao = new AssetsDao(context);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        List<Map<String,Object>> usingAssetsList =  assetsDao.getUsingAssetsList();
        for (int i = 0; i < usingAssetsList.size(); i++) {
            Map<String,Object> usingAssets = usingAssetsList.get(i);
            String propertyId = (String) usingAssets.get("property_id");
            List<Map<String,Object>> assetDataList =  assetsDataDao.queryAssetLastDataByPropertyId(propertyId);
            Map<String,Object> assetData = assetDataList.get(0);
            double price = (double) assetData.get("price");
            double amount = (double) assetData.get("amount");
            double channelAmount = (double) assetData.get("channelAmount");
            if (i == 0){
                insertAssetDataAndValueData(context,propertyId,price,amount,channelAmount);
            }else{
                insertAssetDataAndUpdateValueData(context,propertyId,price,amount,channelAmount);
            }
        }
    }

    public static void updateAssetsPrice(Context mContext){
        WalletServiceUtil.GetUsingAssetsPriceCallback callback = (Context context, JSONArray priceList, Map<String,Object> propertyMap)->{
            try {
                for (int i = 0; i < priceList.length(); i++) {
                    String priceString = priceList.getJSONObject(i).getString("current_price");
                    double price = Double.parseDouble(priceString);
                    String id = priceList.getJSONObject(i).getString("id");
                    String propertyId = "";
                    switch (id){
                        case "bitcoin":
                            propertyId = (String) propertyMap.get("BTC");
                            updateAssetsPriceS(context,propertyId,price);
                            break;
                        case "tether":
                            propertyId = (String) propertyMap.get("USD");
                            updateAssetsPriceS(context,propertyId,price);
                            break;
                        default:
                            propertyId = (String) propertyMap.get(id);
                            updateAssetsPriceS(context,propertyId,price);
                            break;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        };
        WalletServiceUtil.getUsingAssetsPrice(mContext,callback);
    }

    public static void updateBTCAmount(Context context){
        WalletServiceUtil.GetBtcBalanceCallback callback = (double btcBalance)->{
            updateAssetsAmountS(context,"0",btcBalance);
        };
        WalletServiceUtil.getBtcBalance(context,callback);
    }

    public static void updateAssetsAmount(Context context){
        WalletServiceUtil.GetAssetsBalanceCallback callback = (List<LightningOuterClass.AssetBalanceByAddressResponse> assetsList)->{
            for (int i = 0; i <assetsList.size() ; i++) {
                LightningOuterClass.AssetBalanceByAddressResponse asset = assetsList.get(i);
                String propertyId = Long.toString(asset.getPropertyid()) ;
                long balance =  asset.getBalance();
                double amount = UtilFunctions.parseAmount(balance,8);
                updateAssetsAmountS(context,propertyId,amount);
            }
        };
        WalletServiceUtil.getAssetsBalance(context,callback);
    }

    public static void updateUsingAssetsChannelAmount(Context context){
        WalletServiceUtil.GetAssetChannelBalanceCallback callback =(Context mContext,String propertyId,long channelAmountLong)->{
            double channelAmount = 0.0;
            if (propertyId.equals("0")){
                channelAmount = UtilFunctions.parseAmount(channelAmountLong,11);
            }else{
                channelAmount = UtilFunctions.parseAmount(channelAmountLong,8);
            }

            updateAssetChannelsAmountS(context,propertyId,channelAmount);
        };
        WalletServiceUtil.getUsingAssetsChannelBalance(context,callback);
    }

    public static void firstInitEveryDay(Context context){
        getAssetListAndSave(context);
        insertAssetsDataToday(context);
        Thread threadUpdateAssetsPrice = new Thread(()->{
            updateAssetsPrice(context);
        });
        threadUpdateAssetsPrice.run();

        Thread threadUpdateBTCAmount = new Thread(()->{
            updateBTCAmount(context);
        });
        threadUpdateBTCAmount.run();

        Thread threadUpdateAssetsAmount = new Thread(()->{
            updateAssetsAmount(context);
        });
        threadUpdateAssetsAmount.run();

        Thread threadUpdateUsingAssetsChannelAmount = new Thread(()->{
            updateUsingAssetsChannelAmount(context);
        });
        threadUpdateUsingAssetsChannelAmount.run();
    }
}
