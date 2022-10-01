package com.omni.wallet.framelibrary.socket;

import okhttp3.WebSocket;
import okio.ByteString;


public interface IWsManager {
    WebSocket getWebSocket();

    void startConnect();

    void stopConnect();

    boolean isWsConnected();

    int getCurrentStatus();

    void setCurrentStatus(int currentStatus);

    boolean sendMessage(String msg);

    boolean sendMessage(ByteString byteString);
}
