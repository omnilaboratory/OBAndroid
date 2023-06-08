package com.omni.wallet_mainnet.common;

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
                this.startParams = "--noseedbackup --nolisten --protocol.no-anchors --trickledelay=5000 " +
                        "--debuglevel=debug \n" +
                        "--autopilot.active " +
                        "--maxpendingchannels=100 " +
                        "--bitcoin.active " +
                        "--bitcoin.regtest " +
                        "--bitcoin.node=neutrino " +
                        "--enable-upfront-shutdown " +
                        "--tlsdisableautofill " +
                        "--norest "+
                        "--accept-keysend "+
                        "--spay-url=43.138.107.248:38332 "+
                        "--nobootstrap" +
                        "--neutrino.connect=" + BTC_HOST_ADDRESS +
                        " --omnicoreproxy.rpchost=" + OMNI_HOST_ADDRESS_PORT +
                        " --alias=";
                break;
            case TEST:
                this.OMNI_HOST_ADDRESS_PORT = ConstantInOB.TEST_NET_OMNI_HOST_ADDRESS_PORT;
                this.BTC_HOST_ADDRESS = ConstantInOB.TEST_NET_BTC_HOST_ADDRESS;
                this.startParams = "--noseedbackup --nolisten --protocol.no-anchors --trickledelay=5000 " +
                        "--debuglevel=debug \n" +
                        "--autopilot.active " +
                        "--maxpendingchannels=100 " +
                        "--bitcoin.active " +
                        "--bitcoin.testnet " +
                        "--bitcoin.node=neutrino " +
                        "--enable-upfront-shutdown " +
                        "--tlsdisableautofill " +
                        "--norest "+
                        "--accept-keysend "+
                        "--spay-url=192.144.199.67:38332 "+
                        "--neutrino.connect=" + BTC_HOST_ADDRESS +
                        " --omnicoreproxy.rpchost=" + OMNI_HOST_ADDRESS_PORT +
                        " --alias=";
                break;
            case MAIN:
                this.OMNI_HOST_ADDRESS_PORT = ConstantInOB.MAIN_NET_OMNI_HOST_ADDRESS_PORT;
                this.BTC_HOST_ADDRESS = ConstantInOB.MAIN_NET_BTC_HOST_ADDRESS;
                this.startParams = "--noseedbackup --nolisten --protocol.no-anchors --trickledelay=5000 " +
                        "--debuglevel=debug \n" +
                        "--autopilot.active " +
                        "--maxpendingchannels=100 " +
                        "--bitcoin.active " +
                        "--bitcoin.mainnet " +
                        "--bitcoin.node=neutrino " +
                        "--enable-upfront-shutdown " +
                        "--tlsdisableautofill " +
                        "--norest "+
                        "--accept-keysend "+
                        "--spay-url=110.40.210.253:58332 "+
                        "--neutrino.feeurl=https://nodes.lightning.computer/fees/v1/btc-fee-estimates.json " +
                        "--nobootstrap" +
                        " --neutrino.addpeer=bb1.breez.technology" +
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
