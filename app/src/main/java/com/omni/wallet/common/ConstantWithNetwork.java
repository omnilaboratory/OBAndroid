package com.omni.wallet.common;

public class ConstantWithNetwork {
    private static ConstantWithNetwork mInstance;
    private String logPath;
    private String downloadDirectory;
    private String downloadBaseUrl;
    private String OMNI_HOST_ADDRESS_PORT;
    private String BTC_HOST_ADDRESS;
    private String liquidityNodePubKey;
    private String getBlockHeightUrl;
    private String startParams;

    private ConstantWithNetwork(NetworkType networkType) {
        StartParams params = StartParams.getInstance(networkType);
        switch (networkType) {
            case REG:
                this.logPath = ConstantInOB.regTestLogPath;
                this.downloadDirectory = ConstantInOB.downloadDirectoryRegTest;
                this.downloadBaseUrl = ConstantInOB.downloadBaseUrlReg;
                this.OMNI_HOST_ADDRESS_PORT = params.getOMNI_HOST_ADDRESS_PORT();
                this.BTC_HOST_ADDRESS = params.getBTC_HOST_ADDRESS();
                this.liquidityNodePubKey = ConstantInOB.regLiquidityNodePubkey;
                this.getBlockHeightUrl = "http://" + ConstantInOB.BTCHostAddressRegTest + ":18332";
                this.startParams = params.getStartParams();
                break;
            case TEST:
                this.logPath = ConstantInOB.testLogPath;
                this.downloadDirectory = ConstantInOB.downloadDirectoryTestNet;
                this.downloadBaseUrl = ConstantInOB.downloadBaseUrlTestNet;
                this.OMNI_HOST_ADDRESS_PORT = params.getOMNI_HOST_ADDRESS_PORT();
                this.BTC_HOST_ADDRESS = params.getBTC_HOST_ADDRESS();
                this.liquidityNodePubKey = ConstantInOB.testLiquidityNodePubkey;
                this.getBlockHeightUrl = "http://" + ConstantInOB.TEST_NET_BTC_HOST_ADDRESS + ":18332";
                this.startParams = params.getStartParams();
                break;
            case MAIN:
                this.logPath = ConstantInOB.mainLogPath;
                this.downloadDirectory = ConstantInOB.downloadDirectoryMainNet;
                this.downloadBaseUrl = ConstantInOB.downloadBaseUrlMainNet;
                this.OMNI_HOST_ADDRESS_PORT = params.getOMNI_HOST_ADDRESS_PORT();
                this.BTC_HOST_ADDRESS = params.getBTC_HOST_ADDRESS();
                this.liquidityNodePubKey = ConstantInOB.mainLiquidityNodePubkey;
                this.getBlockHeightUrl = "https://api.blockcypher.com/v1/btc/main";
                this.startParams = params.getStartParams();
                break;
        }
    }

    public static ConstantWithNetwork getInstance(NetworkType networkType) {
        if (mInstance == null) {
            synchronized (StartParams.class) {
                if (mInstance == null) {
                    mInstance = new ConstantWithNetwork(networkType);
                }
            }
        }
        return mInstance;
    }


    public String getLogPath() {
        return logPath;
    }

    public String getDownloadDirectory() {
        return downloadDirectory;
    }

    public String getDownloadBaseUrl() {
        return downloadBaseUrl;
    }

    public String getOMNI_HOST_ADDRESS_PORT() {
        return OMNI_HOST_ADDRESS_PORT;
    }

    public String getBTC_HOST_ADDRESS() {
        return BTC_HOST_ADDRESS;
    }

    public String getLiquidityNodePubKey() {
        return liquidityNodePubKey;
    }

    public String getGetBlockHeightUrl() {
        return getBlockHeightUrl;
    }

    public String getStartParams() {
        return startParams;
    }
}
