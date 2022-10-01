package com.omni.wallet.framelibrary.entity;

/**
 * 接口数据返回整体解析的实体类
 */

public class HttpResponseEntity {
    // 接口状态码
    // 状态码大于0是成功，大于1是表示成功的描述
    // 状态码小于等于0是失败，小于0是表示失败的描述
    private String code;
    // 数据
    private Object data;
    // 状态描述
    private String info;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "{" +
                "code='" + code + '\'' +
                ", data=" + data +
                ", info='" + info + '\'' +
                '}';
    }
}
