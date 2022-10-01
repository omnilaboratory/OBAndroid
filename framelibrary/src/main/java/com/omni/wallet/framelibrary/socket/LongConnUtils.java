package com.omni.wallet.framelibrary.socket;

import android.content.Context;

import com.omni.wallet.baselibrary.http.interceptor.LogInterceptor;
import com.omni.wallet.baselibrary.utils.LogUtils;
import com.omni.wallet.baselibrary.utils.StringUtils;
import com.omni.wallet.framelibrary.http.HttpRequestUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 长连接工具类
 */

public class LongConnUtils {
    private static final String TAG = LongConnUtils.class.getSimpleName();

    private WsManager mWebSocketManager;

    /**
     * 开启长连接
     */
    public void startConnect(Context context, LongConnCallback callback) {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new LogInterceptor())
                .pingInterval(50, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        mWebSocketManager = new WsManager.Builder(context)
                .client(okHttpClient)
                .needReconnect(true)
                .wsUrl(HttpRequestUtils.AUCTION_SOCKET)
                .build();
        mWebSocketManager.setWsStatusListener(new MyWebSocketListener(callback));
        mWebSocketManager.startConnect();
    }

    /**
     * 长连接的监听
     */
    private class MyWebSocketListener extends WsStatusListener {
        private LongConnCallback mCallback;

        public MyWebSocketListener(LongConnCallback callback) {
            this.mCallback = callback;
        }

        @Override
        public void onOpen(Response response) {
            super.onOpen(response);
            LogUtils.e(TAG, "长连接打开");
        }

        @Override
        public void onMessage(String text) {
            LogUtils.e(TAG, "接到长连接返回消息：" + text);
            if (StringUtils.isEmpty(text)) {
                return;
            }
            if (WsManager.MSG_SOCKET_HEART.equals(text)) {
                return;
            }
            if (mCallback != null) {
                mCallback.onMessage(text);
            }
        }
    }

    /**
     * 断开链接
     */
    public void stopConnect() {
        if (mWebSocketManager != null) {
            mWebSocketManager.stopConnect();
        }
    }

    /**
     * 根据网络状态断开和链接
     */
    public void refreshConnect(boolean netAvailable) {
        if (netAvailable) {
            if (!mWebSocketManager.isWsConnected()) {
                mWebSocketManager.startConnect();
            }
        } else {
            if (mWebSocketManager.isWsConnected()) {
                mWebSocketManager.stopConnect();
            }
        }
    }


    /**
     * 长连接监听
     */
    public interface LongConnCallback {
        void onMessage(String text);
    }


}
