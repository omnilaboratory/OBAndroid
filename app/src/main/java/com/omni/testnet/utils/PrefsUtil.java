package com.omni.testnet.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.omni.testnet.base.AppApplication;

/**
 * This class simplifies management of preferences.
 */
public class PrefsUtil {

    // shared preference references
    public static final String PREVENT_SCREEN_RECORDING = "preventScreenRecording";
    public static final String FIRST_CURRENCY_IS_PRIMARY = "firstCurrencyIsPrimary";
    public static final String PIN_HASH = "pin_hash";
    public static final String PIN_LENGTH = "pin_length";
    public static final String SETTINGS_VERSION = "settings_ver";
    public static final String ON_CHAIN_FEE_TIER = "on_chain_fee_tier";
    public static final String BIOMETRICS_PREFERRED = "biometrics_preferred";
    public static final String CURRENT_WALLET_CONFIG = "current_wallet_config";
    public static final String AVAILABLE_FIAT_CURRENCIES = "fiat_available";
    public static final String LANGUAGE = "language";
    public static final String LANGUAGE_SYSTEM_DEFAULT = "system";
    public static final String EXCHANGE_RATE_PROVIDER = "exchangeRateProvider";
    public static final String IS_DEFAULT_CURRENCY_SET = "isDefaultCurrencySet";
    public static final String FIRST_CURRENCY = "firstCurrency";
    public static final String SECOND_CURRENCY = "secondCurrency";
    public static final String LAST_CLIPBOARD_SCAN = "lastClipboardScan";
    public static final String SCAN_CLIPBOARD = "scanClipboard";
    public static final String SHOW_IDENTITY_TAP_HINT = "identityTapHint";
    public static final String NODE_INFO_CACHE = "nodeInfoCache";

    // testnet config preferences references
    public static final String WALLET_CONFIGS = "wallet_configs";
    public static final String CONTACTS = "contacts";
    public static final String RANDOM_SOURCE = "random_source";

    // default values
    public static final String DEFAULT_FIAT_CURRENCIES = "[]";


    // Access to default shared prefs
    public static SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(AppApplication.getAppContext());
    }

    public static SharedPreferences.Editor editPrefs() {
        return getPrefs().edit();
    }

    // Shortcuts to often used preferences
    public static boolean isScreenRecordingPrevented() {
        return getPrefs().getBoolean(PREVENT_SCREEN_RECORDING, true);
    }

    public static boolean isFirstCurrencyPrimary() {
        return getPrefs().getBoolean(FIRST_CURRENCY_IS_PRIMARY, true);
    }

    public static boolean isBiometricPreferred() {
        return getPrefs().getBoolean(BIOMETRICS_PREFERRED, false);
    }

    public static boolean isBiometricEnabled() {
        return getPrefs().getBoolean("biometricsEnabled", true);
    }

    public static String getCurrentWalletConfig() {
        return getPrefs().getString(CURRENT_WALLET_CONFIG, "");
    }

    public static String getFirstCurrency() {
        return getPrefs().getString(FIRST_CURRENCY, MonetaryUtil.SATOSHI_UNIT);
    }

    public static String getSecondCurrency() {
        return getPrefs().getString(SECOND_CURRENCY, "USD");
    }

    public static boolean isTorEnabled() {
        return getPrefs().getBoolean("isTorEnabled", true);
    }
}
