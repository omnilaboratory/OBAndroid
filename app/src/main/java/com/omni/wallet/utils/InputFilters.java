package com.omni.wallet.utils;

import android.text.InputFilter;
import android.text.LoginFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFilters {
    /**
     * @param editText The editText will set filter
     * @description set editText unable to input "[].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？" and space and Newline character and Page feed and Carriage return
     */
    public static void setPassEditTextInhibitInputSpeChat(EditText editText){
        InputFilter passwordInputFilter = new InputFilter(){
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
                String speChat="[\\[\\]\\s\\n\\r\\f.<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find())return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{passwordInputFilter});
    }

    public static void setPassEditTextInputChat(EditText editText){
        InputFilter passwordInputFilter = (source, start, end, dest, dstart, dend) -> {
            String speChat = "[a-zA-Z\\d!@#$%^&*()\\[\\]{}|;:,.<>/?]";
            Pattern pattern = Pattern.compile(speChat);
            Matcher matcher = pattern.matcher(source.toString());
            if (matcher.find()){
                return null;
            }else{
                return "";
            }
        };
        editText.setFilters(new InputFilter[]{passwordInputFilter});
    }

}
