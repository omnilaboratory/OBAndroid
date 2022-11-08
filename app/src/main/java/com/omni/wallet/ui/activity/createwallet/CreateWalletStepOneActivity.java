package com.omni.wallet.ui.activity.createwallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.utils.NumberFormatter;
import com.omni.wallet.view.dialog.LoadingDialog;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import lndmobile.Callback;
import lndmobile.Lndmobile;
import lnrpc.LightningOuterClass;
import lnrpc.Walletunlocker;

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
    
    private void initTvForSeeds(){
        System.out.println("--------------------------------Start init tv for seeds--------------------------------");
        
        
        
        for (int rowNum = 1; rowNum <= 8; rowNum++) {
            System.out.println("--------------------------------row number:"+rowNum+"--------------------------------");
            RelativeLayout rowContent = new RelativeLayout(this);
            RelativeLayout.LayoutParams rowContentLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rowContent.setLayoutParams(rowContentLayoutParams);

            LinearLayout rowInnerContent = new LinearLayout(this);
            LinearLayout.LayoutParams rowInnerContentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowInnerContent.setOrientation(LinearLayout.HORIZONTAL);
            rowInnerContent.setPadding(0,20,0,30);
            rowInnerContent.setBaselineAligned(false);
            rowInnerContent.setLayoutParams(rowInnerContentParams);

            for (int itemNum = 1; itemNum <= 3; itemNum++) {
                
                int noNum = (rowNum - 1) * 3 + itemNum;
                System.out.println("--------------------------------item number:"+noNum+"--------------------------------");
                LinearLayout itemInnerContent = new LinearLayout(this);
                LinearLayout.LayoutParams itemInnerContentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                itemInnerContent.setOrientation(LinearLayout.HORIZONTAL);
                itemInnerContent.setBaselineAligned(false);
                itemInnerContent.setLayoutParams(itemInnerContentParams);

                TextView itemNoWidget = new TextView(this);
                RelativeLayout.LayoutParams itemNoWidgetParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                String itemNoString = NumberFormatter.formatNo(2, noNum) + ".";
                itemNoWidget.setText(itemNoString);
                itemNoWidget.setTextColor(getResources().getColor(R.color.color_white));
                itemNoWidget.setTextSize(20.0f);
                itemNoWidget.setLayoutParams(itemNoWidgetParams);

                TextView itemSeeWidget = new TextView(this);
                RelativeLayout.LayoutParams itemSeedWidgetParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                itemSeeWidget.setText(seedArray.get(noNum-1));
                itemSeeWidget.setTextColor(getResources().getColor(R.color.color_black));
                itemSeeWidget.setTextSize(20.0f);
                itemSeeWidget.setLayoutParams(itemSeedWidgetParams);

                itemInnerContent.addView(itemNoWidget);
                itemInnerContent.addView(itemSeeWidget);
                rowInnerContent.addView(itemInnerContent);
                System.out.println("--------------------------------item number:"+noNum+"over--------------------------------");
            }
            
            rowContent.addView(rowInnerContent);
            System.out.println("--------------------------------row number:"+rowNum+"rowInner add over--------------------------------");
            System.out.println("--------------------------------row number:"+rowNum+lvSeedContent.toString()+"rowInner add start--------------------------------");
            lvSeedContent.addView(rowContent);
            System.out.println("--------------------------------row number:"+rowNum+lvSeedContent.toString()+"rowInner add over--------------------------------");
            System.out.println("--------------------------------row number:"+rowNum+"over--------------------------------");
        }
        System.out.println("--------------------------------End init tv for seeds--------------------------------");
    }

    private void createSeeds() {
        Log.e("create error:","123456789");
        mLoadingDialog.show();
        Walletunlocker.GenSeedRequest genSeedRequest = Walletunlocker.GenSeedRequest.newBuilder().build();
        Lndmobile.genSeed(genSeedRequest.toByteArray(), new Callback() {
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
                    mLoadingDialog.dismiss();
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
