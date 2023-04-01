package com.omni.wallet.utils;

public class CheckInputRules {
    private static final String TAG = CheckInputRules.class.getSimpleName();
    static public int checkPwd(String password) {
        String tempPassword = password;
        int checkValue = 0;
        //密码为空或者长度小于8位则返回false(If password is null or length less than 8 then return false! )
        if (password == null || password.length() < 8) return -1;
        boolean isREG_NUMBER = false;
        boolean isREG_LOWERCASE = false;
        boolean isREG_UPPERCASE = false;
        boolean isREG_SYMBOL = false;
        for (int i = 0; i < password.length(); i++) {
            if (checkValue<4){
                char c = password.charAt(i);
                if(isREG_NUMBER){
                    if(isREG_LOWERCASE){
                        if(isREG_UPPERCASE){
                            if(!isREG_SYMBOL){
                                if(charIsAllowedSimple(c)){
                                    isREG_SYMBOL = charIsLowerCases(c);
                                    checkValue ++;
                                }
                            }
                        }else{
                            if(charIsUpperCases(c)){
                                isREG_UPPERCASE = charIsLowerCases(c);
                                checkValue ++;
                            }else{
                                if(charIsAllowedSimple(c)){
                                    isREG_SYMBOL = charIsLowerCases(c);
                                    checkValue ++;
                                }
                            }
                        }
                    }else{
                        if(charIsLowerCases(c)){
                            isREG_LOWERCASE = charIsLowerCases(c);
                            checkValue ++;
                        }else {
                            if(charIsUpperCases(c)){
                                isREG_UPPERCASE = charIsLowerCases(c);
                                checkValue ++;
                            }else{
                                if(charIsAllowedSimple(c)){
                                    isREG_SYMBOL = charIsLowerCases(c);
                                    checkValue ++;
                                }
                            }
                        }
                    }
                }else{
                    if(charIsNum(c)){
                        isREG_NUMBER = charIsNum(c);
                        checkValue ++;
                    }else{
                        if(charIsLowerCases(c)){
                            isREG_LOWERCASE = charIsLowerCases(c);
                            checkValue ++;
                        }else {
                            if(charIsUpperCases(c)){
                                isREG_UPPERCASE = charIsLowerCases(c);
                                checkValue ++;
                            }else{
                                if(charIsAllowedSimple(c)){
                                    isREG_SYMBOL = charIsLowerCases(c);
                                    checkValue ++;
                                }
                            }
                        }
                    }

                }
            }else {
                break;
            }
        }
        return checkValue;
    }

    private static boolean charIsNum(char c){
        return '0' <= c && '9' >= c;
    }

    private static boolean charIsLowerCases(char c) {
        return 'a' <= c && 'z' >= c;
    }

    private static boolean charIsUpperCases(char c) {
        return 'a' <= c && 'z' >= c;
    }

    private static boolean charIsAllowedSimple(char c){
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
