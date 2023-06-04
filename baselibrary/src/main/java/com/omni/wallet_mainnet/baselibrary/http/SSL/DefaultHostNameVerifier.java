package com.omni.wallet_mainnet.baselibrary.http.SSL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


public class DefaultHostNameVerifier implements HostnameVerifier {
    private static final String TAG = DefaultHostNameVerifier.class.getSimpleName();

    @Override
    public boolean verify(String hostname, SSLSession session) {
//        // Always return true，接受任意域名服务器
//        LogUtils.e(TAG, "========HostnameVerifier====verify()============>");
//        Certificate[] localCertificates = new Certificate[0];
//        try {
//            //获取证书链中的所有证书
//            localCertificates = session.getPeerCertificates();
//        } catch (SSLPeerUnverifiedException e) {
//            e.printStackTrace();
//        }
//        //打印所有证书内容
//        for (Certificate c : localCertificates) {
//            LogUtils.e(TAG, "verify: " + c.toString());
//        }
//        try {
//            //将证书链中的第一个写到文件
//            String filePath = Environment.getExternalStorageDirectory() + File.separator + "ca.cer";
//            FileUtils.ByteToFile(localCertificates[0].getEncoded(), filePath);
//        } catch (CertificateEncodingException e) {
//            e.printStackTrace();
//        }
        return true;
    }
}
