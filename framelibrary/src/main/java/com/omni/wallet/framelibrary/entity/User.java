package com.omni.wallet.framelibrary.entity;

import android.content.Context;

import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.framelibrary.utils.PreferencesUtils;


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
