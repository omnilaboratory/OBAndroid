package com.omni.wallet.baselibrary.utils;

public class NumericalUtils {

	private NumericalUtils() {
	}

	public static String byte2Hex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String byteHexString = Integer.toHexString(bytes[i] & 0xFF);
			if (byteHexString.length() == 1) {
				sb.append("0");
			}
			sb.append(byteHexString);
		}
		return sb.toString();
	}

	public static String byte2HexUpperCase(byte[] bytes) {
		return byte2Hex(bytes).toUpperCase();
	}

	public static String byte2HexLowerCase(byte[] bytes) {
		return byte2Hex(bytes).toLowerCase();
	}
}
