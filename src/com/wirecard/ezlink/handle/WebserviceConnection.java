package com.wirecard.ezlink.handle;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.R.string;
import com.wirecard.ezlink.constants.StringConstants;
import com.wirecard.ezlink.model.QRCode;
import com.wirecard.ezlink.model.ReceiptRequest;
import com.wirecard.ezlink.sqlite.DBHelper;
import com.wirecard.ezlink.webservices.debitCommand.DebitCommandFault;
import com.wirecard.ezlink.webservices.debitCommand.DebitCommandReq;
import com.wirecard.ezlink.webservices.debitCommand.DebitCommandRes;
import com.wirecard.ezlink.webservices.debitCommand.DebitCommandResError;
import com.wirecard.ezlink.webservices.debitCommand.DebitCommandSoap;
import com.wirecard.ezlink.webservices.debitCommand.EZLING_WS_HEADER;
import com.wirecard.ezlink.webservices.debitCommand.EZLING_WS_HEADER_1;
import com.wirecard.ezlink.webservices.debitCommand.EZLING_WS_REQ_BODY;
import com.wirecard.ezlink.webservices.debitCommand.EZLING_WS_RES_BODY;
import com.wirecard.ezlink.webservices.debitCommand.EZLING_WS_RES_ENV;
import com.wirecard.ezlink.webservices.receipt.RecieptFault;
import com.wirecard.ezlink.webservices.receipt.RecieptReq;
import com.wirecard.ezlink.webservices.receipt.RecieptReqError;
import com.wirecard.ezlink.webservices.receipt.RecieptRes;
import com.wirecard.ezlink.webservices.receipt.RecieptResError;
import com.wirecard.ezlink.webservices.receipt.RecieptSoap;
import com.wirecard.ezlink.webservices.tranxHistory.VDGEZLING_WS_TRANX_REQ_BODY;
import com.wirecard.ezlink.webservices.tranxHistory.VDGEZLING_WS_TRANX_REQ_HEADER;
import com.wirecard.ezlink.webservices.tranxHistory.VDGEZLING_WS_TRANX_RES_ENV;
import com.wirecard.ezlink.webservices.tranxHistory.VDGEZLING_WS__TRANX_RES_BODY;
import com.wirecard.ezlink.webservices.tranxHistory.VDGTranxHistoryDetail;
import com.wirecard.ezlink.webservices.tranxHistory.VDGTranxHistoryReq;
import com.wirecard.ezlink.webservices.tranxHistory.VDGTranxHistorySoap;
import com.wirecard.ezlink.webservices.tranxHistory.VDGTranxList;

public class WebserviceConnection {
	private Context _context;
	private DBHelper db;
	public static DebitCommandFault debitCommandFault;
	public static RecieptFault recieptFault;
	
	public WebserviceConnection(Context _context) {
		this._context = _context;
		this.db = new DBHelper(_context);
	}

	public VDGTranxList getTranxHistory(String cardNo) {
		VDGTranxList tranxList = null;
		String exception = null;
		try {
			VDGTranxHistorySoap historySoap = new VDGTranxHistorySoap();
			VDGEZLING_WS_TRANX_REQ_HEADER reqHeader = new VDGEZLING_WS_TRANX_REQ_HEADER("", "", "", "", "", "", "", "");
			VDGTranxHistoryReq tranxHistoryReq = new VDGTranxHistoryReq(cardNo, new BigInteger("20"));
			VDGEZLING_WS_TRANX_REQ_BODY reqBody = new VDGEZLING_WS_TRANX_REQ_BODY(tranxHistoryReq);
			
			VDGEZLING_WS_TRANX_RES_ENV sd = historySoap.GetTranxHistory(reqHeader, reqBody);
			
			VDGEZLING_WS__TRANX_RES_BODY resBody = sd.EZLING_WS__TRANX_RES_BODY;
			tranxList = resBody.TranxList;
			VDGTranxHistoryDetail historyDetail;
			if(tranxList != null) {
				for (int i = 0; i < tranxList.size(); i++) {
					historyDetail = tranxList.get(i);
					Log.d("History Detail " + i, historyDetail.MERCHANTNAME + "/" + historyDetail.AMOUNT + "/" + historyDetail.TRANXDATE + "/" + historyDetail.STATUS);
				}
			} else {
				tranxList = new VDGTranxList();
			}
		}catch (Exception e) {
			exception = e.toString();
			Log.e("getTranxHistoryError", exception);
//			e.printStackTrace();
		} finally {
			if(null!=exception && (exception.contains("EOFException") || exception.contains("EPIPE"))) {
				return getTranxHistory(cardNo);
			}
		}
		return tranxList;
	}
	
