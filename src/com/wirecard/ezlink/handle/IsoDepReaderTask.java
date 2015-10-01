package com.wirecard.ezlink.handle;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.R;
import com.wirecard.ezlink.activity.ConfirmationActivity;
import com.wirecard.ezlink.activity.NFCActivity;
import com.wirecard.ezlink.activity.PaymentActivity;
import com.wirecard.ezlink.activity.QRCodeScannerActivity;
import com.wirecard.ezlink.activity.SecondActivity;
import com.wirecard.ezlink.activity.TranxHistoryActivity;
import com.wirecard.ezlink.constants.StringConstants;
import com.wirecard.ezlink.fragment.NFCFragment;
import com.wirecard.ezlink.fragment.TapCardFragment;
import com.wirecard.ezlink.model.QRCode;
import com.wirecard.ezlink.webservices.receipt.RecieptReqError;

public class IsoDepReaderTask extends AsyncTask<IsoDep, Void, String> {
	private Context _context;
    private Activity activity;
    private SharedPreferences sharedpreferences;
    private QRCode qrCode;
	Dialog dialog;
	boolean detectCard = false;
	private boolean getTranxHistory;
	public static IsoDep isoDepStatic;
	Toast toast;
	public static CountDownTimer countDownTimer;
	public static AlertDialog.Builder alertDialog;
	private String currentActivity;
	private WebserviceConnection wsConnection;
	
