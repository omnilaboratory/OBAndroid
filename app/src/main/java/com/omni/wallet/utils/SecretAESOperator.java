package com.omni.wallet.utils;


import android.util.Log;

import com.omni.wallet.framelibrary.utils.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecretAESOperator {
    private static final String TAG = SecretAESOperator.class.getSimpleName();
    private static final String sKey = "omniWalletSecret";
    private static final String ivParameter = "2404020124040201";
    private static SecretAESOperator instance = null;

    private SecretAESOperator() {

    }

    public static SecretAESOperator getInstance() {
        if (instance == null)
            instance = new SecretAESOperator();
        return instance;
    }

    // 加密
    public String encrypt(String sSrc) {
        String secretString = "";
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
            secretString = Base64.encode(encrypted);
            Log.d(TAG, "encrypt: password " + sSrc);

        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            Log.e(TAG, "encrypt: " + e.getMessage() );
            e.printStackTrace();
        }
        return secretString;
    }

    // 解密
    public String decrypt(String sSrc) {
        String decodeString = "";
        try {
            byte[] raw = sKey.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.decode(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            decodeString = new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return decodeString;
    }

}
