package com.omni.wallet.framelibrary.entity;


import com.google.gson.annotations.SerializedName;
import com.omni.wallet.baselibrary.utils.StringUtils;

/**
 * 版本更新实体
 */

public class UpdateEntity {
    private String createtime;
    private String ts;
    private int dr;
    private int id;
    private String version;// 新的版本号
    private String type;// 设备类型（安卓1 2 IOS ）
    private String url;// APK下载链接
    private String operator;// 操作人员
    @SerializedName("isupdate")
    private String isForceUpdate;// 是否强制更新 1强制，0普通更新
    @SerializedName("introdution")
    private String desc;// 新版本描述
    @SerializedName("apptype")
    private String appType;// app类型 0鑫房帮 经纪人APP 1鑫房链
    private String primaryKeyString;

    public static final String FORCE_UPDATE = "1";// 强制更新
    public static final String NORMAL_UPDATE = "0";// 普通更新

    /**
     * 是否强制更新
     */
    public boolean isForceUpdate() {
        return FORCE_UPDATE.equals(isForceUpdate);
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public int getDr() {
        return dr;
    }

    public void setDr(int dr) {
        this.dr = dr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        version = StringUtils.isEmpty(version) ? "" : version;
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(String isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public String getDesc() {
        desc = StringUtils.isEmpty(desc) ? "发现新版本" : desc;
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getPrimaryKeyString() {
        return primaryKeyString;
    }

    public void setPrimaryKeyString(String primaryKeyString) {
        this.primaryKeyString = primaryKeyString;
    }
}
