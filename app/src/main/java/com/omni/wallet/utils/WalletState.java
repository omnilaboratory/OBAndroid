package com.omni.wallet.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.framelibrary.entity.User;

import lnrpc.Stateservice;
import obdmobile.Callback;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;

import static android.content.Context.MODE_PRIVATE;

public class WalletState {
    private static final String SETTING = "setting";
    WalletStateCallback walletStateCallback;
    @SuppressLint("CommitPrefEdits")
    public WalletState() {
    }

    private static WalletState mInstance;

    public static WalletState getInstance() {
        if (mInstance == null) {
            synchronized (WalletState.class) {
                if (mInstance == null) {
                    mInstance = new WalletState();
                }
            }
        }
        return mInstance;
    }

    public void registerListenWalletState(Context context,SharedPreferences.OnSharedPreferenceChangeListener sharedPreferencesListener) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SETTING,MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener);
    }

    public void unregisterListenWalletState(Context context,SharedPreferences.OnSharedPreferenceChangeListener sharedPreferencesListener) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SETTING,MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener);
    }

    public void setWalletStateCallback(WalletStateCallback walletStateCallback){
        this.walletStateCallback = walletStateCallback;
    }


    public void subscribeWalletState(Context context){
        Stateservice.SubscribeStateRequest subscribeStateRequest = Stateservice.SubscribeStateRequest.newBuilder().build();
        Obdmobile.subscribeState(subscribeStateRequest.toByteArray(),new RecvStream(){
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null){
                    return;
                }
                try {
                    Stateservice.SubscribeStateResponse subscribeStateResponse = Stateservice.SubscribeStateResponse.parseFrom(bytes);
                    int walletState = subscribeStateResponse.getStateValue();
                    User.getInstance().setWalletState(context,walletState);
                    walletStateCallback.callback(walletState);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface WalletStateCallback {
        void callback(int walletState);
    }

}
