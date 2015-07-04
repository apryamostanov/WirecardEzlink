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
import com.wirecard.ezlink.activity.NFCActivity;
import com.wirecard.ezlink.activity.PaymentActivity;
import com.wirecard.ezlink.activity.QRCodeScannerActivity;
import com.wirecard.ezlink.activity.SecondActivity;
import com.wirecard.ezlink.activity.TranxHistoryActivity;
import com.wirecard.ezlink.constants.StringConstants;
import com.wirecard.ezlink.fragment.NFCFragment;
import com.wirecard.ezlink.fragment.TagCardFragment;
import com.wirecard.ezlink.fragment.TransactionHistoryFragment;
import com.wirecard.ezlink.model.Card;
import com.wirecard.ezlink.model.ErrorCode;
import com.wirecard.ezlink.model.QRCode;

public class IsoDepReaderTask extends AsyncTask<IsoDep, Void, String> {
	private Context _context;
    private Activity activity;
    private SharedPreferences sharedpreferences;
    private Card card;
    private QRCode qrCode;
	Dialog dialog;
	boolean detectCard = false;
	private boolean getTranxHistory;
	public static IsoDep isoDepStatic;
	Toast toast;
	public static CountDownTimer countDownTimer;
	public static AlertDialog.Builder alertDialog;
	
