package com.wirecard.ezlink.model;

import java.io.Serializable;
import java.util.Random;
import java.util.StringTokenizer;

import android.os.Parcel;
import android.os.Parcelable;

public class QRCode implements Serializable{
	private String QR_MER_NAME;
	private String QR_MER_ID;
	private String QR_MER_TRAX_REF_NO;
	private String QR_ORDER_NO;
	private String QR_AMOUNT;
	private String QR_CURRENCY;
	private String QR_RRN;
	private String QR_RES_CODE;
	private String QR_RES_ENCRYPT;
	private String DECRYPT_QR_MER_ID;
	private String DECRYPT_QR_MER_TRAX_REF_NO;
	private String DECRYPT_QR_ORDER_NO;
	private String DECRYPT_QR_AMOUNT;
	
	private static QRCode qrCode = new QRCode();
	
	private QRCode() {}
	
	public static QRCode getInstance( ) {
	      return qrCode;
	}
	
	public void partDecrypt(String decryptContent) {
		String part[] = decryptContent.split("&");
		setDECRYPT_QR_MER_ID(part[0]);
		setDECRYPT_QR_MER_TRAX_REF_NO(part[1]);
		setDECRYPT_QR_ORDER_NO(part[2]);
		setDECRYPT_QR_AMOUNT(part[3]);
	}
	
	public boolean validateQRCode() {
		boolean match = false;
		
		if(getQR_MER_ID().equals(getDECRYPT_QR_MER_ID()) && getQR_MER_TRAX_REF_NO().equals(getDECRYPT_QR_MER_TRAX_REF_NO())
				&& getQR_ORDER_NO().equals(getDECRYPT_QR_ORDER_NO()) && getQR_AMOUNT().equals(getDECRYPT_QR_AMOUNT())) {
			match = true;
		}
		return match;
	}
	public void getQRCodeDetail(String contents) {
		StringTokenizer tokenizer = new StringTokenizer(contents, "&");
		String[] pair;
		while (tokenizer.hasMoreElements()) {
			String token = (String) tokenizer.nextElement();
			pair = token.split(":");
			if (token.startsWith("QR_MER_NAME")) {
				setQR_MER_NAME(pair[1]);
				continue;
			}
			if (token.startsWith("QR_MER_ID")) {
				setQR_MER_ID(pair[1]);
				continue;
			}
			if (token.startsWith("QR_MER_TRAX_REF_NO")) {
				setQR_MER_TRAX_REF_NO(pair[1]);
				continue;
			}
			if (token.startsWith("QR_ORDER_NO")) {
				setQR_ORDER_NO(pair[1]);
				continue;
			}
			if (token.startsWith("QR_AMOUNT")) {
				setQR_AMOUNT(pair[1]);
				continue;
			}
			if (token.startsWith("QR_CURRENCY")) {
				setQR_CURRENCY(pair[1]);
				continue;
			}
			if (token.startsWith("QR_RRN")) {
				setQR_RRN(pair[1]);
				continue;
			}
			if (token.startsWith("QR_RES_CODE")) {
				setQR_RES_CODE(pair[1]);
				continue;
			}
			if (token.startsWith("QR_RES_ENCRYPT")) {
				setQR_RES_ENCRYPT(pair[1]);
				continue;
			}
		}
	}

	public String getQR_MER_NAME() {
		return QR_MER_NAME;
	}

	public void setQR_MER_NAME(String qR_MER_NAME) {
		QR_MER_NAME = qR_MER_NAME;
	}

	public String getQR_MER_ID() {
		return QR_MER_ID;
	}

	public void setQR_MER_ID(String qR_MER_ID) {
		QR_MER_ID = qR_MER_ID;
	}

	public String getQR_MER_TRAX_REF_NO() {
		return QR_MER_TRAX_REF_NO;
	}

	public void setQR_MER_TRAX_REF_NO(String qR_MER_TRAX_REF_NO) {
		QR_MER_TRAX_REF_NO = qR_MER_TRAX_REF_NO;
	}

	public String getQR_ORDER_NO() {
		return QR_ORDER_NO;
	}

	public void setQR_ORDER_NO(String qR_ORDER_NO) {
		QR_ORDER_NO = qR_ORDER_NO;
	}

	public String getQR_AMOUNT() {
		return QR_AMOUNT;
	}

	public void setQR_AMOUNT(String qR_AMOUNT) {
		QR_AMOUNT = qR_AMOUNT;
	}

	public String getQR_CURRENCY() {
		return QR_CURRENCY;
	}

	public void setQR_CURRENCY(String qR_CURRENCY) {
		QR_CURRENCY = qR_CURRENCY;
	}

	public String getQR_RRN() {
		return QR_RRN;
	}

	public void setQR_RRN(String qR_RRN) {
		QR_RRN = qR_RRN;
	}

	public String getQR_RES_CODE() {
		return QR_RES_CODE;
	}

	public void setQR_RES_CODE(String qR_RES_CODE) {
		QR_RES_CODE = qR_RES_CODE;
	}

	public String getQR_RES_ENCRYPT() {
		return QR_RES_ENCRYPT;
	}

	public void setQR_RES_ENCRYPT(String qR_RES_ENCRYPT) {
		QR_RES_ENCRYPT = qR_RES_ENCRYPT;
	}

	public String getDECRYPT_QR_MER_ID() {
		return DECRYPT_QR_MER_ID;
	}

	public void setDECRYPT_QR_MER_ID(String dECRYPT_QR_MER_ID) {
		DECRYPT_QR_MER_ID = dECRYPT_QR_MER_ID;
	}

	public String getDECRYPT_QR_MER_TRAX_REF_NO() {
		return DECRYPT_QR_MER_TRAX_REF_NO;
	}

	public void setDECRYPT_QR_MER_TRAX_REF_NO(String dECRYPT_QR_MER_TRAX_REF_NO) {
		DECRYPT_QR_MER_TRAX_REF_NO = dECRYPT_QR_MER_TRAX_REF_NO;
	}

	public String getDECRYPT_QR_ORDER_NO() {
		return DECRYPT_QR_ORDER_NO;
	}

	public void setDECRYPT_QR_ORDER_NO(String dECRYPT_QR_ORDER_NO) {
		DECRYPT_QR_ORDER_NO = dECRYPT_QR_ORDER_NO;
	}

	public String getDECRYPT_QR_AMOUNT() {
		return DECRYPT_QR_AMOUNT;
	}

	public void setDECRYPT_QR_AMOUNT(String dECRYPT_QR_AMOUNT) {
		DECRYPT_QR_AMOUNT = dECRYPT_QR_AMOUNT;
	}
}
