package com.omni.wallet.obdMethods;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.baselibrary.utils.LogUtils;

import lnrpc.Stateservice;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class WalletState {
    private static final String TAG = WalletState.class.getSimpleName();
    public static final int NON_EXISTING = 0;
    public static final int LOCKED = 1;
    public static final int UNLOCKED = 2;
    public static final int RPC_ACTIVE = 3;
    public static final int SERVER_ACTIVE = 4;
    public static final int WAITING_TO_START = 255;
    public static final int ELSE = -1;
    private WalletStateCallback walletStateCallback = null;
    private Handler handler = new Handler();
    private int WALLET_STATE = -100;
    private Context mContext;
    @SuppressLint("CommitPrefEdits")
    public WalletState() {
    }
    private Runnable walletStateRunnable = new Runnable() {
        @Override
        public void run() {
            getWalletState();
            handler.postDelayed(this,500);
        }
    };

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

    public void setWalletState(int walletState){
        this.WALLET_STATE = walletState;
    }

    public int getStaticWalletState(){
        return this.WALLET_STATE;
    }

    public void setWalletStateCallback(WalletStateCallback walletStateCallback){
        this.walletStateCallback = walletStateCallback;
    }

    public void subscribeWalletState(Context context){
        this.mContext = context;
        handler.post(walletStateRunnable);
    }

    private void getWalletState(){
        Stateservice.GetStateRequest getStateRequest = Stateservice.GetStateRequest.newBuilder().build();
        Obdmobile.getState(getStateRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                LogUtils.e(TAG,e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(byte[] bytes) {
                if (bytes == null){
                    if(WALLET_STATE != -1){
                        Log.d(TAG,"get null state");
                        walletStateCallback.callback(-1);
                        WALLET_STATE = -1;
                    }
                    return;
                }
                try {
                    Stateservice.SubscribeStateResponse subscribeStateResponse = Stateservice.SubscribeStateResponse.parseFrom(bytes);
                    int walletState = subscribeStateResponse.getStateValue();
                    if (walletState != WALLET_STATE){
                        LogUtils.e(TAG,String.valueOf(walletState));
                        WALLET_STATE = walletState;
                        if (WALLET_STATE == 4){
                            BackupUtils.getInstance().backupChannelToFile(mContext);
                            cancelSubscribe();
                        }
                        walletStateCallback.callback(walletState);
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void cancelSubscribe(){
        handler.removeCallbacks(walletStateRunnable);
    }

    public interface WalletStateCallback {
        void callback(int walletState);
    }

}
