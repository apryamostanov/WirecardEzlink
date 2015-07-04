package com.wirecard.ezlink.model;

import android.content.Context;

import com.wirecard.ezlink.handle.WebserviceConnection;
import com.wirecard.ezlink.webservices.receipt.RecieptReqError;

public class ErrorCode {
	private WebserviceConnection webservice;
	private Context _context;
	private static final String ERROR_CODE_00 = "00";
	private static final String ERROR_CODE_01 = "01";
	private static final String ERROR_CODE_02 = "02";
	private static final String ERROR_CODE_03 = "03";
	private static final String ERROR_CODE_04 = "04";
	private static final String ERROR_CODE_05 = "05";
	private static final String ERROR_CODE_06 = "06";
	private static final String ERROR_CODE_17 = "17";
	private static final String ERROR_CODE_11 = "11";
	private static final String ERROR_CODE_12 = "12";
	private static final String ERROR_CODE_15 = "15";
	private static final String ERROR_CODE_20 = "20";
	private static final String ERROR_CODE_21 = "21";
	private static final String ERROR_CODE_22 = "22";
	private static final String ERROR_CODE_23 = "23";
	private static final String ERROR_CODE_24 = "24";
	private static final String ERROR_CODE_25 = "25";
	private static final String ERROR_CODE_26 = "26";
	private static final String ERROR_CODE_27 = "27";
	private static final String ERROR_CODE_28 = "28";
	private static final String ERROR_CODE_29 = "29";
	private static final String ERROR_CODE_30 = "30";
	private static final String ERROR_CODE_31 = "31";
	private static final String ERROR_CODE_32 = "32";
	private static final String ERROR_CODE_33 = "33";
	private static final String SUCCESSFUL = "Successful";
	private static final String DEBIT_COMMAND_SUCCESSFUL_BUT_NO_RESPONSE_FROM_CARD = "Debit command successful but no response from card";
	private static final String INVALID_MERCHANT = "Invalid Merchant";
	private static final String INVALID_MERCHANT_DATA = "Invalid Merchant Data";
	private static final String SECURITY_CODE_INVALID = "Security Code Invalid";
	private static final String DUPLICATED_TRANSACTION = "Duplicate Transaction";
	private static final String INVALID_QRCODE = "Invalid QR-Code(Check Sum not match)";
	private static final String INVALID_QRCODE_DATA = "Invalid QR Code Data (Invalid Amount or Response Code wrong)";
	private static final String INVALID_CARD = "Invalid Card (Card Bin)";
	private static final String CANNOT_DETECT_CARD = "Can not detect the ez-link card(Card Bin)";
	private static final String BACKLIST_CARD = "Backlist card";
	private static final String INSUFFICIENT_BALANCE = "Insufficient Balance";
	private static final String NFC_COMMUNICATION_ERROR = "NFC Communication Error";
	private static final String NO_CARD_DETECTED = "No Card Detected";
	private static final String COMMUNICATION_TIMEOUT = "Communication Timeout";
	private static final String NO_RESPONSE_FROM_CARD = "No Response from Card";
	private static final String APPLICATION_ERROR = "Application Error";
	private static final String DEBIT_COMMAND_ERROR = "Debit command Error";
	private static final String RECEIPT_COMMAND_ERROR = "Receipt command Error";
	private static final String INVALID_COMMAND_FROM_HOST = "Invalid command from Host";
	private static final String NO_RESPONSE_FROM_TERMINAL = "No Response from Terminal";
	private static final String COMMUNICATION_ERROR_WITH_SAM_MANAGER = "Communication Error with SAM Manager";
	private static final String TAG_IS_LOST = "Tag is lost.";
	private static final String INVALID_COMMAND_FROM_CARD = "Invalid command from Card";
	private static final String DEBIT_CARD_NOT_AVAILABLE = "Debit card not available";
	private static final String TRANSCEIVE_FAILED = "Transceive failed";
	
	//JANA
	private static final String CONNECTION_ISSUE = "Connection with Backend Failed";
	private static final String CONNECTION_CLOSING_ISSUE = "Can NOT close connection";
	private static final String TAG_LOST = "Connection with Card Failed.";
	private static final String TIME_OUT = "Payment timeout. Please scan new qrcode again.";
	
	
	
	public ErrorCode(Context context) {
		this.webservice = new WebserviceConnection(context);
	}
	
	public void sendError(QRCode qrCode, String result) {
		if(result.contains(TRANSCEIVE_FAILED)) {
			webservice.uploadReceiptData(qrCode, "", new RecieptReqError(ERROR_CODE_24, NO_RESPONSE_FROM_CARD));
		} else if(result.contains(TAG_IS_LOST)) {
			webservice.uploadReceiptData(qrCode, "", new RecieptReqError(ERROR_CODE_21, NFC_COMMUNICATION_ERROR));
		} else {
			webservice.uploadReceiptData(qrCode, "", new RecieptReqError(ERROR_CODE_25, APPLICATION_ERROR));
		}
	}

