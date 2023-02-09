package com.omni.testnet.baselibrary.http.SSL;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;


public class MySSLSocketFactory {

    private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型

    private static final String KEY_STORE_PASSWORD = "****";//证书密码（应该是客户端证书密码）
    private static final String KEY_STORE_TRUST_PASSWORD = "***";//授信证书密码（应该是服务端证书密码）

    public static SSLSocketFactory getSocketFactory(Context context) {
//        InputStream trust_input = context.getResources().openRawResource(R.raw.trust);//服务器授信证书
//        InputStream client_input = context.getResources().openRawResource(R.raw.client);//客户端证书
//        try {
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            trustStore.load(trust_input, KEY_STORE_TRUST_PASSWORD.toCharArray());
//            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
//            keyStore.load(client_input, KEY_STORE_PASSWORD.toCharArray());
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(trustStore);
//
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());
//            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
//            SSLSocketFactory factory = sslContext.getSocketFactory();
//            return factory;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            try {
//                trust_input.close();
//                client_input.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return null;
    }


    public static OkHttpClient getSSLClientIgnoreExpire(OkHttpClient client, Context context, String assetsSSLFileName) {
        InputStream inputStream = getStream(context, assetsSSLFileName);
        try {
            //Certificate
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = null;
            final String pubSub;
            final String pubIssuer;
            certificate = certificateFactory.generateCertificate(inputStream);
            Principal pubSubjectDN = ((X509Certificate) certificate).getSubjectDN();
            Principal pubIssuerDN = ((X509Certificate) certificate).getIssuerDN();
            pubSub = pubSubjectDN.getName();
            pubIssuer = pubIssuerDN.getName();
            // Create an SSLContext that uses our TrustManager
            final TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            /**
                             for (X509Certificate cert : chain) {
                             // Make sure that it hasn't expired.
                             cert.checkValidity();
                             // Verify the certificate's public key chain.
                             try {
                             cert.verify(((X509Certificate) ca).getPublicKey());
                             } catch (Exception e) {
                             e.printStackTrace();
                             }
                             }
                             */
                            //1、判断证书是否是本地信任列表里颁发的证书
                            try {
                                TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                                tmf.init((KeyStore) null);
                                for (TrustManager trustManager : tmf.getTrustManagers()) {
                                    ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
                                }
                            } catch (Exception e) {
                                throw new CertificateException(e);
                            }
                            //2、判断服务器证书 发布方的标识名  和 本地证书 发布方的标识名 是否一致
                            //3、判断服务器证书 主体的标识名  和 本地证书 主体的标识名 是否一致
                            //getIssuerDN()  获取证书的 issuer（发布方的标识名）值。
                            //getSubjectDN()  获取证书的 subject（主体的标识名）值。
                            //Log.e("sssss", "server--"+chain[0].getSubjectDN().getName());
                            //Log.e("sssss", "server--"+chain[0].getIssuerDN().getName());
                            if (!chain[0].getSubjectDN().getName().equals(pubSub)) {
                                throw new CertificateException("server's SubjectDN is not equals to client's SubjectDN");
                            }
                            if (!chain[0].getIssuerDN().getName().equals(pubIssuer)) {
                                throw new CertificateException("server's IssuerDN is not equals to client's IssuerDN");
                            }
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            //SSLContext  and SSLSocketFactory
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            //okhttpclient
            OkHttpClient.Builder builder = client.newBuilder();
            builder.sslSocketFactory(sslSocketFactory);
            return builder.build();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    private static InputStream getStream(Context context, String assetsFileName) {
        try {
            return context.getAssets().open(assetsFileName);
        } catch (Exception var3) {
            return null;
        }
    }
}
