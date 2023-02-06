package com.omni.wallet.utils;

import android.text.LoginFilter;

public class PasswordFilter extends LoginFilter.PasswordFilterGMail {
    public PasswordFilter() {
        super();
    }

    @Override
    public boolean isAllowed(char c) {
        if ('0' <= c && '9' >= c)
            return true;
        if ('a' <= c && 'z' >= c)
            return true;
        if ('A' <= c && 'Z' >= c)
            return true;
        if ('~' == c)
            return true;
        if ('`' == c)
            return true;
        if ('!' == c)
            return true;
        if ('@' == c)
            return true;
        if ('#' == c)
            return true;
        if ('$' == c)
            return true;
        if ('%' == c)
            return true;
        if ('^' == c)
            return true;
        if ('*' == c)
            return true;
        if ('(' == c)
            return true;
        if (')' == c)
            return true;
        if ('-' == c)
            return true;
        if ('_' == c)
            return true;
        if ('=' == c)
            return true;
        if ('+' == c)
            return true;
        if ('[' == c)
            return true;
        if ('{' == c)
            return true;
        if (']' == c)
            return true;
        if ('}' == c)
            return true;
        if ('|' == c)
            return true;
        if (';' == c)
            return true;
        if (':' == c)
            return true;
        if (',' == c)
            return true;
        if ('<' == c)
            return true;
        if ('.' == c)
            return true;
        if ('>' == c)
            return true;
        if ('/' == c)
            return true;
        if ('?' == c)
            return true;
        return false;
    }
}
