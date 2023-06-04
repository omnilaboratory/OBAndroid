package com.omni.wallet_mainnet.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class CopyUtil {
    static public void SelfCopy(Context context,String copyText,String toastMsg){

        //将地址复制到粘贴板
        //Copy the address to clipboard
        ClipboardManager cm =(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("text", copyText);
        cm.setPrimaryClip(mClipData);
        System.out.println(mClipData.toString());

        //判断粘贴板中是否已经有字符串，如果有则弹出提示，已经将地址复制到粘贴板
        //Check is there any string in the clipboard.If clipboard is not empty then give alert: Already copy the address to clipboard.
        if(!mClipData.toString().isEmpty()){
            Toast copySuccessToast = Toast.makeText(context,toastMsg,Toast.LENGTH_LONG);
            copySuccessToast.setGravity(Gravity.TOP,0,30);
            copySuccessToast.show();
        }
    }
}
