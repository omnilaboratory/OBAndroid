package com.omni.wallet.base;

import android.content.Context;

public class ConstantInOB {
    public static String basePath = "";
    public static String regTestLogPath = "/logs/bitcoin/regtest/lnd.log";
    public static String testLogPath = "/logs/bitcoin/testnet/lnd.log";
    public static String downloadDirectoryRegTest = "/data/chain/bitcoin/regtest/";
    public static String downloadDirectoryTestNet = "/data/chain/bitcoin/testnet/";
    public static String OMNIHostAddressRegTest = "43.138.107.248";
    public static String OMNIHostAddressPortRegTest = "43.138.107.248:18332";
    public static String BTCHostAddressRegTest = "43.138.107.248";
    public static String BTCHostAddressPortRegTest = "43.138.107.248:18332";
    public static String blockHeaderBin = "block_headers.bin";
    public static String blockHeader = "block_headers";
    public static String neutrinoDB = "neutrino.db";
    public static String neutrino = "neutrino";
    public static String regFilterHeaderBin = "reg_filter_headers.bin";
    public static String regFilterHeader = "reg_filter_headers";
    public static String downloadBaseUrl = "https://cache.oblnd.top/neutrino-regtest/";
    public static String downloadBaseUrlTestNet = "https://cache.oblnd.top/neutrino-testnet/";
    public static String TEST_NET_BTC_HOST_ADDRESS="192.144.199.67";
    public static String TEST_NET_OMNI_HOST_ADDRESS_PORT="192.144.199.67:18332";



    public static final String neutrinoRegTestConfig = "--trickledelay=5000 --debuglevel=debug --alias=alice\n" +
            "--autopilot.active --maxpendingchannels=100 " +
            "--bitcoin.active --bitcoin.regtest --bitcoin.node=neutrino " +
            "--enable-upfront-shutdown " +
            "--tlsdisableautofill " +
            "--norest "+
            "--neutrino.connect=" + BTCHostAddressRegTest +
            " --omnicoreproxy.rpchost=" + OMNIHostAddressPortRegTest ;

    public static final String normalRegTestConfig = "--trickledelay=5000 --debuglevel=debug --alias=alice\n" +
            "--autopilot.active --maxpendingchannels=100 " +
            "--bitcoin.active --bitcoin.regtest --bitcoin.node=omnicoreproxy " +
            "--enable-upfront-shutdown " +
            "--tlsdisableautofill " +
            "--norest "+
            "--omnicoreproxy.rpchost=" + BTCHostAddressRegTest + ":18332 " +
            "--omnicoreproxy.zmqpubrawblock=tcp://" + BTCHostAddressRegTest + ":28332 " +
            "--omnicoreproxy.zmqpubrawtx=tcp://" + BTCHostAddressRegTest + ":28333";

    public static final String neutrinoTestNetConfig = "--trickledelay=5000 --debuglevel=debug --alias=alice\n" +
            "--autopilot.active --maxpendingchannels=100 " +
            "--bitcoin.active --bitcoin.testnet --bitcoin.node=neutrino " +
            "--enable-upfront-shutdown " +
            "--tlsdisableautofill " +
            "--norest "+
            "--neutrino.connect=" + TEST_NET_BTC_HOST_ADDRESS +
            " --omnicoreproxy.rpchost=" + TEST_NET_OMNI_HOST_ADDRESS_PORT;

//    testnet config

    public static final String usingNeutrinoConfig = neutrinoTestNetConfig;
    public static final String usingDownloadBaseUrl = downloadBaseUrlTestNet;
    public static final String usingBTCHostAddress = TEST_NET_BTC_HOST_ADDRESS;
    public static final String usingLogPath = testLogPath;
    public static final String usingDownloadDirectory = downloadDirectoryTestNet;

//    regTest neutrino config
   /* public static final String usingNeutrinoConfig = neutrinoRegTestConfig;
    public static final String usingDownloadBaseUrl = downloadBaseUrl;
    public static final String usingBTCHostAddress = BTCHostAddressRegTest;
    public static final String usingLogPath = regTestLogPath;
    public static final String usingDownloadDirectory = downloadDirectoryRegTest;*/

    public static final long SECOND_MILLIS = 1000;
    public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final long WEEK_MILLIS = 7 * DAY_MILLIS;


    public ConstantInOB(Context context) {
        basePath = context.getExternalCacheDir() + "";
    }

    public String getBasePath() {
        return basePath;
    }

    public String getRegTestLogPath() {
        return basePath + usingLogPath;
    }

    public String getTestLogPath() {
        return basePath + testLogPath;
    }

    public String getBlockHeaderBinPath() {
        return basePath + usingDownloadDirectory + "/" + blockHeaderBin;
    }

    public String getNeutrinoDBPath() {
        return basePath + usingDownloadDirectory + "/" + neutrinoDB;
    }

    public String getRegFilterHeaderBinPath() {
        return basePath + usingDownloadDirectory + "/" + regFilterHeaderBin;
    }

    public String getDownloadDirectoryPath() {
        return basePath + usingDownloadDirectory;
    }


}