	public IsoDepReaderTask(Context context) {
		this._context = context;
		this.activity = (Activity) context;
		this.wsConnection = new WebserviceConnection(context);
		this.toast =  Toast.makeText(_context, R.string.error_detect, Toast.LENGTH_LONG);
		this.alertDialog = new AlertDialog.Builder(_context)
        .setTitle("Scan again..!!")
        .setMessage(StringConstants.MessageRemarks.SCAN_AGAIN_MSG)
        .setNegativeButton(android.R.string.no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startCountDownTimer();
			}
		})
        .setPositiveButton(android.R.string.yes, new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg) {
				Intent in = new Intent(_context, QRCodeScannerActivity.class);
				_context.startActivity(in);
				((Activity) _context).finish();
			}
        });
		this.countDownTimer = new CountDownTimer(60000, 15000-500) {
			public void onTick(long millisUntilFinished) {
				Log.e("millisUntilFinished", String.valueOf(millisUntilFinished));
				if(!detectCard && millisUntilFinished < 50000 && NFCActivity.showToastMgs == true) {
					toast.show();
				}
			}
			public void onFinish() {
				if(!detectCard) {
					NFCFragment.error.setVisibility(View.VISIBLE);
					NFCFragment.error_content.setText(R.string.no_card_detected);
					
					timeout();
					//.create().show();
				}
			}
		};
		
	}
	
	public IsoDepReaderTask(Context context, SharedPreferences sharedpreferences, 
			Dialog dialog, boolean getTranxHistory, String currentActivity ) {
		this._context = context;
		this.activity = (Activity) context;
		this.wsConnection = new WebserviceConnection(context);
		this.sharedpreferences = sharedpreferences;
		this.dialog = dialog;
		this.getTranxHistory = getTranxHistory;
		this.currentActivity = currentActivity;
	}

	public void timeout() {
		if(null != alertDialog)
			alertDialog.create().show();
	}
	// if within 1 minute that device cannot communicate with card then showing a Toast
	public void startCountDownTimer() {
		if(null != countDownTimer) {
			Log.e("Start countDownTimer","start");
			countDownTimer.start();
		}
	}
	
	public void cancelCountDownTimer() {
		if(null != countDownTimer) {
			Log.e("Cancel countDownTimer","Cancel");
			countDownTimer.cancel();
		}
	}
		
	@Override
	protected String doInBackground(IsoDep... params) {
		Log.e("IsoDepReaderTask", "Do in Background..");
		String errorStr = null;
		detectCard = true;
		qrCode = QRCode.getInstance();
		
		IsoDep isoDep = params[0];
		if (isoDep != null) {
			try {
				if (!isoDep.isConnected()) {
					isoDep.connect();
				}
				isoDep.setTimeout(5000);
				
				ReaderModeAccess modeAccess = new ReaderModeAccess(isoDep);
				modeAccess.init();
				byte[] challengeResponse = modeAccess.getChallenge();
				
				String terminalRN = "CF549C2B7520389C";
				StringBuffer purseString = new StringBuffer("903203000A1403");
				purseString.append(terminalRN);
				purseString.append("00");
				String purseData = modeAccess.getPurseData(purseString.toString());
				if(purseData.length() < 134) {
					errorStr = StringConstants.ErrorDecription.INVALID_COMMAND_FROM_CARD;
					return errorStr;
				}
				String cardRN = modeAccess.getCardRN(challengeResponse);
				String cardNo = CardInfoHandler.getCardNo(purseData);
				double balance = CardInfoHandler.getPurseBal(purseData);
				String purseCreationDate = CardInfoHandler.getPurseCreationDate(purseData);
				String purseExpiryDate = CardInfoHandler.getPurseExpiryDate(purseData);
				String purseStatus = CardInfoHandler.getPusrseStatus(purseData);
				String autoloadStatus = CardInfoHandler.getALStatus(purseData);
				String auloloadAmount = CardInfoHandler.getALAmount(purseData);
				boolean isSurrenderedCard = CardInfoHandler.isSurrenderedCard(purseData);
				
				Log.d("card no", cardNo);
				Log.d("balance", balance+"");
				Log.d("expiry date", purseExpiryDate);
				Log.d("purseStatus", purseStatus);
				Log.d("autoloadStatus", autoloadStatus);
				Log.d("auloloadAmount", auloloadAmount);
				Log.d("isSurrenderedCard", isSurrenderedCard + "");
				//Check card bin
				boolean checkCardBin = CardInfoHandler.checkCardBin(cardNo);
				Log.d("CARDBIN",String.valueOf(checkCardBin));
				if(!checkCardBin) {
					errorStr = StringConstants.ErrorRemarks.CARD_NOT_EZLINK;
					return errorStr;
				}
				
				if(getTranxHistory) {
					Intent in = new Intent(_context, TranxHistoryActivity.class);
					in.putExtra("cardNo", cardNo);
					in.putExtra("purseBalance", String.format("%.2f", balance));
					in.putExtra("expiryDate", purseExpiryDate);
					in.putExtra("currentActivity", currentActivity);
					activity.startActivity(in);
					activity.finish();
				} else {
					Editor editor = sharedpreferences.edit();
					//Check expiry date
					boolean cardExpiry = CardInfoHandler.checkExpiryDate(purseCreationDate, purseExpiryDate);
					if(cardExpiry) {
						errorStr = StringConstants.ErrorRemarks.EXPIRED_CARD;
						return errorStr;
					}
					
					//isSurrenderedCard
					if(isSurrenderedCard) {
						errorStr = StringConstants.ErrorRemarks.INVALID_CARD;
						return errorStr;
					}
					
					//Check payment amount is less than $500.00
					if(Double.parseDouble(qrCode.getQR_AMOUNT()) > 500) {
						errorStr = StringConstants.ErrorRemarks.AMT_ALERT;
						return errorStr;
					}
					
					//Check purse status
					if(!purseStatus.equals("Enabled")) {
						errorStr = StringConstants.ErrorRemarks.INVALID_CARD;
						return errorStr;
					}
				
					if(NFCActivity.excuseDebit == false) {
						//save data into session
						editor.putString("cardNo", cardNo);
						editor.putString("terminalRN", terminalRN);
						editor.putString("autoloadStatus", autoloadStatus);
						editor.putString("auloloadAmount", auloloadAmount);
						editor.putString("cardRN", cardRN);
						editor.putString("purseData", purseData);
						
						double autoAmt;
						double paymentAmt = Double.parseDouble(qrCode.getQR_AMOUNT());
						if(autoloadStatus.equals("Enabled")) {
							try {
								autoAmt = Double.parseDouble(auloloadAmount);
								if(paymentAmt > (balance+autoAmt)) {
									errorStr = StringConstants.ErrorDecription.INSUFFICIENT_BALANCE;
									return errorStr;
								} else if((balance < paymentAmt) && (paymentAmt <= (balance+autoAmt) )) {
									editor.putBoolean("needAutoLoad", true);
								}
							} catch(Exception e) {
								errorStr = StringConstants.ErrorDecription.AUTO_LOAD_AMOUNT_ERROR;
								return errorStr;
							}
						} else {
							if(balance < paymentAmt) {
								errorStr = StringConstants.ErrorDecription.INSUFFICIENT_BALANCE;
								return errorStr;
							}
						}
						
						editor.commit();
						
						Log.d("debitCommand", "++CALL WS++"+System.currentTimeMillis());
						NFCActivity.debitCommand = wsConnection.getDebitCommand();
						Log.d("debitCommand", "++RECIEVED WS++"+System.currentTimeMillis());
					}
					
					if (null != NFCActivity.debitCommand) {
						NFCActivity.excuseDebit = true;
						String receiptData = modeAccess.getReceipt(NFCActivity.debitCommand, wsConnection);
						
						if(receiptData != null && receiptData.contains("9000")) {
							Log.d("receiptData", receiptData);
							Log.d("RECIEPT", "++CALL WS++"+System.currentTimeMillis());
							// Upload receipt data
							if(NFCActivity.excuseReceipt == false) {
								String receiptResponse = wsConnection.uploadReceiptData(receiptData, new RecieptReqError(StringConstants.ErrorCode.ERROR_CODE_00, StringConstants.ErrorDecription.SUCCESSFUL));
								Log.d("RECIEPT", "++RESULT WS++"+System.currentTimeMillis());
								NFCActivity.excuseReceipt = true;
								if(receiptResponse==null) {
									if(WebserviceConnection.recieptFault != null) {
										errorStr = WebserviceConnection.recieptFault.message;
										WebserviceConnection.recieptFault = null;
										return errorStr;
									} 
								}
							}
							modeAccess.init();
							modeAccess.getChallenge();
							String purseData2 = modeAccess.getPurseData(purseString.toString());
							if(purseData2.length() < 10) {
								errorStr = StringConstants.ErrorDecription.INVALID_COMMAND_FROM_CARD;
								return errorStr;
							}
							
							String cardNumber = CardInfoHandler.getCardNo(purseData2);
							if(!cardNo.equals(cardNumber)) {
		//						wsConnection.uploadReceiptData("", new RecieptReqError(StringConstants.ErrorCode.ERROR_CODE_15, StringConstants.ErrorDecription.INVALID_CARD));
								errorStr = StringConstants.ErrorRemarks.CARD_DIFFIRENT;
								return errorStr;
							}
							
							double currentBalance = CardInfoHandler.getPurseBal(purseData2);
							Log.d("preBal", balance+"");
							Log.d("curentBal", currentBalance + "");
							// check the card balance is correct after payment is successful by comparing with  old and new balances.
							boolean deductedBalance = false;
							boolean needAutoload = sharedpreferences.getBoolean("needAutoLoad", false);
							if(needAutoload) {
								String autoLoadAmt = sharedpreferences.getString("auloloadAmount", "0");
								deductedBalance = CardInfoHandler.checkCurrentBalanceWithAutoLoadAmt(currentBalance, balance, qrCode.getQR_AMOUNT(), autoLoadAmt);
							} else {
								deductedBalance = CardInfoHandler.checkCurrentBalance(currentBalance, balance, qrCode.getQR_AMOUNT());
							}
							
							if(!deductedBalance) {
		//						wsConnection.uploadReceiptData("", new RecieptReqError(StringConstants.ErrorCode.ERROR_CODE_34, StringConstants.ErrorDecription.CARD_BALANCE_IS_NOT_CORRECT));
								errorStr = StringConstants.ErrorDecription.CARD_BALANCE_IS_NOT_CORRECT;
								return errorStr;
							}
							
							editor.putString("merchantName", qrCode.getQR_MER_NAME());
							editor.putString("paymentAmt", String.format("%.2f", Double.parseDouble(qrCode.getQR_AMOUNT())));
							editor.putString("prevBal", String.format("%.2f", balance));
							editor.putString("currentBal", String.format("%.2f", currentBalance));
							editor.commit();
							Intent in = new Intent(_context, ConfirmationActivity.class);
							activity.startActivity(in);
							activity.finish();
							
						} else if(receiptData != null) {
							Log.d("receiptData", receiptData);
							//check purse balance is updated or not
							modeAccess.getChallenge();
							String purseData2 = modeAccess.getPurseData(purseString.toString());
							if(purseData2.length() < 48) {
								errorStr = StringConstants.ErrorDecription.INVALID_COMMAND_FROM_CARD;
								return errorStr;
							}
							double curentBal = CardInfoHandler.getPurseBal(purseData2);
							Log.d("curentBal", curentBal+"");
							Log.d("purseBalance", balance+"");
							// debit command is successful 
							boolean deductedBalance = false;
							boolean needAutoload = sharedpreferences.getBoolean("needAutoLoad", false);
							if(needAutoload) {
								String autoLoadAmt = sharedpreferences.getString("auloloadAmount", "0");
								deductedBalance = CardInfoHandler.checkCurrentBalanceWithAutoLoadAmt(curentBal, balance, qrCode.getQR_AMOUNT(), autoLoadAmt);
							} else {
								deductedBalance = CardInfoHandler.checkCurrentBalance(curentBal, balance, qrCode.getQR_AMOUNT());
							}
							
							if(deductedBalance) {
//								wsConnection.uploadReceiptData("", new RecieptReqError(StringConstants.ErrorCode.ERROR_CODE_01, StringConstants.ErrorDecription.DEBIT_COMMAND_SUCCESSFUL_BUT_NO_RESPONSE_FROM_CARD));
								editor.putString("merchantName", qrCode.getQR_MER_NAME());
								editor.putString("paymentAmt", String.format("%.2f", Double.parseDouble(qrCode.getQR_AMOUNT())));
								editor.putString("prevBal", String.format("%.2f", balance));
								editor.putString("currentBal", String.format("%.2f", curentBal));
								editor.commit();
								Intent in = new Intent(_context, ConfirmationActivity.class);
								activity.startActivity(in);
								activity.finish();
								return null;
							} else {
								errorStr = StringConstants.ErrorRemarks.TRANX_FAILURE;
								return errorStr;
							}
						} else {
							errorStr=StringConstants.ErrorDecription.TAG_LOST;
							return errorStr;
						}
					} else {
						if(WebserviceConnection.debitCommandFault != null) {
							errorStr = WebserviceConnection.debitCommandFault.message;
							if(errorStr.equals("TRANSACTION TIME OUT")) {
								errorStr = StringConstants.ErrorRemarks.TIME_OUT;
							}
							WebserviceConnection.debitCommandFault = null;
						} else {
							errorStr=StringConstants.ErrorDecription.CONNECTION_ISSUE;
						}
						return errorStr;
					}
					NFCActivity.showToastMgs = false;
				}
			} catch (Exception e) {
				if(e.toString().contains("dead") || e.toString().contains("die")) {
					errorStr = e.getMessage();
				} else {
					errorStr = StringConstants.ErrorDecription.TAG_LOST;
				}
				Log.e("doInBackgroundError CATCH", "" +errorStr);
			} finally {
				isoDepStatic = isoDep;
			}
		}
		return errorStr;
	}

	@Override
	protected void onPostExecute(String response) {
		if(null != response) {
			detectCard = false;
			if(getTranxHistory == true) {
				TapCardFragment.error.setVisibility(View.VISIBLE);
				TapCardFragment.error_content.setText(response);
			} else {
				NFCFragment.error.setVisibility(View.VISIBLE);
				NFCFragment.error_content.setText(response);
			}
			if(response.contains("dead") || response.contains("die")) {
				new AlertDialog.Builder(_context)
		        .setTitle("Scan again..!!")
		        .setMessage(StringConstants.MessageRemarks.SCAN_AGAIN_MSG)
		        .setNegativeButton(android.R.string.no, null)
		        .setPositiveButton(android.R.string.yes, new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg) {
						cancelCountDownTimer();
						Intent in = new Intent(_context, QRCodeScannerActivity.class);
						_context.startActivity(in);
						((Activity) _context).finish();
					}
		        }).create().show();
			}
		} else {
			cancelCountDownTimer();
			QRCode.getNew();
		}
		if (dialog != null && dialog.isShowing())
        {
			dialog.dismiss();
			dialog = null;
        }
	}
}