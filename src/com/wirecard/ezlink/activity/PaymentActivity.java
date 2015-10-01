package com.wirecard.ezlink.activity;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.R;
import com.wirecard.ezlink.constants.StringConstants;
import com.wirecard.ezlink.fragment.ContactFragment;
import com.wirecard.ezlink.fragment.HelpFragment;
import com.wirecard.ezlink.fragment.PaymentFragment;
import com.wirecard.ezlink.fragment.TapCardFragment;
import com.wirecard.ezlink.fragment.TermsAndConditionsFragment;
import com.wirecard.ezlink.handle.CardInfoHandler;
import com.wirecard.ezlink.handle.Util;
import com.wirecard.ezlink.handle.ConnectionDetector;
import com.wirecard.ezlink.handle.IsoDepReaderTask;
import com.wirecard.ezlink.handle.ReaderModeAccess;
import com.wirecard.ezlink.handle.WebserviceConnection;
import com.wirecard.ezlink.model.QRCode;
import com.wirecard.ezlink.navigationdrawer.DrawerItemCustomAdapter;
import com.wirecard.ezlink.navigationdrawer.ObjectDrawerItem;
import com.wirecard.ezlink.webservices.debitCommand.DebitCommandFault;
import com.wirecard.ezlink.webservices.receipt.RecieptReqError;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.TagLostException;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PaymentActivity extends FragmentActivity {
	private NfcAdapter mNfcAdapter;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private Button payButton;
	private WebserviceConnection wsConnection;
	private QRCode qrCode;
	private ConnectionDetector cd;
	private CardInfoHandler card;
	private String cardNo;
	private boolean detectCard;
	private String debitCommand;
	private String paymentAmt;
	private String merchantName;
	private String purseBalance;
	private ProgressDialog dialog;
	private PendingIntent pendingIntent;
	private IntentFilter[] filters;
	private String[][] techList;
	private ReaderModeAccess readerModeAccess;
	
	private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private boolean tapCardAgain;
    private FragmentManager fragmentManager;
    private boolean noReceiptResponse;
    private boolean excuseDebit;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e("PAYMENT ACTIVITY", "first line On Create..");
		super.onCreate(savedInstanceState);
		Log.e("PAYMENT ACTIVITY", "2 On Create..");
		setContentView(R.layout.activity_payment);
		Log.e("PAYMENT ACTIVITY", "On Create..");
		
		fragmentManager = getSupportFragmentManager();
		// for proper titles
		mTitle = mDrawerTitle = getTitle();

		// initialize properties
		mNavigationDrawerItemTitles = getResources().getStringArray(
				R.array.navigation_drawer_payment);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// list the drawer items
		ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[5];

		drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_action_copy, StringConstants.MessageRemarks.PAYMENT);
		drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_action_share, StringConstants.MessageRemarks.TRANX_HISTORY_STR);
		drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_action_share, StringConstants.MessageRemarks.CONTACT);
		drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_action_share, StringConstants.MessageRemarks.TERMS_CONDITIONS);
		drawerItem[4] = new ObjectDrawerItem(R.drawable.ic_action_share, StringConstants.MessageRemarks.HELP);

		// Pass the folderData to our ListView adapter
		DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this,
				R.layout.listview_item_row, drawerItem);

		// Set the adapter for the list view
		mDrawerList.setAdapter(adapter);

		// set the item click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// for app icon control for nav drawer
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(mTitle);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(mDrawerTitle);
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			selectItem(0);
		}

		sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		card = new CardInfoHandler();
		cd = new ConnectionDetector(this);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		wsConnection = new WebserviceConnection(this);

		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		filters = new IntentFilter[] { new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED) };
		techList = new String[][] { new String[] { IsoDep.class.getName() } };
		handleIntent(getIntent());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
       if (mDrawerToggle.onOptionsItemSelected(item)) {
           return true;
       }
       
       return super.onOptionsItemSelected(item);
	}
	
	// to change up caret
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	
	
	// navigation drawer click listener
	private class DrawerItemClickListener implements OnItemClickListener {
		
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	    
	}

    private void selectItem(int position) {
    	
        // update the main content by replacing fragments
    	
        Fragment fragment = null;
        
        switch (position) {
        case 0:
            fragment = new PaymentFragment();
            break;
        case 1:
            fragment = new TapCardFragment();
            break;
        case 2:
        	fragment = new ContactFragment();
        	break;
        case 3:
        	fragment = new TermsAndConditionsFragment();
        	break;
        case 4:
        	fragment = new HelpFragment();
        	break;
        default:
            break;
        }
        
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "fragment"+position).commit();
//            fragmentManager.beginTransaction().add(R.id.content_frame, fragment, "fragment"+position).commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
    
	@Override
    protected void onResume() {
        super.onResume();
        cd.ensureSensorIsOn();
		mNfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techList);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		PaymentFragment paymentFragment = (PaymentFragment) fragmentManager.findFragmentByTag("fragment0");
		if(paymentFragment != null) {
			Log.d("PaymentFragment is", " not null");
			payButton = (Button) paymentFragment.getView().findViewById(R.id.payButton);
			showInfo();
		} else {
			Log.d("PaymentFragment is", " null");
		}
