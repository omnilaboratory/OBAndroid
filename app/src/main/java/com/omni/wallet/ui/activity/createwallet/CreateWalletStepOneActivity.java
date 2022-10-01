package com.omni.wallet.ui.activity.createwallet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.utils.NumberFormatter;


import butterknife.OnClick;

public class CreateWalletStepOneActivity extends AppBaseActivity {
    String[] seedArray = {"about", "gun", "blind", "method", "addict", "scrub", "red", "risbon", "such", "kitchen", "prevent", "gap", "super", "risk", "survey", "cable", "image", "weather", "prize", "item", "between", "moral", "worth", "must"};
    String seedsString = "";
    Context ctx = CreateWalletStepOneActivity.this;

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
        for (int idx = 0;idx<seedArray.length;idx++){
            seedsString = seedsString + seedArray[idx]+ " ";
        }
        /**
         * 使用SharedPreferences 对象，在生成seeds时候将
         * seeds备份到本地文件
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
            rowContent.setId(rowNum);

            LinearLayout rowInnerContent = new LinearLayout(this);
            LinearLayout.LayoutParams rowInnerContetnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowInnerContent.setOrientation(LinearLayout.HORIZONTAL);
            rowInnerContetnParams.setMargins(0, 0, 0, 60);
            rowInnerContent.setBaselineAligned(false);
            rowInnerContent.setLayoutParams(rowInnerContetnParams);
            rowInnerContent.setId(rowNum);

            for (int itemNum = 1; itemNum <= 3; itemNum++) {
                int noNum = (rowNum - 1) * 3 + itemNum;

                LinearLayout itemInnerContent = new LinearLayout(this);
                LinearLayout.LayoutParams itemInnerContetnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                itemInnerContent.setOrientation(LinearLayout.HORIZONTAL);
                itemInnerContent.setBaselineAligned(false);
                itemInnerContent.setLayoutParams(itemInnerContetnParams);
                itemInnerContent.setId(noNum);

                TextView itemNoWidget = new TextView(this);
                RelativeLayout.LayoutParams itemNoWidgetParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                String itemNoString = NumberFormatter.formatNo(2, noNum) + ".";
                itemNoWidget.setText(itemNoString);
                itemNoWidget.setTextColor(getResources().getColor(R.color.color_white));
                itemNoWidget.setTextSize(20.0f);
                itemNoWidget.setLayoutParams(itemNoWidgetParams);
                itemNoWidget.setId(noNum);

                TextView itemSeeWidget = new TextView(this);
                RelativeLayout.LayoutParams itemSeedWidgetParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                itemSeeWidget.setText(seedArray[noNum - 1]);
                itemSeeWidget.setTextColor(getResources().getColor(R.color.color_black));
                itemSeeWidget.setTextSize(20.0f);
                itemSeeWidget.setLayoutParams(itemSeedWidgetParams);
                itemSeeWidget.setId(noNum);

                itemInnerContent.addView(itemNoWidget);
                itemInnerContent.addView(itemSeeWidget);
                rowInnerContent.addView(itemInnerContent);
            }

            rowContent.addView(rowInnerContent);
            linearLayout.addView(rowContent);
        }
    }


    /**
     * 点击Copy
     */
    @OnClick(R.id.btn_copy)
    public void clickCopy() {
        ClipboardManager cm =(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("text", seedsString);
        cm.setPrimaryClip(mClipData);
        if(!mClipData.toString().isEmpty()){
            String toastString = getResources().getString(R.string.toast_create_copy_success);
            Toast copySuccessToast = Toast.makeText(CreateWalletStepOneActivity.this,toastString,Toast.LENGTH_LONG);
            copySuccessToast.setGravity(Gravity.TOP,0,30);
            copySuccessToast.show();
        }

    }

    /**
     * 点击Back
     */
    @OnClick(R.id.btn_back)
    public void clickBack() {
        finish();
    }

    /**
     * 点击Forward
     */
    @OnClick(R.id.btn_forward)
    public void clickForward() {
        switchActivity(CreateWalletStepTwoActivity.class);
    }
}
