package com.omni.wallet.SharedPreferences;


import android.content.Context;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.common.NetworkType;

public class WalletInfo {
    private static final String TAG = WalletInfo.class.getSimpleName();

    private WalletInfo(){}

    private static WalletInfo mInstance;

    public static WalletInfo getInstance() {
        if (mInstance == null) {
            synchronized (WalletInfo.class) {
                if (mInstance == null) {
                    mInstance = new WalletInfo();
                }
            }
        }
        return mInstance;
    }

    private String initWalletType;

    private String macaroonString;

    private String seedString;

    private String recoverySeedString;

    private boolean headerBinChecked;

    private boolean filterHeaderBinChecked;

    private boolean neutrinoDbChecked;

    private String alias;

    private String walletAddress;

    private String fromPubKey;

    private long totalBlock;

    private int assetsCount;

    private String assetListString;

    private String passwordSecret;

    private String newPassSecret;

    private String nodeVersion;

    private String startParams;

    public void setInitWalletType(Context context, String value, NetworkType networkType){
        initWalletType = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveInitWalletType(context,value);
                break;
            case TEST:
                TestWalletInfo.saveInitWalletType(context,value);
                break;
            case REG:
                RegWalletInfo.saveInitWalletType(context,value);
                break;
        }
    }

    public String getInitWalletType(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getInitWalletType(context);
                break;
            case TEST:
                temp = TestWalletInfo.getInitWalletType(context);
                break;
            case REG:
                temp = RegWalletInfo.getInitWalletType(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        initWalletType = temp;
        return initWalletType;
    }

    public void setMacaroonString(Context context, String value, NetworkType networkType){
        macaroonString = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveMacaroonString(context,value);
                break;
            case TEST:
                TestWalletInfo.saveMacaroonString(context,value);
                break;
            case REG:
                RegWalletInfo.saveMacaroonString(context,value);
                break;
        }
    }

    public String getMacaroonString(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getMacaroonString(context);
                break;
            case TEST:
                temp = TestWalletInfo.getMacaroonString(context);
                break;
            case REG:
                temp = RegWalletInfo.getMacaroonString(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        macaroonString = temp;
        return macaroonString;
    }

    public void setSeedString(Context context, String value, NetworkType networkType){
        seedString = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveSeedString(context,value);
                break;
            case TEST:
                TestWalletInfo.saveSeedString(context,value);
                break;
            case REG:
                RegWalletInfo.saveSeedString(context,value);
                break;
        }
    }

    public String getSeedString(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getSeedString(context);
                break;
            case TEST:
                temp = TestWalletInfo.getSeedString(context);
                break;
            case REG:
                temp = RegWalletInfo.getSeedString(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        seedString = temp;
        return seedString;
    }

    public void setRecoverySeedString(Context context, String value, NetworkType networkType){
        recoverySeedString = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveRecoverySeedString(context,value);
                break;
            case TEST:
                TestWalletInfo.saveRecoverySeedString(context,value);
                break;
            case REG:
                RegWalletInfo.saveRecoverySeedString(context,value);
                break;
        }
    }

    public String getRecoverySeedString(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getRecoverySeedString(context);
                break;
            case TEST:
                temp = TestWalletInfo.getRecoverySeedString(context);
                break;
            case REG:
                temp = RegWalletInfo.getRecoverySeedString(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        recoverySeedString = temp;
        return recoverySeedString;
    }

    public void setHeaderBinChecked(Context context, boolean value, NetworkType networkType){
        headerBinChecked = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveHeaderBinChecked(context,value);
                break;
            case TEST:
                TestWalletInfo.saveHeaderBinChecked(context,value);
                break;
            case REG:
                RegWalletInfo.saveHeaderBinChecked(context,value);
                break;
        }
    }

    public boolean getHeaderBinChecked(Context context, NetworkType networkType){
        boolean temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getHeaderBinChecked(context);
                break;
            case TEST:
                temp = TestWalletInfo.getHeaderBinChecked(context);
                break;
            case REG:
                temp = RegWalletInfo.getHeaderBinChecked(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        headerBinChecked = temp;
        return headerBinChecked;
    }

    public void setFilterHeaderBinChecked(Context context, boolean value, NetworkType networkType){
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveFilterHeaderBinChecked(context,value);
                break;
            case TEST:
                TestWalletInfo.saveFilterHeaderBinChecked(context,value);
                break;
            case REG:
                RegWalletInfo.saveFilterHeaderBinChecked(context,value);
                break;
        }
    }

    public boolean getFilterHeaderBinChecked(Context context, NetworkType networkType){
        boolean temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getFilterHeaderBinChecked(context);
                break;
            case TEST:
                temp = TestWalletInfo.getFilterHeaderBinChecked(context);
                break;
            case REG:
                temp = RegWalletInfo.getFilterHeaderBinChecked(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        filterHeaderBinChecked = temp;
        return filterHeaderBinChecked;
    }

    public void setNeutrinoDbChecked(Context context, boolean value, NetworkType networkType){
        neutrinoDbChecked = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveNeutrinoDbChecked(context,value);
                break;
            case TEST:
                TestWalletInfo.saveNeutrinoDbChecked(context,value);
                break;
            case REG:
                RegWalletInfo.saveNeutrinoDbChecked(context,value);
                break;
        }
    }

    public boolean getNeutrinoDbChecked(Context context, NetworkType networkType){
        boolean temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getNeutrinoDbChecked(context);
                break;
            case TEST:
                temp = TestWalletInfo.getNeutrinoDbChecked(context);
                break;
            case REG:
                temp = RegWalletInfo.getNeutrinoDbChecked(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        neutrinoDbChecked = temp;
        return neutrinoDbChecked;
    }

    public void setAlias(Context context, String value, NetworkType networkType){
        alias = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveAlias(context,value);
                break;
            case TEST:
                TestWalletInfo.saveAlias(context,value);
                break;
            case REG:
                RegWalletInfo.saveAlias(context,value);
                break;
        }
    }

    public String getAlias(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getAlias(context);
                break;
            case TEST:
                temp = TestWalletInfo.getAlias(context);
                break;
            case REG:
                temp = RegWalletInfo.getAlias(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        alias = temp;
        return alias;
    }

    public void setWalletAddress(Context context, String value, NetworkType networkType){
        walletAddress = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveWalletAddress(context,value);
                break;
            case TEST:
                TestWalletInfo.saveWalletAddress(context,value);
                break;
            case REG:
                RegWalletInfo.saveWalletAddress(context,value);
                break;
        }
    }

    public String getWalletAddress(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getWalletAddress(context);
                break;
            case TEST:
                temp = TestWalletInfo.getWalletAddress(context);
                break;
            case REG:
                temp = RegWalletInfo.getWalletAddress(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        walletAddress = temp;
        return walletAddress;
    }


    public void setFromPubKey(Context context, String value, NetworkType networkType){
        fromPubKey = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveFromPubKey(context,value);
                break;
            case TEST:
                TestWalletInfo.saveFromPubKey(context,value);
                break;
            case REG:
                RegWalletInfo.saveFromPubKey(context,value);
                break;
        }
    }

    public String getFromPubKey(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getFromPubKey(context);
                break;
            case TEST:
                temp = TestWalletInfo.getFromPubKey(context);
                break;
            case REG:
                temp = RegWalletInfo.getFromPubKey(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        fromPubKey = temp;
        return fromPubKey;
    }

    public void setTotalBlock(Context context, long value, NetworkType networkType){
        totalBlock = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveTotalBlock(context,value);
                break;
            case TEST:
                TestWalletInfo.saveTotalBlock(context,value);
                break;
            case REG:
                RegWalletInfo.saveTotalBlock(context,value);
                break;
        }
    }

    public long getTotalBlock(Context context, NetworkType networkType){
        long temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getTotalBlock(context);
                break;
            case TEST:
                temp = TestWalletInfo.getTotalBlock(context);
                break;
            case REG:
                temp = RegWalletInfo.getTotalBlock(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        totalBlock = temp;
        return totalBlock;
    }

    public void setAssetsCount(Context context, int value, NetworkType networkType){
        assetsCount = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveAssetCount(context,value);
                break;
            case TEST:
                TestWalletInfo.saveAssetCount(context,value);
                break;
            case REG:
                RegWalletInfo.saveAssetCount(context,value);
                break;
        }
    }

    public int getAssetsCount(Context context, NetworkType networkType){
        int temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getAssetsCount(context);
                break;
            case TEST:
                temp = TestWalletInfo.getAssetsCount(context);
                break;
            case REG:
                temp = RegWalletInfo.getAssetsCount(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        assetsCount = temp;
        return assetsCount;
    }

    public void setAssetListString(Context context, String value, NetworkType networkType){
        assetListString = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveAssetListString(context,value);
                break;
            case TEST:
                TestWalletInfo.saveAssetListString(context,value);
                break;
            case REG:
                RegWalletInfo.saveAssetListString(context,value);
                break;
        }
    }

    public String getAssetListString(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getAssetListString(context);
                break;
            case TEST:
                temp = TestWalletInfo.getAssetListString(context);
                break;
            case REG:
                temp = RegWalletInfo.getAssetListString(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        assetListString = temp;
        return assetListString;
    }

    public void setPasswordSecret(Context context, String value, NetworkType networkType){
        passwordSecret = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.savePasswordSecret(context,value);
                break;
            case TEST:
                TestWalletInfo.savePasswordSecret(context,value);
                break;
            case REG:
                RegWalletInfo.savePasswordSecret(context,value);
                break;
        }
    }

    public String getPasswordSecret(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getPasswordSecret(context);
                break;
            case TEST:
                temp = TestWalletInfo.getPasswordSecret(context);
                break;
            case REG:
                temp = RegWalletInfo.getPasswordSecret(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        passwordSecret = temp;
        return passwordSecret;
    }

    public void setNewPassSecret(Context context, String value, NetworkType networkType){
        newPassSecret = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveNewPasswordSecret(context,value);
                break;
            case TEST:
                TestWalletInfo.saveNewPasswordSecret(context,value);
                break;
            case REG:
                RegWalletInfo.saveNewPasswordSecret(context,value);
                break;
        }
    }

    public String getNewPassSecret(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getNewPasswordSecret(context);
                break;
            case TEST:
                temp = TestWalletInfo.getNewPasswordSecret(context);
                break;
            case REG:
                temp = RegWalletInfo.getNewPasswordSecret(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        newPassSecret = temp;
        return newPassSecret;
    }

    public void setNodeVersion(Context context, String value, NetworkType networkType){
        nodeVersion = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveNodeVersion(context,value);
                break;
            case TEST:
                TestWalletInfo.saveNodeVersion(context,value);
                break;
            case REG:
                RegWalletInfo.saveNodeVersion(context,value);
                break;
        }
    }

    public String getNodeVersion(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getNodeVersion(context);
                break;
            case TEST:
                temp = TestWalletInfo.getNodeVersion(context);
                break;
            case REG:
                temp = RegWalletInfo.getNodeVersion(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        nodeVersion = temp;
        return nodeVersion;
    }

    public void setStartParams(Context context, String value, NetworkType networkType){
        nodeVersion = value;
        switch (networkType) {
            case MAIN:
                MainWalletInfo.saveStartParams(context,value);
                break;
            case TEST:
                TestWalletInfo.saveStartParams(context,value);
                break;
            case REG:
                RegWalletInfo.saveStartParams(context,value);
                break;
        }
    }

    public String getStartParams(Context context, NetworkType networkType){
        String temp;
        switch (networkType) {
            case MAIN:
                temp = MainWalletInfo.getStartParams(context);
                break;
            case TEST:
                temp = TestWalletInfo.getStartParams(context);
                break;
            case REG:
                temp = RegWalletInfo.getStartParams(context);
                break;
            default:
                throw new IllegalStateException(TAG + "Unexpected value: " + networkType);
        }
        startParams = temp;
        return startParams;
    }
}
