package com.wirecard.ezlink.fragment;

import java.util.List;

import com.example.R;
import com.wirecard.ezlink.activity.WelcomeActivity;
import com.wirecard.ezlink.constants.StringConstants;
import com.wirecard.ezlink.handle.WebserviceConnection;
import com.wirecard.ezlink.model.ReceiptRequest;
import com.wirecard.ezlink.sqlite.DBHelper;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PendingUploadTranxFragment extends Fragment {
	private WebserviceConnection wsConnection;
	private Dialog dialog;
	private boolean uploadToHost = false;
	TextView status;
	
	public PendingUploadTranxFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_pending_upload_tranx, container, false);
		status = (TextView) rootView.findViewById(R.id.status);
		wsConnection = new WebserviceConnection(getActivity());
		//check there are any Receipt requests have send to host yet.
		DBHelper db = new DBHelper(getActivity());
		List<ReceiptRequest> list = db.getAllReceiptRequest();
		uploadToHost = list.isEmpty()? false:true;
		Log.d("uploadToHost", String.valueOf(uploadToHost));
		
		if(uploadToHost) {
			dialog = ProgressDialog.show(getActivity(), StringConstants.MessageRemarks.THERE_ARE + list.size() 
					+ StringConstants.MessageRemarks.RECEIPT_REQUEST_UPLOAD, StringConstants.MessageRemarks.PLEASE_WAIT, true);
			new ReceiptAsyncTask().execute(list);
		} else {
			status.setText("There is(are) no pending upload transaction(s)");
		}
		return rootView;
	}

	private class ReceiptAsyncTask extends AsyncTask<List<ReceiptRequest>, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(List<ReceiptRequest>... params) {
			
			for (int i = 0; i < params[0].size(); i++) {
				try {
					ReceiptRequest receipt = params[0].get(i);
					publishProgress(params[0].size() - i);
					if(wsConnection.uploadReceiptDataAgain(receipt) == false) {
						return false;
					}
				} catch (Exception e) {
					Log.e("Pending Upload Tranx Error: ", e.toString());
					return false;
				}
			}

			return true;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			dialog.setTitle("There are " + values[0] + " Receipt Request need to upload to host");
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result == false) {
				status.setText("There is some problems to upload pending transaction");
			} else {
				status.setText("There is no pending upload transaction");
			}
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
		}
		
	}
}