	public static String getDebitCardNotAvailable() {
		return DEBIT_CARD_NOT_AVAILABLE;
	}


	public static String getErrorCode00() {
		return ERROR_CODE_00;
	}

	public static String getErrorCode01() {
		return ERROR_CODE_01;
	}

	public static String getErrorCode02() {
		return ERROR_CODE_02;
	}

	public static String getErrorCode03() {
		return ERROR_CODE_03;
	}

	public static String getErrorCode04() {
		return ERROR_CODE_04;
	}

	public static String getErrorCode05() {
		return ERROR_CODE_05;
	}

	public static String getErrorCode06() {
		return ERROR_CODE_06;
	}

	public static String getErrorCode17() {
		return ERROR_CODE_17;
	}

	public static String getErrorCode11() {
		return ERROR_CODE_11;
	}

	public static String getErrorCode12() {
		return ERROR_CODE_12;
	}

	public static String getErrorCode15() {
		return ERROR_CODE_15;
	}

	public static String getErrorCode20() {
		return ERROR_CODE_20;
	}

	public static String getErrorCode21() {
		return ERROR_CODE_21;
	}

	public static String getErrorCode22() {
		return ERROR_CODE_22;
	}

	public static String getErrorCode23() {
		return ERROR_CODE_23;
	}

	public static String getErrorCode24() {
		return ERROR_CODE_24;
	}

	public static String getErrorCode25() {
		return ERROR_CODE_25;
	}

	public static String getErrorCode26() {
		return ERROR_CODE_26;
	}

	public static String getErrorCode27() {
		return ERROR_CODE_27;
	}

	public static String getErrorCode28() {
		return ERROR_CODE_28;
	}

	public static String getErrorCode29() {
		return ERROR_CODE_29;
	}

	public static String getErrorCode30() {
		return ERROR_CODE_30;
	}

	public static String getErrorCode31() {
		return ERROR_CODE_31;
	}

	public static String getErrorCode32() {
		return ERROR_CODE_32;
	}

	public static String getErrorCode33() {
		return ERROR_CODE_33;
	}

	public static String getSuccessful() {
		return SUCCESSFUL;
	}

	public static String getDebitCommandSuccessfulButNoResponseFromCard() {
		return DEBIT_COMMAND_SUCCESSFUL_BUT_NO_RESPONSE_FROM_CARD;
	}

	public static String getInvalidMerchant() {
		return INVALID_MERCHANT;
	}

	public static String getInvalidMerchantData() {
		return INVALID_MERCHANT_DATA;
	}

	public static String getSecurityCodeInvalid() {
		return SECURITY_CODE_INVALID;
	}

	public static String getDuplicatedTransaction() {
		return DUPLICATED_TRANSACTION;
	}

	public static String getInvalidQrcode() {
		return INVALID_QRCODE;
	}

	public static String getInvalidQrcodeData() {
		return INVALID_QRCODE_DATA;
	}

	public static String getInvalidCard() {
		return INVALID_CARD;
	}

	public static String getCannotDetectCard() {
		return CANNOT_DETECT_CARD;
	}

	public static String getBacklistCard() {
		return BACKLIST_CARD;
	}

	public static String getInsufficientBalance() {
		return INSUFFICIENT_BALANCE;
	}

	public static String getNfcCommunicationError() {
		return NFC_COMMUNICATION_ERROR;
	}

	public static String getNoCardDetected() {
		return NO_CARD_DETECTED;
	}

	public static String getCommunicationTimeout() {
		return COMMUNICATION_TIMEOUT;
	}

	public static String getNoResponseFromCard() {
		return NO_RESPONSE_FROM_CARD;
	}

	public static String getApplicationError() {
		return APPLICATION_ERROR;
	}

	public static String getDebitCommandError() {
		return DEBIT_COMMAND_ERROR;
	}

	public static String getReceiptCommandError() {
		return RECEIPT_COMMAND_ERROR;
	}

	public static String getInvalidCommandFromHost() {
		return INVALID_COMMAND_FROM_HOST;
	}

	public static String getNoResponseFromTerminal() {
		return NO_RESPONSE_FROM_TERMINAL;
	}

	public static String getCommunicationErrorWithSamManager() {
		return COMMUNICATION_ERROR_WITH_SAM_MANAGER;
	}

	public static String getTagIsLost() {
		return TAG_IS_LOST;
	}

	public static String getInvalidCommandFromCard() {
		return INVALID_COMMAND_FROM_CARD;
	}
	//JANA
	public static String getConnectionIssue() {
		return CONNECTION_ISSUE;
	}
	
	public static String getTagLost() {
		return TAG_LOST;
	}
	
	public static String getConnectionCloseIssue() {
		return CONNECTION_CLOSING_ISSUE;
	}

	public static String getTimeOut() {
		return TIME_OUT;
	}
	
	

}
