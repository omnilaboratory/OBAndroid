package com.omni.testnet.data;

import android.content.Context;

import com.omni.testnet.baselibrary.utils.BasePreferencesUtils;

public class AssetsDataOfToday extends BasePreferencesUtils {
    private static final String TAG = AssetsDataOfToday.class.getSimpleName();
    private static final  String ASSETS_DATA = "assetsData";
    private AssetsDataOfToday(){}
    private static AssetsDataOfToday mInstance;

    public static AssetsDataOfToday getInstance(){
        if (mInstance == null){
            synchronized (AssetsDataOfToday.class){
                if (mInstance == null){
                    mInstance = new AssetsDataOfToday();
                }
            }
        }
        return mInstance;
    }

    private static final String LAST_UPDATE_BTC_PRICE_DATE = "lastUpdateBTCPriceDate";
    private static final String LAST_UPDATE_BTC_CHANNEL_AMOUNT_DATE = "lastUpdateBTCChannelAmountDate";
    private static final String LAST_UPDATE_BTC_AMOUNT_DATE = "lastUpdateBTCAmountDate";
    private static final String LAST_UPDATE_BTC_PRICE = "btcPrice";
    private static final String LAST_UPDATE_BTC_AMOUNT = "btcAmount";
    private static final String LAST_UPDATE_BTC_CHANNEL_AMOUNT = "btcChannelAmount";
    private static final String LAST_UPDATE_DOLLAR_PRICE_DATE = "lastUpdateDollarPriceDate";
    private static final String LAST_UPDATE_DOLLAR_CHANNEL_AMOUNT_DATE = "lastUpdateDollarChannelAmountDate";
    private static final String LAST_UPDATE_DOLLAR_AMOUNT_DATE = "lastUpdateDollarAmountDate";
    private static final String LAST_UPDATE_DOLLAR_PRICE = "dollarPrice";
    private static final String LAST_UPDATE_DOLLAR_AMOUNT = "dollarAmount";
    private static final String LAST_UPDATE_DOLLAR_CHANNEL_AMOUNT = "dollarChannelAmount";

    /**
     * @description data about btc
     * @param lastUpdateBTCPriceDate the last date of update btc price
     * @param lastUpdateBTCChannelAmountDate the last date of update btc channel amount
     * @param lastUpdateBTCAmountDate the last date of update btc amount
     * @param btcPrice btc price
     * @param btcAmount btc amount
     * @param btcChannelAmount btc channel amount
     */
    private String lastUpdateBTCPriceDate;
    private String lastUpdateBTCChannelAmountDate;
    private String lastUpdateBTCAmountDate;
    private double btcPrice;
    private double btcAmount;
    private double btcChannelAmount;
    /**
     * @description data about dollar
     * @param lastUpdateDollarPriceDate the last date of update dollar price
     * @param lastUpdateDollarChannelAmountDate the last date of update dollar channel amount
     * @param lastUpdateDollarAmountDate the last date of update dollar amount
     * @param dollarPrice dollar price
     * @param dollarAmount dollar amount
     * @param dollarChannelAmount dollar channel amount
     */
    private String lastUpdateDollarPriceDate;
    private String lastUpdateDollarChannelAmountDate;
    private String lastUpdateDollarAmountDate;
    private double dollarPrice;
    private double dollarAmount;
    private double dollarChannelAmount;

    public void setLastUpdateBTCAmountDate(Context context,String lastUpdateBTCAmountDate) {
        putString(ASSETS_DATA,context,LAST_UPDATE_BTC_AMOUNT_DATE,lastUpdateBTCAmountDate);
        this.lastUpdateBTCAmountDate = lastUpdateBTCAmountDate;
    }

    public String getLastUpdateBTCAmountDate(Context context) {
        lastUpdateBTCAmountDate = getString(ASSETS_DATA,context,LAST_UPDATE_BTC_AMOUNT_DATE,"");
        if (lastUpdateBTCAmountDate.isEmpty()){
            lastUpdateBTCAmountDate = "";
        }
        return lastUpdateBTCAmountDate;
    }

