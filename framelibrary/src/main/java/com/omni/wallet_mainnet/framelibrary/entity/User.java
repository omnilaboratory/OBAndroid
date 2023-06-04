package com.omni.wallet_mainnet.framelibrary.entity;

import android.content.Context;

import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;
import com.omni.wallet_mainnet.framelibrary.utils.PreferencesUtils;


public class User {
    private static final String TAG = User.class.getSimpleName();

    private User() {
    }

    private static User mInstance;

    public static User getInstance() {
        if (mInstance == null) {
            synchronized (User.class) {
                if (mInstance == null) {
                    mInstance = new User();
                }
            }
        }
        return mInstance;
    }

    // 用户Token
    private String token;
    // 用户ID
    private String userId;
    // 用户手机号
    private String phone;
    // 头像
    private String header;
    // 用户姓名
    private String realName;
    // 用户职位
    private String userJob;
    // 二维码链接
    private String qrCodeLink;
    // 物业公司ID
    private String companyId;
    // 新用户第一次登陆标识
    private String firstLogin;

    // 页面间传递JSon数据
    private String jsonStr;
    // 是否正在显示版本更新的Dialog
    private boolean isShowUpdateDialog = false;
    /***************************Omni Wallet**********************************/
    // Alias(别名)
    private String alias;
    // Network type(网络类型)
    private String network;
    // Wallet address(钱包地址)
    private String walletAddress;
    // Node version(节点版本)
    private String nodeVersion;
    // Btc price(Btc价格)
    private String btcPrice;
    // Btc price change(Btc价格变化)
    private String btcPriceChange;
    // Usdt price(usdt价格)
    private String usdtPrice;
    // PubKey of its own node(自身节点的pubKey)
    private String fromPubKey;

    private String initWalletType;

    private String macaroonString;

    private String channelBackupPathArray;

    private String seedString;

    private String passwordMd5;

    private String recoverySeedString;

    private Boolean created;

    private Boolean synced;

    private Boolean seedChecked;

    private Boolean startCreate;

    private int walletState;

    private boolean restoredChannel;

    private long totalBlock;

    // Useful token amount
    private int assetsCount;

    // Balance amount(账户余额)
    private long balanceAmount;

    private String newPassMd5String;

    private boolean headerBinChecked;

    private boolean filterHeaderBinChecked;

    private boolean neutrinoDbChecked;

    private String assetListString;

    private boolean isWalletDataMoved;

    private boolean isSeedStringSecreted;

    private boolean isBackUp;

    public String getToken(Context context) {
        token = PreferencesUtils.getTokenFromLocal(context);
        return token;
    }

    public void setToken(Context context, String token) {
        this.token = token;
        PreferencesUtils.saveTokenToLocal(context, token);
    }

    public String getPhone(Context context) {
        phone = PreferencesUtils.getMobileFromLocal(context);
        phone = StringUtils.isEmpty(phone) ? "" : phone;
        return phone;
    }

    public void setPhone(Context context, String phone) {
        this.phone = phone;
        PreferencesUtils.saveMobileToLocal(context, phone);
    }

    public String getUserId(Context context) {
        userId = PreferencesUtils.getUserIdFromLocal(context);
        return userId;
    }

    public void setUserId(Context context, String userId) {
        PreferencesUtils.saveUserIdToLocal(context, userId);
        this.userId = userId;
    }

    public String getHeader(Context context) {
        header = PreferencesUtils.getHeaderFromLocal(context);
        return header;
    }

    public void setHeader(Context context, String header) {
        PreferencesUtils.saveHeaderToLocal(context, header);
        this.header = header;
    }

    public String getRealName(Context context) {
        realName = PreferencesUtils.getRealNameFromLocal(context);
        return realName;
    }

    public void setRealName(Context context, String realName) {
        PreferencesUtils.saveRealNameToLocal(context, realName);
        this.realName = realName;
    }

    public String getUserJob(Context context) {
        userJob = PreferencesUtils.getUserJobFromLocal(context);
        return userJob;
    }

    public void setUserJob(Context context, String userJob) {
        PreferencesUtils.saveUserJobToLocal(context, userJob);
        this.userJob = userJob;
    }

    public String getQrCodeLink(Context context) {
        qrCodeLink = PreferencesUtils.getQRCodeLinkFromLocal(context);
        return qrCodeLink;
    }

