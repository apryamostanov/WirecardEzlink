package com.wirecard.ezlink.activity;

import java.math.BigDecimal;
import java.util.List;

import com.example.R;
import com.wirecard.ezlink.constants.StringConstants;
import com.wirecard.ezlink.handle.SoundControl;
import com.wirecard.ezlink.handle.Util;
import com.wirecard.ezlink.handle.WebserviceConnection;
import com.wirecard.ezlink.model.QRCode;
import com.wirecard.ezlink.model.ReceiptRequest;
import com.wirecard.ezlink.sqlite.DBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QRCodeScannerActivity extends Activity {
	
	NfcAdapter mNfcAdapter =null;

	private Button nfcButton;
	private TextView tv_merchantNo;
	private TextView tv_merchantName;
	private TextView tv_merchantRefNo;
	private TextView tv_orderNo;
	private TextView tv_paymentAmt;
	private QRCode qrCode;
	private String qrCodeContents;
	private PendingIntent pendingIntent;
	private IntentFilter[] filters;
	private String[][] techList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_qrcode);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		 
		pendingIntent = PendingIntent.getActivity(
				this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		filters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) };
		techList = new String[][] {new String[] { IsoDep.class.getName() } };

		tv_merchantNo = (TextView) findViewById(R.id.tv_merchantNo);
		tv_merchantName = (TextView) findViewById(R.id.tv_merchantName);
		tv_merchantRefNo = (TextView) findViewById(R.id.tv_merchantRefNo);
		tv_orderNo = (TextView) findViewById(R.id.tv_orderNo);
		tv_paymentAmt = (TextView) findViewById(R.id.tv_paymentAmt);

		// check device has camera or not
		Context context = this;
		PackageManager packageManager = context.getPackageManager();
		// if device support camera?
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Log.i("camera", "This device has camera!");
//			SoundControl.init(getApplicationContext());
//			AudioManager aManager=(AudioManager)getSystemService(AUDIO_SERVICE);
//			aManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			Intent intent = new Intent(
					"com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);

			nfcButton = (Button) findViewById(R.id.nfcButton);
			nfcButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent in = new Intent(QRCodeScannerActivity.this, NFCActivity.class);
					startActivity(in);
					finish();
				}
			});
			
		}else{
			final Toast toast =  Toast.makeText(this, StringConstants.ErrorRemarks.NO_CAMERA, Toast.LENGTH_LONG);
			toast.show();
			new CountDownTimer(9000, 1000)
			{
			    public void onTick(long millisUntilFinished) {toast.show();}
			    public void onFinish() {toast.show();}
			}.start();
			Log.i("camera", "This device has no camera!");
			finish();
		}
		
	}

//	private void playBeep()
//    {
//        if (SoundControl.isSoundLoaded())
//        {
//            SoundControl.playSound(0);
//        }
//    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				if(Util.getVibratePref(this)) {
					((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100L);
				}
//				if(Util.getSoundPref(this)) {
//					playBeep();
//				}
				qrCodeContents = intent.getStringExtra("SCAN_RESULT");
				partField(qrCodeContents);
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				onBackPressed();
			}
		}
	}
	
	private void partField(String contents) {
		String merchantNo = "";
		String merchantName= "";
		String merchantRefNo = "";
		String orderNo= "";
		String paymentAmount = "";
		String responseCode = "";
		String encrypt = "";
		try {
			if(contents != null) {
				qrCode = QRCode.getInstance();
				qrCode.getQRCodeDetail(contents);
				merchantNo = qrCode.getQR_MER_ID();
				merchantName = qrCode.getQR_MER_NAME();
				merchantRefNo = qrCode.getQR_MER_TRAX_REF_NO();
				orderNo = qrCode.getQR_ORDER_NO();
				paymentAmount = qrCode.getQR_AMOUNT();
				responseCode = qrCode.getQR_RES_CODE();
				encrypt = qrCode.getQR_RES_ENCRYPT();
			}
			
			//Validate qrCode
			String decryptContent = Util.decrypt3DES("120000001200000012000000", encrypt);
			Log.d("decryptContent: ", decryptContent);
			qrCode.partDecrypt(decryptContent);
			boolean validateQRCode = qrCode.validateQRCode();
			//check response code 
			if(!validateQRCode) {
				final Toast toast = Toast.makeText(this, StringConstants.ErrorRemarks.QRCODE_INVALID, Toast.LENGTH_LONG);
				Intent in = new Intent(QRCodeScannerActivity.this, SecondActivity.class);
				startActivity(in);
		    	finish();
				new CountDownTimer(4000, 1000)
				{
				    public void onTick(long millisUntilFinished) {toast.show();}
				    public void onFinish() {
				    	toast.show();
				    }
				}.start();
				
			}
			
			tv_merchantNo.setText(merchantNo);
			tv_merchantName.setText(merchantName);
			tv_merchantRefNo.setText(merchantRefNo);
			tv_orderNo.setText(orderNo);
			tv_merchantNo.setText(merchantNo);
			tv_paymentAmt.setText("$" + paymentAmount);
			
		} catch(Exception e) {
			final Toast toast = Toast.makeText(this, StringConstants.ErrorRemarks.QRCODE_BELONG_TO, Toast.LENGTH_LONG);
			Intent in = new Intent(QRCodeScannerActivity.this, SecondActivity.class);
			startActivity(in);
	    	finish();
			new CountDownTimer(4000, 1000)
			{
			    public void onTick(long millisUntilFinished) {toast.show();}
			    public void onFinish() {
			    	toast.show();
			    	
			    }
			}.start();
		}
	}

	@Override
    protected void onResume() {
        super.onResume();
		mNfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techList);
    }
	
	@Override
	public void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100L);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		Intent backIntent = new Intent(this, SecondActivity.class);
		backIntent.putExtra("firstTime", false);
	    startActivity(backIntent);
	    finish();
//	    super.onBackPressed();
	}
}
