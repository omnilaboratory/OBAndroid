package com.omni.wallet.utils;

import android.text.InputFilter;
import android.text.LoginFilter;
import android.text.Spanned;

public class SeedFilter extends LoginFilter.PasswordFilterGMail {
    public SeedFilter() {
        super();
    }
    @Override
    public boolean isAllowed(char c) {
        if ('a' <= c && 'z' >= c)
            return true;
        return false;
    }
}
