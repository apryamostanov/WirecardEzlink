package com.wirecard.ezlink.activity;

import java.util.List;
import java.util.Timer;

import com.example.R;
import com.wirecard.ezlink.handle.Util;
import com.wirecard.ezlink.handle.WebserviceConnection;
import com.wirecard.ezlink.model.ReceiptRequest;
import com.wirecard.ezlink.sqlite.DBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends Activity {
	private WebserviceConnection wsConnection;
	private Dialog dialog;
	private boolean uploadToHost = false;
	ImageView welcome;
	AnimationDrawable myAnimationDrawable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);


		welcome = (ImageView) findViewById(R.id.welcome);
		myAnimationDrawable = (AnimationDrawable) welcome
				.getDrawable();
/*		
NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if(!(mNfcAdapter.isEnabled())){
			//finish();
			
			 new AlertDialog.Builder(this)
		        .setTitle("Enable NFC..!!")
		        .setMessage("Please Enable NFC to do payment..")
		       // .setNegativeButton(android.R.string.no, null)
		        .setPositiveButton(android.R.string.yes, new android.content.DialogInterface.OnClickListener() {
		        	/*
		            public void onClick(DialogInterface arg0, int arg1) {
		                WelcomeActivity.super.onBackPressed();
		            }
		            

					
					public void onClick(DialogInterface arg0, int arg) {
						
						Intent setnfc = new Intent(Settings.ACTION_NFC_SETTINGS);                             
						startActivity(setnfc);	
						
					}

					
		        }).create().show();
			
			
			
		}
	*/	
		
		wsConnection = new WebserviceConnection(this);
		//check there are any Receipt requests have send to host yet.
		DBHelper db = new DBHelper(this);
		List<ReceiptRequest> list = db.getAllReceiptRequest();
		uploadToHost = list.isEmpty()? false:true;
		Log.d("uploadToHost", String.valueOf(uploadToHost));
		
		if(uploadToHost) {
			dialog = ProgressDialog.show(WelcomeActivity.this, "There are " + list.size() + " Receipt Request need to upload to host", "Please wait...", true);
		} 
		new ReceiptAsyncTask().execute(list);
	}
	
	private class ReceiptAsyncTask extends AsyncTask<List<ReceiptRequest>, Integer, Void> {
		@Override
		protected Void doInBackground(List<ReceiptRequest>... params) {
			try {
				for(int i=0; i<params[0].size();i++) {
					ReceiptRequest receipt = params[0].get(i);
					publishProgress(params[0].size() - i);
//					wsConnection.uploadReceiptDataAgain(receipt);
				}
			} catch(Exception e) {
				
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			dialog.setTitle("There are " + values[0] + " Receipt Request need to upload to host");
		}

		@Override
		protected void onPostExecute(Void result) {
			if(uploadToHost) {
				dialog.dismiss();
			}
//					welcome.setBackgroundResource(R.drawable.welcome_animation);
//					AnimationDrawable drawable = (AnimationDrawable) welcome.getBackground();
					final AnimationDrawable drawable = (AnimationDrawable) welcome.getDrawable();
					drawable.start();
					checkIfAnimationDone(drawable);
		}
		
	}
	
	private void checkIfAnimationDone(AnimationDrawable anim){
	    final AnimationDrawable a = anim;
	    int timeBetweenChecks = 300;
	    Handler h = new Handler();
	    h.postDelayed(new Runnable(){
	        public void run(){
	            if (a.getCurrent() != a.getFrame(a.getNumberOfFrames() - 1)){
	                checkIfAnimationDone(a);
	            } else{
	            	//finish();
        			Intent i = new Intent(getBaseContext(), SecondActivity.class);
        			startActivity(i);
        			finish();
	            }
	        }
	    }, timeBetweenChecks);
	}
	
	public abstract class CustomAnimationDrawableNew extends AnimationDrawable {

		/** Handles the animation callback. */
		Handler mAnimationHandler;

		public CustomAnimationDrawableNew(AnimationDrawable aniDrawable) {
		    /* Add each frame to our animation drawable */
		    for (int i = 0; i < aniDrawable.getNumberOfFrames(); i++) {
		        this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i));
		    }
		}

		@Override
		public void start() {
		    super.start();
		    /*
		     * Call super.start() to call the base class start animation method.
		     * Then add a handler to call onAnimationFinish() when the total
		     * duration for the animation has passed
		     */
		    mAnimationHandler = new Handler();
		    mAnimationHandler.postDelayed(new Runnable() {

		        public void run() {
		            onAnimationFinish();
		        }
		    }, getTotalDuration());

		}

		/**
		 * Gets the total duration of all frames.
		 * 
		 * @return The total duration.
		 */
		public int getTotalDuration() {

		    int iDuration = 0;

		    for (int i = 0; i < this.getNumberOfFrames(); i++) {
		        iDuration += this.getDuration(i);
		    }

		    return iDuration;
		}

		/**
		 * Called when the animation finishes.
		 */
		abstract void onAnimationFinish();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
