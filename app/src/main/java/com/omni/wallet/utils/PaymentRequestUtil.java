package com.omni.wallet.utils;

import android.support.annotation.NonNull;

import com.omni.wallet.baselibrary.utils.LogUtils;

import java.util.Arrays;

public class PaymentRequestUtil {
    private static final String LOG_TAG = PaymentRequestUtil.class.getName();

    public static String getMemo(@NonNull String paymentRequest) {
        try {
            // Extract the tagged data fields in the bech32 encoded string.
            // Start: position of bech32 separator "1" + 1 + timestamp (7)
            // End: length - checksum (6) - signature (104)
            String taggedPart = paymentRequest.substring(paymentRequest.lastIndexOf("1") + 1 + 7, paymentRequest.length() - 6 - 104);

            byte[] decodedBech32 = Bech32.bech32Decode(paymentRequest, false).second;

            // Extract tagged data fields in decoded byte array
            byte[] decodedTaggedPart = Arrays.copyOfRange(decodedBech32, 7, decodedBech32.length - 104);

            // Find start and end index of memo data
            int start = 0, end = 0;
            int index = 0;
            while (index < taggedPart.length()) {
                boolean isMemo = taggedPart.charAt(index) == 'd';
                int currentDataFieldLength = getTaggedDataFieldLength(taggedPart.substring(index + 1, index + 3));
                if (isMemo) {
                    start = index + 3;
                    end = index + 3 + currentDataFieldLength;
                    break;
                }
                index = index + 3 + currentDataFieldLength;
            }

            if (start != 0) {
                byte[] decodedMemoPart = Arrays.copyOfRange(decodedTaggedPart, start, end);
                return new String(Bech32.regroupBytes(decodedMemoPart));
            } else {
                return null;
            }
        } catch (Exception e) {
            LogUtils.e(LOG_TAG, "Error while trying to read memo of " + paymentRequest);
            e.printStackTrace();
            return null;
        }
    }

    private static int getTaggedDataFieldLength(String lengthString) {
        return Bech32.CHARSET.indexOf(lengthString.charAt(0)) * 32 + Bech32.CHARSET.indexOf(lengthString.charAt(1));
    }

}
