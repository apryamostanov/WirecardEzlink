package com.wirecard.ezlink.constants;

public interface StringConstants {
	
	public interface ErrorCode {
		String ERROR_CODE_00 = "00";
		 String ERROR_CODE_01 = "01";
		 String ERROR_CODE_02 = "02";
		 String ERROR_CODE_03 = "03";
		 String ERROR_CODE_04 = "04";
		 String ERROR_CODE_05 = "05";
		 String ERROR_CODE_06 = "06";
		 String ERROR_CODE_17 = "17";
		 String ERROR_CODE_11 = "11";
		 String ERROR_CODE_12 = "12";
		 String ERROR_CODE_15 = "15";
		 String ERROR_CODE_20 = "20";
		 String ERROR_CODE_21 = "21";
		 String ERROR_CODE_22 = "22";
		 String ERROR_CODE_23 = "23";
		 String ERROR_CODE_24 = "24";
		 String ERROR_CODE_25 = "25";
		 String ERROR_CODE_26 = "26";
		 String ERROR_CODE_27 = "27";
		 String ERROR_CODE_28 = "28";
		 String ERROR_CODE_29 = "29";
		 String ERROR_CODE_30 = "30";
		 String ERROR_CODE_31 = "31";
		 String ERROR_CODE_32 = "32";
		 String ERROR_CODE_33 = "33";
		 String ERROR_CODE_34 = "34";
	}

	public interface ErrorDecription {
		String SUCCESSFUL = "Successful";
		 String DEBIT_COMMAND_SUCCESSFUL_BUT_NO_RESPONSE_FROM_CARD = "Debit command successful but no response from card";
		 String INVALID_MERCHANT = "Invalid Merchant";
		 String INVALID_MERCHANT_DATA = "Invalid Merchant Data";
		 String SECURITY_CODE_INVALID = "Security Code Invalid";
		 String DUPLICATED_TRANSACTION = "Duplicate Transaction";
		 String INVALID_QRCODE = "Invalid QR-Code(Check Sum not match)";
		 String INVALID_QRCODE_DATA = "Invalid QR Code Data (Invalid Amount or Response Code wrong)";
		 String INVALID_CARD = "Invalid Card";
		 String CANNOT_DETECT_CARD = "Can not detect the ez-link card(Card Bin)";
		 String BACKLIST_CARD = "Backlist card";
		 String INSUFFICIENT_BALANCE = "Insufficient Balance";
		 String NFC_COMMUNICATION_ERROR = "NFC Communication Error";
		 String NO_CARD_DETECTED = "No Card Detected";
		 String COMMUNICATION_TIMEOUT = "Communication Timeout";
		 String NO_RESPONSE_FROM_CARD = "No Response from Card";
		 String APPLICATION_ERROR = "Application Error";
		 String DEBIT_COMMAND_ERROR = "Debit command Error";
		 String RECEIPT_COMMAND_ERROR = "Receipt command Error";
		 String INVALID_COMMAND_FROM_HOST = "Invalid command from Host";
		 String NO_RESPONSE_FROM_TERMINAL = "No Response from Terminal";
		 String COMMUNICATION_ERROR_WITH_SAM_MANAGER = "Communication Error with SAM Manager";
		 String INVALID_COMMAND_FROM_CARD = "Invalid command from Card";
		 String DEBIT_CARD_NOT_AVAILABLE = "Debit card not available";
		 String TRANSCEIVE_FAILED = "Transceive failed";
		 String CARD_BALANCE_IS_NOT_CORRECT = "Card balance is not correct";
		 String CONNECTION_ISSUE = "Connection with Backend Failed";
		 String TAG_LOST = "Connection with Card Failed. Please tap Card again";
		 String CONNECTION_CLOSING_ISSUE = "Can not close connection";
		 String AUTO_LOAD_AMOUNT_ERROR = "Auto load amount is N.A";
	}
	public interface Validation {
		public final String TRANX_CODE_PAY = "COT";
		public final String TRANX_CODE_EQUERY = "EQR";