	public String getDebitCommand() {
		String debitCommand = null;
		String exception = null;
		try {
			QRCode qrCode = QRCode.getInstance();
			SharedPreferences sharedPreferences = _context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
			Editor editor = sharedPreferences.edit();
			String merchantNo = qrCode.getQR_MER_ID();
			String orderNo = qrCode.getQR_ORDER_NO();
			String merchantRefNo = qrCode.getQR_MER_TRAX_REF_NO();
			BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(qrCode.getQR_AMOUNT()));
			amount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
			String currency = qrCode.getQR_CURRENCY();
			Date dateNow = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmmss");
			String date = dateFormat.format(dateNow);
			TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
			String deviceId = telephonyManager.getDeviceId();
			String cardNo = sharedPreferences.getString("cardNo", null);
			String cardRN = sharedPreferences.getString("cardRN", null);
			String cardSN = sharedPreferences.getString("cardSN", null);
			String purseData = sharedPreferences.getString("purseData", null);
			String terminalRN = sharedPreferences.getString("terminalRN", null);
			BigInteger retrycnt = BigInteger.ONE;
			DebitCommandSoap debitCommandSoap = new DebitCommandSoap();
			
			EZLING_WS_HEADER header = new EZLING_WS_HEADER("EC", "WD", date, deviceId, "1", "", "", "T111", "");
			DebitCommandReq debitCommandReq = new DebitCommandReq(merchantNo,  orderNo, merchantRefNo, amount, currency, terminalRN, cardRN, cardNo, cardSN, purseData, retrycnt);
//			DebitCommandReq debitCommandReq = new DebitCommandReq(merchantNo,  "20150105142509", "20150105142509", BigDecimal.valueOf(0.01), currency, terminalRN, cardRN, cardNo, cardSN, purseData, 1);
			EZLING_WS_REQ_BODY body = new EZLING_WS_REQ_BODY(debitCommandReq);
			
			
			EZLING_WS_RES_ENV ws_RES_ENV = debitCommandSoap.DebitCommand(header, body);
			EZLING_WS_RES_BODY res_BODY = ws_RES_ENV.EZLING_WS_RES_BODY;
			
			//get debit command
			DebitCommandRes debitCommandRes = (DebitCommandRes) res_BODY.getProperty(0);
			
			debitCommand = (String) debitCommandRes.getProperty(3);
			//check transaction info with web service header info
//			boolean match = Common.checkTranxInfo(qrCode, res_BODY., merchantRefNo, orderNo)
		} catch (DebitCommandFault fault) {
			if(fault != null) {
				if(fault.message.equals("TRANSACTION ALREADY COMPLETED") 
						|| fault.message.equals("BLACKLISTED CARD") || fault.message.equals("TRANSACTION TIME OUT")) {
					debitCommandFault = fault;
				}
			}
		}
		
		catch (Exception e) {
			exception = e.toString();
			Log.e("getDebitCommandError", exception);
//			e.printStackTrace();
		} finally {
			if(null!=exception && (exception.contains("EOFException") || exception.contains("EPIPE"))) {
				return getDebitCommand();
			}
		}
		
