package com.omni.wallet.framelibrary.utils;

import android.content.Context;

import com.omni.wallet.baselibrary.utils.BasePreferencesUtils;


public class PreferencesUtils extends BasePreferencesUtils {
    private static final String TAG = PreferencesUtils.class.getSimpleName();

    // sp文件的名字
    private static final String SETTINGS = "settings";

    // 保存相关信息的KEY
    private static final String KEY_UPDATE_APK_SIZE = "downloadApkSizeKey";//存储更新使用的APK文件的大小的KEY
    private static final String KEY_ID = "userIdKey";// 保存用户ID的Key
    private static final String KEY_TOKEN = "tokenKey";// 保存用户Token的Key
    private static final String KEY_MOBILE = "mobileKey";// 保存用户手机号的Key
    private static final String KEY_HEADER = "headerKey";// 保存用户头像的Key
    private static final String KEY_REAL_NAME = "realNameKey";// 保存用户姓名的Key
    private static final String KEY_USER_JOB = "userJobKey";// 保存用户职位的Key
    private static final String KEY_QR_CODE_LINK = "QRCodeLinkKey";// 保存用户二维码链接的Key
    private static final String KEY_COMPANY_ID = "companyIdKey";// 保存用户物业公司ID的Key
    private static final String KEY_FIRST_LOGIN = "firstLoginKey";// 新用户第一次登陆标识Ke
    private static final String KEY_VERSION_CODE = "versionCodeKey";// 保存版本信息的Key
    /****************************Omni Wallet*********************************/
    private static final String KEY_NETWORK = "networkKey";// 保存网络类型的Key
    private static final String KEY_WALLET_ADDRESS = "walletAddressKey";// 保存钱包地址的Key
    private static final String KEY_NODE_VERSION = "nodeVersionKey";// 保存节点版本的Key
    private static final String KEY_BTC_PRICE = "btcPriceKey";// 保存btc价格的Key
    private static final String KEY_BTC_PRICE_CHANGE = "btcPriceChangeKey";// 保存btc价格变化的Key
    private static final String KEY_USDT_PRICE = "usdtPriceChangeKey";// 保存usdt价格变化的Key
    private static final String KEY_FROM_PUBKEY = "fromPubKeyKey";// 自身节点pubkey的Key

    /**
     * 版本信息本地化
     */
    public static void saveVersionToLocal(Context context, int value) {
        putInt(SETTINGS, context, KEY_VERSION_CODE, value);
    }

    /**
     * 获取本地保存的版本信息
     */
    public static int getVersionFromLocal(Context context) {
        return getInt(SETTINGS, context, KEY_VERSION_CODE, 0);
    }

    /**
     * 获取本地存储的用户ID
     */
    public static String getUserIdFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_ID, "");
    }

    /**
     * 用户ID存储到本地
     */
    public static void saveUserIdToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_ID, value);
    }

    /**
     * 获取本地存储的Token
     */
    public static String getTokenFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_TOKEN, "");
    }

    /**
     * 用户Token存储到本地
     */
    public static void saveTokenToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_TOKEN, value);
    }

    /**
     * 获取本地存储的用户手机号
     */
    public static String getMobileFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_MOBILE);
    }

    /**
     * 用户手机号存储到本地
     */
    public static void saveMobileToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_MOBILE, value);
    }

    /**
     * 获取本地存储的用户头像
     */
    public static String getHeaderFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_HEADER);
    }

    /**
     * 用户头像存储到本地
     */
    public static void saveHeaderToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_HEADER, value);
    }


    /**
     * 获取本地存储的用户真实姓名
     */
    public static String getRealNameFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_REAL_NAME);
    }

    /**
     * 用户真实姓名存储到本地
     */
    public static void saveRealNameToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_REAL_NAME, value);
    }

    /**
     * 获取本地存储的用户职位
     */
    public static String getUserJobFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_USER_JOB);
    }

    /**
     * 用户职位存储到本地
     */
    public static void saveUserJobToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_USER_JOB, value);
    }

    /**
     * 获取本地存储的二维码链接
     */
    public static String getQRCodeLinkFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_QR_CODE_LINK);
    }

    /**
     * 二维码链接存储到本地
     */
    public static void saveQRCodeLinkToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_QR_CODE_LINK, value);
    }

    /**
     * 获取本地存储的物业公司ID
     */
    public static String getCompanyIDFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_COMPANY_ID);
    }

    /**
     * 物业公司ID存储到本地
     */
    public static void saveCompanyToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_COMPANY_ID, value);
    }

    /**
     * 获取新用户第一次登陆标识
     */
    public static String getFirstLoginFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_FIRST_LOGIN);
    }

    /**
     * 新用户第一次登陆标识存储到本地
     */
    public static void saveFirstLoginToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_FIRST_LOGIN, value);
    }

    /**
     * APK文件大小信息本地化
     */
    public static void saveUpdateAPKSizeToLocal(Context context, long value) {
        putLong(SETTINGS, context, KEY_UPDATE_APK_SIZE, value);
    }

    /**
     * 获取本地保存的更新使用的APK文件大小
     */
    public static long getUpdateAPKSizeFromLocal(Context context) {
        return getLong(SETTINGS, context, KEY_UPDATE_APK_SIZE, 0L);
    }

    /***************************Omni Wallet**********************************/
    /**
     * 获取本地保存的网络类型
     */
    public static String getNetworkFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_NETWORK);
    }

    /**
     * 网络类型本地化
     */
    public static void saveNetworkToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_NETWORK, value);
    }

    /**
     * 获取本地保存的钱包地址
     */
    public static String getWalletAddressFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_WALLET_ADDRESS);
    }

    /**
     * 钱包地址本地化
     */
    public static void saveWalletAddressToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_WALLET_ADDRESS, value);
    }

    /**
     * 获取本地保存的节点版本
     */
    public static String getNodeVersionFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_NODE_VERSION);
    }

    /**
     * 节点版本本地化
     */
    public static void saveNodeVersionToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_NODE_VERSION, value);
    }

    /**
     * 获取本地保存的btc价格
     */
    public static String getBtcPriceFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_BTC_PRICE);
    }

    /**
     * btc价格本地化
     */
    public static void saveBtcPriceToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_BTC_PRICE, value);
    }

    /**
     * 获取本地保存的btc价格变化
     */
    public static String getBtcPriceChangeFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_BTC_PRICE_CHANGE);
    }

    /**
     * btc价格变化本地化
     */
    public static void saveBtcPriceChangeToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_BTC_PRICE_CHANGE, value);
    }

    /**
     * 获取本地保存的usdt价格
     */
    public static String getUsdtPriceFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_USDT_PRICE);
    }

    /**
     * usdt价格本地化
     */
    public static void saveUsdtPriceToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_USDT_PRICE, value);
    }

    /**
     * 获取本地保存的自身节点pubkey
     */
    public static String getFromPubKeyFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_FROM_PUBKEY);
    }

    /**
     * 自身节点pubkey本地化
     */
    public static void saveFromPubKeyToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_FROM_PUBKEY, value);
    }
}
