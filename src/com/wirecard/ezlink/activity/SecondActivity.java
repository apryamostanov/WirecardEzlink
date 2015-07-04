/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wirecard.ezlink.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.R;
import com.wirecard.ezlink.constants.StringConstants;
import com.wirecard.ezlink.fragment.HelpFragment;
import com.wirecard.ezlink.fragment.PendingUploadTranxFragment;
import com.wirecard.ezlink.fragment.ScanFragment;
import com.wirecard.ezlink.fragment.TagCardFragment;
import com.wirecard.ezlink.handle.SoundControl;
import com.wirecard.ezlink.handle.Util;
import com.wirecard.ezlink.handle.ConnectionDetector;
import com.wirecard.ezlink.handle.IsoDepReaderTask;
import com.wirecard.ezlink.handle.WebserviceConnection;
import com.wirecard.ezlink.model.ReceiptRequest;
import com.wirecard.ezlink.sqlite.DBHelper;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends FragmentActivity implements ActionBar.TabListener {
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    String[] pageTitle;
    private PendingIntent pendingIntent;
	private IntentFilter[] filters;
	private String[][] techList;
	private NfcAdapter mNfcAdapter = null;
	private boolean uploadToHost = false;
	private Dialog dialog;
	private ConnectionDetector cd;
	private boolean firstTime = true;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        cd = new ConnectionDetector(this);
        
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);
        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        
        // Set color for Action Bar
//        ColorDrawable colorDrawable = new ColorDrawable();
//        colorDrawable.setColor(0x036687);
//        actionBar.setBackgroundDrawable(colorDrawable);
//        invalidateOptionsMenu();
        
        pageTitle = getResources().getStringArray(R.array.page_title_array);
        int[] icon = {R.drawable.camera, R.drawable.history, R.drawable.upload};
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
        	Tab tab = actionBar.newTab();
			tab.setText(pageTitle[i]);
			tab.setIcon(icon[i]);
			tab.setTabListener(this);
			actionBar.addTab(tab);
        }
        
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(
				this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		filters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) };
		techList = new String[][] {new String[] { IsoDep.class.getName() } };
		
		Bundle args = getIntent().getExtras();
		
		firstTime = null!=args?args.getBoolean("firstTime"):true;
		Log.e("-------First time---------: " , firstTime+"");
        if (firstTime && Util.getStartScanPref(getApplicationContext()))
        {
        	Intent intent = new Intent(this, QRCodeScannerActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        	startActivity(intent);
            finish();
//            return;
        }
	}

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
		public Fragment getItem(int i) {
			Fragment fragment = null;
			
			switch (i) {
			case 0:
				fragment = new ScanFragment();
				break;
			case 1:
				fragment = new TagCardFragment();
				break;
			case 2:
				fragment = new PendingUploadTranxFragment();
				break;
			}

			return fragment;
		}

        @Override
        public int getCount() {
            return 3;
        }
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
	protected void onNewIntent(Intent intent) {
		Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + mViewPager.getCurrentItem());
		String fragmentName = fragment.toString();
		if(fragmentName.contains("TagCardFragment")) {
			((Vibrator)getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100L);
			Log.d("tagCardFragment is ", "not null");
			handleIntent(intent);
		}
	}
	
	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			ProgressDialog dialog = ProgressDialog.show(SecondActivity.this, StringConstants.MessageRemarks.HOLD_CARD,
					StringConstants.MessageRemarks.SCANNING, true);
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			IsoDep isoDep = IsoDep.get(tag);
			new IsoDepReaderTask(this, null, null, dialog, true).execute(isoDep);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.items, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.phone:
			Intent i = new Intent(getApplicationContext(), UserSettingActivity.class);
            startActivityForResult(i, 0);
			Toast.makeText(getBaseContext(), "getVibratePref: " + Util.getVibratePref(this) ,
					Toast.LENGTH_SHORT).show();
			break;
		}

		return true;
	}
	
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	        .setTitle(StringConstants.MessageRemarks.CLOSING_APP)
	        .setMessage(StringConstants.MessageRemarks.ASKING_EXIT)
	        .setNegativeButton(android.R.string.no, null)
	        .setPositiveButton(android.R.string.yes, new android.content.DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface arg0, int arg) {
					getIntent().setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					SecondActivity.super.onBackPressed();
					finish();
				}
	        }).create().show();
	}
}
