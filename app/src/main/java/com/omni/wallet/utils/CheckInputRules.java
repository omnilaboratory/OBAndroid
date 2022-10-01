package com.omni.wallet.utils;

public class CheckInputRules {
    static public int checkePwd(String password){
        int checkValue = 0;
        //数字
        String REG_NUMBER = ".*\\d+.*";
        //小写字母
        String REG_UPPERCASE = ".*[A-Z]+.*";
        //大写字母
        String REG_LOWERCASE = ".*[a-z]+.*";
        //特殊符号
        String REG_SYMBOL = ".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*";
        //密码为空或者长度小于8位则返回false
        if (password == null || password.length() <8 ) return -1;
        int j = 0;
        if (password.matches(REG_NUMBER)) checkValue++;
        if (password.matches(REG_LOWERCASE))checkValue++;
        if (password.matches(REG_UPPERCASE)) checkValue++;
        if (password.matches(REG_SYMBOL)) checkValue++;
        return checkValue;

    }
}
