package com.omni.wallet_mainnet.utils;

import android.util.Pair;

import java.util.Locale;


/**
 * This class is a slightly modified version of SamouraiDev's implementation which can be found here:
 * https://github.com/sipa/bech32/pull/19
 */

public class Bech32 {

    public static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

    public static String bech32Encode(String hrp, byte[] data) {

        byte[] chk = createChecksum(hrp.getBytes(), data);
        byte[] combined = new byte[chk.length + data.length];

        System.arraycopy(data, 0, combined, 0, data.length);
        System.arraycopy(chk, 0, combined, data.length, chk.length);

        byte[] xlat = new byte[combined.length];
        for (int i = 0; i < combined.length; i++) {
            xlat[i] = (byte) CHARSET.charAt(combined[i]);
        }

        byte[] ret = new byte[hrp.getBytes().length + xlat.length + 1];
        System.arraycopy(hrp.getBytes(), 0, ret, 0, hrp.getBytes().length);
        System.arraycopy(new byte[]{0x31}, 0, ret, hrp.getBytes().length, 1);
        System.arraycopy(xlat, 0, ret, hrp.getBytes().length + 1, xlat.length);

        return new String(ret);
    }

    public static Pair<String, byte[]> bech32Decode(String bech, boolean checkLength) throws Exception {

        byte[] buffer = bech.getBytes();
        for (byte b : buffer) {
            if (b < 0x21 || b > 0x7e) {
                throw new Exception("bech32 characters out of range");
            }
        }

        if (!bech.equals(bech.toLowerCase(Locale.ROOT)) && !bech.equals(bech.toUpperCase(Locale.ROOT))) {
            throw new Exception("bech32 cannot mix upper and lower case");
        }

        bech = bech.toLowerCase();
        int pos = bech.lastIndexOf("1");
        if (pos < 1) {
            throw new Exception("bech32 missing separator");
        } else if (pos + 7 > bech.length()) {
            throw new Exception("bech32 separator misplaced");
        } else if (bech.length() < 8) {
            throw new Exception("bech32 input too short");
        } else if (bech.length() > 90) {
            if (checkLength) {
                throw new Exception("bech32 input too long");
            }
        }

        String s = bech.substring(pos + 1);
        for (int i = 0; i < s.length(); i++) {
            if (CHARSET.indexOf(s.charAt(i)) == -1) {
                throw new Exception("bech32 characters out of range");
            }
        }

        byte[] hrp = bech.substring(0, pos).getBytes();

        byte[] dataWithChecksum = new byte[bech.length() - pos - 1];
        for (int j = 0, i = pos + 1; i < bech.length(); i++, j++) {
            dataWithChecksum[j] = (byte) CHARSET.indexOf(bech.charAt(i));
        }

        if (!verifyChecksum(hrp, dataWithChecksum)) {
            throw new Exception("invalid bech32 checksum");
        }

        byte[] data = new byte[dataWithChecksum.length - 6];
        System.arraycopy(dataWithChecksum, 0, data, 0, dataWithChecksum.length - 6);

        return Pair.create(new String(hrp), data);
    }

    private static int polymod(byte[] values) {

        final int[] GENERATORS = {0x3b6a57b2, 0x26508e6d, 0x1ea119fa, 0x3d4233dd, 0x2a1462b3};

        int chk = 1;

        for (byte b : values) {
            byte top = (byte) (chk >> 0x19);
            chk = b ^ ((chk & 0x1ffffff) << 5);
            for (int i = 0; i < 5; i++) {
                chk ^= ((top >> i) & 1) == 1 ? GENERATORS[i] : 0;
            }
        }

        return chk;
    }

    private static byte[] hrpExpand(byte[] hrp) {

        byte[] buf1 = new byte[hrp.length];
        byte[] buf2 = new byte[hrp.length];
        byte[] mid = new byte[1];

        for (int i = 0; i < hrp.length; i++) {
            buf1[i] = (byte) (hrp[i] >> 5);
        }
        mid[0] = 0x00;
        for (int i = 0; i < hrp.length; i++) {
            buf2[i] = (byte) (hrp[i] & 0x1f);
        }

        byte[] ret = new byte[(hrp.length * 2) + 1];
        System.arraycopy(buf1, 0, ret, 0, buf1.length);
        System.arraycopy(mid, 0, ret, buf1.length, mid.length);
        System.arraycopy(buf2, 0, ret, buf1.length + mid.length, buf2.length);

        return ret;
    }

    private static boolean verifyChecksum(byte[] hrp, byte[] data) {

        byte[] exp = hrpExpand(hrp);

        byte[] values = new byte[exp.length + data.length];
        System.arraycopy(exp, 0, values, 0, exp.length);
        System.arraycopy(data, 0, values, exp.length, data.length);

        return (1 == polymod(values));
    }

    private static byte[] createChecksum(byte[] hrp, byte[] data) {

        final byte[] zeroes = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        byte[] expanded = hrpExpand(hrp);
        byte[] values = new byte[zeroes.length + expanded.length + data.length];

        System.arraycopy(expanded, 0, values, 0, expanded.length);
        System.arraycopy(data, 0, values, expanded.length, data.length);
        System.arraycopy(zeroes, 0, values, expanded.length + data.length, zeroes.length);

        int polymod = polymod(values) ^ 1;
        byte[] ret = new byte[6];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (byte) ((polymod >> 5 * (5 - i)) & 0x1f);
        }

        return ret;
    }

    public static byte[] regroupBytes(byte[] bytes) {
        // regroups the "5 bit" bytes to 8 bit bytes.
        boolean[] bitArray = new boolean[bytes.length * 5];
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 3; j < 8; j++)
                bitArray[i * 5 + j - 3] = (bytes[i] & (byte) (128 / Math.pow(2, j))) != 0;
        }
        return bitArrayToBytes(bitArray);
    }

    private static byte[] bitArrayToBytes(boolean[] input) {
        byte[] toReturn = new byte[input.length / 8];
        for (int entry = 0; entry < toReturn.length; entry++) {
            for (int bit = 0; bit < 8; bit++) {
                if (input[entry * 8 + bit]) {
                    toReturn[entry] |= (128 >> bit);
                }
            }
        }
        return toReturn;
    }

}