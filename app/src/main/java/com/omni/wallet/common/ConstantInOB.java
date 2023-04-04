package com.omni.wallet.common;

import android.content.Context;

public class ConstantInOB {
    public static int beforeHomePageRequestCode = 9;
    private static String basePath = "";
    public static String blockHeaderBin = "block_headers.bin";
    public static String blockHeader = "block_headers";
    public static String neutrinoDB = "neutrino.db";
    public static String neutrino = "neutrino";
    public static String regFilterHeaderBin = "reg_filter_headers.bin";
    public static String regFilterHeader = "reg_filter_headers";
    public static String peer = "peers";
    public static String peerJson = "peers.json";
    public static NetworkType networkType = NetworkType.MAIN;

    static String regTestLogPath = "/logs/bitcoin/regtest/lnd.log";
    static String testLogPath = "/logs/bitcoin/testnet/lnd.log";
    static String mainLogPath = "/logs/bitcoin/mainnet/lnd.log";

    static String downloadDirectoryRegTest = "/data/chain/bitcoin/regtest/";
    static String downloadDirectoryTestNet = "/data/chain/bitcoin/testnet/";
    static String downloadDirectoryMainNet = "/data/chain/bitcoin/mainnet/";

    static String downloadBaseUrlReg = "https://cache.oblnd.top/neutrino-regtest/";
    static String downloadBaseUrlTestNet = "https://cache.oblnd.top/neutrino-testnet/";
    static String downloadBaseUrlMainNet = "https://cache.oblnd.top/neutrino-mainnet/";

    static String OMNIHostAddressPortRegTest = "43.138.107.248:18332";
    static String BTCHostAddressRegTest = "43.138.107.248";
    static String TEST_NET_BTC_HOST_ADDRESS="192.144.199.67";
    static String TEST_NET_OMNI_HOST_ADDRESS_PORT="192.144.199.67:18332";
    static String MAIN_NET_BTC_HOST_ADDRESS = "mainnet4-btcd.zaphq.io";
    static String MAIN_NET_OMNI_HOST_ADDRESS_PORT="54.187.22.125:18332";

    static String testLiquidityNodePubkey="025af4448f55bf1e6de1ae0486d4d103427c4e559a62ed7f8035bb1ed1af734f61@192.144.199.67:9735";
    static String regLiquidityNodePubkey="0357702800d8926b9077a621bc04320b187b73bcecf381ae07d0a2b36defd1a715@43.138.107.248:9735";
    static String mainLiquidityNodePubkey="03cd43d4ae622cff751564a093d9ba4d347a29cffadac9bddbf5d698646411c23e@54.187.22.125:9735";


    private static final long SECOND_MILLIS = 1000;
    public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final long WEEK_MILLIS = 7 * DAY_MILLIS;


    public ConstantInOB(Context context) {
        basePath = context.getExternalCacheDir() + "";
    }

    public String getBasePath() {
        return basePath;
    }


}
