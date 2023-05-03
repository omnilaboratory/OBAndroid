package com.omni.wallet.utils;

import android.content.Context;
import android.util.Log;

import com.omni.wallet.SharedPreferences.WalletInfo;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.common.NetworkType;
import com.omni.wallet.data.Node;
import com.omni.wallet.data.NodeActions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestAppConfig {
    private static final String TAG = RequestAppConfig.class.getSimpleName();

    public interface GetAppConfigJsonSuccessCallback {
        void callback(String startParams);
    }

    public interface GetAppConfigJsonFailedCallback {
        void callback();
    }

    public static void getAppConfigJson(Context mContext,GetAppConfigJsonSuccessCallback successCallback,GetAppConfigJsonFailedCallback failedCallback) {
        HttpUtils.with(mContext)
                .get()
                .url("https://cache.oblnd.top/app.cfg")
                .execute(new EngineCallback() {
                    @Override
                    public void onPreExecute(Context context, Map<String, Object> params) {

                    }

                    @Override
                    public void onCancel(Context context) {

                    }

                    @Override
                    public void onError(Context context, String errorCode, String errorMsg) {
                        Log.d(TAG, errorMsg);
                        failedCallback.callback();
                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        actionAboutNode(context, result);
                        String startParams = actionAboutParams(context, result);
                        successCallback.callback(startParams);
                    }

                    @Override
                    public void onSuccess(Context context, byte[] result) {

                    }

                    @Override
                    public void onProgressInThread(Context context, Progress progress) {

                    }

                    @Override
                    public void onFileSuccess(Context context, String filePath) {

                    }
                });
    }

    private static void actionAboutNode(Context context, String result) {
        try {
            JSONObject resultObject = new JSONObject(result);
            JSONObject testNetObject = new JSONObject(resultObject.getString("testnet"));
            JSONObject regNetObject = new JSONObject(resultObject.getString("regtest"));
            JSONObject mainNetObject = new JSONObject(resultObject.getString("mainnet"));
            JSONArray testNetNodeObject = new JSONArray(testNetObject.getString("nodes"));
            JSONArray regNetNodeObject = new JSONArray(regNetObject.getString("nodes"));
            JSONArray mainNetNodeObject = new JSONArray(mainNetObject.getString("nodes"));
            Log.d(TAG, result);
            List<Node> nodes = new ArrayList<>();
            for (int i = 0; i < testNetNodeObject.length(); i++) {
                JSONObject nodeObject = new JSONObject(testNetNodeObject.get(i).toString());
                String alias = nodeObject.getString("alias");
                String spayUrl = nodeObject.getString("spayurl");
                String nodeUrl = nodeObject.getString("node_url");
                Node node = new Node(alias, spayUrl, nodeUrl, NetworkType.TEST);
                nodes.add(node);
                Log.d(TAG, nodeObject.toString());
            }
            for (int i = 0; i < regNetNodeObject.length(); i++) {
                JSONObject nodeObject = new JSONObject(regNetNodeObject.get(i).toString());
                String alias = nodeObject.getString("alias");
                String spayUrl = nodeObject.getString("spayurl");
                String nodeUrl = nodeObject.getString("node_url");
                Node node = new Node(alias, spayUrl, nodeUrl, NetworkType.REG);
                nodes.add(node);
                Log.d(TAG, nodeObject.toString());
            }
            for (int i = 0; i < mainNetNodeObject.length(); i++) {
                JSONObject nodeObject = new JSONObject(mainNetNodeObject.get(i).toString());
                String alias = nodeObject.getString("alias");
                String spayUrl = nodeObject.getString("spayurl");
                String nodeUrl = nodeObject.getString("node_url");
                Node node = new Node(alias, spayUrl, nodeUrl, NetworkType.MAIN);
                nodes.add(node);
                Log.d(TAG, nodeObject.toString());
            }
            NodeActions.clearAndAddMultipleNodes(context, nodes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String actionAboutParams(Context context, String result) {
        String startParams ="";
        try {
            JSONObject resultObject = new JSONObject(result);
            JSONObject testNetObject = new JSONObject(resultObject.getString("testnet"));
//            JSONArray testNetNodeObject = new JSONArray(testNetObject.getString("nodes"));
            String testOmniCoreproxy = testNetObject.getString("omnicoreproxy");
            String testNeutrinoUrl = testNetObject.getString("neutrino_url");
//            JSONObject testFirstNodeObject = new JSONObject(testNetNodeObject.get(0).toString());
//            String testSpayUrl =  testFirstNodeObject.getString("spayurl");
            String testStartParams =
                    " --autopilot.active" +
                            " --bitcoin.active" +
                            " --bitcoin.testnet" +
                            " --bitcoin.node=neutrino" +
                            " --debuglevel=debug \n" +
                            " --enable-upfront-shutdown" +
                            " --maxpendingchannels=100" +
                            " --norest"+
                            " --neutrino.connect=" + testNeutrinoUrl +
                            " --omnicoreproxy.rpchost=" + testOmniCoreproxy +
//                            " --spayurl=" + testSpayUrl +
                            " --trickledelay=5000 " +
                            " --tlsdisableautofill " +
                            " --alias=";


            JSONObject regNetObject = new JSONObject(resultObject.getString("regtest"));
//            JSONArray regNetNodeObject = new JSONArray(regNetObject.getString("nodes"));
            String regOmniCoreproxy = regNetObject.getString("omnicoreproxy");
            String regNeutrinoUrl = regNetObject.getString("neutrino_url");
//            JSONObject regFirstNodeObject = new JSONObject(regNetNodeObject.get(0).toString());
//            String regSpayUrl =  regFirstNodeObject.getString("spayurl");
            String regStartParams =
                    " --autopilot.active" +
                            " --bitcoin.active" +
                            " --bitcoin.regtest" +
                            " --bitcoin.node=neutrino" +
                            " --debuglevel=debug \n" +
                            " --enable-upfront-shutdown" +
                            " --maxpendingchannels=100" +
                            " --norest"+
                            " --nobootstrap" +
                            " --neutrino.connect=" + regNeutrinoUrl +
                            " --omnicoreproxy.rpchost=" + regOmniCoreproxy +
//                            " --spayurl=" + regSpayUrl +
                            " --trickledelay=5000 " +
                            " --tlsdisableautofill " +
                            " --alias=";


            JSONObject mainNetObject = new JSONObject(resultObject.getString("mainnet"));
//            JSONArray mainNetNodeObject = new JSONArray(mainNetObject.getString("nodes"));
            String mainOmniCoreproxy = mainNetObject.getString("omnicoreproxy");
            String mainFeeUrl = mainNetObject.getString("feeurl");
//            JSONObject mainFirstNodeObject = new JSONObject(mainNetNodeObject.get(0).toString());
//            String mainSpayUrl =  mainFirstNodeObject.getString("spayurl");
            String mainStartParams =
                    " --autopilot.active" +
                    " --bitcoin.active" +
                    " --bitcoin.mainnet" +
                    " --bitcoin.node=neutrino" +
                    " --debuglevel=debug \n" +
                    " --enable-upfront-shutdown" +
                    " --maxpendingchannels=100" +
                    " --norest"+
                    " --neutrino.feeurl=" + mainFeeUrl +
                    " --nobootstrap" +
                    " --neutrino.addpeer=btcd-mainnet.lightning.computer" +
                    " --neutrino.addpeer=mainnet1-btcd.zaphq.io" +
                    " --neutrino.addpeer=mainnet2-btcd.zaphq.io" +
                    " --neutrino.addpeer=mainnet3-btcd.zaphq.io" +
                    " --neutrino.addpeer=mainnet4-btcd.zaphq.io" +
                    " --omnicoreproxy.rpchost=" + mainOmniCoreproxy +
//                    " --spayurl=" + mainSpayUrl +
                    " --trickledelay=5000 " +
                    " --tlsdisableautofill " +
                    " --alias=";
            WalletInfo.getInstance().setStartParams(context,mainStartParams,NetworkType.MAIN);
            WalletInfo.getInstance().setStartParams(context,regStartParams,NetworkType.REG);
            WalletInfo.getInstance().setStartParams(context,testStartParams,NetworkType.TEST);
            switch (ConstantInOB.networkType){
                case MAIN:
                    startParams =  mainStartParams;
                    break;
                case TEST:
                    startParams =  testStartParams;
                    break;
                case REG:
                    startParams =  regStartParams;
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return startParams;
    }
}
