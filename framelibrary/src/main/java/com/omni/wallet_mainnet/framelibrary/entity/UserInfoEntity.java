package com.omni.wallet_mainnet.framelibrary.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 用户信息接口请求实体
 */

public class UserInfoEntity {

    @SerializedName("userid")
    private String userId;//用户主键
    private String token;// 用户Token
    @SerializedName("headurl")
    private String headerUrl;//头像
    @SerializedName("username")
    private String realName;// 真实姓名
    private String userJob;// 用户职位
    @SerializedName("loginMobile")
    private String loginPhone;//账号就是手机号
    @SerializedName("userqrcodeurl")
    private String QRCode;// 二维码
    @SerializedName("monthMoney")
    private String monthMoney;// 本月收益
    @SerializedName("walletBalance")
    private String availableMoney;// 可用余额
    @SerializedName("companyId")
    private String companyId;// 物业公司主体表id
    @SerializedName("first_login")
    private String firstLogin;// 第一次登陆标识
    @SerializedName("reflect")
    private String reflect;// 贡献值

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserJob() {
        return userJob;
    }

    public void setUserJob(String userJob) {
        this.userJob = userJob;
    }

    public String getLoginPhone() {
        return loginPhone;
    }

    public void setLoginPhone(String loginPhone) {
        this.loginPhone = loginPhone;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getMonthMoney() {
        return monthMoney;
    }

    public void setMonthMoney(String monthMoney) {
        this.monthMoney = monthMoney;
    }

    public String getAvailableMoney() {
        return availableMoney;
    }

    public void setAvailableMoney(String availableMoney) {
        this.availableMoney = availableMoney;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(String firstLogin) {
        this.firstLogin = firstLogin;
    }

    public String getReflect() {
        return reflect;
    }

    public void setReflect(String reflect) {
        this.reflect = reflect;
    }
}
