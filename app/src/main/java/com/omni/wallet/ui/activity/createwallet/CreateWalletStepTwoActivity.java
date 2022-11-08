package com.omni.wallet.ui.activity.createwallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.template.DisablePasteEditText;
import com.omni.wallet.utils.NumberFormatter;

import java.util.ArrayList;

import butterknife.OnClick;

public class CreateWalletStepTwoActivity extends AppBaseActivity {
    private ArrayList<EditText> list = new ArrayList<>();

    Context ctx = CreateWalletStepTwoActivity.this;
    String[] seedList;


    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_create_wallet_step_two;
    }

    @Override
    protected void initView() {

    }


    @Override
    protected void initData() {
        initEvForSeeds();
    }
    
    @SuppressLint("LongLogTag")
    private void initEvForSeeds(){
        /**
         * 从xml文件中读取seeds
         * Get seeds form xml file
         */
        SharedPreferences secretData = ctx.getSharedPreferences("secretData", MODE_PRIVATE);
        String seedsString = secretData.getString("seeds", "none");
        seedList = seedsString.split(" ");

        /**
         * 动态渲染24个输入框
         * Dynamically render 24 input boxes
         */
        LinearLayout editListContent = findViewById(R.id.edit_text_Content);


        for (int row = 1; row <= 8; row++) {
            RelativeLayout rowContent = new RelativeLayout(this);
            RelativeLayout.LayoutParams rowContentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rowContent.setLayoutParams(rowContentParams);

            LinearLayout rowInner = new LinearLayout(this);
            LinearLayout.LayoutParams rowInnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            rowInnerParams.setMargins(0,0,0,10);
            rowInner.setOrientation(LinearLayout.HORIZONTAL);
            rowInner.setLayoutParams(rowInnerParams);
            for (int cell = 1; cell <= 3; cell++) {
                String noNum = NumberFormatter.formatNo(2, (row - 1) * 3 + cell) + ".";
                RelativeLayout cellContent = new RelativeLayout(this);
                LinearLayout.LayoutParams cellContentParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                cellContent.setLayoutParams(cellContentParams);

                LinearLayout cellInner = new LinearLayout(this);
                LinearLayout.LayoutParams cellInnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cellInner.setOrientation(LinearLayout.HORIZONTAL);
                cellInner.setLayoutParams(cellInnerParams);

                TextView noText = new TextView(this);
                RelativeLayout.LayoutParams noTextParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                noText.setTextColor(getResources().getColor(R.color.color_white));
                noText.setTextSize(20.0f);
                noText.setText(noNum);
                noText.setLayoutParams(noTextParams);

                EditText cellEditText = new DisablePasteEditText(this);
                LinearLayout.LayoutParams cellEditTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                cellEditText.setBackground(null);
                cellEditText.setHint(getResources().getString(R.string.create_seed_input_hit));
                cellEditText.setTextSize(16.0f);
                cellEditText.setTextColor(getResources().getColor(R.color.color_black));
                cellEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
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
        Boolean checkResult = true;
        for (int i = 0; i < list.size(); i++) {
            String inputItemText = list.get(i).getText().toString();
            String seed_no = "seed_" + Integer.toString(i);
            Log.d(seed_no, seedList[i]);
            if (inputItemText.equals(seedList[i])) {
                Log.d(Integer.toString(i), inputItemText);
            } else {
                checkResult = false;
                String toastTextHead = getResources().getString(R.string.toast_check_seeds_wrong_head);
                String toastTextEnd = getResources().getString(R.string.toast_check_seeds_wrong_end);
                String toastText = toastTextHead + Integer.toString(i+1) + "" + toastTextEnd;
                Toast checkWrongToast = Toast.makeText(CreateWalletStepTwoActivity.this,toastText,Toast.LENGTH_LONG);
                checkWrongToast.setGravity(Gravity.TOP,0,40);
                checkWrongToast.show();
                Log.d(seed_no, "wrong");
                break;
            }

        }
        if (checkResult) {
            switchActivity(CreateWalletStepThreeActivity.class);
        }

    }


    /**
     *测试专用，点击test_enter所在view将自动填写所有seed，为了方便测试，完成后删除下方代码
     * Code for test,when click test enter view while auto-fill all seed.Before public apk delete follow code.
     */
    @OnClick(R.id.description_text_test)
    public void clickForEnterSeeds(){
        
        for (int i = 0; i < list.size(); i++) {
            Log.e("seed item",seedList[i]);
            list.get(i).setText(seedList[i]);
        }
    }

    @OnClick(R.id.description_text_clear)
    public void clickForClearSeeds(){
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setText("");
        }
    }

}
