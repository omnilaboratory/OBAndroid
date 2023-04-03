package com.omni.wallet.baselibrary.http.SSL;

import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;


public class TrustAllSSLSocketFactory {
    private static final String TAG = TrustAllSSLSocketFactory.class.getSimpleName();

    /**
     * 默认信任所有的证书
     */
    public SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory mSSLSocketFactory = null;
        try {
            SSLContext mSSLContext = SSLContext.getInstance("TLS");
            mSSLContext.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
            mSSLSocketFactory = mSSLContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mSSLSocketFactory;
    }
}
