package com.omni.wallet_mainnet.utils;

import android.content.Context;
import android.os.Build;

import com.omni.wallet_mainnet.base.AppApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This Singleton helps to display any value in the desired format.
 */
public class MonetaryUtil {

    public static final String BTC_UNIT = "BTC";
    public static final String MBTC_UNIT = "mBTC";
    public static final String BIT_UNIT = "bit";
    public static final String SATOSHI_UNIT = "unit";

    private static final String LOG_TAG = MonetaryUtil.class.getName();

    private static MonetaryUtil mInstance;
    private Context mContext;
    private Currency mFirstCurrency;
    private Currency mSecondCurrency;


    private MonetaryUtil() {
        mContext = AppApplication.getAppContext();

        loadFirstCurrencyFromPrefs(PrefsUtil.getFirstCurrency());

        String SecondCurrency = PrefsUtil.getSecondCurrency();
        switch (SecondCurrency) {
            case BTC_UNIT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setSecondCurrency(SecondCurrency, 1e-8, "\u20BF");
                } else {
                    setSecondCurrency(SecondCurrency, 1e-8);
                }
                break;
            case MBTC_UNIT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setSecondCurrency(SecondCurrency, 1e-5, "m\u20BF");
                } else {
                    setSecondCurrency(SecondCurrency, 1e-5);
                }
                break;
            case BIT_UNIT:
                setSecondCurrency(SecondCurrency, 1e-2);
                break;
            case SATOSHI_UNIT:
                setSecondCurrency(SecondCurrency, 1d);
                break;
            default:
                // Here we go if the user has selected a fiat currency as second currency.
                if (PrefsUtil.getPrefs().getString("fiat_" + PrefsUtil.getSecondCurrency(), "").isEmpty()) {
                    mSecondCurrency = new Currency(PrefsUtil.getSecondCurrency(), 0, 0);
                } else {
                    loadSecondCurrencyFromPrefs(PrefsUtil.getSecondCurrency());
                }
        }
    }

    public static MonetaryUtil getInstance() {
        if (mInstance == null) {
            mInstance = new MonetaryUtil();
        }

        return mInstance;
    }

    public Currency getFirstCurrency() {
        return mFirstCurrency;
    }

    public Currency getSecondCurrency() {
        return mSecondCurrency;
    }

    /**
     * Get the amount and display unit of the primary currency as properly formatted string.
     *
     * @param value in Satoshis
     * @return formatted string
     */
    public String getPrimaryDisplayAmountAndUnit(long value) {
        return getPrimaryDisplayAmount(value) + " " + getPrimaryDisplayUnit();
    }


    /**
     * Get the amount of the primary currency as properly formatted string.
     *
     * @param value in Satoshis
     * @return formatted string
     */
    public String getPrimaryDisplayAmount(long value) {
        if (PrefsUtil.isFirstCurrencyPrimary()) {
            return getFirstCurrencyAmount(value);
        } else {
            return getSecondCurrencyAmount(value);
        }
    }


    /**
     * Get the display unit of the primary currency as properly formatted string.
     *
     * @return formatted string
     */
    public String getPrimaryDisplayUnit() {
        if (PrefsUtil.isFirstCurrencyPrimary()) {
            return getFirstDisplayUnit();
        } else {
            return getSecondDisplayUnit();
        }
    }


    /**
     * Get the amount and display unit of the secondary currency as properly formatted string.
     *
     * @param value in Satoshis
     * @return formatted string
     */
    public String getSecondaryDisplayAmountAndUnit(long value) {
        return getSecondaryDisplayAmount(value) + " " + getSecondaryDisplayUnit();
    }


    /**
     * Get the amount of the secondary currency as properly formatted string.
     *
     * @param value in Satoshis
     * @return formatted string
     */
    public String getSecondaryDisplayAmount(long value) {
        if (PrefsUtil.isFirstCurrencyPrimary()) {
            return getSecondCurrencyAmount(value);
        } else {
            return getFirstCurrencyAmount(value);
        }
    }


    /**
     * Get the display unit of the secondary currency as properly formatted string.
     *
     * @return formatted string
     */
    public String getSecondaryDisplayUnit() {
        if (PrefsUtil.isFirstCurrencyPrimary()) {
            return getSecondDisplayUnit();
        } else {
            return getFirstDisplayUnit();
        }
    }


    /**
     * This function returns how old our fiat exchange rate data is.
     *
     * @return Age in seconds.
     */
    public long getExchangeRateAge() {
        return (System.currentTimeMillis() / 1000) - mSecondCurrency.getTimestamp();
    }

    /**
     * Load the first currency from the default settings using a currencyCode (BTC, mBTC, ...)
     *
     * @param currencyCode
     */
    public void loadFirstCurrencyFromPrefs(String currencyCode) {

        switch (currencyCode) {
            case BTC_UNIT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setFirstCurrency(currencyCode, 1e-8, "\u20BF");
                } else {
                    setFirstCurrency(currencyCode, 1e-8);
                }
                break;
            case MBTC_UNIT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setFirstCurrency(currencyCode, 1e-8, "m\u20BF");
                } else {
                    setFirstCurrency(currencyCode, 1e-8);
                }
                break;
            case BIT_UNIT:
                setFirstCurrency(currencyCode, 1e-2);
                break;
            case SATOSHI_UNIT:
                setFirstCurrency(currencyCode, 1d);
                break;
            default:
                setFirstCurrency(currencyCode, 1e-8);
        }
    }


    /**
     * Load the second currency from the default settings using a currencyCode (USD, EUR, BTC, ...)
     * By loading it, we have access to it without parsing the JSON string over and over.
     *
     * @param currencyCode (USD, EUR, etc.)
     */
    public void loadSecondCurrencyFromPrefs(String currencyCode) {
        switch (currencyCode) {
            case BTC_UNIT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setSecondCurrency(currencyCode, 1e-8, "\u20BF");
                } else {
                    setSecondCurrency(currencyCode, 1e-8);
                }
                break;
            case MBTC_UNIT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setSecondCurrency(currencyCode, 1e-8, "m\u20BF");
                } else {
                    setSecondCurrency(currencyCode, 1e-8);
                }
                break;
            case BIT_UNIT:
                setSecondCurrency(currencyCode, 1e-2);
                break;
            case SATOSHI_UNIT:
                setSecondCurrency(currencyCode, 1d);
                break;
            default:

                try {
                    JSONObject selectedCurrency = new JSONObject(PrefsUtil.getPrefs().getString("fiat_" + currencyCode, "{}"));
                    Currency currency;
                    if (selectedCurrency.has("symbol")) {
                        currency = new Currency(currencyCode,
                                selectedCurrency.getDouble("rate"),
                                selectedCurrency.getLong("timestamp"),
                                selectedCurrency.getString("symbol"));
                    } else {
                        currency = new Currency(currencyCode,
                                selectedCurrency.getDouble("rate"),
                                selectedCurrency.getLong("timestamp"));
                    }

                    mSecondCurrency = currency;
                } catch (JSONException e) {
                    // App was probably never started before. If we can't find the fiat in the prefs,
                    // create a placeholder currency.
                    mSecondCurrency = new Currency("USD", 0, 0);
                }
        }
    }


    /**
     * Switch which of the currencies (first or second one) is used as primary currency
     */
    public void switchCurrencies() {
        if (PrefsUtil.isFirstCurrencyPrimary()) {
            PrefsUtil.editPrefs().putBoolean(PrefsUtil.FIRST_CURRENCY_IS_PRIMARY, false).apply();
        } else {
            PrefsUtil.editPrefs().putBoolean(PrefsUtil.FIRST_CURRENCY_IS_PRIMARY, true).apply();
        }
    }

    /**
     * Get primary currency object
     *
     * @return
     */
    public Currency getPrimaryCurrency() {
        if (PrefsUtil.isFirstCurrencyPrimary()) {
            return mFirstCurrency;
        } else {
            return mSecondCurrency;
        }
    }


    /**
     * Get secondary currency object
     *
     * @return
     */
    public Currency getSecondaryCurrency() {
        if (PrefsUtil.isFirstCurrencyPrimary()) {
            return mSecondCurrency;
        } else {
            return mFirstCurrency;
        }
    }

    /**
     * Use this method to switch the input amount of a textfield right before currencies are swapped.
     *
     * @param primaryValue value string of the input field
     * @return value string for the input field converted in the secondary currency
     */
    public String convertPrimaryToSecondaryCurrency(String primaryValue) {
        if (primaryValue == null || primaryValue.equals("")) {
            return "";
        } else {
            if (PrefsUtil.isFirstCurrencyPrimary()) {
                double value = Double.parseDouble(primaryValue);
                double result = (value / mFirstCurrency.getRate() * mSecondCurrency.getRate());
                DecimalFormat df = TextInputCurrencyFormat(mSecondCurrency);
                return df.format(result);
            } else {
                double value = Double.parseDouble(primaryValue);
                ;
                double result = (value / mSecondCurrency.getRate()) * mFirstCurrency.getRate();
                DecimalFormat df = TextInputCurrencyFormat(mFirstCurrency);
                return df.format(result);
            }
        }
    }

    /**
     * Converts the supplied value to satoshis. The exchange rate of the primary currency is used.
     *
     * @param primaryValue
     * @return String without grouping or fractions
     */
    public String convertPrimaryToSatoshi(String primaryValue) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(0);
        if (primaryValue == null || primaryValue.equals("")) {
            return "0";
        } else {
            if (PrefsUtil.isFirstCurrencyPrimary()) {
                double value = Double.parseDouble(primaryValue);
                double result = (value / mFirstCurrency.getRate());
                return df.format(result);
            } else {
                double value = Double.parseDouble(primaryValue);
                double result = (value / mSecondCurrency.getRate());
                return df.format(result);
            }
        }
    }

    /**
     * Converts the supplied value to satoshis. The exchange rate of the secondary currency is used.
     *
     * @param secondaryValue
     * @return String without grouping or fractions
     */
    public String convertSecondaryToSatoshi(String secondaryValue) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(0);
        if (secondaryValue == null || secondaryValue.equals("")) {
            return "0";
        } else {
            if (PrefsUtil.isFirstCurrencyPrimary()) {
                double value = Double.parseDouble(secondaryValue);
                double result = (value / mSecondCurrency.getRate());
                return df.format(result);
            } else {
                double value = Double.parseDouble(secondaryValue);
                double result = (value / mFirstCurrency.getRate());
                return df.format(result);
            }
        }
    }

    /**
     * Converts the given satoshis to primary currency.
     *
     * @param value
     * @return String without grouping
     */
    public String convertSatoshiToPrimary(Long value) {

        if (value == 0) {
            return "0";
        } else {
            if (PrefsUtil.isFirstCurrencyPrimary()) {
                double result = (value * mFirstCurrency.getRate());
                DecimalFormat df = TextInputCurrencyFormat(mFirstCurrency);
                return df.format(result);
            } else {
                double result = (value * mSecondCurrency.getRate());
                DecimalFormat df = TextInputCurrencyFormat(mSecondCurrency);
                return df.format(result);
            }
        }
    }

    /**
     * Converts the given satoshis to secondary currency.
     *
     * @param value
     * @return String without grouping
     */
    public String convertSatoshiToSecondary(Long value) {

        if (value == 0) {
            return "0";
        } else {
            if (PrefsUtil.isFirstCurrencyPrimary()) {
                double result = (value * mSecondCurrency.getRate());
                DecimalFormat df = TextInputCurrencyFormat(mSecondCurrency);
                return df.format(result);
            } else {
                double result = (value * mFirstCurrency.getRate());
                DecimalFormat df = TextInputCurrencyFormat(mFirstCurrency);
                return df.format(result);
            }
        }
    }

    /**
     * Converts the supplied value to bitcoin. The exchange rate of the primary currency is used.
     *
     * @param primaryValue
     * @return String without grouping and maximum fractions of 8 digits
     */
    public String convertPrimaryToBitcoin(String primaryValue) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(8);

        if (primaryValue == null || primaryValue.equals("")) {
            return "0";
        } else {
            if (PrefsUtil.isFirstCurrencyPrimary()) {
                double value = Double.parseDouble(primaryValue);
                double result = (value / mFirstCurrency.getRate() / 1e8);
                return df.format(result);
            } else {
                double value = Double.parseDouble(primaryValue);
                double result = (value / mSecondCurrency.getRate() / 1e8);
                return df.format(result);
            }
        }
    }

    /**
     * Converts the supplied value to bitcoin. The exchange rate of the secondary currency is used.
     *
     * @param secondaryValue
     * @return String without grouping and maximum fractions of 8 digits
     */
    public String convertSecondaryToBitcoin(String secondaryValue) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(8);

        if (secondaryValue == null || secondaryValue.equals("")) {
            return "0";
        } else {
            if (PrefsUtil.isFirstCurrencyPrimary()) {
                double value = Double.parseDouble(secondaryValue);
                double result = (value / mSecondCurrency.getRate() / 1e8);
                return df.format(result);
            } else {
                double value = Double.parseDouble(secondaryValue);
                double result = (value / mFirstCurrency.getRate() / 1e8);
                return df.format(result);
            }
        }
    }

    /**
     * Converts the given satoshis to bitcoin currency.
     *
     * @param satoshiValue
     * @return String without grouping and maximum fractions of 8 digits
     */
    public String convertSatoshiToBitcoin(String satoshiValue) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(8);

        if (satoshiValue == null || satoshiValue.equals("")) {
            return "0";
        } else {
            double value = Double.parseDouble(satoshiValue);
            double result = (value / 1e8);
            return df.format(result);
        }
    }


    /**
     * Checks if a numerical currency input is valid
     *
     * @param input
     * @return boolean
     */
    public boolean validateCurrencyInput(String input, Currency currency) {

        int numberOfDecimals = 0;

        // Bitcoin
        if (currency.isBitcoin()) {

            String btcUnit = PrefsUtil.getFirstCurrency();
            switch (btcUnit) {
                case BTC_UNIT:
                    numberOfDecimals = 8;
                    break;
                case MBTC_UNIT:
                    numberOfDecimals = 5;
                    break;
                case BIT_UNIT:
                    numberOfDecimals = 2;
                    break;
                case SATOSHI_UNIT:
                    numberOfDecimals = 0;
                    break;
                default:
                    numberOfDecimals = 0;
            }
        }

        // Fiat
        else {
            numberOfDecimals = 2;
        }

        if (input.equals(".")) {
            return true;
        }

        // Regex selecting any or no number of digits optionally followed by "." or "," that is followed by up to numberOfDecimals digits
        String regexPattern = "[0-9]*([\\.,]{0,1}[0-9]{0," + numberOfDecimals + "})";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);

        boolean matchedPattern = matcher.matches();

        // Check if the input is too large. We limit the input to the value of 1000 BTC.
        // This will prevent any overflow errors when calculating in satoshis or later mSats.

        boolean notTooBig = false;
        try {
            notTooBig = (Long.parseLong(convertPrimaryToSatoshi(input)) <= 1e11);
        } catch (NumberFormatException e) {
            return false;
        }

        boolean validZeros;
        if (input.startsWith("0")) {
            if (input.length() > 1) {
                if (input.startsWith("0.")) {
                    validZeros = true;
                } else {
                    validZeros = false;
                }
            } else {
                validZeros = true;
            }
        } else {
            validZeros = true;
        }

        return matchedPattern && notTooBig && validZeros;
    }


    private void setFirstCurrency(String currencyCode, Double rate) {
        mFirstCurrency = new Currency(currencyCode, rate);
    }

    private void setFirstCurrency(String currencyCode, Double rate, String symbol) {
        mFirstCurrency = new Currency(currencyCode, rate, symbol);
    }


    private void setSecondCurrency(String currencyCode, Double rate) {
        mSecondCurrency = new Currency(currencyCode, rate);
    }

    private void setSecondCurrency(String currencyCode, Double rate, String symbol) {
        mSecondCurrency = new Currency(currencyCode, rate, symbol);
    }

    public void setSecondCurrency(String currencyCode, Double rate, Long timestamp) {
        mSecondCurrency = new Currency(currencyCode, rate, timestamp);
    }

    void setSecondCurrency(String currencyCode, Double rate, Long timestamp, String symbol) {
        mSecondCurrency = new Currency(currencyCode, rate, timestamp, symbol);
    }


    private String getFirstDisplayUnit() {
        if (mFirstCurrency.getSymbol() == null) {
            return mFirstCurrency.getCode();
        } else {
            return mFirstCurrency.getSymbol();
        }
    }

    private String getSecondDisplayUnit() {
        if (mSecondCurrency.getSymbol() == null) {
            return mSecondCurrency.getCode();
        } else {
            return mSecondCurrency.getSymbol();
        }
    }

    private String getFirstCurrencyAmount(long value) {
        if (mFirstCurrency.isBitcoin()) {
            switch (mFirstCurrency.getCode()) {
                case BTC_UNIT:
                    return formatAsBtcDisplayAmount(value);
                case MBTC_UNIT:
                    return formatAsMbtcDisplayAmount(value);
                case BIT_UNIT:
                    return formatAsBitsDisplayAmount(value);
                case SATOSHI_UNIT:
                    return formatAsSatoshiDisplayAmount(value);
                default:
                    return formatAsBtcDisplayAmount(value);
            }
        } else {
            return getFiatDisplayAmount(value);
        }
    }

    private String getSecondCurrencyAmount(long value) {
        if (mSecondCurrency.isBitcoin()) {
            switch (mSecondCurrency.getCode()) {
                case BTC_UNIT:
                    return formatAsBtcDisplayAmount(value);
                case MBTC_UNIT:
                    return formatAsMbtcDisplayAmount(value);
                case BIT_UNIT:
                    return formatAsBitsDisplayAmount(value);
                case SATOSHI_UNIT:
                    return formatAsSatoshiDisplayAmount(value);
                default:
                    return formatAsBtcDisplayAmount(value);
            }
        } else {
            return getFiatDisplayAmount(value);
        }
    }


    ////////// Bitcoin display functions /////////////


    private String formatAsBtcDisplayAmount(long value) {
        Locale loc = mContext.getResources().getConfiguration().locale;
        NumberFormat nf = NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat) nf;
        df.setMaximumFractionDigits(8);
        df.setMinimumIntegerDigits(1);
        df.setMaximumIntegerDigits(16);
        return df.format(value / 1e8);
    }

    private String formatAsMbtcDisplayAmount(long value) {
        Locale loc = mContext.getResources().getConfiguration().locale;
        NumberFormat nf = NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat) nf;
        df.setMaximumFractionDigits(5);
        df.setMinimumIntegerDigits(1);
        df.setMaximumIntegerDigits(19);
        return df.format(value / 100000d);
    }

    private String formatAsBitsDisplayAmount(long value) {
        Locale loc = mContext.getResources().getConfiguration().locale;
        NumberFormat nf = NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat) nf;
        df.setMaximumFractionDigits(2);
        df.setMinimumIntegerDigits(1);
        df.setMaximumIntegerDigits(22);
        String result = df.format(value / 100d);

        // If we have a fraction, then always show 2 fraction digits for bits
        if (result.contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()))) {
            df.setMinimumFractionDigits(2);
            return df.format(value / 100d);
        } else {
            return result;
        }
    }

    private String formatAsSatoshiDisplayAmount(long value) {
        Locale loc = mContext.getResources().getConfiguration().locale;
        NumberFormat nf = NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat) nf;
        df.setMinimumIntegerDigits(1);
        df.setMaximumIntegerDigits(16);
        return df.format(value);
    }


    ////////// Fiat functions /////////////


    private String getFiatDisplayAmount(long value) {
        double fiatValue = (mSecondCurrency.getRate()) * value;
        return formatAsFiatDisplayAmount(fiatValue);
    }


    private String formatAsFiatDisplayAmount(double value) {
        Locale loc = mContext.getResources().getConfiguration().locale;
        NumberFormat nf = NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat) nf;
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setMinimumIntegerDigits(1);
        df.setMaximumIntegerDigits(22);
        String result = df.format(value);
        return result;
    }


    private DecimalFormat TextInputCurrencyFormat(final Currency currency) {
        // We have to use the Locale.US here to ensure Double.parse works correctly later.
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) nf;
        df.setGroupingUsed(false);
        if (currency.isBitcoin()) {

            switch (currency.getCode()) {
                case BTC_UNIT:
                    df.setMaximumFractionDigits(8);
                    break;
                case MBTC_UNIT:
                    df.setMaximumFractionDigits(5);
                    break;
                case BIT_UNIT:
                    df.setMaximumFractionDigits(2);
                    break;
                case SATOSHI_UNIT:
                    df.setMaximumFractionDigits(0);
                    break;
                default:
                    df.setMaximumFractionDigits(8);
            }
        } else {
            df.setMaximumFractionDigits(2);
        }
        return df;
    }

}
