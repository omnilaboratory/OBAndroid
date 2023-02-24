package com.omni.wallet.data;

import android.content.Context;
import android.util.Log;

import com.omni.wallet.entity.event.InitChartEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.utils.TimeFormatUtil;
import com.omni.wallet.utils.UtilFunctions;
import com.omni.wallet.utils.WalletServiceUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lnrpc.LightningOuterClass;

public class AssetsActions {

    private static final String TAG = AssetsActions.class.getSimpleName();

    public interface ActionCallBack {
        void callback();
    }

    public static void updateAssetsValueDataValueLast(Context context) {
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        try {
            long date = TimeFormatUtil.getCurrentDayMills();
            List<Map<String, Object>> dataList = assetsDataDao.queryAllAssetsDataByDate(date);
            Log.e(TAG, "updateAssetsValueDataValueLast: dataList" + dataList.toString());
            double value = 0.0;
            for (int i = 0; i < dataList.size(); i++) {
                double dataValue = 0.0;
                Map<String, Object> item = dataList.get(i);
                double price = (double) item.get("price");
                double amount = (double) item.get("amount");
                double channel_amount = 0;
                if (item.get("channelAmount") != null) {
                    channel_amount = (double) item.get("channelAmount");
                }
                dataValue = (channel_amount + amount) * price;
                value = dataValue + value;
            }
            AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
            assetsValueDataDao.updateAssetValueData(value, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void updateAssetsPrice(Context context, String propertyId, double price) {
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.updateAssetDataPrice(propertyId, price);
    }

    public static void updateAssetsAmount(Context context, String propertyId, double amount) {
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.updateAssetDataAmount(propertyId, amount);
    }

    public static void updateAssetChannelsAmount(Context context, String propertyId, double amount) {
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.updateAssetDataChannelAmount(propertyId, amount);
    }

    public static void updateAssetsPriceS(Context context, String propertyId, double price) {
        updateAssetsPrice(context, propertyId, price);
        updateAssetsValueDataValueLast(context);
    }

    public static void updateAssetsAmountS(Context context, String propertyId, double amount) {
        updateAssetsAmount(context, propertyId, amount);
        updateAssetsValueDataValueLast(context);
        AssetsDao assetsDao = new AssetsDao(context);
        assetsDao.changeAssetIsUse(propertyId, 1);
    }

    public static void updateAssetChannelsAmountS(Context context, String propertyId, double amount) {
        updateAssetChannelsAmount(context, propertyId, amount);
        updateAssetsValueDataValueLast(context);
        AssetsDao assetsDao = new AssetsDao(context);
        assetsDao.changeAssetIsUse(propertyId, 1);
    }

    public static void insertAssetData(Context context, String propertyId, double price, double amount, double channelAmount) {
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData(propertyId, price, amount, channelAmount);
    }

    public static void insertAssetDataAndValueData(Context context, String propertyId, double price, double amount, double channelAmount) {
        insertAssetData(context, propertyId, price, amount, channelAmount);
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        try {
            long date = TimeFormatUtil.getCurrentDayMills();
            double value = (amount + channelAmount) * price;
            assetsValueDataDao.insertAssetValueData(value, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void insertAssetDataAndUpdateValueData(Context context, String propertyId, double price, double amount, double channelAmount) {
        insertAssetData(context, propertyId, price, amount, channelAmount);
        updateAssetsValueDataValueLast(context);
    }

    public static void insertOrUpdateAssetDataAndUpdateValueData(Context context, String propertyId, double amount) {
        Log.e(TAG, "insertOrUpdateAssetDataAndUpdateValueData: amount" + amount);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        try {
            long date = TimeFormatUtil.getCurrentDayMills();
            assetsDataDao.insertOrUpdateAssetDataByAmount(propertyId, amount, date);
            updateAssetsValueDataValueLast(context);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }




    public static void initDbForInstallApp(Context context) {
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        try {
            long date = TimeFormatUtil.getCurrentDayMills();
            assetsValueDataDao.insertAssetValueData(0, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        getBtcBalanceAction(context);
    }

    public static void initOrUpdateDataStartApp(Context context) {
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        assetsValueDataDao.completeAssetData();
        getBtcBalanceAction(context);

    }

    public static void getUsingAssetsPriceAction(Context context){
        WalletServiceUtil.GetUsingAssetsPriceCallback getUsingAssetsPriceCallback
                = (Context context1, JSONArray priceList, Map<String, Object> propertyMap) -> {
            Log.e(TAG, "initOrUpdateDataStartApp: getPriceSuccess");
            try {
                for (int i = 0; i < priceList.length(); i++) {
                    String priceString = priceList.getJSONObject(i).getString("current_price");
                    Log.e(TAG, "initOrUpdateDataStartApp: price: " + priceString);
                    double price = Double.parseDouble(priceString);
                    String id = priceList.getJSONObject(i).getString("id");
                    String propertyId = "";
                    switch (id) {
                        case "bitcoin":
                            propertyId = (String) propertyMap.get("btc");
                            updateAssetsPriceS(context, propertyId, price);
                            break;
                        case "tether":
                            propertyId = (String) propertyMap.get("ftoken");
                            updateAssetsPriceS(context, propertyId, price);
                            break;
                        default:
                            propertyId = (String) propertyMap.get(id);
                            updateAssetsPriceS(context, propertyId, price);
                            break;
                    }

                }
                EventBus.getDefault().post(new InitChartEvent());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        WalletServiceUtil.GetUsingAssetsPriceErrorCallback getUsingAssetsPriceErrorCallback
                = (Context mContext, String errorCode, String errorMsg, List<Map<String, Object>> usingAssetsList, Map<String, Object> propertyMap) -> {
            Log.e(TAG, "getPriceError:" + errorMsg);
            AssetsDataDao assetsDataDao = new AssetsDataDao(context);
            for (int i = 0; i < usingAssetsList.size(); i++) {
                String id = (String) usingAssetsList.get(i).get("token_name");
                String propertyId = "";
                List<Map<String, Object>> list = new ArrayList<>();
                switch (id) {
                    case "btc":
                        propertyId = (String) propertyMap.get("btc");
                        list = assetsDataDao.queryAssetLastDataByPropertyId(propertyId);
                        Map<String, Object> map = list.get(0);
                        if (map != null) {
                            double price = (Double) map.get("price");
                            if (price == 0) {
                                AssetsActions.updateAssetsPriceS(context, propertyId, 16000.0);
                            } else {
                                AssetsActions.updateAssetsPriceS(context, propertyId, price);
                            }

                        } else {
                            AssetsActions.updateAssetsPriceS(context, propertyId, 16000.0);
                        }

                        break;
                    case "ftoken":
                        propertyId = (String) propertyMap.get("ftoken");
                        list = assetsDataDao.queryAssetLastDataByPropertyId(propertyId);
                        Map<String, Object> mapT = list.get(0);
                        if (mapT != null) {
                            double price = (Double) mapT.get("price");
                            if (price == 0) {
                                AssetsActions.updateAssetsPriceS(context, propertyId, 1.0);
                            } else {
                                AssetsActions.updateAssetsPriceS(context, propertyId, price);
                            }
                        } else {
                            AssetsActions.updateAssetsPriceS(context, propertyId, 1.0);
                        }
                        break;
                    default:
                        propertyId = (String) propertyMap.get(id);
                        AssetsActions.updateAssetsPriceS(context, propertyId, 0);
                        break;
                }

            }
            EventBus.getDefault().post(new InitChartEvent());
        };
        WalletServiceUtil.getUsingAssetsPrice(context, getUsingAssetsPriceCallback, getUsingAssetsPriceErrorCallback);
    }

    public static void getAssetsChannelBalanceAction(Context context){
        WalletServiceUtil.GetAssetChannelBalanceCallback getAssetChannelBalanceCallback
                = (Context mContext, String propertyId, long channelAmountLong, int index, int assetCount) -> {

            double channelAmount = 0.0;
            if (propertyId.equals("0")) {
                channelAmount = UtilFunctions.parseAmount(channelAmountLong, 11);
            } else {
                channelAmount = UtilFunctions.parseAmount(channelAmountLong, 8);
            }

            updateAssetChannelsAmountS(context, propertyId, channelAmount);
            if (index == assetCount - 1) {
                Log.e(TAG, "initOrUpdateDataStartApp: getAssetsChannelBalanceSuccess318");
                getUsingAssetsPriceAction(context);
            }
        };

        WalletServiceUtil.GetAssetChannelBalanceErrorCallback getAssetChannelBalanceErrorCallback
                = (Context mContext, Exception e) -> {
            Log.e(TAG, e.getMessage());
            getUsingAssetsPriceAction(context);
            e.printStackTrace();
        };
        WalletServiceUtil.getUsingAssetsChannelBalance(context, getAssetChannelBalanceCallback, getAssetChannelBalanceErrorCallback);
    }

    public static void getAssetsBalanceAction(Context context){
        WalletServiceUtil.GetAssetsBalanceCallback getAssetsBalanceCallback
                = (List<LightningOuterClass.AssetBalanceByAddressResponse> assetsList) -> {
            Log.e(TAG, "initOrUpdateDataStartApp: getAssetsBalanceSuccess443");
            for (int i = 0; i < assetsList.size(); i++) {
                AssetsDao assetsDao = new AssetsDao(context);
                LightningOuterClass.AssetBalanceByAddressResponse asset = assetsList.get(i);
                String propertyId = Long.toString(asset.getPropertyid());
                long balance = asset.getBalance();
                double amount = UtilFunctions.parseAmount(balance, 8);
                insertOrUpdateAssetDataAndUpdateValueData(context, propertyId, amount);
                assetsDao.changeAssetIsUse(propertyId, 1);
            }
            getAssetsChannelBalanceAction(context);
        };

        WalletServiceUtil.GetAssetsBalanceErrorCallback getAssetsBalanceErrorCallback
                = (Context mContext, Exception e) -> {
            Log.e(TAG, e.getMessage());
            getAssetsChannelBalanceAction(context);
            e.printStackTrace();
        };
        WalletServiceUtil.getAssetsBalance(context, getAssetsBalanceCallback, getAssetsBalanceErrorCallback);
    }

    public static void getBtcBalanceAction(Context context){
        WalletServiceUtil.GetBtcBalanceCallback getBtcBalanceCallback
                = (double btcBalance) -> {
            Log.e(TAG, "initOrUpdateDataStartApp: btcBalance" + btcBalance);
            insertOrUpdateAssetDataAndUpdateValueData(context, "0", btcBalance);
            getAssetsBalanceAction(context);
        };

        WalletServiceUtil.GetBtcBalanceErrorCallback getBtcBalanceErrorCallback = (Exception e, Context mContext) -> {
            Log.e(TAG, e.getMessage());
            getAssetsBalanceAction(context);
            e.printStackTrace();
        };
        Log.e(TAG, "initOrUpdateDataStartApp: start");
        WalletServiceUtil.getBtcBalance(context, getBtcBalanceCallback, getBtcBalanceErrorCallback);
    }

    public static void getAssetsListAction(Context context, int assetsCount){
        WalletServiceUtil.GetAssetListCallback getAssetListCallback
                = (Context mContext, List<LightningOuterClass.Asset> assetsList) -> {
            AssetsDao assetsDao = new AssetsDao(mContext);
            int getAssetsCount = assetsList.size();
            if (getAssetsCount > assetsCount) {
                assetsDao.checkAndInsertAsset("0", "btc");
                assetsDao.changeAssetIsUse("0", 1);
                for (int i = 0; i < getAssetsCount; i++) {
                    LightningOuterClass.Asset assets = assetsList.get(i);
                    String propertyId = Long.toString(assets.getPropertyid());
                    String tokenName = assets.getName();
                    assetsDao.checkAndInsertAsset(propertyId, tokenName);
                }
                User.getInstance().setAssetsCount(mContext, getAssetsCount);
            }
            if (assetsCount > 0) {
                initOrUpdateDataStartApp(mContext);
            } else {
                initDbForInstallApp(mContext);
            }
        };

        WalletServiceUtil.GetAssetListErrorCallback getAssetListErrorCallback = (Context mContext, Exception e) -> {
            Log.e(TAG, e.getMessage());
            if (assetsCount == 0) {
                initOrUpdateAction(mContext);
            } else {
                initOrUpdateDataStartApp(mContext);
            }
            e.printStackTrace();
        };

        WalletServiceUtil.getAssetsList(context, getAssetListCallback, getAssetListErrorCallback);
    }

    /**
     * @author Tong
     * @E-mail tch081092@gmail.com
     * @description init or update data when initial account lightning page
     * @param context
     */
    public static void initOrUpdateAction(Context context) {
        int assetsCount = User.getInstance().getAssetsCount(context);
        getAssetsListAction(context,assetsCount);

    }

    public static Map<String, Object> getDataForChart(Context context) {
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        List<Map<String, Object>> valueList2 = assetsValueDataDao.queryAssetValueDataAll();
        List<Map<String, Object>> valueList = assetsValueDataDao.queryAssetValueDataOneYear();
        Map<String, Double> changeMap = new HashMap<>();
        List<Map<String, Object>> chartDataList = new ArrayList<>();
        Collections.reverse(valueList);
        Log.e(TAG + "getDataForChart ", valueList.toString());
        if (valueList.size() > 0) {
            double value = 0;
            double changePercent = 0;
            if (valueList.size() > 1) {
                value = (double) valueList.get(valueList.size() - 1).get("value");
                changePercent = Math.floor(
                        ((double) valueList.get(valueList.size() - 1).get("value")
                                - (double) valueList.get(valueList.size() - 2).get("value"))
                                / (double) valueList.get(valueList.size() - 2).get("value") * 10000
                ) / 100.0;
            } else {
                value = (double) valueList.get(valueList.size() - 1).get("value");
                changePercent = 0;
            }

            changeMap.put("value", value);
            changeMap.put("percent", changePercent);


            if (valueList.size() > 14) {
                for (int i = valueList.size() - 1; i > valueList.size() - 8; i--) {
                    Map<String, Object> chartData = new HashMap<>();
                    long dateMills = (long) valueList.get(i).get("date");
                    double mapValue = (double) valueList.get(i).get("value");
                    chartData.put("date", dateMills);
                    chartData.put("value", mapValue);
                    chartDataList.add(chartData);
                }

                for (int j = valueList.size() - 8; j > 6; j = j - 7) {
                    Map<String, Object> chartData = new HashMap<>();
                    long dateMills = (long) valueList.get(j - 3).get("date");
                    double mapValue = ((double) valueList.get(j).get("value")
                            + (double) valueList.get(j - 1).get("value")
                            + (double) valueList.get(j - 2).get("value")
                            + (double) valueList.get(j - 3).get("value")
                            + (double) valueList.get(j - 4).get("value")
                            + (double) valueList.get(j - 5).get("value")
                            + (double) valueList.get(j - 6).get("value")) / 7;
                    chartData.put("date", dateMills);
                    chartData.put("value", mapValue);
                    chartDataList.add(chartData);
                }
                Collections.reverse(chartDataList);

            } else {
                for (int i = 0; i < valueList.size(); i++) {
                    Map<String, Object> chartData = new HashMap<>();
                    long dateMills = (long) valueList.get(i).get("date");
                    double mapValue = (double) valueList.get(i).get("value");
                    chartData.put("date", dateMills);
                    chartData.put("value", mapValue);
                    chartDataList.add(chartData);
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("changeData", changeMap);
        data.put("chartData", chartDataList);
        return data;

    }
}