		public final String INVALID_MERCHANT = "INVALID_MERCHANT";
		public final String VALID_MERCHANT = "VALID_MERCHANT";
		public final String INVALID_ACCESS_CODE = "INVALID_ACCESS_CODE";
		public final String VALID_ACCESS_CODE = "VALID_ACCESS_CODE";
		public final String INVALID_TRANSACTION_CODE = "INVALID TRANSACTION CODE";
		public final String INVALID_HASH_CODE = "INVALID HASH CODE";

		public final String UNIQUE_TRANSCTION = "UNIQUE_TRANSCTION";
		public final String NON_UNIQUE_TRANSACTION = "NON_UNIQUE_TRANSACTION";

		public final String PAYMENT_NOT_COMPLETED = "PAYMENT NOT COMPLETED";
		public final String PAYMENT_COMPLETED = "PAYMENT COMPLETED";

		public final String ACCESS_CODE_SECURED_KEY_VALUE_YES = "Y";

		public final String TRANXCODE_ERROR = "ERR";

		public final String TRANX_STATUS_QR = "Q";
		public final String TRANX_STATUS_RECIEPT = "R";

		public final String HASH_CODE_ALGO = "SHA256";

		public final int MAX_ORDER_INFO_LENGTH = 19;
	}
	
	public interface MessageRemarks {
		String PROCESSING = "Processing";
		String TRANX_HISTORY = "Please wait while your transaction history is being processed...";
		String HOLD_CARD = "Please hold on to your card";
		String SCANNING = "Scanning...";
		String CLOSING_APP = "Closing Application..!!";
		String ASKING_EXIT = "Do you really want to Exit..??";
		String SCAN_AGAIN = "Scan again..!!";
		String SCAN_AGAIN_MSG = "Are you sure you want to scan QR code again..?";
		String THERE_ARE = "There are ";
		String THERE_IS = "There is ";
		String RECEIPT_REQUEST_UPLOAD = " Receipt Request need to upload to host";
		String PLEASE_WAIT = "Please wait...";
		String CONFIRMATION = "Confirmation";
		String TRANX_HISTORY_STR = "Transaction History";
		String CONTACT = "Contact";
		String TERMS_CONDITIONS = "Terms & Conditions";
		String HELP = "Help";
		String EXIT = "Are you sure you want to exit from the application..??";
		String TAP_CARD = "Tap Card";
		String PAYMENT = "Payment";
	}

	public interface WebserviceConnectionUrl {
//		String URL_DEBIT="http://192.168.6.89:8090/EzLinkWebServices/DebitCommandService";
//		String URL_RECEIPT="http://192.168.6.89:8090/EzLinkWebServices/RecieptService";
//		String URL_HISTORY="http://192.168.6.89:8090/EzLinkWebServices/TranxHistoryService";
		String URL_DEBIT="http://203.125.133.73:8090/EzLinkWebServices/DebitCommandService";
		String URL_RECEIPT="http://203.125.133.73:8090/EzLinkWebServices/RecieptService";
		String URL_HISTORY="http://203.125.133.73:8090/EzLinkWebServices/TranxHistoryService";
	}
	public interface ErrorRemarks {
		String NO_TRANX_HISTORY = "There is no transaction history";
		 String CONNECTION_ISSUE = "Connection with Backend Failed";
		 String CONNECTION_CLOSING_ISSUE = "Can NOT close connection";
		 String TAG_LOST = "Connection with Card Failed.";
		 String TIME_OUT = "Payment timeout. Please scan new qrcode again.";
		 String NO_CAMERA = "Cannot scan the Ez-Link QR Code. This mobile doesn\'t has camera";
		String QRCODE_INVALID = "QRCode is invalid";
		String QRCODE_BELONG_TO = "This QRCode doesn't belong to Ecommerce. Please scan right Ecommerce's QRCode.";
		String TRANX_FAILURE = "Transaction is fail. Please scan QRCode again.";
		String CARD_DIFFIRENT = "Invalid Card. Please put correct card.";
		String AMT_ALERT = "Transaction amount is greater than S $500.00";
		String INVALID_CARD = "Invalid Card";
		String EXPIRED_CARD = "Expired Card";
		String CARD_NOT_EZLINK = "Card is not Ez-Link type";
	}
}