    public void setQrCodeLink(Context context, String qrCodeLink) {
        PreferencesUtils.saveQRCodeLinkToLocal(context, qrCodeLink);
        this.qrCodeLink = qrCodeLink;
    }

    public String getCompanyId(Context context) {
        companyId = PreferencesUtils.getCompanyIDFromLocal(context);
        return companyId;
    }

    public void setCompanyId(Context context, String companyId) {
        PreferencesUtils.saveCompanyToLocal(context, companyId);
        this.companyId = companyId;
    }

    public String getFirstLogin(Context context) {
        firstLogin = PreferencesUtils.getFirstLoginFromLocal(context);
        return firstLogin;
    }

    public void setFirstLogin(Context context, String firstLogin) {
        PreferencesUtils.saveFirstLoginToLocal(context, firstLogin);
        this.firstLogin = firstLogin;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    public boolean isShowUpdateDialog() {
        return isShowUpdateDialog;
    }

    public void setShowUpdateDialog(boolean showUpdateDialog) {
        isShowUpdateDialog = showUpdateDialog;
    }

    /***************************Omni Wallet**********************************/
    public String getAlias(Context context) {
        alias = PreferencesUtils.getAliasFromLocal(context);
        return alias;
    }

    public void setAlias(Context context, String alias) {
        PreferencesUtils.saveAliasToLocal(context, alias);
        this.alias = alias;
    }

    public String getNetwork(Context context) {
        network = PreferencesUtils.getNetworkFromLocal(context);
        return network;
    }

    public void setNetwork(Context context, String network) {
        PreferencesUtils.saveNetworkToLocal(context, network);
        this.network = network;
    }

    public String getWalletAddress(Context context) {
        walletAddress = PreferencesUtils.getWalletAddressFromLocal(context);
        if (walletAddress == null) {
            walletAddress = "";
        }
        return walletAddress;
    }

    public void setWalletAddress(Context context, String walletAddress) {
        PreferencesUtils.saveWalletAddressToLocal(context, walletAddress);
        this.walletAddress = walletAddress;
    }

    public String getNodeVersion(Context context) {
        nodeVersion = PreferencesUtils.getNodeVersionFromLocal(context);
        return nodeVersion;
    }

    public void setNodeVersion(Context context, String nodeVersion) {
        PreferencesUtils.saveNodeVersionToLocal(context, nodeVersion);
        this.nodeVersion = nodeVersion;
    }

    public String getBtcPrice(Context context) {
        btcPrice = PreferencesUtils.getBtcPriceFromLocal(context);
        return btcPrice;
    }

    public void setBtcPrice(Context context, String btcPrice) {
        PreferencesUtils.saveBtcPriceToLocal(context, btcPrice);
        this.btcPrice = btcPrice;
    }

    public String getBtcPriceChange(Context context) {
        btcPriceChange = PreferencesUtils.getBtcPriceChangeFromLocal(context);
        return btcPriceChange;
    }

    public void setBtcPriceChange(Context context, String btcPriceChange) {
        PreferencesUtils.saveBtcPriceChangeToLocal(context, btcPriceChange);
        this.btcPriceChange = btcPriceChange;
    }

    public String getUsdtPrice(Context context) {
        usdtPrice = PreferencesUtils.getUsdtPriceFromLocal(context);
        return usdtPrice;
    }

    public void setUsdtPrice(Context context, String usdtPrice) {
        PreferencesUtils.saveUsdtPriceToLocal(context, usdtPrice);
        this.usdtPrice = usdtPrice;
    }

    public String getFromPubKey(Context context) {
        fromPubKey = PreferencesUtils.getFromPubKeyFromLocal(context);
        return fromPubKey;
    }

    public void setFromPubKey(Context context, String fromPubKey) {
        PreferencesUtils.saveFromPubKeyToLocal(context, fromPubKey);
        this.fromPubKey = fromPubKey;
    }

    public String getInitWalletType(Context context) {
        initWalletType = PreferencesUtils.getInitWalletType(context);
        if (initWalletType == null) {
            initWalletType = "";
        }
        return initWalletType;
    }

    public void setInitWalletType(Context context, String initWalletType) {
        PreferencesUtils.saveInitWalletType(context, initWalletType);
        this.initWalletType = initWalletType;
    }

    public String getMacaroonString(Context context) {
        macaroonString = PreferencesUtils.getMacaroonString(context);
        if (macaroonString == null) {
            macaroonString = "";
        }
        return macaroonString;
    }

    public void setMacaroonString(Context context, String macaroonString) {
        PreferencesUtils.saveMacaroonString(context, macaroonString);
        this.macaroonString = macaroonString;
    }

    public String getChannelBackupPathArray(Context context) {
        channelBackupPathArray = PreferencesUtils.getChannelBackupPath(context);
        if (channelBackupPathArray == null) {
            channelBackupPathArray = "";
        }
        return channelBackupPathArray;
    }

    public void setChannelBackupPathArray(Context context, String channelBackupPath) {
        PreferencesUtils.saveChannelBackupPath(context, channelBackupPath);
        this.channelBackupPathArray = channelBackupPath;
    }

    public void setPasswordMd5(Context context, String passwordMd5) {
        PreferencesUtils.savePasswordMd5(context, passwordMd5);
        this.passwordMd5 = passwordMd5;
    }

    public String getPasswordMd5(Context context) {
        passwordMd5 = PreferencesUtils.getPasswordMd5(context);
        if (passwordMd5 == null) {
            passwordMd5 = "";
        }
        return passwordMd5;
    }

    public void setSeedString(Context context, String seedString) {
        PreferencesUtils.saveSeedString(context, seedString);
        this.seedString = seedString;
    }

    public String getSeedString(Context context) {
        seedString = PreferencesUtils.getSeedString(context);
        if (seedString == null) {
            seedString = "";
        }
        return seedString;
    }

    public Boolean getCreated(Context context) {
        created = PreferencesUtils.getCreated(context);
        if (created == null) {
            created = false;
        }
        return created;
    }

    public void setCreated(Context context, Boolean created) {
        PreferencesUtils.saveCreated(context, created);
        this.created = created;
    }

    public Boolean getSynced(Context context) {
        synced = PreferencesUtils.getSynced(context);
        if (synced == null) {
            synced = false;
        }
        return synced;
    }

    public void setSynced(Context context, Boolean synced) {
        PreferencesUtils.saveSynced(context, synced);
        this.synced = synced;
    }

    public Boolean getSeedChecked(Context context) {
        seedChecked = PreferencesUtils.getSeedChecked(context);
        if (seedChecked == null) {
            seedChecked = false;
        }
        return seedChecked;
    }

    public void setSeedChecked(Context context, Boolean seedChecked) {
        PreferencesUtils.saveSeedChecked(context, seedChecked);
        this.seedChecked = seedChecked;
    }

    public String getRecoverySeedString(Context context) {
        recoverySeedString = PreferencesUtils.getRecoverySeedString(context);
        if (recoverySeedString == null) {
            recoverySeedString = "";
        }
        return recoverySeedString;
    }

    public void setRecoverySeedString(Context context, String recoverySeedString) {
        PreferencesUtils.saveRecoverySeedString(context, recoverySeedString);
        this.recoverySeedString = recoverySeedString;
    }

    public Boolean getStartCreate(Context context) {
        startCreate = PreferencesUtils.getStartCreate(context);
        if (startCreate == null) {
            startCreate = false;
        }
        return startCreate;
    }

    public void setStartCreate(Context context, Boolean startCreate) {
        PreferencesUtils.saveStartCreate(context, startCreate);
        this.startCreate = startCreate;
    }

    public void setWalletState(Context context, int walletState) {
        PreferencesUtils.saveWalletState(context, walletState);
        this.walletState = walletState;
    }

    public int getWalletState(Context context) {
        walletState = PreferencesUtils.getWalletState(context);
        if (walletState == -1) {
            walletState = -1;
        }
        return walletState;
    }

    public boolean isRestoredChannel(Context context) {
        restoredChannel = PreferencesUtils.getRestoredChannel(context);
        if (!restoredChannel) {
            restoredChannel = false;
        }
        return restoredChannel;
    }

    public void setRestoredChannel(Context context, boolean restoredChannel) {
        PreferencesUtils.saveRestoredChannel(context, restoredChannel);
        this.restoredChannel = restoredChannel;
    }

    public long getTotalBlock(Context context) {
        totalBlock = PreferencesUtils.getTotalBlock(context);
        if (totalBlock == -1) {
            totalBlock = 0;
        }
        return totalBlock;
    }

    public void setTotalBlock(Context context, long totalBlock) {
        PreferencesUtils.saveTotalBlock(context, totalBlock);
        this.totalBlock = totalBlock;
    }

    public int getAssetsCount(Context context) {
        assetsCount = PreferencesUtils.getAssetsCount(context);
        if (assetsCount == -1) {
            assetsCount = 0;
        }
        return assetsCount;
    }

    public void setAssetsCount(Context context, int count) {
        PreferencesUtils.saveAssetsCount(context, count);
        this.assetsCount = count;
    }

    public long getBalanceAmount(Context context) {
        balanceAmount = PreferencesUtils.getBalanceAmount(context);
        return balanceAmount;
    }

    public void setBalanceAmount(Context context, long balanceAmount) {
        PreferencesUtils.saveBalanceAmount(context, balanceAmount);
        this.balanceAmount = balanceAmount;
    }

    public String getNewPasswordMd5(Context context){
        newPassMd5String = PreferencesUtils.getNewPassMd5String(context);
        if (newPassMd5String == null) {
            newPassMd5String = "";
        }
        return newPassMd5String;
    }

    public void setNewPasswordMd5(Context context, String newPassMd5String) {
        PreferencesUtils.saveNewPassMd5String(context, newPassMd5String);
        this.newPassMd5String = newPassMd5String;
    }

    public boolean isHeaderBinChecked(Context context){
        headerBinChecked = PreferencesUtils.getHeaderBinChecked(context);
        return headerBinChecked;
    }

    public void setHeaderBinChecked(Context context, boolean headerBinChecked) {
        PreferencesUtils.saveHeaderBinChecked(context, headerBinChecked);
        this.headerBinChecked = headerBinChecked;
    }

    public boolean isFilterHeaderBinChecked(Context context){
        filterHeaderBinChecked = PreferencesUtils.getFilterHeaderBinChecked(context);
        return filterHeaderBinChecked;
    }

    public void setFilterHeaderBinChecked(Context context, boolean filterHeaderBinChecked) {
        PreferencesUtils.saveFilterHeaderBinChecked(context, filterHeaderBinChecked);
        this.filterHeaderBinChecked = filterHeaderBinChecked;
    }

    public boolean isNeutrinoDbChecked(Context context){
        neutrinoDbChecked = PreferencesUtils.getNeutrinoDbChecked(context);
        return neutrinoDbChecked;
    }

    public void setNeutrinoDbChecked(Context context, boolean neutrinoDbChecked) {
        PreferencesUtils.saveNeutrinoDbChecked(context, neutrinoDbChecked);
        this.neutrinoDbChecked = neutrinoDbChecked;
    }

    public String getAssetListString(Context context) {
        assetListString = PreferencesUtils.getAssetListStringFromLocal(context);
        return assetListString;
    }

    public void setAssetListString(Context context, String assetListString) {
        PreferencesUtils.saveAssetListStringToLocal(context, assetListString);
        this.assetListString = assetListString;
    }

    /**
     * 清空用户登录相关信息
     */
    public void clearUserLoginInfo(Context context) {
        // 清空用户的token
        setToken(context, "");
        // 清空用户ID
        setUserId(context, "");
        // 头像
        setHeader(context, "");
        // 姓名
        setRealName(context, "");
    }


    /**
     * 用户是否登录
     */
    public boolean isLogin(Context context) {
        //token不为空就算登录了
        return !StringUtils.isEmpty(getToken(context));
    }

    /**
     * 清除内存中的User信息
     */
    public void clear() {
        mInstance = null;
    }


    public void setWalletInfoMoved(Context context, boolean isMoved) {
        PreferencesUtils.saveWalletInfoMoved(context, isMoved);
        this.isWalletDataMoved = isMoved;
    }

    public boolean getWalletInfoMoved(Context context) {
        isWalletDataMoved = PreferencesUtils.getWalletInfoMoved(context);
        return isWalletDataMoved;
    }

    public void setSeedSecreted(Context context, boolean isSecreted) {
        PreferencesUtils.saveSeedStringSecreted(context, isSecreted);
        this.isSeedStringSecreted = isSecreted;
    }

    public boolean getSeedSecreted(Context context) {
        isSeedStringSecreted = PreferencesUtils.getSeedStringSecreted(context);
        return isSeedStringSecreted;
    }

    public boolean isBackUp(Context context){
        isBackUp = PreferencesUtils.getBackUp(context);
        return isBackUp;
    }

    public void setBackUp(Context context, boolean isBackUp) {
        PreferencesUtils.saveBackUp(context, isBackUp);
        this.isBackUp = isBackUp;
    }
}
