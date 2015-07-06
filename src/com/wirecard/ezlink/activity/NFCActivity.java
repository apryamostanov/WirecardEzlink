package com.wirecard.ezlink.activity;

import java.io.IOException;
import java.math.BigDecimal;

import com.example.R;
import com.wirecard.ezlink.constants.StringConstants;
import com.wirecard.ezlink.fragment.HelpFragment;
import com.wirecard.ezlink.fragment.NFCFragment;
import com.wirecard.ezlink.fragment.TagCardFragment;
import com.wirecard.ezlink.fragment.TermsAndConditionsFragment;
import com.wirecard.ezlink.handle.Util;
import com.wirecard.ezlink.handle.ConnectionDetector;
import com.wirecard.ezlink.handle.IsoDepReaderTask;
import com.wirecard.ezlink.handle.ReaderModeAccess;
import com.wirecard.ezlink.handle.WebserviceConnection;
import com.wirecard.ezlink.model.Card;
import com.wirecard.ezlink.model.QRCode;
import com.wirecard.ezlink.navigationdrawer.DrawerItemCustomAdapter;
import com.wirecard.ezlink.navigationdrawer.ObjectDrawerItem;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NFCActivity extends FragmentActivity {
	private NfcAdapter mNfcAdapter;
	private QRCode qrCode;
	private WebserviceConnection wsConnection;
	private ConnectionDetector cd;
	private boolean detectCard;
	private SharedPreferences sharedpreferences;
	private ProgressDialog dialog;
	private TextView error_content;
	private TextView error;
	private IsoDepReaderTask isoDepReaderTask;
	private PendingIntent pendingIntent;
	private IntentFilter[] filters;
	private String[][] techList;
	public static IsoDep isoDepStatic;
	private ReaderModeAccess readerModeAccess;
	private Card card;
	private Util common;
	public static boolean showToastMgs;
	private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private FragmentManager fragmentManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabcard);
		
		fragmentManager = getSupportFragmentManager();
		// for proper titles
		mTitle = mDrawerTitle = getTitle();

		// initialize properties
		mNavigationDrawerItemTitles = getResources().getStringArray(
				R.array.navigation_drawer_tagcard);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// list the drawer items
		ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[4];

		drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_action_copy,	StringConstants.MessageRemarks.TAP_CARD);
		drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_contact, StringConstants.MessageRemarks.TRANX_HISTORY_STR);
		drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_term, StringConstants.MessageRemarks.TERMS_CONDITIONS);
		drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_action_share, StringConstants.MessageRemarks.HELP);

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

		qrCode = QRCode.getInstance();
		isoDepReaderTask = new IsoDepReaderTask(this);
		wsConnection = new WebserviceConnection(this);
		cd = new ConnectionDetector(this);
		sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		error = (TextView) findViewById(R.id.error);
		error_content = (TextView) findViewById(R.id.error_content);
		card = new Card();
		common = new Util();
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(
				this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		filters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) };
		techList = new String[][] {new String[] { IsoDep.class.getName() } };
		showToastMgs = true;
		
		if (savedInstanceState == null) {
			// on first time display view for first nav item
			selectItem(0);
		}
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
            fragment = new NFCFragment();
            break;
        case 1:
            fragment = new TagCardFragment();
            break;
        case 2:
        	fragment = new TermsAndConditionsFragment();
        	break;
        case 3:
        	fragment = new HelpFragment();
        	break;
        default:
            break;
        }
        
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "fragment"+position).commit();
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
            if(position == 0) {
            	isoDepReaderTask.startCountDownTimer();
            } else {
            	isoDepReaderTask.cancelCountDownTimer();
            }
        } else {
            // error in creating fragment
            Log.e("NFCActivity", "Error in creating fragment");
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
    }
	
	@Override
	public void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	protected void onNewIntent(Intent intent) {
		if(Util.getVibratePref(this)) {
			((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100L);
		}
		if(null != fragmentManager.findFragmentByTag("fragment0") || null != fragmentManager.findFragmentByTag("fragment1")) {
			handleIntent(intent);
		}
	}
	
	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			dialog = ProgressDialog.show(NFCActivity.this, StringConstants.MessageRemarks.HOLD_CARD, StringConstants.MessageRemarks.SCANNING, true);
			Log.e("NFC ACTIVITY", "Calling IsoDepReaderTask..");
			detectCard = true;
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			IsoDep isoDep = IsoDep.get(tag);
			TagCardFragment tagCardFragment = (TagCardFragment) fragmentManager.findFragmentByTag("fragment1");
			if(tagCardFragment != null) {
				Log.d("tagCardFragment is ", "not null");
				isoDepReaderTask = new IsoDepReaderTask(this, sharedpreferences, qrCode, dialog, true, "NFCActivity");
			} else {
				isoDepReaderTask = new IsoDepReaderTask(this, sharedpreferences, qrCode, dialog, false, "NFCActivity");
			}
			isoDepReaderTask.execute(isoDep);
//			new IsoDepReaderTask2().execute(isoDep);
		}
	}
	
	/*class IsoDepReaderTask2 extends AsyncTask<IsoDep, Void, String> {

		@Override
		protected String doInBackground(IsoDep... params) {
			Log.e("IsoDepReaderTask", "Do in Background..");
			String result = null;
			detectCard = true;
			IsoDep isoDep = params[0];
			Editor editor = sharedpreferences.edit();
			if (isoDep != null) {
				try {
					if (!isoDep.isConnected()) {
						isoDep.connect();
					}
					isoDep.setTimeout(5000);
					readerModeAccess = new ReaderModeAccess(isoDep);
					
					readerModeAccess.init();
					readerModeAccess.getChallenge();
					String purseData = readerModeAccess.getPurseData();
					if(purseData.length() < 48) {
//						errorCode.sendErrorToReceipt(qrCode, ErrorCode.getErrorCode24() + ":" + ErrorCode.getInvalidCommandFromCard());
						result = ErrorCode.getInvalidCommandFromCard();
						return result;
					}
					String cardNo = card.getCardNo(purseData);
					String purseExpiryDate = card.getPurseExpiryDate(purseData);
					String purseStatus = card.getPusrseStatus(purseData);
					String balance = card.getPurseBal(purseData);

					Log.d("card no", cardNo);
					Log.d("balance", balance);
					Log.d("expiry date", purseExpiryDate);
					Log.d("purseStatus", purseStatus);

					//Check expiry date
					boolean cardExpiry = card.checkExpiryDate(purseExpiryDate);
					if(cardExpiry) {
						result = "Card is expired";
						return result;
					}
					//Check card bin
					boolean checkCardBin = card.checkCardBin(cardNo);
					Log.d("CARDBIN",String.valueOf(checkCardBin));
					if(!checkCardBin) {
						result = "This is not an ez-link card as it is not issued by EZ-Link Pte Ltd.";
//						errorCode.sendErrorToReceipt(qrCode, ErrorCode.getErrorCode15() + ":" + ErrorCode.getInvalidCard());
						return result;
					}
					
					//save data into session
					editor.putString("cardNo", cardNo);
					editor.putString("terminalRN", readerModeAccess.getTerminalRN());
					editor.putString("balance", String.valueOf(balance));
					editor.putString("purseRequest", readerModeAccess.getPurseRequest());
					editor.commit();
					
					Bundle b = new Bundle();
					b.putSerializable("qrCode", qrCode);
					
					//Intent in = new Intent(_context, PaymentActivity.class);
					Intent in = new Intent(NFCActivity.this, PaymentActivity.class);
					in.putExtras(b);
					Log.d("PAY","GONNA START PAYMENT ACTIVITY");
					startActivity(in);
					Log.d("PAY","AFTER START PAYMENT ACTIVITY");
					finish();
					Log.d("PAY","AFTER FINISH");
				} catch (Exception e) {
					result = e.getMessage();
					// errorCode.sendError(qrCode, result);
					Log.e("doInBackgroundError CATCH", "" + e.toString());
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
				error.setVisibility(View.VISIBLE);
				error_content.setText(response);
			}
			if (dialog != null && dialog.isShowing())
	        {
				dialog.dismiss();
				dialog = null;
	        }
		}
	}*/
	
	@Override
	public void onBackPressed(){
		 new AlertDialog.Builder(this)
	        .setTitle(StringConstants.MessageRemarks.SCAN_AGAIN)
	        .setMessage(StringConstants.MessageRemarks.SCAN_AGAIN_MSG)
	        .setNegativeButton(android.R.string.no, null)
	        .setPositiveButton(android.R.string.yes, new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg) {
					showToastMgs = false;
					isoDepReaderTask.cancelCountDownTimer();
					Intent in = new Intent(NFCActivity.this, SecondActivity.class);
					startActivity(in);
					finish();
					NFCActivity.super.onBackPressed();
				}
	        }).create().show();
	}
}	
