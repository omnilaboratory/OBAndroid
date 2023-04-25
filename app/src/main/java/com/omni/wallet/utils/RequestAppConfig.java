package com.omni.wallet.utils;

import android.content.Context;
import android.util.Log;

import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.baselibrary.http.callback.EngineCallback;
import com.omni.wallet.baselibrary.http.progress.entity.Progress;
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

    public static void getNodes(Context mContext) {
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
                    }

                    @Override
                    public void onSuccess(Context context, String result) {
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
                            NodeActions.clearAndAddMultipleNodes(context,nodes);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

    public static void getStartParams(Context mContext) {
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
                    }

                    @Override
                    public void onSuccess(Context context, String result) {
                        try {
                            JSONObject resultObject = new JSONObject(result);
                            JSONObject testNetObject = new JSONObject(resultObject.getString("testnet"));
                            JSONObject regNetObject = new JSONObject(resultObject.getString("regtest"));
                            JSONObject mainNetObject = new JSONObject(resultObject.getString("mainnet"));
                            Log.d(TAG, result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
}
