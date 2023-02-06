package com.omni.wallet.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import lnrpc.Stateservice;
import obdmobile.Obdmobile;
import obdmobile.RecvStream;

import static android.content.Context.MODE_PRIVATE;

public class WalletState {
    private static final String SETTING = "setting";
    private static final String TAG = WalletState.class.getSimpleName();
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
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(byte[] bytes) {

                if (bytes == null){
                    Log.e(TAG,"get null state");
                    return;
                }
                try {
                    Stateservice.SubscribeStateResponse subscribeStateResponse = Stateservice.SubscribeStateResponse.parseFrom(bytes);
                    int walletState = subscribeStateResponse.getStateValue();
                    walletStateCallback.callback(walletState);
                    Log.e(TAG,String.valueOf(walletState));
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
