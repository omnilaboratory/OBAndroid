package com.omni.wallet.SharedPreferences;

import android.content.Context;

import com.omni.wallet.baselibrary.utils.BasePreferencesUtils;

public class MainWalletInfo extends BasePreferencesUtils {
    private static final String TAG = MainWalletInfo.class.getSimpleName();

    // sp文件的名字
    // sp file`s name
    private static final String MAIN_WALLET_INFO = "mainWalletInfo";

    // keys
    // 键值
    private static final String INIT_WALLET_TYPE = "initWalletType";
    private static final String MACAROON_STRING = "macaroonString";
    private static final String SEED_STRING = "seedString";
    private static final String RECOVERY_SEED_STRING = "recoverySeedString";
    private static final String HEADER_BIN_CHECKED = "headerBinChecked";
    private static final String FILTER_HEADER_BIN_CHECKED = "filterHeaderBinChecked";
    private static final String NEUTRINO_DB_CHECKED = "neutrinoDbChecked";
    private static final String ALIAS = "alias";
    private static final String WALLET_ADDRESS = "walletAddress";
    private static final String FROM_PUB_KEY = "fromPubKey";
    private static final String TOTAL_BLOCK = "totalBlock";
    private static final String ASSETS_COUNT = "assetsCount";
    private static final String ASSET_LIST_STRING = "assetListString";
    private static final String PASSWORD_SECRET = "passwordSecret";
    private static final String NEW_PASSWORD_SECRET_STRING = "newPassSecret";
    private static final String NODE_VERSION = "nodeVersion";
    private static final String START_PARAMS = "startParams";

    public static void saveInitWalletType(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, INIT_WALLET_TYPE, value);
    }

    public static String getInitWalletType(Context context) {
        return getString(MAIN_WALLET_INFO, context, INIT_WALLET_TYPE);
    }

    public static void saveMacaroonString(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, MACAROON_STRING, value);
    }

    public static String getMacaroonString(Context context) {
        return getString(MAIN_WALLET_INFO, context, MACAROON_STRING);
    }

    public static void saveSeedString(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, SEED_STRING, value);
    }

    public static String getSeedString(Context context) {
        return getString(MAIN_WALLET_INFO, context, SEED_STRING);
    }

    public static void saveRecoverySeedString(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, RECOVERY_SEED_STRING, value);
    }

    public static String getRecoverySeedString(Context context) {
        return getString(MAIN_WALLET_INFO, context, RECOVERY_SEED_STRING);
    }

    public static void saveHeaderBinChecked(Context context, boolean value) {
        putBoolean(MAIN_WALLET_INFO, context, HEADER_BIN_CHECKED, value);
    }

    public static boolean getHeaderBinChecked(Context context) {
        return getBoolean(MAIN_WALLET_INFO, context, HEADER_BIN_CHECKED);
    }

    public static void saveFilterHeaderBinChecked(Context context, boolean value) {
        putBoolean(MAIN_WALLET_INFO, context, FILTER_HEADER_BIN_CHECKED, value);
    }

    public static boolean getFilterHeaderBinChecked(Context context) {
        return getBoolean(MAIN_WALLET_INFO, context, FILTER_HEADER_BIN_CHECKED);
    }

    public static void saveNeutrinoDbChecked(Context context, boolean value) {
        putBoolean(MAIN_WALLET_INFO, context, NEUTRINO_DB_CHECKED, value);
    }

    public static boolean getNeutrinoDbChecked(Context context) {
        return getBoolean(MAIN_WALLET_INFO, context, NEUTRINO_DB_CHECKED);
    }

    public static void saveAlias(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, ALIAS, value);
    }

    public static String getAlias(Context context) {
        return getString(MAIN_WALLET_INFO, context, ALIAS);
    }

    public static void saveWalletAddress(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, WALLET_ADDRESS, value);
    }

    public static String getWalletAddress(Context context) {
        return getString(MAIN_WALLET_INFO, context, WALLET_ADDRESS);
    }

    public static void saveFromPubKey(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, FROM_PUB_KEY, value);
    }

    public static String getFromPubKey(Context context) {
        return getString(MAIN_WALLET_INFO, context, FROM_PUB_KEY);
    }

    public static void saveTotalBlock(Context context, long value) {
        putLong(MAIN_WALLET_INFO, context, TOTAL_BLOCK, value);
    }

    public static long getTotalBlock(Context context) {
        return getLong(MAIN_WALLET_INFO, context, TOTAL_BLOCK);
    }

    public static void saveAssetCount(Context context, int value) {
        putInt(MAIN_WALLET_INFO, context, ASSETS_COUNT, value);
    }

    public static int getAssetsCount(Context context) {
        return getInt(MAIN_WALLET_INFO, context, ASSETS_COUNT);
    }

    public static void saveAssetListString(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, ASSET_LIST_STRING, value);
    }

    public static String getAssetListString(Context context) {
        return getString(MAIN_WALLET_INFO, context, ASSET_LIST_STRING);
    }

    public static void savePasswordSecret(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, PASSWORD_SECRET, value);
    }

    public static String getPasswordSecret(Context context) {
        return getString(MAIN_WALLET_INFO, context, PASSWORD_SECRET);
    }

    public static void saveNewPasswordSecret(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, NEW_PASSWORD_SECRET_STRING, value);
    }

    public static String getNewPasswordSecret(Context context) {
        return getString(MAIN_WALLET_INFO, context, NEW_PASSWORD_SECRET_STRING);
    }

    public static void saveNodeVersion(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, NODE_VERSION, value);
    }

    public static String getNodeVersion(Context context) {
        return getString(MAIN_WALLET_INFO, context, NODE_VERSION);
    }

    public static void saveStartParams(Context context, String value) {
        putString(MAIN_WALLET_INFO, context, START_PARAMS, value);
    }

    public static String getStartParams(Context context) {
        return getString(MAIN_WALLET_INFO, context, START_PARAMS);
    }
}
