package com.omni.wallet.utils;

public class CheckInputRules {
    static public int checkPwd(String password) {
        int checkValue = 0;
        //数字(Numbers)
        String REG_NUMBER = ".*\\d+.*";
        //小写字母(Lower letter)
        String REG_UPPERCASE = ".*[A-Z]+.*";
        //大写字母(Upper letter)
        String REG_LOWERCASE = ".*[a-z]+.*";
        //特殊符号(Particular Symbol)
        String REG_SYMBOL = ".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*";
        //密码为空或者长度小于8位则返回false(If password is null or length less than 8 then return false! )
        if (password == null || password.length() < 8) return -1;
        if (password.matches(REG_NUMBER)) {
            checkValue++;
            if (password.matches(REG_LOWERCASE)) {
                checkValue++;
                if (password.matches(REG_UPPERCASE)) {
                    checkValue++;
                    if (password.matches(REG_SYMBOL)) {
                        checkValue++;
                    }
                }
            }
        }
        return checkValue;
    }
}
