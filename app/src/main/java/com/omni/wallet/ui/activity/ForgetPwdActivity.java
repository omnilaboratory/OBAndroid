package com.omni.wallet.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.template.DisablePasteEditText;
import com.omni.wallet.utils.CheckRules;
import com.omni.wallet.utils.NumberFormatter;

import java.util.ArrayList;

import butterknife.OnClick;

public class ForgetPwdActivity extends AppBaseActivity {
    private ArrayList<EditText> list = new ArrayList<EditText>();
    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

//    @Override
//    protected int titleId() {
//        return R.string.recover_wallet;
//    }

    @Override
    protected int getContentView() {
        return R.layout.activity_recover_wallet_step_one;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        /**
         * 动态渲染24个输入框
         * Dynamically render 24 input boxes
         */
        LinearLayout editListContent = findViewById(R.id.edit_text_Content);


        for (int row = 1; row <= 8; row++) {
            RelativeLayout rowContent = new RelativeLayout(this);
            RelativeLayout.LayoutParams rowContentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rowContentParams.setMargins(0, 0, 0, 5);
            rowContent.setLayoutParams(rowContentParams);
            rowContent.setLongClickable(false);

            LinearLayout rowInner = new LinearLayout(this);
            LinearLayout.LayoutParams rowInnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            rowInner.setOrientation(LinearLayout.HORIZONTAL);
            rowInner.setLayoutParams(rowInnerParams);
            rowInner.setLongClickable(false);
            for (int cell = 1; cell <= 3; cell++) {
                String noNum = NumberFormatter.formatNo(2, (row - 1) * 3 + cell) + ".";
                RelativeLayout cellContent = new RelativeLayout(this);
                LinearLayout.LayoutParams cellContentParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                cellContent.setLayoutParams(cellContentParams);
                cellContent.setLongClickable(false);

                LinearLayout cellInner = new LinearLayout(this);
                LinearLayout.LayoutParams cellInnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cellInner.setOrientation(LinearLayout.HORIZONTAL);
                cellInner.setLayoutParams(cellInnerParams);
                cellInner.setLongClickable(false);

                TextView noText = new TextView(this);
                RelativeLayout.LayoutParams noTextParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                noText.setTextColor(getResources().getColor(R.color.color_white));
                noText.setTextSize(16.0f);
                noText.setText(noNum);
                noText.setLayoutParams(noTextParams);

                EditText cellEditText = new DisablePasteEditText(this);
                LinearLayout.LayoutParams cellEditTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                cellEditText.setBackground(null);
                cellEditText.setHint(getResources().getString(R.string.create_seed_input_hit));
                cellEditText.setTextSize(16.0f);
                cellEditText.setTextColor(getResources().getColor(R.color.color_black));
                cellEditText.setLayoutParams(cellEditTextParams);

                cellInner.addView(noText);
                cellInner.addView(cellEditText);
                cellContent.addView(cellInner);
                rowInner.addView(cellContent);

                list.add(cellEditText);

            }
            rowContent.addView(rowInner);
            editListContent.addView(rowContent);
        }
    }



    /**
     * 点击Paste
     * click paste button
     */
    @OnClick(R.id.btn_paste)
    public void clickPaste() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData =   clipboard.getPrimaryClip();
        ClipData.Item item = clipData.getItemAt(0);

        if(item!=null){

            String seedsString = item.getText().toString();
            boolean checkStringFlag = CheckRules.checkSeedString(seedsString);
            if(checkStringFlag){
                String[] seedsArr = seedsString.split(" ");
                for (int i = 0;i<seedsArr.length;i++){
                    EditText et = list.get(i);
                    et.setText(seedsArr[i]);
                }
            }else{
                Toast errorToast = Toast.makeText(ForgetPwdActivity.this,getResources().getString(R.string.toast_recover_paste_error),Toast.LENGTH_LONG);
                errorToast.setGravity(Gravity.TOP,0,30);
                errorToast.show();
            }

        }else{
            Toast errorToast = Toast.makeText(ForgetPwdActivity.this,getResources().getString(R.string.toast_recover_paste_error),Toast.LENGTH_LONG);
            errorToast.setGravity(Gravity.TOP,0,30);
            errorToast.show();
        }

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
        switchActivity(ForgetPwdNextActivity.class);
    }
}
