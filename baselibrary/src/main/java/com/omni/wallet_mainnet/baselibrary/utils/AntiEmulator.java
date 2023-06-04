package com.omni.wallet_mainnet.baselibrary.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 检测是否模拟器
 */

public class AntiEmulator {
    private static final String TAG = AntiEmulator.class.getSimpleName();
    private static String[] mKnownPipes = {
            "/dev/socket/qemud",
            "/dev/qemu_pipe"
    };

    private static String[] mKnownQemuDrivers = {
            "goldfish"
    };

    private static String[] mKnownFiles = {
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
    };

    private static String[] mKnownNumbers = {"15555215554", "15555215556",
            "15555215558", "15555215560", "15555215562", "15555215564",
            "15555215566", "15555215568", "15555215570", "15555215572",
            "15555215574", "15555215576", "15555215578", "15555215580",
            "15555215582", "15555215584",};

    private static String[] mKnownDeviceIds = {
            "000000000000000" // 默认ID
    };

    private static String[] mKnownImeiIds = {
            "310260000000000" // 默认的 imsi id
    };

    /**
     * 检测是否模拟器
     */
    public static boolean isEmulator(Context context) {
        boolean result = checkPipes() || checkQEmuDriverFile() || CheckEmulatorFiles()
                || CheckPhoneNumber(context) || CheckDeviceIDS(context) || CheckImeiIds(context)
                || CheckEmulatorBuild(context) || CheckOperatorNameAndroid(context) || test();
        LogUtils.e(TAG, "检测结果：" + result);
        return result;
    }

    //检测“/dev/socket/qemud”，“/dev/qemu_pipe”这两个通道
    public static boolean checkPipes() {
        for (int i = 0; i < mKnownPipes.length; i++) {
            String pipes = mKnownPipes[i];
            File qemuSocket = new File(pipes);
            if (qemuSocket.exists()) {
                LogUtils.e(TAG, "Find pipes!");
                return true;
            }
        }
        LogUtils.e(TAG, "Not Find pipes!");
        return false;
    }

    // 检测驱动文件内容
    // 读取文件内容，然后检查已知QEmu的驱动程序的列表
    public static Boolean checkQEmuDriverFile() {
        File driverFile = new File("/proc/tty/drivers");
        if (driverFile.exists() && driverFile.canRead()) {
            byte[] data = new byte[1024];  //(int)driverFile.length()
            try {
                InputStream inStream = new FileInputStream(driverFile);
                inStream.read(data);
                inStream.close();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            String driver_data = new String(data);
            for (String knownQemuDriver : AntiEmulator.mKnownQemuDrivers) {
                if (driver_data.indexOf(knownQemuDriver) != -1) {
                    LogUtils.e(TAG, "Find know_qemu_drivers!");
                    return true;
                }
            }
        }
        LogUtils.e(TAG, "Not Find mKnownQemuDrivers!");
        return false;
    }

    //检测模拟器上特有的几个文件
    public static Boolean CheckEmulatorFiles() {
        for (int i = 0; i < mKnownFiles.length; i++) {
            String fileName = mKnownFiles[i];
            File qemuFile = new File(fileName);
            if (qemuFile.exists()) {
                LogUtils.e(TAG, "Find Emulator Files!");
                return true;
            }
        }
        LogUtils.e(TAG, "Not Find Emulator Files!");
        return false;
    }

    // 检测模拟器默认的电话号码
    public static Boolean CheckPhoneNumber(Context context) {
        if (PermissionChecker.checkReadPhoneStatePermission(context)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNumber = telephonyManager.getLine1Number();
            for (String number : mKnownNumbers) {
                if (number.equalsIgnoreCase(phoneNumber)) {
                    LogUtils.e(TAG, "Find PhoneNumber!");
                    return true;
                }
            }
        }
        LogUtils.e(TAG, "Not Find PhoneNumber!");
        return false;
    }

    //检测设备IDS 是不是 “000000000000000”
    public static Boolean CheckDeviceIDS(Context context) {
        if (PermissionChecker.checkReadPhoneStatePermission(context)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceIds = telephonyManager.getDeviceId();
            for (String knowDevicesId : mKnownDeviceIds) {
                if (knowDevicesId.equalsIgnoreCase(deviceIds)) {
                    LogUtils.e(TAG, "Find ids: 000000000000000!");
                    return true;
                }
            }
        }
        LogUtils.e(TAG, "Not Find ids: 000000000000000!");
        return false;
    }

    // 检测imsi id是不是“310260000000000”
    public static Boolean CheckImeiIds(Context context) {
        if (PermissionChecker.checkReadPhoneStatePermission(context)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imeiIds = telephonyManager.getSubscriberId();
            for (String knowImei : mKnownImeiIds) {
                if (knowImei.equalsIgnoreCase(imeiIds)) {
                    LogUtils.e(TAG, "Find imsi ids: 310260000000000!");
                    return true;
                }
            }
        }
        LogUtils.e(TAG, "Not Find imsi ids: 310260000000000!");
        return false;
    }

    //检测手机上的一些硬件信息
    public static Boolean CheckEmulatorBuild(Context context) {
        String BOARD = android.os.Build.BOARD;
        String BOOTLOADER = android.os.Build.BOOTLOADER;
        String BRAND = android.os.Build.BRAND;
        String DEVICE = android.os.Build.DEVICE;
        String HARDWARE = android.os.Build.HARDWARE;
        String MODEL = android.os.Build.MODEL;
        String PRODUCT = android.os.Build.PRODUCT;
        if (BOARD == "unknown" || BOOTLOADER == "unknown"
                || BRAND == "generic" || DEVICE == "generic"
                || MODEL == "sdk" || PRODUCT == "sdk"
                || HARDWARE == "goldfish") {
            LogUtils.e(TAG, "Find Emulator by EmulatorBuild!");
            return true;
        }
        LogUtils.e(TAG, "Not Find Emulator by EmulatorBuild!");
        return false;
    }

    //检测手机运营商家
    public static boolean CheckOperatorNameAndroid(Context context) {
        String szOperatorName = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName();
        if ("android".equals(szOperatorName.toLowerCase())) {
            LogUtils.e(TAG, "Find Emulator by OperatorName!");
            return true;
        }
        LogUtils.e(TAG, "Not Find Emulator by OperatorName!");
        return false;
    }

    public static boolean test() {
        boolean result;
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("getprop ro.kernel.qemu");
            os = new DataOutputStream(process.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), "gbk"));
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            result = (Integer.valueOf(in.readLine()) == 1);
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        LogUtils.e(TAG, result ? "是模拟器" : "不是模拟器");
        return result;
    }
}
