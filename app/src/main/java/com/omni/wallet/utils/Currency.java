package com.omni.wallet.utils;

/**
 * This class is used to create currency objects,
 * which hold all information relevant for Zap about that currency.
 */
public class Currency {

    /**
     * The currency Code. Used as display symbol if Symbol is empty.
     * Example: "USD", "EUR", "BTC", ...
     */
    private String mCode;

    /**
     * The exchange rate to Satoshis, as Satoshis are always our base unit.
     */
    private double mRate;

    /**
     * The symbol commonly used.
     * Example for USD: $
     */
    private String mSymbol;

    /**
     * Time of the exchange rate data (in seconds since 00:00:00 UTC on January 1, 1970)
     * This is used to protect the User from initiate an "invoice" with old exchange data.
     */
    private long mTimestamp;

    /**
     * States if this currency is a bitcoin unit (e.g. mBtc) or another currency
     * with a changing exchange rate like fiat currencies or other cryptos.
     */
    private boolean mIsBitcoin;

    public Currency(String code, double rate, long timestamp) {
        mIsBitcoin = false;
        mCode = code;
        mRate = rate;
        mTimestamp = timestamp;
    }

    public Currency(String code, double rate, long timestamp, String symbol) {
        mIsBitcoin = false;
        mCode = code;
        mRate = rate;
        mTimestamp = timestamp;
        mSymbol = symbol;
    }

    public Currency(String code, double rate) {
        mIsBitcoin = true;
        mCode = code;
        mRate = rate;
    }

    public Currency(String code, double rate, String symbol) {
        mIsBitcoin = true;
        mCode = code;
        mRate = rate;
        mSymbol = symbol;
    }


    public String getCode() {
        return mCode;
    }

    public double getRate() {
        return mRate;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public boolean isBitcoin() {
        return mIsBitcoin;
    }
}