		return debitCommand;		
	}
	
	public String uploadReceiptData(String receiptData, RecieptReqError reqError) {
		
		String exception = null;
		String uploadResult = null;
		QRCode qrCode = QRCode.getInstance();
		SharedPreferences sharedPreferences = _context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		String merchantNo = qrCode.getQR_MER_ID();
		String orderNo = qrCode.getQR_ORDER_NO();
		String merchantRefNo = qrCode.getQR_MER_TRAX_REF_NO();
		BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(qrCode.getQR_AMOUNT()));
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		String currency = qrCode.getQR_CURRENCY();
		Date dateNow = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmmss");
		String date = dateFormat.format(dateNow);
		TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = telephonyManager.getDeviceId();
		String cardNo = sharedPreferences.getString("cardNo", null);
		try {
			RecieptSoap recieptSoap = new RecieptSoap();
			com.wirecard.ezlink.webservices.receipt.EZLING_WS_HEADER ezling_WS_HEADER = new com.wirecard.ezlink.webservices.receipt.EZLING_WS_HEADER("EC", "WD", date, deviceId, "1", "", "", "T111", "");
			RecieptReq recieptReq = new RecieptReq(merchantNo, orderNo, merchantRefNo, cardNo, amount, receiptData);
//			RecieptReqError reqError = new RecieptReqError("00", ErrorCode.getSuccessful());
			com.wirecard.ezlink.webservices.receipt.EZLING_WS_REQ_BODY ezling_WS_REQ_BODY = new com.wirecard.ezlink.webservices.receipt.EZLING_WS_REQ_BODY(recieptReq, reqError);
			
			com.wirecard.ezlink.webservices.receipt.EZLING_WS_RES_ENV ezling_WS_RES_ENV = recieptSoap.Reciept(ezling_WS_HEADER, ezling_WS_REQ_BODY);
			com.wirecard.ezlink.webservices.receipt.EZLING_WS_RES_BODY ws_RES_BODY = (com.wirecard.ezlink.webservices.receipt.EZLING_WS_RES_BODY) ezling_WS_RES_ENV.getProperty(1);
			RecieptRes res = (RecieptRes) ws_RES_BODY.getProperty(0);

			uploadResult = (String) res.getProperty(4);
			
		} catch (RecieptFault fault) {
			if(fault != null) {
				if(fault.message.equals("TRANSACTION ALREADY COMPLETED")) {
					recieptFault = fault;
				}
			}
				
		} catch (Exception e) {
			exception = e.toString();
			Log.e("uploadReceiptDataError", exception);
			//save receipt data into sqlite if sending receipt to host fail
//			db.addReceiptRequest(new ReceiptRequest(merchantNo, orderNo, merchantRefNo, cardNo, amount, receiptData, (String)reqError.getProperty(0), (String)reqError.getProperty(1)));
		} finally {
			if(null!=exception && (exception.contains("EOFException") || exception.contains("EPIPE"))) {
				return uploadReceiptData(receiptData, reqError);
			} else if(null != exception) {
				//save receipt data into sqlite if sending receipt to host fail
				db.addReceiptRequest(new ReceiptRequest(merchantNo, orderNo, merchantRefNo, cardNo, amount, receiptData, (String)reqError.getProperty(0), (String)reqError.getProperty(1), date));
			}
		}
		
		return uploadResult;
	}
	
	public boolean uploadReceiptDataAgain(ReceiptRequest receiptRequest) {
		String exception = null;
		Date dateNow = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HHmmss");
		String date = dateFormat.format(dateNow);
		TelephonyManager telephonyManager = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = telephonyManager.getDeviceId();
		String merchantNo = receiptRequest.getMerchantNo();
		String orderNo = receiptRequest.getOrderNo();
		String merchantRefNo = receiptRequest.getMerchantRefNo();
		String cardNo = receiptRequest.getCan();
		BigDecimal amount = receiptRequest.getAmount();
		String receiptData = receiptRequest.getReceiptData();
		RecieptReqError reqError = new RecieptReqError(receiptRequest.getErrorCode(), receiptRequest.getErrorDescript());
		try {
			RecieptSoap recieptSoap = new RecieptSoap();
			com.wirecard.ezlink.webservices.receipt.EZLING_WS_HEADER ezling_WS_HEADER = new com.wirecard.ezlink.webservices.receipt.EZLING_WS_HEADER("EC", "WD", date, deviceId, "1", "", "", "T111", "");
//			RecieptReq recieptReq = new RecieptReq(merchantNo, "20150105142509", "20150105142509", cardNo, amount, receiptData);
			RecieptReq recieptReq = new RecieptReq(merchantNo, orderNo, merchantRefNo, cardNo, amount, receiptData);
			com.wirecard.ezlink.webservices.receipt.EZLING_WS_REQ_BODY ezling_WS_REQ_BODY = new com.wirecard.ezlink.webservices.receipt.EZLING_WS_REQ_BODY(recieptReq, reqError);
			
			com.wirecard.ezlink.webservices.receipt.EZLING_WS_RES_ENV ezling_WS_RES_ENV = recieptSoap.Reciept(ezling_WS_HEADER, ezling_WS_REQ_BODY);
			//remove ReceiptRequest in sqlite
			db.deleteReceiptRequest(receiptRequest);
			return true;
		} catch (Exception e) {
			exception = e.toString();
			Log.e("receiptAgainError", exception);
			// save This Receipt Request when can not upload it to host
//			e.printStackTrace();
		} finally {
			if(null!=exception && (exception.contains("EOFException") || exception.contains("EPIPE"))) {
				return uploadReceiptDataAgain(receiptRequest);
			} 
//			else if(null!=exception) {
//				db.addReceiptRequest(receiptRequest);
//			}
		}
		return false;
	}
}
