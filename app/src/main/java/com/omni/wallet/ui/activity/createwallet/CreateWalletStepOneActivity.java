package com.omni.wallet.ui.activity.createwallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.view.dialog.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lnrpc.Walletunlocker;
import obdmobile.Callback;
import obdmobile.Obdmobile;

public class CreateWalletStepOneActivity extends AppBaseActivity {
    List <String> seedArray = new ArrayList();
    String seedsString = "";
    Context ctx = CreateWalletStepOneActivity.this;
    LoadingDialog mLoadingDialog;

    @BindView(R.id.seed_content)
    LinearLayout lvSeedContent;

    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_create_wallet_setp_one;
    }


    @Override
    protected void initView() {
        mLoadingDialog = new LoadingDialog(mContext);
        
    }

    @Override
    protected void initData() {
        createSeeds();
    }
  
    private void createSeeds() {
        Log.e("create error:","123456789");
        mLoadingDialog.show();
        Walletunlocker.GenSeedRequest genSeedRequest = Walletunlocker.GenSeedRequest.newBuilder().build();
        Obdmobile.genSeed(genSeedRequest.toByteArray(), new Callback() {
            @Override
            public void onError(Exception e) {
                Log.e("create error:",e.toString());
                mLoadingDialog.dismiss();
            }

            @Override
            public void onResponse(byte[] bytes) {
                try{
                    Walletunlocker.GenSeedResponse genSeedResponse = Walletunlocker.GenSeedResponse.parseFrom(bytes);
                    List genSeedResponseList =  genSeedResponse.getCipherSeedMnemonicList();
                    for (int i = 0; i<genSeedResponseList.size();i++){
                        seedArray.add(genSeedResponseList.get(i).toString());
                        Log.e("seed item",genSeedResponseList.get(i).toString());
                    }
                    for (int idx = 0;idx<seedArray.size();idx++){
                        seedsString = seedsString + seedArray.get(idx)+ " ";
                    }
                    /**
                     * 使用SharedPreferences 对象，在生成seeds时候将seeds备份到本地文件
                     * Use SharedPreferences Class to back up seeds to local file,when create seeds.
                     */
                    SharedPreferences secretData = ctx.getSharedPreferences("secretData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = secretData.edit();
                    editor.putString("seeds",seedsString);
                    editor.commit();
//                    initTvForSeeds();
                    TextView textView01 = (TextView)findViewById(R.id.seed_text_1);
                    textView01.setText(seedArray.get(0));
                    TextView textView02 = (TextView)findViewById(R.id.seed_text_2);
                    textView02.setText(seedArray.get(1));
                    TextView textView03 = (TextView)findViewById(R.id.seed_text_3);
                    textView03.setText(seedArray.get(2));
                    TextView textView04 = (TextView)findViewById(R.id.seed_text_4);
                    textView04.setText(seedArray.get(3));
                    TextView textView05 = (TextView)findViewById(R.id.seed_text_5);
                    textView05.setText(seedArray.get(4));
                    TextView textView06 = (TextView)findViewById(R.id.seed_text_6);
                    textView06.setText(seedArray.get(5));
                    TextView textView07 = (TextView)findViewById(R.id.seed_text_7);
                    textView07.setText(seedArray.get(6));
                    TextView textView08 = (TextView)findViewById(R.id.seed_text_8);
                    textView08.setText(seedArray.get(7));
                    TextView textView09 = (TextView)findViewById(R.id.seed_text_9);
                    textView09.setText(seedArray.get(8));
                    TextView textView10 = (TextView)findViewById(R.id.seed_text_10);
                    textView10.setText(seedArray.get(9));
                    TextView textView11 = (TextView)findViewById(R.id.seed_text_11);
                    textView11.setText(seedArray.get(10));
                    TextView textView12 = (TextView)findViewById(R.id.seed_text_12);
                    textView12.setText(seedArray.get(11));
                    TextView textView13 = (TextView)findViewById(R.id.seed_text_13);
                    textView13.setText(seedArray.get(12));
                    TextView textView14 = (TextView)findViewById(R.id.seed_text_14);
                    textView14.setText(seedArray.get(13));
                    TextView textView15 = (TextView)findViewById(R.id.seed_text_15);
                    textView15.setText(seedArray.get(14));
                    TextView textView16 = (TextView)findViewById(R.id.seed_text_16);
                    textView16.setText(seedArray.get(15));
                    TextView textView17 = (TextView)findViewById(R.id.seed_text_17);
                    textView17.setText(seedArray.get(16));
                    TextView textView18 = (TextView)findViewById(R.id.seed_text_18);
                    textView18.setText(seedArray.get(17));
                    TextView textView19 = (TextView)findViewById(R.id.seed_text_19);
                    textView19.setText(seedArray.get(18));
                    TextView textView20 = (TextView)findViewById(R.id.seed_text_20);
                    textView20.setText(seedArray.get(19));
                    TextView textView21 = (TextView)findViewById(R.id.seed_text_21);
                    textView21.setText(seedArray.get(20));
                    TextView textView22 = (TextView)findViewById(R.id.seed_text_22);
                    textView22.setText(seedArray.get(21));
                    TextView textView23 = (TextView)findViewById(R.id.seed_text_23);
                    textView23.setText(seedArray.get(22));
                    TextView textView24 = (TextView)findViewById(R.id.seed_text_24);
                    textView24.setText(seedArray.get(23));
                    runOnUiThread(() -> mLoadingDialog.dismiss());
                }catch (InvalidProtocolBufferException e){
                    e.printStackTrace();
                    mLoadingDialog.dismiss();
                }
            }
        });
    }


    /**
     * 汉：点击copy图标复制地址
     * En：Click copy icon button,duplicate user`s wallet address to clipboard
     * author:Tong ChangHui
     * E-mail:tch081092@gmail.com
     * date:2022-10-08
     */
    @OnClick(R.id.btn_copy)
    public void clickCopy() {
        //接收需要复制成功的提示语
        //Get the notice when you copy success
        String toastString = getResources().getString(R.string.toast_create_copy_success);
        CopyUtil.SelfCopy(CreateWalletStepOneActivity.this,seedsString,toastString);
    }

    /**
     * 点击Back
     * click back button
     */
    @OnClick(R.id.btn_back)
    public void clickBack() {
        finish();
    }

    /**
     * 点击Forward
     * click forward button
     */
    @OnClick(R.id.btn_forward)
    public void clickForward() {
        switchActivity(CreateWalletStepTwoActivity.class);
    }
}
