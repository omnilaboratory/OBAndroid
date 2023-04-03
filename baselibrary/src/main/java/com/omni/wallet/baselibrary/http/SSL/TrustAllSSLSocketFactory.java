package com.omni.wallet.baselibrary.http.SSL;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class TrustAllSSLSocketFactory {
    private static final String TAG = TrustAllSSLSocketFactory.class.getSimpleName();

    /**
     * 默认信任所有的证书
     */
    public SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory mSSLSocketFactory = null;
        try {
            SSLContext mSSLContext = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = {new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            mSSLContext.init(null, trustManagers, new SecureRandom());
            mSSLSocketFactory = mSSLContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mSSLSocketFactory;
    }
}
