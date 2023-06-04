package com.omni.wallet_mainnet.lightning;

import android.support.annotation.NonNull;

import com.omni.wallet_mainnet.utils.UtilFunctions;

public class LightningParser {

    private static int NODE_URI_MIN_LENGTH = 66;

    public static LightningNodeUri parseNodeUri(@NonNull String uri) {
        if (uri.isEmpty() || uri.length() < NODE_URI_MIN_LENGTH) {
            return null;
        }

        if (uri.length() == NODE_URI_MIN_LENGTH) {
            // PubKey only
            if (UtilFunctions.isHex(uri)) {
                return new LightningNodeUri.Builder().setPubKey(uri).build();
            } else {
                return null;
            }
        }

        if (!(uri.charAt(NODE_URI_MIN_LENGTH) == '@')) {
            // longer and no @ after PubKey. Something is wrong.
            return null;
        }

        String[] parts = uri.split("@");

        if (parts.length != 2) {
            return null;
        }

        if (UtilFunctions.isHex(parts[0])) {
            return new LightningNodeUri.Builder().setPubKey(parts[0]).setHost(parts[1]).build();
        } else {
            return null;
        }
    }
}
