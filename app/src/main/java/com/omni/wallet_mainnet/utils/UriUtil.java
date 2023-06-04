package com.omni.wallet_mainnet.utils;

import android.support.annotation.NonNull;

/**
 * This class is used to handle typical URIs in the bitcoin space.
 */
public class UriUtil {
    public static final String URI_PREFIX_LIGHTNING = "lightning:";
    public static final String URI_PREFIX_BITCOIN = "bitcoin:";
    public static final String URI_PREFIX_LNDCONNECT = "lndconnect://";
    public static final String URI_PREFIX_LUCKY_PACKET = "luckypacket:";

    public static String generateLightningUri(@NonNull String data) {
        if (isLightningUri(data)) {
            return data;
        }

        return URI_PREFIX_LIGHTNING + data;
    }

    public static String generateLuckyPacketUri(@NonNull String data) {
        if (isLuckyPacketUri(data)) {
            return data;
        }

        return URI_PREFIX_LUCKY_PACKET + data;
    }

    public static String generateBitcoinUri(@NonNull String data) {
        if (isBitcoinUri(data)) {
            return data;
        }

        return URI_PREFIX_BITCOIN + data;
    }

    public static String generateLndConnctUri(@NonNull String data) {
        if (isLNDConnectUri(data)) {
            return data;
        }

        return URI_PREFIX_LNDCONNECT + data;
    }

    public static boolean isLightningUri(@NonNull String data) {
        return hasPrefix(URI_PREFIX_LIGHTNING, data);
    }

    public static boolean isLuckyPacketUri(@NonNull String data) {
        return hasPrefix(URI_PREFIX_LUCKY_PACKET, data);
    }

    public static boolean isBitcoinUri(@NonNull String data) {
        return hasPrefix(URI_PREFIX_BITCOIN, data);
    }

    public static boolean isLNDConnectUri(@NonNull String data) {
        return hasPrefix(URI_PREFIX_LNDCONNECT, data);
    }

    public static String removeURI(@NonNull String data) {
        if (isLightningUri(data)) {
            return data.substring(URI_PREFIX_LIGHTNING.length());
        } else if (isBitcoinUri(data)) {
            return data.substring(URI_PREFIX_BITCOIN.length());
        } else if (isLNDConnectUri(data)) {
            return data.substring(URI_PREFIX_LNDCONNECT.length());
        } else if (isLuckyPacketUri(data)) {
            return data.substring(URI_PREFIX_LUCKY_PACKET.length());
        } else {
            return data;
        }
    }

    private static boolean hasPrefix(@NonNull String prefix, @NonNull String data) {
        if (data.isEmpty() || data.length() < prefix.length()) {
            return false;
        }

        return data.substring(0, prefix.length()).equalsIgnoreCase(prefix);
    }
}
