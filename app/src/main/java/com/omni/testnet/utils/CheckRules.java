package com.omni.testnet.utils;

import java.util.regex.Pattern;

public class CheckRules {

    public static Boolean checkSeedString(String seedsString){
        boolean flag = false;
        String reg = "[a-zA-z\\s]*";
        flag = Pattern.matches(reg,seedsString);
        return flag;
    }
}