	public IsoDepReaderTask(Context context) {
		this._context = context;
		this.activity = (Activity) context;
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
	
	public IsoDepReaderTask(Context context, SharedPreferences sharedpreferences, QRCode qrCode, Dialog dialog, boolean getTranxHistory) {
		this._context = context;
		this.activity = (Activity) context;
		this.sharedpreferences = sharedpreferences;
		this.card = new Card();
		this.qrCode = qrCode;
		this.dialog = dialog;
		this.getTranxHistory = getTranxHistory;
	}

	public void timeout() {
		if(null != alertDialog)
			alertDialog.create().show();
	}
	// if within 1 minute that device cannot communicate with card then showing a Toast
	public void startCountDownTimer() {
		if(null != countDownTimer) {
			countDownTimer.start();
		}
	}
	
	public void cancelCountDownTimer() {
		if(null != countDownTimer) {
			countDownTimer.cancel();
		}
	}
		
	@Override
	protected String doInBackground(IsoDep... params) {
		Log.e("IsoDepReaderTask", "Do in Background..");
		String result = null;
		detectCard = true;
		IsoDep isoDep = params[0];
		if (isoDep != null) {
			try {
				if (!isoDep.isConnected()) {
					isoDep.connect();
				}
				isoDep.setTimeout(5000);
				
				byte[] initByte = new byte[8];
				initByte[1] = -92;
				initByte[4] = 2;
				initByte[5] = 64;
	            byte[] initRespose = isoDep.transceive(initByte);
	            Log.d("init()", "request: " + Util.hexString(initByte));
		        Log.d("init()", "response: " + Util.hexString(initRespose));
		        
				byte[] getChallengeByte = { (byte) 0x00, (byte) 0x84,
						(byte) 0x00, (byte) 0x00, (byte) 0x08 };
				Log.d("Challenge Request",Util.hexString(getChallengeByte));
				byte[] challengeResult = isoDep.transceive(getChallengeByte);
				String cardRandomNo = Util.hexString(challengeResult).substring(0, 16);
				Log.d("Challenge Response",Util.hexString(challengeResult));					
				
				//generate Terminal Random No
//				String terminalRN = common.getRandomHexString(16);
				String terminalRN = "CF549C2B7520389C";
				Log.d("terminalRN", terminalRN);
				StringBuffer purseString = new StringBuffer("903203000A1403");
				purseString.append(terminalRN);
				purseString.append("00");
				Log.d("Purse Cmd", purseString.toString());
				/*
				byte[] b2 = new byte[6];
				b2[0] = -112;
				b2[1] = 50;
				b2[2] = 3;
				Log.d("PurseData Request", common.hexString(b2));
                byte[] purseResult = isoDep.transceive(b2);
                */
				byte[] purseByte = Util.hexStringToByteArray(purseString.toString());
				byte[] purseResult = isoDep.transceive(purseByte);
				String purseData = Util.hexString(purseResult);
				Log.d("PurseData Response", purseData);
				if(purseData.length() < 48) {
//					errorCode.sendErrorToReceipt(qrCode, ErrorCode.getErrorCode24() + ":" + ErrorCode.getInvalidCommandFromCard());
					result = ErrorCode.getInvalidCommandFromCard();
					return result;
				}
				String cardNo = card.getCardNo(purseData);
				String balance = card.getPurseBal(purseData);
				String purseCreationDate = card.getPurseCreationDate(purseData);
				String purseExpiryDate = card.getPurseExpiryDate(purseData);
				String purseStatus = card.getPusrseStatus(purseData);
				Log.d("card no", cardNo);
				Log.d("balance", balance);
				Log.d("expiry date", purseExpiryDate);
				Log.d("purseStatus", purseStatus);
				
				//Check card bin
				boolean checkCardBin = card.checkCardBin(cardNo);
				Log.d("CARDBIN",String.valueOf(checkCardBin));
				if(!checkCardBin) {
					result = "Card is not Ez-Link type";
					return result;
				}
				
				if(getTranxHistory) {
//					Activity activity = (Activity) _context;
//					FragmentManager fragmentManager = activity.getFragmentManager();
//					Fragment tranxHistory = new TransactionHistoryFragment();
//					Bundle bundle = new Bundle();
//					bundle.putString("cardNo", cardNo);
//					tranxHistory.setArguments(bundle);
//		            fragmentManager.beginTransaction().replace(R.id.content_frame, tranxHistory).commit();
					Intent in = new Intent(_context, TranxHistoryActivity.class);
					in.putExtra("cardNo", cardNo);
					in.putExtra("purseBalance", balance);
					in.putExtra("expiryDate", purseExpiryDate);
					activity.startActivity(in);
					activity.finish();
				} else {
					//Check expiry date
					boolean cardExpiry = card.checkExpiryDate(purseCreationDate, purseExpiryDate);
					if(cardExpiry) {
						result = "Card is expired";
						return result;
					}
					
					//Check payment amount is less than $500.00
					if(Double.parseDouble(qrCode.getQR_AMOUNT()) > 500) {
						result = "Transaction amount is greater than S $500.00";
						return result;
					}
					
					//Check purse status
					if(!purseStatus.equals("Enabled")) {
						result = "Card status is not enabled";
						return result;
					}
				
					//save data into session
					Editor editor = sharedpreferences.edit();
					editor.putString("cardNo", cardNo);
					editor.putString("terminalRN", terminalRN);
					editor.putString("balance", balance);
					editor.putString("purseRequest", purseString.toString());
//					editor.putString("purseRequest", common.hexString(b2));
					editor.commit();
					
					Intent in = new Intent(_context, PaymentActivity.class);
					Log.d("PAY","GONNA START PAYMENT ACTIVITY");
					activity.startActivity(in);
					Log.d("PAY","AFTER START PAYMENT ACTIVITY");
					activity.finish();
					Log.d("PAY","AFTER FINISH");
					NFCActivity.showToastMgs = false;
				}
			} catch (Exception e) {
				if(e.toString().contains("dead") || e.toString().contains("die")) {
					result = e.toString();
				} else {
					result = ErrorCode.getTagLost();
				}
				Log.e("doInBackgroundError CATCH", "" +result);
			} finally {
				isoDepStatic = isoDep;
			}
		}
		return result;
	}

	@Override
	protected void onPostExecute(String response) {
		if(null != response) {
			detectCard = false;
			if(getTranxHistory == true) {
				TagCardFragment.error.setVisibility(View.VISIBLE);
				TagCardFragment.error_content.setText(response);
			} else {
				NFCFragment.error.setVisibility(View.VISIBLE);
				NFCFragment.error_content.setText(response);
				if(response.contains("dead") || response.contains("die")) {
					new AlertDialog.Builder(_context)
			        .setTitle("Scan again..!!")
			        .setMessage(StringConstants.MessageRemarks.SCAN_AGAIN_MSG)
			        .setNegativeButton(android.R.string.no, null)
			        .setPositiveButton(android.R.string.yes, new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg) {
							Intent in = new Intent(_context, QRCodeScannerActivity.class);
							_context.startActivity(in);
							((Activity) _context).finish();
						}
			        }).create().show();
				}
			}
		} else {
			if(null != countDownTimer) {
				Log.e("stop countDownTimer", "stop");
				countDownTimer.cancel();
			}
		}
		if (dialog != null && dialog.isShowing())
        {
			dialog.dismiss();
			dialog = null;
        }
	}
}