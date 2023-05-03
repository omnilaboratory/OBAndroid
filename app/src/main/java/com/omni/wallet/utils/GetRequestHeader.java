package com.omni.wallet.utils;

import android.content.Context;
import android.util.Log;

import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.framelibrary.entity.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GetRequestHeader {
    public static final String TAG = GetRequestHeader.class.getSimpleName();
    private String BASE_PATH = "";
    private String TLS_PATH = "";
    private String TLS_KEY_PATH = "";
    private String MACAROON_PATH = "";

    public GetRequestHeader(Context context) {
        String network = User.getInstance().getNetwork(context);
        Log.d(TAG + "network: ", network);
        String basePath = context.getExternalFilesDir(null).toString() + "/ObdMobile/" + ConstantInOB.networkType;
        this.BASE_PATH = basePath;
        this.TLS_PATH = basePath + "/tls.cert";
        this.TLS_KEY_PATH = basePath + "/tls.key.pcks8";
        this.MACAROON_PATH = basePath + "/data/chain/bitcoin/" + network + "/admin.macaroon";
    }

    public String getBASE_PATH() {
        return BASE_PATH;
    }

    public void setBASE_PATH(String BASE_PATH) {
        this.BASE_PATH = BASE_PATH;
    }

    public String getTLS_PATH() {
        return TLS_PATH;
    }

    public void setTLS_PATH(String TSL_PATH) {
        this.TLS_PATH = TLS_PATH;
    }

    public String getTLS_KEY_PATH() {
        return TLS_KEY_PATH;
    }

    public void setTLS_KEY_PATH(String TLS_KEY_PATH) {
        this.TLS_KEY_PATH = TLS_KEY_PATH;
    }

    public String getMACAROON_PATH() {
        return MACAROON_PATH;
    }

    public void setMACAROON_PATH(String MACAROON_PATH) {
        this.MACAROON_PATH = MACAROON_PATH;
    }

    public String getTLSString() {
        String cert = "";
        String tlsPath = getTLS_PATH();
        Log.d(TAG + "getTSLString: ", tlsPath);
        File file = new File(tlsPath);
        if (file.exists()) {
            BufferedReader bfr;
            try {
                bfr = new BufferedReader(new FileReader(tlsPath));
                String line;
                line = bfr.readLine();
                StringBuilder sb = new StringBuilder();
                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = bfr.readLine();
                }
                bfr.close();
                cert = sb.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            cert = "";
        }
        return cert;
    }

    public String getTLSKeyString() {
        String cert = "";
        String tlsPath = getTLS_KEY_PATH();
        Log.d(TAG + "getTSLKeyString: ", tlsPath);
        File file = new File(tlsPath);
        if (file.exists()) {
            BufferedReader bfr;
            try {
                bfr = new BufferedReader(new FileReader(tlsPath));
                String line;
                line = bfr.readLine();
                StringBuilder sb = new StringBuilder();
                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = bfr.readLine();
                }
                bfr.close();
                cert = sb.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            cert = "";
        }
        return cert;
    }
}
