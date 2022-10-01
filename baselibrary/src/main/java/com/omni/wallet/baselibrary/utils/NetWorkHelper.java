package com.omni.wallet.baselibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;


/**
 * 网络帮助类 包含一些网络判断方法
 *
 */
public class NetWorkHelper {
    private static final String TAG = NetWorkHelper.class.getSimpleName();

    // (1:2g,2:3g,3:4g,4:WIFI,5:其他)
    //中国移动-chinamobile
    private static final String OPERATOR_CMCC = "中国移动";
    //中国联通-chinaunicom
    private static final String OPERATOR_CUCC = "中国联通";
    //中国电信-chinatelecom
    private static final String OPERATOR_CTCC = "中国电信";
    //其他
    private static final String OPERATOR_NONE = "其他";
    /**
     * 未知
     */
    public static final String NETWORK_TYPE_INVALID = "未知";
    /**
     * WIFI网络
     */
    public static final String NETWORK_TYPE_WIFI = "WIFI";
    /**
     * 4G网络
     */
    public static final String NETWORK_TYPE_4G = "4G";
    /**
     * 3G网络
     */
    public static final String NETWORK_TYPE_3G = "3G";
    /**
     * 2G网络
     */
    public static final String NETWORK_TYPE_2G = "2G";

    /**
     * 检查网络状态 在请求网络前 建议先检查一下 网络状态
     *
     * @return true 有网络； false 没有网络;
     */
    public static boolean checkNetState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        // 建立网络数组
        NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
        if (netInfo != null) {
            for (int i = 0, length = netInfo.length; i < length; i++) {
                // 判断获得的网络状态是否是处于连接状态
                if (netInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        if (networkInfo == null) {
//            return false;
//        }
//        return networkInfo.isConnected();
    }

    /**
     * 当前是否WIFI环境
     */

    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }


    /**
     * 判断wifi 是否可用
     */
    public static boolean isWifiDataEnable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 判断是否Wifi网络
     */
    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return false;
        }
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0: // 3G
                return false; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A: // 3G
                return false; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA: // 3G
                return false; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA: // 3G
                return false; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return false; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                return false; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return false; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return false; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return false; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    /**
     * 判断网络速度 ，wifi 4g is true , else false
     */
    public static boolean isFastNet(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();

            if (type.equalsIgnoreCase("WIFI")) {
                return true;
            } else if (type.equalsIgnoreCase("MOBILE")) {

                if (isFastMobileNetwork(context)) {
                    return true;
                } else
                    return false;
            }
        }
        return false;
    }

    /**
     * 获取网络类型 (1:2g,2:3g,3:4g,4:WIFI,5:其他)
     */
    public static String getNetWorkType(Context context) {
        String type = null;
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectionManager == null) {
            return null;
        }
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return null;
        }
        String typeName = networkInfo.getTypeName();
        if ("WIFI".equals(typeName)) {// wifi
            type = NETWORK_TYPE_WIFI;
        } else if ("MOBILE".equals(typeName)) {// 手机
            int subType = networkInfo.getSubtype();
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_CDMA:// 电信2G
                    type = NETWORK_TYPE_2G;
                    break;
                case TelephonyManager.NETWORK_TYPE_EDGE:// 移动2G
                    type = NETWORK_TYPE_2G;
                    break;
                case TelephonyManager.NETWORK_TYPE_GPRS:// 联通2G
                    type = NETWORK_TYPE_2G;
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:// 版本0.（电信3g）
                    type = NETWORK_TYPE_3G;
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:// 版本A （电信3g）
                    type = NETWORK_TYPE_3G;
                    break;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:// 版本B（电信3g）
                    type = NETWORK_TYPE_3G;
                    break;
                case TelephonyManager.NETWORK_TYPE_HSDPA:// （联通3g）
                    type = NETWORK_TYPE_3G;
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:// UMTS（联通3g）
                    type = NETWORK_TYPE_3G;
                    break;
                case TelephonyManager.NETWORK_TYPE_LTE:// LTE(3g到4g的一个过渡，称为准4g)
                    type = NETWORK_TYPE_4G;
                    break;
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:// 未知
                    type = NETWORK_TYPE_INVALID;
                    break;
                default:
                    type = NETWORK_TYPE_INVALID;
                    break;
            }
        }
        return type;
    }


    /**
     * 获取运营商(1:移动，2：连通，3：电信，4：其他)
     */
    public static String getSimOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = OPERATOR_NONE;
        try {
            if (telephonyManager != null && telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                String name = telephonyManager.getSimOperatorName();// 运营商名字
                // 权限检测
                if (PermissionChecker.checkReadPhoneStatePermission(context)) {
                    String IMSI = telephonyManager.getSubscriberId();// 运营商ID
                    if (IMSI == null || IMSI.equals("")) {
                        return operator;
                    }
                    if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {// 中国移动
                        operator = OPERATOR_CMCC;
                    } else if (IMSI.startsWith("46001")) {// 中国联通
                        operator = OPERATOR_CUCC;
                    } else if (IMSI.startsWith("46003")) {// 中国电信
                        operator = OPERATOR_CTCC;
                    }
                } else {
                    return name;
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "获取手机运营商信息异常:" + e.getMessage());
            e.printStackTrace();
        }
        return operator;
    }

    /**
     * 判断设备 是否使用代理上网
     */
    public static boolean isWifiProxy(Context context) {
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        return (!StringUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }

    /**
     * 是否正在使用VPN
     */
    public static boolean isVpnUsed() {
        try {
            Enumeration niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                ArrayList tempList = Collections.list(niList);
                for (Object temp : tempList) {
                    NetworkInterface networkInterface = (NetworkInterface) temp;
                    if (!networkInterface.isUp() || networkInterface.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    if ("tun0".equals(networkInterface.getName()) || "ppp0".equals(networkInterface.getName())) {
                        LogUtils.e(TAG, "使用VPN上网，VPN接口名称：" + networkInterface.getName());
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
}
