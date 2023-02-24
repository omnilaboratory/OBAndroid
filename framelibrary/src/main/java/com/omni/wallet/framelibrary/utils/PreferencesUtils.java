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
    private static final String KEY_NETWORK = "networkKey";// Save the key of the network type(保存网络类型的Key)
    private static final String KEY_WALLET_ADDRESS = "walletAddressKey";// Save the key of the wallet address(保存钱包地址的Key)
    private static final String KEY_NODE_VERSION = "nodeVersionKey";// Save the key of the node version(保存节点版本的Key)
    private static final String KEY_BTC_PRICE = "btcPriceKey";// Save the key of the BTC price(保存btc价格的Key)
    private static final String KEY_BTC_PRICE_CHANGE = "btcPriceChangeKey";// Save the key of the BTC price change(保存btc价格变化的Key)
    private static final String KEY_USDT_PRICE = "usdtPriceChangeKey";// Save the key of the Usdt price(保存usdt价格变化的Key)
    private static final String KEY_FROM_PUBKEY = "fromPubKeyKey";// Save the key of the pubKey of its own node(自身节点pubkey的Key)
    private static final String KEY_BALANCE_AMOUNT = "balanceAmountKey";// Save the key of the balance amount(账户余额的Key)


    private static final String INIT_WALLET_TYPE = "initWalletType";

    private static final String MACAROON_STRING = "macaroonString";

    private static final String CHANNEL_BACKUP_PATH_ARRAY = "channelBackupPathArray";

    private static final String SEED_STRING = "seedString";

    private static final String PASSWORD_MD5 = "passwordMd5";

    private static final String CREATED = "created";

    private static final String SYNCED = "synced";
    private static final String SEED_CHECKED = "seedChecked";
    private static final String RECOVERY_SEED_STRING = "recoverySeedString";
    private static final String START_CREATE = "startCreate";
    private static final String WALLET_STATE = "walletState";
    private static final String RESTORED_CHANNEL = "restoredChannel";
    private static final String TOTAL_BLOCK = "totalBlock";
    private static final String ASSETS_COUNT = "assetsCount";


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
     * getNetworkFromLocal
     *
     * 获取本地保存的网络类型
     */
    public static String getNetworkFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_NETWORK);
    }

    /**
     * saveNetworkToLocal
     *
     * 网络类型本地化
     */
    public static void saveNetworkToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_NETWORK, value);
    }

    /**
     * getWalletAddressFromLocal
     *
     * 获取本地保存的钱包地址
     */
    public static String getWalletAddressFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_WALLET_ADDRESS);
    }

    /**
     * saveWalletAddressToLocal
     *
     * 钱包地址本地化
     */
    public static void saveWalletAddressToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_WALLET_ADDRESS, value);
    }

    /**
     * getNodeVersionFromLocal
     *
     * 获取本地保存的节点版本
     */
    public static String getNodeVersionFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_NODE_VERSION);
    }

    /**
     * saveNodeVersionToLocal
     *
     * 节点版本本地化
     */
    public static void saveNodeVersionToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_NODE_VERSION, value);
    }

    /**
     * getBtcPriceFromLocal
     *
     * 获取本地保存的btc价格
     */
    public static String getBtcPriceFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_BTC_PRICE);
    }

    /**
     * saveBtcPriceToLocal
     *
     * btc价格本地化
     */
    public static void saveBtcPriceToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_BTC_PRICE, value);
    }

    /**
     * getBtcPriceChangeFromLocal
     *
     * 获取本地保存的btc价格变化
     */
    public static String getBtcPriceChangeFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_BTC_PRICE_CHANGE);
    }

    /**
     * saveBtcPriceChangeToLocal
     *
     * btc价格变化本地化
     */
    public static void saveBtcPriceChangeToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_BTC_PRICE_CHANGE, value);
    }

    /**
     * getUsdtPriceFromLocal
     *
     * 获取本地保存的usdt价格
     */
    public static String getUsdtPriceFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_USDT_PRICE);
    }

    /**
     * saveUsdtPriceToLocal
     *
     * usdt价格本地化
     */
    public static void saveUsdtPriceToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_USDT_PRICE, value);
    }

    /**
     * getFromPubKeyFromLocal
     *
     * 获取本地保存的自身节点pubkey
     */
    public static String getFromPubKeyFromLocal(Context context) {
        return getString(SETTINGS, context, KEY_FROM_PUBKEY);
    }

    /**
     * saveFromPubKeyToLocal
     *
     * 自身节点pubkey本地化
     */
    public static void saveFromPubKeyToLocal(Context context, String value) {
        putString(SETTINGS, context, KEY_FROM_PUBKEY, value);
    }

    public static String getInitWalletType(Context context) {
        return getString(SETTINGS, context, INIT_WALLET_TYPE);
    }

    public static void saveInitWalletType(Context context, String value) {
        putString(SETTINGS, context, INIT_WALLET_TYPE, value);
    }

    public static String getMacaroonString(Context context) {
        return getString(SETTINGS, context, MACAROON_STRING);
    }

    public static void saveMacaroonString(Context context, String value) {
        putString(SETTINGS, context, MACAROON_STRING, value);
    }

    public static String getChannelBackupPath(Context context) {
        return getString(SETTINGS, context, CHANNEL_BACKUP_PATH_ARRAY);
    }

    public static void saveChannelBackupPath(Context context, String value) {
        putString(SETTINGS, context, CHANNEL_BACKUP_PATH_ARRAY, value);
    }

    public static String getSeedString(Context context) {
        return getString(SETTINGS, context, SEED_STRING);
    }

    public static void saveSeedString(Context context, String value) {
        putString(SETTINGS, context, SEED_STRING, value);
    }

    public static String getPasswordMd5(Context context) {
        return getString(SETTINGS, context, PASSWORD_MD5);
    }

    public static void savePasswordMd5(Context context, String value) {
        putString(SETTINGS, context, PASSWORD_MD5, value);
    }

    public static Boolean getCreated(Context context) {
        return getBoolean(SETTINGS, context, CREATED);
    }

    public static void saveCreated(Context context, Boolean value) {
        putBoolean(SETTINGS, context, CREATED, value);
    }

    public static Boolean getSynced(Context context) {
        return getBoolean(SETTINGS, context, SYNCED);
    }

    public static void saveSynced(Context context, Boolean value) {
        putBoolean(SETTINGS, context, SYNCED, value);
    }

    public static Boolean getSeedChecked(Context context) {
        return getBoolean(SETTINGS, context, SEED_CHECKED);
    }

    public static void saveSeedChecked(Context context, Boolean value) {
        putBoolean(SETTINGS, context, SEED_CHECKED, value);
    }

    public static String getRecoverySeedString(Context context) {
        return getString(SETTINGS, context, RECOVERY_SEED_STRING);
    }

    public static void saveRecoverySeedString(Context context, String value) {
        putString(SETTINGS, context, RECOVERY_SEED_STRING, value);
    }

    public static void saveStartCreate(Context context, Boolean value) {
        putBoolean(SETTINGS, context, SYNCED, value);
    }

    public static Boolean getStartCreate(Context context) {
        return getBoolean(SETTINGS, context, START_CREATE);
    }

    public static int getWalletState(Context context) {
        return getInt(SETTINGS, context, WALLET_STATE);
    }

    public static void saveWalletState(Context context, int value) {
        putInt(SETTINGS, context, WALLET_STATE, value);
    }

    public static boolean getRestoredChannel(Context context) {
        return getBoolean(SETTINGS, context, RESTORED_CHANNEL);
    }

    public static void saveTotalBlock(Context context, long value) {
        putLong(SETTINGS, context, TOTAL_BLOCK, value);
    }

    public static long getTotalBlock(Context context) {
        return getLong(SETTINGS, context, TOTAL_BLOCK);
    }

    public static void saveRestoredChannel(Context context, boolean value) {
        putBoolean(SETTINGS, context, RESTORED_CHANNEL, value);
    }

    public static int getAssetsCount(Context context) {
        return getInt(SETTINGS, context, ASSETS_COUNT);
    }

    public static void saveAssetsCount(Context context, int value) {
        putInt(SETTINGS, context, ASSETS_COUNT, value);
    }


    /**
     * getBalanceAmount
     *
     * 获取本地保存的账户余额
     */
    public static long getBalanceAmount(Context context) {
        return getLong(SETTINGS, context, KEY_BALANCE_AMOUNT);
    }

    /**
     * saveBalanceAmount
     *
     * 账户余额本地化
     */
    public static void saveBalanceAmount(Context context, long value) {
        putLong(SETTINGS, context, KEY_BALANCE_AMOUNT, value);
    }
}
