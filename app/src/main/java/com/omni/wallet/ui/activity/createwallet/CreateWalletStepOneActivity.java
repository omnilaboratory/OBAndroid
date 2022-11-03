package com.omni.wallet.ui.activity.createwallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.utils.CopyUtil;
import com.omni.wallet.utils.NumberFormatter;
import com.omni.wallet.view.dialog.LoadingDialog;


import butterknife.OnClick;

public class CreateWalletStepOneActivity extends AppBaseActivity {
    String[] seedArray = {"about", "gun", "blind", "method", "addict", "scrub", "red", "risbon", "such", "kitchen", "prevent", "gap", "super", "risk", "survey", "cable", "image", "weather", "prize", "item", "between", "moral", "worth", "must"};
    String seedsString = "";
    Context ctx = CreateWalletStepOneActivity.this;
    LoadingDialog mLoadingDialog;

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

    }

    @Override
    protected void initData() {
        createSeeds();
        initTvForSeeds();
    }
    
    private void initTvForSeeds(){
        for (int idx = 0;idx<seedArray.length;idx++){
            seedsString = seedsString + seedArray[idx]+ " ";
        }
        /**
         * 使用SharedPreferences 对象，在生成seeds时候将seeds备份到本地文件
         * Use SharedPreferences Class to back up seeds to local file,when create seeds.
         */
        SharedPreferences secretData = ctx.getSharedPreferences("secretData", MODE_PRIVATE);
        SharedPreferences.Editor editor = secretData.edit();
        editor.putString("seeds",seedsString);
        editor.commit();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.seed_content);
        for (int rowNum = 1; rowNum <= 8; rowNum++) {
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
                itemSeeWidget.setText(seedArray[noNum - 1]);
                itemSeeWidget.setTextColor(getResources().getColor(R.color.color_black));
                itemSeeWidget.setTextSize(20.0f);
                itemSeeWidget.setLayoutParams(itemSeedWidgetParams);

                itemInnerContent.addView(itemNoWidget);
                itemInnerContent.addView(itemSeeWidget);
                rowInnerContent.addView(itemInnerContent);
            }

            rowContent.addView(rowInnerContent);
            linearLayout.addView(rowContent);
        }
    }

    private void createSeeds() {
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
