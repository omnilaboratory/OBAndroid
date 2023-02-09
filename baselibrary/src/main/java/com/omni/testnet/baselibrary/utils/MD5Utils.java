package com.omni.testnet.baselibrary.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 工具类
 */
public class MD5Utils {

    /**
     * MD5加密 16位加密，从第9位到25位
     *
     * @param s 原数据
     * @return md5加密后数据
     */
    public static String get16MD5(String s) {
        return get32MD5(s).substring(8, 24);
    }

    /**
     * MD5加密 32位加密
     *
     * @param s 原数据
     * @return md5 加密后数据
     */
    public static String get32MD5(String s) {
        return getByte32MD5(s.getBytes());
    }

    /**
     * MD5加密 32位加密
     *
     * @param s 原数据
     * @return md5 加密后数据
     */
    public static String getByte32MD5(byte[] s) {
        if (s == null || s.length == 0) {
            return "";
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // 不会出现此异常
            return "";
        }
        byte[] targetBytes = messageDigest.digest(s);
        String target = NumericalUtils.byte2Hex(targetBytes);
        return target;
    }

    public static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        LogUtils.e("MD5Utils", "生成文件MD5 : " + value);
        return value;
    }
}
