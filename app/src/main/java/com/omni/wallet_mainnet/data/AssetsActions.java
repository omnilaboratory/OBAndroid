package com.omni.wallet_mainnet.data;

import android.content.Context;
import android.util.Log;

import com.omni.wallet_mainnet.entity.event.InitChartEvent;
import com.omni.wallet_mainnet.framelibrary.entity.User;
import com.omni.wallet_mainnet.utils.TimeFormatUtil;
import com.omni.wallet_mainnet.utils.UtilFunctions;
import com.omni.wallet_mainnet.utils.WalletServiceUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lnrpc.LightningOuterClass;

public class AssetsActions {

    private static final String TAG = AssetsActions.class.getSimpleName();

    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description update last data in assets value table
     * @描述 更新assets value table 最后一条数据
     */
    private static void updateAssetsValueDataValueLast(Context context) {
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        try {
            long date = TimeFormatUtil.getCurrentDayMills();
            // Get a list of all assets today
            List<AssetsDataItem> dataList = assetsDataDao.queryAllAssetsDataByDate(date);
            // Calculate the sum of today's asset values
            double value = 0.0;
            for (int i = 0; i < dataList.size(); i++) {
                double dataValue;
                AssetsDataItem item = dataList.get(i);
                double price = item.getPrice();
                double amount = item.getAmount();
                double channel_amount = 0;
                if (item.getChannel_amount() != 0) {
                    channel_amount = item.getChannel_amount();
                }
                Log.d(TAG, "updateAssetsValueDataValueLast: " + channel_amount +" " + amount +" " +price);
                dataValue = (channel_amount + amount) * price;
                value = dataValue + value;
            }
            // update data
            AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
            assetsValueDataDao.updateAssetValueData(value, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description update assets`s price in assets data table by propertyId
     * @描述 根据propertyId更新相应资产的价格
     * @param propertyId asset property id
     * @param price asset price
     */
    private static void updateAssetsPrice(Context context, String propertyId, double price) {
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.updateAssetDataPrice(propertyId, price);
    }
    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description update assets`s channel balance in assets data table by propertyId
     * @描述 根据propertyId更新相应资产通道中的余额
     * @param propertyId asset property id
     * @param amount asset channel balance
     */
    private static void updateAssetChannelsAmount(Context context, String propertyId, double amount) {
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.updateAssetDataChannelAmount(propertyId, amount);
    }

    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description update assets`s price in assets data table by propertyId then update the value in assets value data table about today
     * @描述 根据propertyId更新相应资产的价格，然后更行assets value data 中今日的value
     * @param propertyId asset property id
     * @param price asset channel balance
     */

    private static void updateAssetsPriceS(Context context, String propertyId, double price) {
        updateAssetsPrice(context, propertyId, price);
        updateAssetsValueDataValueLast(context);
    }

    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description update assets`s balance in assets data table by propertyId then update the value in assets value data table about today
     * @描述 根据propertyId更新相应资产的余额，然后更行assets value data 中今日的amount
     * @param propertyId asset property id
     * @param amount asset channel balance
     */
    private static void updateAssetChannelsAmountS(Context context, String propertyId, double amount) {
        updateAssetChannelsAmount(context, propertyId, amount);
        updateAssetsValueDataValueLast(context);
        AssetsDao assetsDao = new AssetsDao(context);
        assetsDao.changeAssetIsUse(propertyId, 1);
    }
    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description update assets`s channel balance in assets data table by propertyId then update the value in assets value data table about today
     * @描述 根据propertyId更新相应资产通道中的余额，然后更行assets value data 中今日的channel balance
     * @param propertyId asset property id
     * @param amount asset channel balance
     */
    private static void insertOrUpdateAssetDataAndUpdateValueData(Context context, String propertyId, double amount) {
        Log.d(TAG, "insertOrUpdateAssetDataAndUpdateValueData: amount" + amount);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        try {
            long date = TimeFormatUtil.getCurrentDayMills();
            assetsDataDao.insertOrUpdateAssetDataByAmount(propertyId, amount, date);
            updateAssetsValueDataValueLast(context);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description init database after install app
     * @描述 安装app后初始化数据库
     */
    private static void initDbForInstallApp(Context context) {
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        try {
            long date = TimeFormatUtil.getCurrentDayMills();
            assetsValueDataDao.insertAssetValueData(0, date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        getBtcBalanceAction(context);
    }
    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description init database or update database after start app
     * @描述 启动app后初始化或者更新数据
     */
    private static void initOrUpdateDataStartApp(Context context) {
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        assetsValueDataDao.completeAssetData();
        getBtcBalanceAction(context);
    }
    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description the action for get price about using assets
     * @描述 获取正在使用中的assets的价格的操作
     */
    private static void getUsingAssetsPriceAction(Context context){
        // Execute when the price is obtained successfully
        WalletServiceUtil.GetUsingAssetsPriceCallback getUsingAssetsPriceCallback
                = (Context context1, JSONArray priceList, Map<String, Object> propertyMap) -> {
            try {
                for (int i = 0; i < priceList.length(); i++) {
                    String priceString = priceList.getJSONObject(i).getString("current_price");
                    double price = Double.parseDouble(priceString);
                    String id = priceList.getJSONObject(i).getString("id");
                    String propertyId;
                    // Update the price and today's asset value according to propertyId
                    switch (id) {
                        case "bitcoin":
                            propertyId = (String) propertyMap.get("btc");
                            updateAssetsPriceS(context, propertyId, price);
                            break;
                        case "tether":
                            propertyId = (String) propertyMap.get("ftoken");
                            if (propertyId==null){
                                propertyId = (String) propertyMap.get("Usd");
                                if (propertyId==null){
                                    propertyId = (String) propertyMap.get("TetherUS");
                                }
                            }
                            updateAssetsPriceS(context, propertyId, price);
                            break;
                        default:
                            propertyId = (String) propertyMap.get(id);
                            updateAssetsPriceS(context, propertyId, price);
                            break;
                    }

                }
                // Notify the page to update the chart
                EventBus.getDefault().post(new InitChartEvent());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
        // Execute when getting the price fails
        WalletServiceUtil.GetUsingAssetsPriceErrorCallback getUsingAssetsPriceErrorCallback
                = (Context mContext, String errorCode, String errorMsg, List<AssetsItem> usingAssetsList, Map<String, Object> propertyMap) -> {
            Log.d(TAG, "getPriceError:" + errorMsg);
            AssetsDataDao assetsDataDao = new AssetsDataDao(context);
            for (int i = 0; i < usingAssetsList.size(); i++) {
                String id = usingAssetsList.get(i).getToken_name();
                String propertyId;
                List<AssetsDataItem> list;
                // Update the price and today's asset value according to propertyId
                assert id != null;
                switch (id) {
                    case "btc":
                        propertyId = (String) propertyMap.get("btc");
                        list = assetsDataDao.queryAssetLastDataByPropertyId(propertyId);
                        AssetsDataItem map = list.get(0);
                        if (map != null) {
                            double price =  map.getPrice();
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
                    case "Usd":
                    case "TetherUS":
                        propertyId = (String) propertyMap.get("ftoken");
                        if (propertyId == null){
                            propertyId = (String) propertyMap.get("Usd");
                            if (propertyId == null){
                                propertyId = (String) propertyMap.get("TetherUS");
                            }
                        }
                        list = assetsDataDao.queryAssetLastDataByPropertyId(propertyId);
                        AssetsDataItem mapT = list.get(0);
                        if (mapT != null) {
                            double price = mapT.getPrice();
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
            // Notify the page to update the chart
            EventBus.getDefault().post(new InitChartEvent());
        };
        WalletServiceUtil.getUsingAssetsPrice(context, getUsingAssetsPriceCallback, getUsingAssetsPriceErrorCallback);
    }
    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description the action for get channel balance about assets
     * @描述 获取assets的通道余额的操作
     */
    private static void getAssetsChannelBalanceAction(Context context){
        // 获取通道余额成功后执行
        // Execute after successfully obtaining the channel balance
        WalletServiceUtil.GetAssetChannelBalanceCallback getAssetChannelBalanceCallback
                = (Context mContext, String propertyId, long channelAmountLong, int index, int assetCount) -> {

            double channelAmount;
            if (propertyId.equals("0")) {
                channelAmount = UtilFunctions.parseAmount(channelAmountLong, 11);
            } else {
                channelAmount = UtilFunctions.parseAmount(channelAmountLong, 8);
            }

            updateAssetChannelsAmountS(context, propertyId, channelAmount);
            if (index == assetCount - 1) {
                Log.d(TAG, "initOrUpdateDataStartApp: getAssetsChannelBalanceSuccess318");
                getUsingAssetsPriceAction(context);
            }
        };
        // 获取通道余额失败后执行
        // Execute after failing to obtain the channel balance
        WalletServiceUtil.GetAssetChannelBalanceErrorCallback getAssetChannelBalanceErrorCallback
                = (Context mContext, Exception e) -> {
            Log.d(TAG, e.getMessage());
            getUsingAssetsPriceAction(context);
            e.printStackTrace();
        };
        WalletServiceUtil.getUsingAssetsChannelBalance(context, getAssetChannelBalanceCallback, getAssetChannelBalanceErrorCallback);
    }
    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description the action for get balance about assets
     * @描述 获取assets的余额的操作
     */
    private static void getAssetsBalanceAction(Context context){
        // 获取链上余额成功后执行
        // Execute after successfully obtaining the balance on the chain
        WalletServiceUtil.GetAssetsBalanceCallback getAssetsBalanceCallback
                = (List<LightningOuterClass.AssetBalanceByAddressResponse> assetsList) -> {
            Log.d(TAG, "initOrUpdateDataStartApp: getAssetsBalanceSuccess443");
            for (int i = 0; i < assetsList.size(); i++) {
                AssetsDao assetsDao = new AssetsDao(context);
                LightningOuterClass.AssetBalanceByAddressResponse asset = assetsList.get(i);
                String propertyId = Long.toString(asset.getPropertyid());
                long balance = asset.getBalance();
                double amount = UtilFunctions.parseAmount(balance, 8);
                insertOrUpdateAssetDataAndUpdateValueData(context, propertyId, amount);
                assetsDao.changeAssetIsUse(propertyId, 1);
            }
            updateAssetsValueDataValueLast(context);
            getAssetsChannelBalanceAction(context);
        };
        // 获取链上余额失败后执行
        // Execute after failing to obtain the balance on the chain
        WalletServiceUtil.GetAssetsBalanceErrorCallback getAssetsBalanceErrorCallback
                = (Context mContext, Exception e) -> {
            Log.d(TAG, e.getMessage());
            updateAssetsValueDataValueLast(context);
            getAssetsChannelBalanceAction(context);
            e.printStackTrace();
        };
        WalletServiceUtil.getAssetsBalance(context, getAssetsBalanceCallback, getAssetsBalanceErrorCallback);
    }
    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description the action for get balance about btc
     * @描述 获取btc的余额的操作
     */
    private static void getBtcBalanceAction(Context context){
        // 获取链上余额成功后执行
        // Execute after successfully obtaining the btc balance on the chain
        WalletServiceUtil.GetBtcBalanceCallback getBtcBalanceCallback
                = (double btcBalance) -> {
            Log.d(TAG, "initOrUpdateDataStartApp: btcBalance" + btcBalance);
            insertOrUpdateAssetDataAndUpdateValueData(context, "0", btcBalance);
            getAssetsBalanceAction(context);
        };
        // 获取链上btc余额失败后执行
        // Executed after failing to obtain the btc balance on the chain
        WalletServiceUtil.GetBtcBalanceErrorCallback getBtcBalanceErrorCallback = (Exception e, Context mContext) -> {
            Log.d(TAG, e.getMessage());
            getAssetsBalanceAction(context);
            e.printStackTrace();
        };
        Log.d(TAG, "initOrUpdateDataStartApp: start");
        WalletServiceUtil.getBtcBalance(context, getBtcBalanceCallback, getBtcBalanceErrorCallback);
    }
    /**
     * @author Tong Changhui
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description the action for get assets
     * @描述 获取资产列表
     */
    private static void getAssetsListAction(Context context, int assetsCount){
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
            Log.d(TAG, e.getMessage());
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
     * @CreateTime 2023/1/4 14:11
     * @description init or update data when initial account lightning page
     * @描述 初始化或者更新执行方法
     */
    public static void initOrUpdateAction(Context context) {
        int assetsCount = User.getInstance().getAssetsCount(context);
        getAssetsListAction(context,assetsCount);

    }
    /**
     * @author Tong
     * @E-mail tch081092@gmail.com
     * @CreateTime 2023/1/4 14:11
     * @description get the data for line chart
     * @描述 获取折线图所需数据
     */
    public static ChartData getDataForChart(Context context) {
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        List<AssetsValueDataItem> valueList = assetsValueDataDao.queryAssetValueDataOneYear();

        List<AssetsValueDataItem> chartDataList = new ArrayList<>();
        Collections.reverse(valueList);
        ChangeData changeMap = new ChangeData(0,0);
        if (valueList.size() > 0) {
            double value;
            double changePercent;
            if (valueList.size() > 1) {
                value =  valueList.get(valueList.size() - 1).getValue();
                if (valueList.get(valueList.size() - 2).getValue() == 0 ){
                    changePercent = 0;
                }else{
                    changePercent = Math.floor(
                            (valueList.get(valueList.size() - 1).getValue()
                                    - valueList.get(valueList.size() - 2).getValue())
                                    / valueList.get(valueList.size() - 2).getValue() * 10000
                    ) / 100.0;
                }
            } else {
                value = valueList.get(valueList.size() - 1).getValue();
                changePercent = 0;
            }
            changeMap = new ChangeData(value,changePercent);


            if (valueList.size() > 14) {
                for (int i = valueList.size() - 1; i > valueList.size() - 8; i--) {
                    long dateMills =valueList.get(i).getUpdate_date();
                    double mapValue = valueList.get(i).getValue();
                    AssetsValueDataItem item = new AssetsValueDataItem(mapValue,dateMills);
                    chartDataList.add(item);
                }

                for (int j = valueList.size() - 8; j > 6; j = j - 7) {
                    long dateMills = valueList.get(j - 3).getUpdate_date();
                    double mapValue = ( valueList.get(j).getValue()
                            + valueList.get(j - 1).getValue()
                            + valueList.get(j - 2).getValue()
                            + valueList.get(j - 3).getValue()
                            + valueList.get(j - 4).getValue()
                            + valueList.get(j - 5).getValue()
                            + valueList.get(j - 6).getValue()) / 7;
                    AssetsValueDataItem item = new AssetsValueDataItem(mapValue,dateMills);
                    chartDataList.add(item);
                }
                Collections.reverse(chartDataList);

            } else {
                for (int i = 0; i < valueList.size(); i++) {
                    long dateMills = valueList.get(i).getUpdate_date();
                    double mapValue = valueList.get(i).getValue();
                    AssetsValueDataItem item = new AssetsValueDataItem(mapValue,dateMills);
                    chartDataList.add(item);
                }
            }
        }
        for (int i = 0;i<chartDataList.size();i++) {
            AssetsValueDataItem item = chartDataList.get(i);
            Log.d(TAG, "getDataForChart: chartDataListItem" + i + " " + item.getValue() + " " + item.getUpdate_date());
        }
        Log.d(TAG, "getDataForChart: changeMap value" + changeMap.getValue());
        Log.d(TAG, "getDataForChart: changeMap percent" + changeMap.getPercent());
        ChartData data = new ChartData(chartDataList,changeMap);

        return data;

    }
}
