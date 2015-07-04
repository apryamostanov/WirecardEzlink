package com.wirecard.ezlink.handle;

import com.example.R;
import com.wirecard.ezlink.activity.NFCActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectionDetector {
    
    private Context _context;
    private Activity activity;
    private NfcAdapter mNfcAdapter;
    
    public ConnectionDetector(Context context){
        this._context = context;
        this.activity = (Activity) context;
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }
 
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) 
                  for (int i = 0; i < info.length; i++) 
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }
    
    public void ensureSensorIsOn() {
		// get Internet status
        boolean isInternetPresent = isConnectingToInternet();
		if (!isInternetPresent) {
			AlertDialog.Builder b = new AlertDialog.Builder(_context);
			b.setTitle(R.string.wifi_disabled);
			b.setMessage(R.string.wifi_turn_on);
			b.setPositiveButton(R.string.setting,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						}
					});
			b.setIcon(android.R.drawable.ic_dialog_alert);
			b.create().show();
		}
		//if device don't support NFC
		if (mNfcAdapter == null) {
			// Stop here, we definitely need NFC
			final Toast toast =  Toast.makeText(_context, R.string.nfc_support, Toast.LENGTH_LONG);
			toast.show();
			new CountDownTimer(4000, 1000)
			{
			    public void onTick(long millisUntilFinished) {toast.show();}
			    public void onFinish() {toast.show();}
			}.start();
			activity.finish();
			return;

		}
		//if device turn off NFC
		if (!mNfcAdapter.isEnabled()) {
			AlertDialog.Builder b = new AlertDialog.Builder(_context);
			b.setTitle(R.string.nfc_disabled);
			b.setMessage(R.string.nfc_turn_on);
			b.setPositiveButton(R.string.setting,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Send the user to the settings page and hope they turn it on
							if (android.os.Build.VERSION.SDK_INT >= 16)
		                    {
								activity.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
		                    }
							else
		                    {
								activity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		                    }
						}
					});
			b.setIcon(android.R.drawable.ic_dialog_alert);
			b.create().show();
		} 
	}
}
