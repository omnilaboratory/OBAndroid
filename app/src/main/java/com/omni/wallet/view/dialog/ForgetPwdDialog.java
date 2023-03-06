package com.omni.wallet.view.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.dialog.AlertDialog;
import com.omni.wallet.template.DisablePasteEditText;
import com.omni.wallet.utils.CheckRules;
import com.omni.wallet.utils.NumberFormatter;
import com.omni.wallet.utils.SeedFilter;

import java.util.ArrayList;

public class ForgetPwdDialog {
    public static final String TAG = ForgetPwdDialog.class.getSimpleName();

    private Context mContext;
    private ArrayList<EditText> list = new ArrayList<EditText>();
    private AlertDialog mAlertDialog;
    String[] seedList;

    public ForgetPwdDialog(Context context){
        this.mContext = context;
    }

    public void show(){
        if (mAlertDialog == null){
            Log.e(TAG, "show" );
            mAlertDialog = new AlertDialog.Builder(mContext, R.style.dialog_translucent_theme)
                    .setContentView(R.layout.layout_dialog_forget_password)
                    .setAnimation(R.style.popup_anim_style)
                    .fullWidth()
                    .fullHeight()
                    .create();
        }
        LinearLayout editListContent = mAlertDialog.findViewById(R.id.edit_text_Content);
        for (int row = 1; row <= 8; row++) {
            RelativeLayout rowContent = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams rowContentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rowContentParams.setMargins(0, 0, 0, 5);
            rowContent.setLayoutParams(rowContentParams);
            rowContent.setLongClickable(false);

            LinearLayout rowInner = new LinearLayout(mContext);
            LinearLayout.LayoutParams rowInnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            rowInner.setOrientation(LinearLayout.HORIZONTAL);
            rowInner.setLayoutParams(rowInnerParams);
            rowInner.setLongClickable(false);
            for (int cell = 1; cell <= 3; cell++) {
                String noNum = NumberFormatter.formatNo(2, (row - 1) * 3 + cell) + ".";
                RelativeLayout cellContent = new RelativeLayout(mContext);
                LinearLayout.LayoutParams cellContentParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                cellContent.setLayoutParams(cellContentParams);
                cellContent.setLongClickable(false);

                LinearLayout cellInner = new LinearLayout(mContext);
                LinearLayout.LayoutParams cellInnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cellInner.setOrientation(LinearLayout.HORIZONTAL);
                cellInner.setLayoutParams(cellInnerParams);
                cellInner.setLongClickable(false);

                TextView noText = new TextView(mContext);
                RelativeLayout.LayoutParams noTextParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                noText.setTextColor(mContext.getResources().getColor(R.color.color_white));
                noText.setTextSize(14.0f);
                noText.setText(noNum);
                noText.setLayoutParams(noTextParams);

                EditText cellEditText = new DisablePasteEditText(mContext);
                LinearLayout.LayoutParams cellEditTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                cellEditText.setBackground(null);
                cellEditText.setHint(mContext.getResources().getString(R.string.create_seed_input_hit));
                cellEditText.setHintTextColor(mContext.getResources().getColor(R.color.color_white));
                cellEditText.setTextSize(14.0f);
                cellEditText.setTextColor(mContext.getResources().getColor(R.color.color_white));
                cellEditText.setLayoutParams(cellEditTextParams);
                cellEditText.setMaxLines(1);
                cellEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                SeedFilter seedFilter = new SeedFilter();
                cellEditText.setFilters(new InputFilter[]{seedFilter});

                cellInner.addView(noText);
                cellInner.addView(cellEditText);
                cellContent.addView(cellInner);
                rowInner.addView(cellContent);

                list.add(cellEditText);

            }
            rowContent.addView(rowInner);
            editListContent.addView(rowContent);
        }
        mAlertDialog.findViewById(R.id.btn_unlock).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                clickPaste();
            }
        });
        mAlertDialog.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBack();
            }
        });
    }

    public void clickBack() {
        release();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void clickPaste() {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
        ClipData clipData =   clipboard.getPrimaryClip();
        ClipData.Item item = clipData.getItemAt(0);
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
                Toast errorToast = Toast.makeText(mContext,mContext.getResources().getString(R.string.toast_recover_paste_error),Toast.LENGTH_LONG);
                errorToast.setGravity(Gravity.TOP,0,30);
                errorToast.show();
            }

        }else{
            Toast errorToast = Toast.makeText(mContext,mContext.getResources().getString(R.string.toast_recover_paste_error),Toast.LENGTH_LONG);
            errorToast.setGravity(Gravity.TOP,0,30);
            errorToast.show();
        }

    }

    public void clickForward() {
        Boolean checkResult = true;
        for (int i = 0; i < list.size(); i++) {
            String inputItemText = list.get(i).getText().toString().trim();
            String seed_no = "seed_" + Integer.toString(i);
            Log.d(seed_no, seedList[i]);
            if (inputItemText.equals(seedList[i])) {
                Log.d(Integer.toString(i), inputItemText);
            } else {
                checkResult = false;
                String toastTextHead = mContext.getResources().getString(R.string.toast_check_seeds_wrong_head);
                String toastTextEnd = mContext.getResources().getString(R.string.toast_check_seeds_wrong_end);
                String toastText = toastTextHead + Integer.toString(i+1) + "" + toastTextEnd;
                Toast checkWrongToast = Toast.makeText(mContext,toastText,Toast.LENGTH_LONG);
                checkWrongToast.setGravity(Gravity.TOP,0,40);
                checkWrongToast.show();
                Log.d(seed_no, "wrong");
                break;
            }
        }
        if(checkResult){

        }
    }

    public void release(){
        if (mAlertDialog!=null){
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }

}
