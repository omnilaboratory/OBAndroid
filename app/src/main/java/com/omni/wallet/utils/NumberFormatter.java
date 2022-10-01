package com.omni.wallet.utils;

public class NumberFormatter {
    static public String formatNo(int formatLength, int formatNumber)  {
        String formatedString="";
        String toUseString = Integer.toString(formatNumber);
        int toUseStringLength = toUseString.length();
        if(toUseStringLength<formatLength){
            String zeroStr="";
            for (int i=0;i<formatLength-toUseStringLength;i++){
                zeroStr+="0";
            }
            formatedString = zeroStr + toUseString;
        }else{
            formatedString = toUseString;
        }
        return formatedString;
    }
}
