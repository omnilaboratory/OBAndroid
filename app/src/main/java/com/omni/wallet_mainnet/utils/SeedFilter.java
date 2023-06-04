package com.omni.wallet_mainnet.utils;

import android.text.LoginFilter;

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
