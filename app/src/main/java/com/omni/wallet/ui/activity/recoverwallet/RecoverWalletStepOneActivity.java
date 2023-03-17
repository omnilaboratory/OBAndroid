package com.omni.wallet.ui.activity.recoverwallet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.template.DisablePasteEditText;
import com.omni.wallet.utils.CheckRules;
import com.omni.wallet.utils.KeyboardScrollView;
import com.omni.wallet.utils.NumberFormatter;
import com.omni.wallet.utils.SeedFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class RecoverWalletStepOneActivity extends AppBaseActivity {
    private static final String TAG = RecoverWalletStepOneActivity.class.getSimpleName();
    private ArrayList<EditText> list = new ArrayList<EditText>();
    private Context ctx = RecoverWalletStepOneActivity.this;

    @BindView(R.id.edit_text_Content)
    LinearLayout editContent;
    @BindView(R.id.recover_wallet_step_one_content)
    LinearLayout pageContent;
    private LinearLayout mOutView;



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
        /*KeyboardStateObserver.getKeyboardStateObserver(this).
            setKeyboardVisibilityListener(new KeyboardStateObserver.OnKeyboardVisibilityListener() {
                @Override
                public void onKeyboardShow() {
                    Toast.makeText(mContext, "键盘弹出", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onKeyboardHide() {
                    Toast.makeText(mContext, "键盘收回", Toast.LENGTH_SHORT).show();
                }
            });*/
        mOutView = findViewById(R.id.edit_text_Content);
        KeyboardScrollView.controlKeyboardLayout(pageContent, mOutView);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        initEditViewForSeeds();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 动态渲染24个输入框
     * Dynamically render 24 input boxes
     */

    private void initEditViewForSeeds() {
        LinearLayout editListContent = findViewById(R.id.edit_text_Content);
        int [] editTextIds = new int[24];
        for (int idx = 0; idx<editTextIds.length; idx++){
            int id = View.generateViewId();
            Log.d(TAG, "initEditViewForSeeds: " + id);
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
                    TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener(){
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            Log.d(TAG, "onEditorAction: " + actionId);
                            if (actionId == EditorInfo.IME_ACTION_NEXT){
                                findViewById(editTextIds[(finalRow - 1)*3 + finalCell]).requestFocus();
                            }
                            return true;
                        }
                    };
                    cellEditText.setOnEditorActionListener(listener);
                }else{
                    cellEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener(){
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE){
                                clickForward();
                            }
                            return true;
                        }
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

    /**
     * 点击Paste
     * click paste button
     */
    @OnClick(R.id.btn_paste)
    public void clickPaste() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = clipboard.getPrimaryClip();
        ClipData.Item item = null;
        if (clipData!=null){
            item = clipData.getItemAt(0);
        }
        
        if (item != null) {
            String seedsString = item.getText().toString();
            boolean checkStringFlag = CheckRules.checkSeedString(seedsString);
            if (checkStringFlag) {
                String[] seedsArr = seedsString.trim().split(" ");
                for (int i = 0; i < seedsArr.length; i++) {
                    EditText et = list.get(i);
                    et.setText(seedsArr[i].trim());
                }
            } else {
                Toast errorToast = Toast.makeText(RecoverWalletStepOneActivity.this, getResources().getString(R.string.toast_recover_paste_error), Toast.LENGTH_LONG);
                errorToast.setGravity(Gravity.TOP, 0, 30);
                errorToast.show();
            }

        } else {
            Toast errorToast = Toast.makeText(RecoverWalletStepOneActivity.this, getResources().getString(R.string.toast_recover_paste_error), Toast.LENGTH_LONG);
            errorToast.setGravity(Gravity.TOP, 0, 30);
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
        String seedsString = "";
        for (int i = 0; i < list.size(); i++) {
            seedsString = seedsString + list.get(i).getText() + " ";
        }
        User.getInstance().setRecoverySeedString(mContext, seedsString);
        /**
         * 使用SharedPreferences 对象，在生成seeds时候将seeds备份到本地文件
         * Use SharedPreferences Class to back up seeds to local file,when create seeds.
         */
        SharedPreferences secretData = ctx.getSharedPreferences("secretData", MODE_PRIVATE);
        SharedPreferences.Editor editor = secretData.edit();
        editor.putString("seeds", seedsString);
        User.getInstance().setInitWalletType(mContext, "recoveryStepOne");
        editor.apply();
        switchActivity(RecoverWalletStepTwoActivity.class);
//        switchActivity(RestoreChannelActivity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
        finish();
    }
}