//		isoDepReaderTask.countDownTimeConnectToCard();
    }
	
	private void showInfo() {
		qrCode = QRCode.getInstance();
		paymentAmt = qrCode.getQR_AMOUNT();
		merchantName = qrCode.getQR_MER_NAME();
//		String merchantRef = qrCode.getQR_MER_TRAX_REF_NO();
		cardNo = sharedPreferences.getString("cardNo", null);
		purseBalance = sharedPreferences.getString("balance", null);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if(Util.getVibratePref(this)) {
			((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100L);
		}
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			detectCard = true;
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			final IsoDep isoDep = IsoDep.get(tag);
			TapCardFragment tagCardFragment = (TapCardFragment) fragmentManager.findFragmentByTag("fragment1");
			if(tagCardFragment != null) {
				dialog = ProgressDialog.show(PaymentActivity.this, StringConstants.MessageRemarks.HOLD_CARD, StringConstants.MessageRemarks.TRANX_HISTORY, true);
				new com.wirecard.ezlink.handle.IsoDepReaderTask(this, null, dialog, true, "PaymentActivity").execute(isoDep);
			} else {
				payButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog = ProgressDialog.show(PaymentActivity.this, StringConstants.MessageRemarks.PROCESSING, StringConstants.MessageRemarks.SCANNING, true);
						// Request : Debit Command
						if(tapCardAgain) {
							new CurrentBalanceTask().execute(isoDep);
						} else {
							new IsoDepReaderTask().execute(isoDep);
						}
					}
				});
			}
		}
	}
	
	public class IsoDepReaderTask extends AsyncTask<IsoDep, Void, String> {

		public IsoDepReaderTask() {
		}

		@Override
		protected String doInBackground(IsoDep... params) {
			Log.e("PAYMENT ACTIVITY", "ISODEPREADER TASK DO in Back ground..");
			String errorStr = null;
			IsoDep isoDep = params[0];
			
			if (isoDep != null) {
				try {
					Log.d("isConnected", isoDep.isConnected()+"");
					if (!isoDep.isConnected()) {
						isoDep.connect();
						Log.d("isConnected2", isoDep.isConnected()+"");
					}
					isoDep.setTimeout(5000);
					ReaderModeAccess modeAccess = new ReaderModeAccess(isoDep);
					modeAccess.init();
					byte[] challengeResponse = modeAccess.getChallenge();
					String cardRN = modeAccess.getCardRN(challengeResponse);
					String purseRequest = sharedPreferences.getString("purseRequest", null);
					String purseData = modeAccess.getPurseData(purseRequest);
					Log.d("purseData2", purseData);
					if(purseData.length() < 48) {
						errorStr = StringConstants.ErrorDecription.INVALID_COMMAND_FROM_CARD;
						return errorStr;
					}
					String cardNumber = card.getCardNo(purseData);
					if(!cardNo.equals(cardNumber)) {
//						wsConnection.uploadReceiptData("", new RecieptReqError(StringConstants.ErrorCode.ERROR_CODE_15, StringConstants.ErrorDecription.INVALID_CARD));
						errorStr = StringConstants.ErrorRemarks.CARD_DIFFIRENT;
						return errorStr;
					}
					
					if(!excuseDebit) {
						// Execute "Debit Command"
						editor.putString("cardRN", cardRN);
						editor.putString("purseData", purseData);
						editor.commit();
						Log.d("debitCommand", "++CALL WS++"+System.currentTimeMillis());
						debitCommand = wsConnection.getDebitCommand();
						Log.d("debitCommand", "++RECIEVED WS++"+System.currentTimeMillis());
					}
					if (null != debitCommand) {
						if(!excuseDebit) {
							noReceiptResponse = true;
							//debitCommand = "90340000" + debitCommand + "00";
							excuseDebit = true;
						} 
						
						Log.d("debitCommand", debitCommand);
						String receiptData = modeAccess.getReceipt(debitCommand, wsConnection);
						// the mobile get receipt data from card
						noReceiptResponse = false;
						
//						check receiptData
						if(null != receiptData && !receiptData.contains("9000")) {
							Log.d("receiptData", receiptData);
							//check purse balance is updated or not
							modeAccess.getChallenge();
							String purseData2 = modeAccess.getPurseData(purseRequest);
							if(purseData2.length() < 48) {
								errorStr = StringConstants.ErrorDecription.INVALID_COMMAND_FROM_CARD;
								return errorStr;
							}
							String curentBal2 = String.valueOf(card.getPurseBal(purseData2));
							Log.d("curentBal2", curentBal2.toString());
							Log.d("purseBalance", purseBalance);
							// debit command is successful 
							boolean deductedBalance = false;
							boolean needAutoload = sharedPreferences.getBoolean("needAutoLoad", false);
							if(needAutoload) {
								String autoLoadAmt = sharedPreferences.getString("auloloadAmount", "0");
//								deductedBalance = card.checkCurrentBalanceWithAutoLoadAmt(curentBal2, purseBalance, paymentAmt, autoLoadAmt);
							} else {
//								deductedBalance = card.checkCurrentBalance(curentBal2, purseBalance, paymentAmt);
							}
							
							if(deductedBalance) {
								wsConnection.uploadReceiptData("", new RecieptReqError(StringConstants.ErrorCode.ERROR_CODE_01, StringConstants.ErrorDecription.DEBIT_COMMAND_SUCCESSFUL_BUT_NO_RESPONSE_FROM_CARD));
								editor.putString("merchantName", merchantName);
								editor.putString("paymentAmt", paymentAmt);
								editor.putString("prevBal", purseBalance);
								editor.putString("currentBal", String.valueOf(curentBal2));
								editor.commit();
								Intent in = new Intent(PaymentActivity.this, ConfirmationActivity.class);
								startActivity(in);
								finish();
								return null;
							} else {
								errorStr = StringConstants.ErrorRemarks.TRANX_FAILURE;
								return errorStr;
							}
						} else if(null != receiptData) {
							try {
								String receiptResponse = null;
								Log.d("receiptData", receiptData);
								Log.d("RECIEPT", "++CALL WS++"+System.currentTimeMillis());
								// Upload receipt data
								receiptResponse = wsConnection.uploadReceiptData(receiptData, new RecieptReqError(StringConstants.ErrorCode.ERROR_CODE_00, StringConstants.ErrorDecription.SUCCESSFUL));
								Log.d("RECIEPT", "++RESULT WS++"+System.currentTimeMillis());
								if(receiptResponse==null) {
									if(WebserviceConnection.recieptFault != null) {
										errorStr = WebserviceConnection.recieptFault.message;
										WebserviceConnection.recieptFault = null;
										return errorStr;
									} 
									
								}
							} catch(Exception e) {
								Log.e("uploadReceiptData Error", e.toString());
							} finally {
								new CurrentBalanceTask().execute(isoDep);
							}
						} else {
							errorStr=StringConstants.ErrorDecription.TAG_LOST;
							return errorStr;
						}
					}
					else{
						if(WebserviceConnection.debitCommandFault != null) {
							errorStr = WebserviceConnection.debitCommandFault.message;
							WebserviceConnection.debitCommandFault = null;
						} else {
							errorStr=StringConstants.ErrorDecription.CONNECTION_ISSUE;
						}
						return errorStr;
					}
					
				}catch (Exception e) {
					errorStr=StringConstants.ErrorDecription.TAG_LOST;
					Log.e("doInBackgroundError", e.toString());
				} finally {
					//errorStr="TAG LOST";
					try {
						isoDep.close();
					}catch (IOException e) {
						errorStr=StringConstants.ErrorDecription.CONNECTION_CLOSING_ISSUE;
						Log.e("doInBackgroundErrorfINALLY", e.getMessage());
					}
					
				}
			}
			return errorStr;
		}


		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (null != result) {
				PaymentFragment.error_textView.setVisibility(View.VISIBLE);
				PaymentFragment.error_content.setText(result);
			}
			if (dialog != null && dialog.isShowing())
	        {
				dialog.dismiss();
				dialog = null;
	        }
			if(PaymentFragment.dialog != null && PaymentFragment.dialog.isShowing()) {
				PaymentFragment.dialog.dismiss();
				PaymentFragment.dialog = null;
			}
		}
	}
	
	class CurrentBalanceTask extends AsyncTask<IsoDep, Void, String> {
		
		@Override
		protected String doInBackground(IsoDep... params) {
			String errorStr = null;
			try {
				IsoDep isoDep = params[0];
				Log.d("isConnected in CurrentBalanceTask", isoDep.isConnected()+"");
				if (!isoDep.isConnected()) {
					isoDep.connect();
				}
				isoDep.setTimeout(5000);
				ReaderModeAccess modeAccess = new ReaderModeAccess(isoDep);
				if (tapCardAgain || noReceiptResponse) {
					modeAccess.init();
				}
				tapCardAgain = true;
				modeAccess.getChallenge();
				String purseRequest = sharedPreferences.getString("purseRequest", null);
				String purseData = modeAccess.getPurseData(purseRequest);
				Log.d("purseData3", purseData);
				if(purseData.length() < 10) {
					errorStr = StringConstants.ErrorDecription.INVALID_COMMAND_FROM_CARD;
					return errorStr;
				}
				
				String cardNumber = card.getCardNo(purseData);
				if(!cardNo.equals(cardNumber)) {
//					wsConnection.uploadReceiptData("", new RecieptReqError(StringConstants.ErrorCode.ERROR_CODE_15, StringConstants.ErrorDecription.INVALID_CARD));
					errorStr = StringConstants.ErrorRemarks.CARD_DIFFIRENT;
					return errorStr;
				}
				
				String currentBalance = String.valueOf(card.getPurseBal(purseData));
				Log.d("preBal", purseBalance);
				Log.d("curentBal", currentBalance + "");
				// check the card balance is correct after payment is successful by comparing with  old and new balances.
				boolean deductedBalance = false;
				boolean needAutoload = sharedPreferences.getBoolean("needAutoLoad", false);
				if(needAutoload) {
					String autoLoadAmt = sharedPreferences.getString("auloloadAmount", "0");
//					deductedBalance = card.checkCurrentBalanceWithAutoLoadAmt(currentBalance, purseBalance, paymentAmt, autoLoadAmt);
				} else {
//					deductedBalance = card.checkCurrentBalance(currentBalance, purseBalance, paymentAmt);
				}
				
				if(!deductedBalance) {
//					wsConnection.uploadReceiptData("", new RecieptReqError(StringConstants.ErrorCode.ERROR_CODE_34, StringConstants.ErrorDecription.CARD_BALANCE_IS_NOT_CORRECT));
					errorStr = StringConstants.ErrorDecription.CARD_BALANCE_IS_NOT_CORRECT;
					return errorStr;
				}
				
				editor.putString("merchantName", merchantName);
				editor.putString("paymentAmt", paymentAmt);
				editor.putString("prevBal", purseBalance);
				editor.putString("currentBal", currentBalance);
				editor.commit();
				Intent in = new Intent(PaymentActivity.this, ConfirmationActivity.class);
				startActivity(in);
				finish();
			}catch (Exception e) {
				errorStr=StringConstants.ErrorDecription.TAG_LOST;
				Log.e("doInBackgroundError", e.toString());
			}
			return errorStr;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(null != result) {
				detectCard = false;
				PaymentFragment.error_textView.setVisibility(View.VISIBLE);
				PaymentFragment.error_content.setText(result);
			}
			if (dialog != null && dialog.isShowing())
	        {
				dialog.dismiss();
				dialog = null;
	        }
			if(PaymentFragment.dialog != null && PaymentFragment.dialog.isShowing()) {
				PaymentFragment.dialog.dismiss();
				PaymentFragment.dialog = null;
			}
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onBackPressed(){
		 new AlertDialog.Builder(this)
	        .setTitle(StringConstants.MessageRemarks.SCAN_AGAIN)
	        .setMessage(StringConstants.MessageRemarks.SCAN_AGAIN_MSG)
	        .setNegativeButton(android.R.string.no, null)
	        .setPositiveButton(android.R.string.yes, new android.content.DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface arg0, int arg) {
					Intent in = new Intent(PaymentActivity.this, SecondActivity.class);
					startActivity(in);
					finish();
				}
	        }).create().show();
	       
	}
	
}
