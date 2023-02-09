package com.omni.testnet.baselibrary.view.editText;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * 文本变化监听
 */

public class DefaultTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (start >= 0 && count >= 1) {
            onHasContent(count);
        }
        if (before >= 1 && start == 0) {
            onNoContent();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    protected void onHasContent(int count) {
    }

    protected void onNoContent() {
    }

}
