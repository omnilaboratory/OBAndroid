package com.omni.testnet.baselibrary.http.SSL;

import android.content.Context;

import com.omni.testnet.baselibrary.base.BaseApplication;
import com.omni.testnet.baselibrary.utils.LogUtils;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class TrustServiceManager implements X509TrustManager {
    private static final String TAG = TrustServiceManager.class.getSimpleName();

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        //do nothing，接受任意客户端证书
        LogUtils.e(TAG, "========X509TrustManager====checkClientTrusted()============>");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        // 校验客户端证书
        InputStream inputStream = getStream(BaseApplication.applicationContext, "sdic-crit.cer");
        //Certificate
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Certificate certificate = certificateFactory.generateCertificate(inputStream);
        Principal pubSubjectDN = ((X509Certificate) certificate).getSubjectDN();
        Principal pubIssuerDN = ((X509Certificate) certificate).getIssuerDN();
        String pubSub = pubSubjectDN.getName();
        String pubIssuer = pubIssuerDN.getName();
        //1、判断证书是否是本地信任列表里颁发的证书，
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
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
//        LogUtils.e(TAG, "本地证书的主体的标识名" + pubSub);
//        LogUtils.e(TAG, "本地证书的发布方的标识名" + pubIssuer);
//        LogUtils.e(TAG, "service的主体的标识名" + chain[0].getSubjectDN());
//        LogUtils.e(TAG, "service的发布方的标识名" + chain[0].getIssuerDN());
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

    private static InputStream getStream(Context context, String assetsFileName) {
        try {
            return context.getAssets().open(assetsFileName);
        } catch (Exception var3) {
            return null;
        }
    }

}
