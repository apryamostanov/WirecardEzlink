package com.wirecard.ezlink.activity;

import com.example.R;
import com.wirecard.ezlink.fragment.ConfirmationFragment;
import com.wirecard.ezlink.fragment.ContactFragment;
import com.wirecard.ezlink.fragment.HelpFragment;
import com.wirecard.ezlink.fragment.PaymentFragment;
import com.wirecard.ezlink.fragment.TagCardFragment;
import com.wirecard.ezlink.fragment.TermsAndConditionsFragment;
import com.wirecard.ezlink.fragment.TransactionHistoryFragment;
import com.wirecard.ezlink.navigationdrawer.DrawerItemCustomAdapter;
import com.wirecard.ezlink.navigationdrawer.ObjectDrawerItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.tech.IsoDep;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Vibrator;
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

public class ConfirmationActivity extends FragmentActivity {
	private PendingIntent pendingIntent;
	private IntentFilter[] filters;
	private String[][] techList;
	private NfcAdapter mNfcAdapter = null;
	private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private FragmentManager fragmentManager;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmation);

		fragmentManager = getSupportFragmentManager();
		// for proper titles
		mTitle = mDrawerTitle = getTitle();
		
		// initialize properties
		mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_confirmation);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        // list the drawer items
        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[5];
        
        drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_action_copy, "Confirmation");
        drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_action_share, "Transaction History");
        drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_action_share, "Contact");
        drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_action_share, "Terms & Conditions");
        drawerItem[4] = new ObjectDrawerItem(R.drawable.ic_action_share, "Help");
        
        // Pass the folderData to our ListView adapter
        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.listview_item_row, drawerItem);
        
        // Set the adapter for the list view
        mDrawerList.setAdapter(adapter);
        
        // set the item click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        
        // for app icon control for nav drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
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
        
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		filters = new IntentFilter[] { new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED) };
		techList = new String[][] { new String[] { IsoDep.class.getName() } };
		
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
            fragment = new ConfirmationFragment();
            break;
        case 1:
            fragment = new TagCardFragment();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle("Closing Application..!!")
				.setMessage(
						"Are you sure you want to exit from the application..??")
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes,
						new android.content.DialogInterface.OnClickListener() {
							/*
							 * public void onClick(DialogInterface arg0, int
							 * arg1) { WelcomeActivity.super.onBackPressed(); }
							 */

							public void onClick(DialogInterface arg0, int arg) {
								// ConfirmationActivity.super.onBackPressed();
								getIntent().setFlags(
										Intent.FLAG_ACTIVITY_NO_HISTORY);
								finish();
								// System.exit(0);

							}

						}).create().show();
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
		TagCardFragment tagCardFragment = (TagCardFragment) fragmentManager.findFragmentByTag("fragment1");
		if(tagCardFragment != null) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			final IsoDep isoDep = IsoDep.get(tag);
			ProgressDialog dialog = ProgressDialog.show(ConfirmationActivity.this, "Please hold on to your card", "Scanning...", true);
			new com.wirecard.ezlink.handle.IsoDepReaderTask(this, null, null, dialog, true, "ConfirmationActivity").execute(isoDep);
			}
		}
	}
}
