package com.omni.wallet.baselibrary.base;

import android.Manifest;

/**
 * 权限配置
 */

public class PermissionConfig {
    //Calendar权限
    public static String[] CALENDAR = new String[]{
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };
    //Camera权限
    public static String[] CAMERA = new String[]{
            Manifest.permission.CAMERA
    };
    //Contacts权限
    public static String[] CONTACTS = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS
    };
    //Location 权限
    public static String[] LOCATION = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    //MicroPhone 权限
    public static String[] NICROPHONE = new String[]{
            Manifest.permission.RECORD_AUDIO
    };
    //Phone权限
    public static String[] PHONE = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS
    };
    //API > 26 的Phone权限
    public static String[] PHONE26 = new String[]{
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.ANSWER_PHONE_CALLS
    };
    //Sensor 权限
    public static String[] SENSOR = new String[]{
            Manifest.permission.BODY_SENSORS
    };
    //SMS 权限
    public static String[] SMS = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS
    };
    //Storage权限
    public static String[] STORAGE = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
}
