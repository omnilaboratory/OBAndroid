package com.omni.wallet_mainnet.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class UtilFunctions {
    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    private static final String LOG_TAG = UtilFunctions.class.getName();

    public static String sha256Hash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] sha256HashByte(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String hex) {
        int l = hex.length();
        byte[] data = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }


    public static String getQueryParam(URL url, String parameter) {
        if (url != null && url.getQuery() != null) {
            String[] params = url.getQuery().split("&");
            for (String param : params) {
                String name = param.split("=")[0];
                if (parameter.equals(name)) {
                    return param.split("=")[1];
                }
            }
        }
        return null;
    }

    public static boolean isHex(String input) {
        return input.matches("^[0-9a-fA-F]+$");
    }

    public static double roundDouble(double value, int places) {
        if (places < 0)
            places = 0;

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(final byte[] b) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    public static int intFromByteArray(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                ((bytes[3] & 0xFF) << 0);
    }

    public static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value};
    }

    public static double parseAmount(long value,int decimal){
        double doubleValue = Double.parseDouble(String.valueOf(value));
        return doubleValue/Math.pow(10,decimal);
    }
}