    public void setLastUpdateBTCChannelAmountDate(Context context,String lastUpdateBTCChannelAmountDate) {
        putString(ASSETS_DATA,context,LAST_UPDATE_BTC_CHANNEL_AMOUNT_DATE,lastUpdateBTCChannelAmountDate);
        this.lastUpdateBTCChannelAmountDate = lastUpdateBTCChannelAmountDate;
    }

    public String getLastUpdateBTCChannelAmountDate(Context context) {
        lastUpdateBTCChannelAmountDate = getString(ASSETS_DATA,context,LAST_UPDATE_BTC_CHANNEL_AMOUNT_DATE,"");
        if (lastUpdateBTCChannelAmountDate.isEmpty()){
            lastUpdateBTCChannelAmountDate = "";
        }
        return lastUpdateBTCChannelAmountDate;
    }

    public void setLastUpdateBTCPriceDate(Context context,String lastUpdateBTCPriceDate) {
        putString(ASSETS_DATA,context,LAST_UPDATE_BTC_PRICE_DATE,lastUpdateBTCPriceDate);
        this.lastUpdateBTCPriceDate = lastUpdateBTCPriceDate;
    }

    public String getLastUpdateBTCPriceDate(Context context) {
        lastUpdateBTCPriceDate = getString(ASSETS_DATA,context,LAST_UPDATE_BTC_PRICE_DATE,"");
        if (lastUpdateBTCPriceDate.isEmpty()){
            lastUpdateBTCPriceDate = "";
        }
        return lastUpdateBTCPriceDate;
    }

    public void setLastUpdateDollarAmountDate(Context context,String lastUpdateDollarAmountDate) {
        putString(ASSETS_DATA,context,LAST_UPDATE_DOLLAR_AMOUNT_DATE,lastUpdateDollarAmountDate);
        this.lastUpdateDollarAmountDate = lastUpdateDollarAmountDate;
    }

    public String getLastUpdateDollarAmountDate() {
        return lastUpdateDollarAmountDate;
    }

    public void setLastUpdateDollarChannelAmountDate(String lastUpdateDollarChannelAmountDate) {
        this.lastUpdateDollarChannelAmountDate = lastUpdateDollarChannelAmountDate;
    }

    public String getLastUpdateDollarChannelAmountDate() {
        return lastUpdateDollarChannelAmountDate;
    }

    public void setLastUpdateDollarPriceDate(String lastUpdateDollarPriceDate) {
        this.lastUpdateDollarPriceDate = lastUpdateDollarPriceDate;
    }

    public String getLastUpdateDollarPriceDate() {
        return lastUpdateDollarPriceDate;
    }

    public void setBtcAmount(double btcAmount) {
        this.btcAmount = btcAmount;
    }

    public double getBtcAmount() {
        return btcAmount;
    }

    public void setBtcChannelAmount(double btcChannelAmount) {
        this.btcChannelAmount = btcChannelAmount;
    }

    public double getBtcChannelAmount() {
        return btcChannelAmount;
    }

    public void setBtcPrice(double btcPrice) {
        this.btcPrice = btcPrice;
    }

    public double getBtcPrice() {
        return btcPrice;
    }

    public void setDollarAmount(double dollarAmount) {
        this.dollarAmount = dollarAmount;
    }

    public double getDollarAmount() {
        return dollarAmount;
    }

    public void setDollarChannelAmount(double dollarChannelAmount) {
        this.dollarChannelAmount = dollarChannelAmount;
    }

    public double getDollarChannelAmount() {
        return dollarChannelAmount;
    }

    public void setDollarPrice(double dollarPrice) {
        this.dollarPrice = dollarPrice;
    }

    public double getDollarPrice() {
        return dollarPrice;
    }
}
