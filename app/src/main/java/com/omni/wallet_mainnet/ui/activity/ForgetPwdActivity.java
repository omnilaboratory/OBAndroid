package com.omni.wallet_mainnet.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.wallet_mainnet.R;
import com.omni.wallet_mainnet.base.AppBaseActivity;
import com.omni.wallet_mainnet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet_mainnet.framelibrary.entity.User;
import com.omni.wallet_mainnet.template.DisablePasteEditText;
import com.omni.wallet_mainnet.utils.CheckRules;
import com.omni.wallet_mainnet.utils.KeyboardScrollView;
import com.omni.wallet_mainnet.utils.NumberFormatter;
import com.omni.wallet_mainnet.utils.SeedFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ForgetPwdActivity extends AppBaseActivity {
    private ArrayList<EditText> list = new ArrayList<>();
    String[] seedList;

    @BindView(R.id.forget_pwd_content)
    LinearLayout pageContent;
    private LinearLayout mOutView;
    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_forget_pwd;
    }

    @Override
    protected void initView() {
        mOutView = findViewById(R.id.edit_text_Content);
        KeyboardScrollView.controlKeyboardLayout(pageContent, mOutView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        /*
         * 从xml文件中读取seeds
         * Get seeds form xml file
         */
        String seedsString = User.getInstance().getSeedString(mContext);
        seedList = seedsString.split(" ");
        initEditViewForSeeds();
    }

    /*
     * 动态渲染24个输入框
     * Dynamically render 24 input boxes
     */

    private void initEditViewForSeeds() {
        LinearLayout editListContent = findViewById(R.id.edit_text_Content);
        int [] editTextIds = new int[24];
        for (int idx = 0; idx<editTextIds.length; idx++){
            int id = View.generateViewId();
            editTextIds[idx] = id;
        }

        for (int row = 1; row <= 8; row++) {
            RelativeLayout rowContent = new RelativeLayout(this);
            RelativeLayout.LayoutParams rowContentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rowContentParams.setMargins(0, 0, 0, 5);
            rowContent.setLayoutParams(rowContentParams);
            rowContent.setLongClickable(false);

            LinearLayout rowInner = new LinearLayout(this);
            LinearLayout.LayoutParams rowInnerParams = new LinearLayout.LayoutParams(MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
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
                noText.setTextSize(14.0f);
                noText.setText(noNum);
                noText.setLayoutParams(noTextParams);

                EditText cellEditText = new DisablePasteEditText(this);
                LinearLayout.LayoutParams cellEditTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                cellEditText.setBackground(null);
                cellEditText.setHint(getResources().getString(R.string.create_seed_input_hit));
                cellEditText.setTextColor(getResources().getColor(R.color.color_white));
                cellEditText.setHintTextColor(getResources().getColor(R.color.color_white));
                cellEditText.setTextSize(14.0f);
                cellEditText.setLayoutParams(cellEditTextParams);
                cellEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                SeedFilter seedFilter = new SeedFilter();
                cellEditText.setFilters(new InputFilter[]{seedFilter});
                int d = (row - 1)*3 + cell;
                int id = editTextIds[(row - 1)*3 + cell - 1];
                cellEditText.setId(id);
                if (d < 24){
                    cellEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    cellEditText.setNextFocusUpId(editTextIds[(row - 1)*3 + cell]);
                    int finalCell = cell;
                    int finalRow = row;
                    TextView.OnEditorActionListener listener = (v, actionId, event) -> {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            findViewById(editTextIds[(finalRow - 1)*3 + finalCell]).requestFocus();
                        }
                        return true;
                    };
                    cellEditText.setOnEditorActionListener(listener);
                }else{
                    cellEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    TextView.OnEditorActionListener listener = (v, actionId, event) -> {
                        if (actionId == EditorInfo.IME_ACTION_DONE){
                            clickForward();
                        }
                        return true;
                    };
                    cellEditText.setOnEditorActionListener(listener);
                }

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

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 点击Paste
     * click paste button
     */
    @OnClick(R.id.btn_paste)
    public void clickPaste() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData =   clipboard.getPrimaryClip();
        ClipData.Item item = null;
        if (clipData!=null){
            item = clipData.getItemAt(0);
        }
        if(item!=null){
            String seedsString = item.getText().toString();
            boolean checkStringFlag = CheckRules.checkSeedString(seedsString);
            if(checkStringFlag){
                String[] seedsArr = seedsString.trim().split(" ");
                for (int i = 0;i<seedsArr.length;i++){
                    EditText et = list.get(i);
                    et.setText(seedsArr[i].trim());
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
        switchActivityFinish(UnlockActivity.class);
    }

    /**
     * 点击Forward
     * click forward button
     */
    @OnClick(R.id.btn_forward)
    public void clickForward() {
        boolean checkResult = true;
        for (int i = 0; i < list.size(); i++) {
            String inputItemText = list.get(i).getText().toString().trim();
            String seed_no = "seed_" + i;
            Log.d(seed_no, seedList[i]);
            if (inputItemText.equals(seedList[i])) {
                Log.d(Integer.toString(i), inputItemText);
            } else {
                checkResult = false;
                String toastTextHead = getResources().getString(R.string.toast_check_seeds_wrong_head);
                String toastTextEnd = getResources().getString(R.string.toast_check_seeds_wrong_end);
                String toastText = toastTextHead + (i + 1) + "" + toastTextEnd;
                Toast checkWrongToast = Toast.makeText(ForgetPwdActivity.this,toastText,Toast.LENGTH_LONG);
                checkWrongToast.setGravity(Gravity.TOP,0,40);
                checkWrongToast.show();
                Log.d(seed_no, "wrong");
                break;
            }
        }
        if(checkResult){
            switchActivity(ForgetPwdNextActivity.class);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
        finish();
    }

}
