package com.omni.wallet_mainnet.utils;

import java.util.regex.Pattern;

public class CheckRules {

    public static Boolean checkSeedString(String seedsString){
        boolean flag = false;
        String reg = "[a-zA-Z\\s]*";
        flag = Pattern.matches(reg,seedsString);
        return flag;
    }
}
