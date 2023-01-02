package com.omni.wallet.framelibrary.entity;

import android.content.Context;

import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.framelibrary.utils.PreferencesUtils;

import okio.ByteString;


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
    // 网络类型
    private String network;
    // 钱包地址
    private String walletAddress;
    // 节点版本
    private String nodeVersion;
    // Btc价格
    private String btcPrice;
    // Btc价格变化
    private String btcPriceChange;
    // usdt价格
    private String usdtPrice;
    // 自身节点的pubKey
    private String fromPubKey;
    
    private String initWalletType;
    
    private String macaroonString;
    
    private String channelBackupPath;
    
    private String seedString;
    
    private String passwordMd5;
    
    private String recoverySeedString;
    
    private Boolean created;
    
    private Boolean synced;
    
    private Boolean seedChecked;
    
    private Boolean startCreate;

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
        if (walletAddress==null){
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
        if(initWalletType == null){
            initWalletType = "";
        }
        return initWalletType;
    }

    public void setInitWalletType(Context context,String initWalletType) {
        PreferencesUtils.saveInitWalletType(context, initWalletType);
        this.initWalletType = initWalletType;
    }
    public String getMacaroonString(Context context) {
        macaroonString = PreferencesUtils.getMacaroonString(context);
        if(macaroonString == null){
            macaroonString = "";
        }
        return macaroonString;
    }

    public void setMacaroonString(Context context,String macaroonString) {
        PreferencesUtils.saveMacaroonString(context, macaroonString);
        this.macaroonString = macaroonString;
    }

    public String getChannelBackupPath(Context context) {
        channelBackupPath = PreferencesUtils.getChannelBackupPath(context);
        if(channelBackupPath == null){
            channelBackupPath = "";
        }
        return channelBackupPath;
    }

    public void setChannelBackupPath(Context context,String channelBackupPath) {
        PreferencesUtils.saveChannelBackupPath(context, channelBackupPath);
        this.channelBackupPath = channelBackupPath;
    }

    public void setPasswordMd5(Context context,String passwordMd5) {
        PreferencesUtils.savePasswordMd5(context, passwordMd5);
        this.passwordMd5 = passwordMd5;
    }

    public String getPasswordMd5(Context context) {
        passwordMd5 = PreferencesUtils.getPasswordMd5(context);
        if(passwordMd5 == null){
            passwordMd5 = "";
        }
        return passwordMd5;
    }

    public void setSeedString(Context context,String seedString) {
        PreferencesUtils.saveSeedString(context, seedString);
        this.seedString = seedString;
    }

    public String getSeedString(Context context) {
        seedString = PreferencesUtils.getSeedString(context);
        if(seedString == null){
            seedString = "";
        }
        return seedString;
    }

    public Boolean getCreated(Context context) {
        created = PreferencesUtils.getCreated(context);
        if(created == null){
            created = false;
        }
        return created;
    }

    public void setCreated(Context context,Boolean created) {
        PreferencesUtils.saveCreated(context, created);
        this.created = created;
    }

    public Boolean getSynced(Context context) {
        synced = PreferencesUtils.getSynced(context);
        if(synced == null){
            synced = false;
        }
        return synced;
    }

    public void setSynced(Context context,Boolean synced) {
        PreferencesUtils.saveSynced(context, synced);
        this.synced = synced;
    }

    public Boolean getSeedChecked(Context context) {
        seedChecked = PreferencesUtils.getSeedChecked(context);
        if(seedChecked == null){
            seedChecked = false;
        }
        return seedChecked;
    }

    public void setSeedChecked(Context context,Boolean seedChecked) {
        PreferencesUtils.saveSeedChecked(context, seedChecked);
        this.seedChecked = seedChecked;
    }

    public String getRecoverySeedString(Context context) {
        recoverySeedString = PreferencesUtils.getRecoverySeedString(context);
        if(recoverySeedString == null){
            recoverySeedString = "";
        }
        return recoverySeedString;
    }

    public void setRecoverySeedString(Context context,String recoverySeedString) {
        PreferencesUtils.saveRecoverySeedString(context, recoverySeedString);
        this.recoverySeedString = recoverySeedString;
    }

    public Boolean getStartCreate(Context context) {
        startCreate = PreferencesUtils.getStartCreate(context);
        if (startCreate == null){
            startCreate = false;
        }
        return startCreate;
    }

    public void setStartCreate(Context context,Boolean startCreate) {
        PreferencesUtils.saveStartCreate(context, startCreate);
        this.startCreate = startCreate;
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

    
}
