package com.wirecard.ezlink.handle;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.preference.PreferenceManager;

import com.wirecard.ezlink.model.QRCode;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class Util {

	public Util() {
		// TODO Auto-generated constructor stub
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String getRandomHexString(int numchars) {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		while (sb.length() < numchars) {
			sb.append(Integer.toHexString(r.nextInt()));
		}

		return sb.toString().substring(0, numchars).toUpperCase();
	}

	public static String hexString(byte[] b) {
		StringBuffer d = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			char hi = Character.forDigit((b[i] >> 4) & 0x0F, 16);
			char lo = Character.forDigit(b[i] & 0x0F, 16);
			d.append(Character.toUpperCase(hi));
			d.append(Character.toUpperCase(lo));
		}
		return d.toString();
	}

	/*
	 * public static String encrypt3DES(String clearKey, String clearData)
	 * throws Exception {
	 * 
	 * byte[] keyData = clearKey.getBytes("UTF8");
	 * 
	 * SecretKey key = new SecretKeySpec(keyData, "DESede"); Cipher cipher =
	 * Cipher.getInstance("DESede/CBC/PKCS5Padding"); IvParameterSpec ivSpec =
	 * new IvParameterSpec(new byte[8]); cipher.init(Cipher.ENCRYPT_MODE, key,
	 * ivSpec);
	 * 
	 * byte[] stringBytes = clearData.getBytes("UTF8"); byte[] cipherText =
	 * cipher.doFinal(stringBytes);
	 * 
	 * BASE64Encoder encoder = new BASE64Encoder(); String base64 =
	 * encoder.encode(cipherText);// string base 64
	 * 
	 * String strHex = hexString(base64.getBytes());// string to hex
	 * 
	 * return strHex; }
	 */

	// data is HEX
	public static String decrypt3DES(String clearKey, String hexData)
			throws Exception {
		byte[] keyData = clearKey.getBytes("UTF8");

		SecretKey key = new SecretKeySpec(keyData, "DESede");
		Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		IvParameterSpec ivSpec = new IvParameterSpec(new byte[8]);
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

		String _data = new String(hexStringToByteArray(hexData));

		BASE64Decoder decoder = new BASE64Decoder();
		byte[] raw = decoder.decodeBuffer(_data);
		byte[] stringBytes = cipher.doFinal(raw);
		String clear = new String(stringBytes, "UTF8");

		return clear;
	}

	public static boolean checkTranxInfo(QRCode qrCode, String merchantNo,
			String merchantRefNo, String orderNo) {
		boolean match = false;

		if (qrCode.getQR_MER_ID().equals(merchantNo)
				&& qrCode.getQR_MER_TRAX_REF_NO().equals(merchantRefNo)
				&& qrCode.getQR_ORDER_NO().equals(orderNo)) {
			match = true;
		}
		return match;
	}

	public static boolean getSoundPref(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("preference_sound", false);
	}

	public static boolean getVibratePref(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("preference_vibrate", false);
	}

	public static boolean getStartScanPref(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("preference_start_scan", false);
	}
}
