package com.wirecard.ezlink.handle;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.wirecard.ezlink.model.ReceiptRequest;

public class ReceiptAsyncTask extends AsyncTask<List<ReceiptRequest>, Integer, Boolean> {
	private WebserviceConnection wsConnection;
	private Dialog dialog;
	
	public ReceiptAsyncTask(Context context, Dialog dialog) {
		wsConnection = new WebserviceConnection(context);
		this.dialog = dialog;
	}
	
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