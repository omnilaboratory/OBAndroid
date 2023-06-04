package com.omni.wallet_mainnet.baselibrary.view.editText;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.omni.wallet_mainnet.baselibrary.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 根据多个EditText是否有内容控制Button是否可用的TextWatcher
 */

public class MultiEditTextWatcher implements TextWatcher {
    private static final String TAG = MultiEditTextWatcher.class.getSimpleName();
    private View mBtnView;
    private List<EditText> mEtList = new ArrayList<>();


    /**
     * 设置需要添加监听的EditText
     */
    public void setEtList(List<EditText> etList) {
        this.mEtList = etList;
        if (mEtList == null) {
            mEtList = new ArrayList<>();
        }
        // 遍历集合，添加监听
        for (EditText editText : mEtList) {
            editText.addTextChangedListener(this);
        }
    }

    /**
     * 设置需要添加监听的EditText
     */
    public void setEditTexts(EditText... etList) {
        this.mEtList = Arrays.asList(etList);
        // 遍历集合，添加监听
        for (EditText editText : mEtList) {
            editText.addTextChangedListener(this);
        }
    }

    public void setBtnView(View btnView) {
        this.mBtnView = btnView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mBtnView != null) {
            mBtnView.setEnabled(false);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int flag = mEtList.size();
        for (EditText editText : mEtList) {
            if (StringUtils.isEmpty(editText.getText().toString())) {
                flag--;
                break;
            }
        }
        if (flag == mEtList.size()) {
            mBtnView.setEnabled(true);
        } else {
            mBtnView.setEnabled(false);
        }
    }

    public void release() {
        for (EditText editText : mEtList) {
            editText.removeTextChangedListener(this);
        }
    }
}
