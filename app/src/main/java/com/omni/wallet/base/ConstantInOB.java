package com.omni.wallet.base;

import android.content.Context;

public class ConstantInOB {
    public static String basePath = "";
    public static String regTestLogPath = "/logs/bitcoin/regtest/lnd.log";
    public static String testLogPath = "/logs/bitcoin/testnet/lnd.log";
    public static String downloadDirectory = "/data/chain/bitcoin/regtest/";
    public static String OMNIHostAddressRegTest= "43.138.107.248";
    public static String OMNIHostAddressPortRegTest= "43.138.107.248:18332";
    public static String BTCHostAddressRegTest = "43.138.107.248";
    public static String BTCHostAddressPortRegTest = "43.138.107.248:18332";
    public static String blockHeaderBin = "block_headers.bin";
    public static String blockHeader = "block_headers";
    public static String neutrinoDB = "neutrino.db";
    public static String neutrino = "neutrino";
    public static String regFilterHeaderBin = "reg_filter_headers.bin";
    public static String regFilterHeader = "reg_filter_headers";
    public static String downloadBaseUrl = "https://cache.oblnd.top/neutrino-regtest/";

    public static final String neutrinoRegTestConfig = "--trickledelay=5000 --debuglevel=debug --alias=alice\n" +
            "--autopilot.active --maxpendingchannels=100 " +
            "--bitcoin.active --bitcoin.regtest --bitcoin.node=neutrino " +
            "--neutrino.connect="+BTCHostAddressRegTest+
            "--omnicoreproxy.rpchost=" + OMNIHostAddressPortRegTest;

    public static final String normalRegTestConfig = "--trickledelay=5000 --debuglevel=debug --alias=alice\n" +
            "--autopilot.active --maxpendingchannels=100 " +
            "--bitcoin.active --bitcoin.regtest --bitcoin.node=omnicoreproxy " +
            "--omnicoreproxy.rpchost=" +BTCHostAddressRegTest +":18332 "+
            "--omnicoreproxy.zmqpubrawblock=tcp://" +BTCHostAddressRegTest +":28332 "+
            "--omnicoreproxy.zmqpubrawtx=tcp://" +BTCHostAddressRegTest +":28333";

    public static final long SECOND_MILLIS = 1000;
    public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final long WEEK_MILLIS = 7 * DAY_MILLIS;
    
    
    public ConstantInOB(Context context){
        basePath = context.getExternalCacheDir() + "";
    }        

    public String getBasePath() {
        return basePath;
    }

    public String getRegTestLogPath() {
        return basePath + regTestLogPath;
    }

    public String getTestLogPath() {
        return basePath + testLogPath;
    }
    
    public String getBlockHeaderBinPath(){
        return basePath + downloadDirectory + "/" + blockHeaderBin;
    }

    public String getNeutrinoDBPath(){
        return basePath + downloadDirectory + "/" + neutrinoDB;
    }

    public String getRegFilterHeaderBinPath(){
        return basePath + downloadDirectory + "/" + regFilterHeaderBin;
    }
    
    public String getDownloadDirectoryPath(){
        return basePath + downloadDirectory;
    }
}
