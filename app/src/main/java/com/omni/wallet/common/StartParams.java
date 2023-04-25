package com.omni.wallet.common;

public class StartParams {
    private static StartParams mInstance;
    private String OMNI_HOST_ADDRESS_PORT;
    private String BTC_HOST_ADDRESS;
    private String startParams;

    private StartParams(NetworkType networkType){
        switch (networkType){
            case REG:
                this.OMNI_HOST_ADDRESS_PORT = ConstantInOB.OMNIHostAddressPortRegTest;
                this.BTC_HOST_ADDRESS = ConstantInOB.BTCHostAddressRegTest;
                this.startParams = "--trickledelay=5000 " +
                        "--debuglevel=debug \n" +
                        "--autopilot.active " +
                        "--maxpendingchannels=100 " +
                        "--bitcoin.active " +
                        "--bitcoin.regtest " +
                        "--bitcoin.node=neutrino " +
                        "--enable-upfront-shutdown " +
                        "--tlsdisableautofill " +
                        "--norest "+
                        "--nobootstrap" +
                        "--neutrino.connect=" + BTC_HOST_ADDRESS +
                        " --omnicoreproxy.rpchost=" + OMNI_HOST_ADDRESS_PORT +
                        " --alias=";
                break;
            case TEST:
                this.OMNI_HOST_ADDRESS_PORT = ConstantInOB.TEST_NET_OMNI_HOST_ADDRESS_PORT;
                this.BTC_HOST_ADDRESS = ConstantInOB.TEST_NET_BTC_HOST_ADDRESS;
                this.startParams = "--trickledelay=5000 " +
                        "--debuglevel=debug \n" +
                        "--autopilot.active " +
                        "--maxpendingchannels=100 " +
                        "--bitcoin.active " +
                        "--bitcoin.testnet " +
                        "--bitcoin.node=neutrino " +
                        "--enable-upfront-shutdown " +
                        "--tlsdisableautofill " +
                        "--norest "+
                        "--neutrino.connect=" + BTC_HOST_ADDRESS +
                        " --omnicoreproxy.rpchost=" + OMNI_HOST_ADDRESS_PORT +
                        " --alias=";
                break;
            case MAIN:
                this.OMNI_HOST_ADDRESS_PORT = ConstantInOB.MAIN_NET_OMNI_HOST_ADDRESS_PORT;
                this.BTC_HOST_ADDRESS = ConstantInOB.MAIN_NET_BTC_HOST_ADDRESS;
                this.startParams = "--trickledelay=5000 " +
                        "--debuglevel=debug \n" +
                        "--autopilot.active " +
                        "--maxpendingchannels=100 " +
                        "--bitcoin.active " +
                        "--bitcoin.mainnet " +
                        "--bitcoin.node=neutrino " +
                        "--enable-upfront-shutdown " +
                        "--tlsdisableautofill " +
                        "--norest "+
                        "--neutrino.feeurl=https://nodes.lightning.computer/fees/v1/btc-fee-estimates.json " +
                        "--nobootstrap" +
                        " --neutrino.addpeer=btcd-mainnet.lightning.computer" +
                        " --neutrino.addpeer=mainnet1-btcd.zaphq.io" +
                        " --neutrino.addpeer=mainnet2-btcd.zaphq.io" +
                        " --neutrino.addpeer=mainnet3-btcd.zaphq.io" +
                        " --neutrino.addpeer=mainnet4-btcd.zaphq.io" +
                        " --omnicoreproxy.rpchost=" + OMNI_HOST_ADDRESS_PORT +
                        " --alias=";
                break;
        }
    }

    public static StartParams getInstance(NetworkType networkType) {
        if (mInstance == null) {
            synchronized (StartParams.class) {
                if (mInstance == null) {
                    mInstance = new StartParams(networkType);
                }
            }
        }
        return mInstance;
    }

    String getOMNI_HOST_ADDRESS_PORT() {
        return OMNI_HOST_ADDRESS_PORT;
    }

    String getBTC_HOST_ADDRESS() {
        return BTC_HOST_ADDRESS;
    }

    String getStartParams() {
        return startParams;
    }

}
