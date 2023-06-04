package com.omni.wallet_mainnet.framelibrary.socket;

import okhttp3.Response;
import okio.ByteString;

/**
 * 长连接状态
 */

public abstract class WsStatusListener {

    public void onOpen(Response response) {
    }

    public void onMessage(String text) {
    }

    public void onMessage(ByteString bytes) {
    }

    public void onReconnect() {

    }

    public void onClosing(int code, String reason) {
    }


    public void onClosed(int code, String reason) {
    }

    public void onFailure(Throwable t, Response response) {
    }
}
