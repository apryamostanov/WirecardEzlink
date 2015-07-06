package com.wirecard.ezlink.activity;

import static com.wirecard.ezlink.listView.Constant.FIRST_COLUMN;
import static com.wirecard.ezlink.listView.Constant.FOURTH_COLUMN;
import static com.wirecard.ezlink.listView.Constant.SECOND_COLUMN;
import static com.wirecard.ezlink.listView.Constant.THIRD_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.R;
import com.wirecard.ezlink.constants.StringConstants;
import com.wirecard.ezlink.handle.ConnectionDetector;
import com.wirecard.ezlink.handle.IsoDepReaderTask;
import com.wirecard.ezlink.handle.WebserviceConnection;
import com.wirecard.ezlink.listView.ListviewAdapter;
import com.wirecard.ezlink.webservices.tranxHistory.VDGTranxHistoryDetail;
import com.wirecard.ezlink.webservices.tranxHistory.VDGTranxList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class TranxHistoryActivity extends Activity {
	private WebserviceConnection wsConnection;
	private ArrayList<HashMap<String, String>> list;
	private ListView lview;
	private TextView tranxHistoryError;
	private TextView cardNoTextView;
	private TextView balanceTextView;
	private TextView expiryDateTextView;
	private Dialog dialog;
	private NfcAdapter mNfcAdapter = null;
	private PendingIntent pendingIntent;
	private IntentFilter[] filters;
	private String[][] techList;
	private ConnectionDetector cd;
	private String cardNo;
	private String preActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_transaction_history);
		setTitle("Transaction History");
		cd = new ConnectionDetector(this);
		dialog = ProgressDialog.show(this,
				StringConstants.MessageRemarks.PROCESSING,
				StringConstants.MessageRemarks.TRANX_HISTORY, true);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		filters = new IntentFilter[] { new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED) };
		techList = new String[][] { new String[] { IsoDep.class.getName() } };
		wsConnection = new WebserviceConnection(this);
		lview = (ListView) findViewById(R.id.listview);
		tranxHistoryError = (TextView) findViewById(R.id.tranxHistoryError);
		cardNoTextView = (TextView) findViewById(R.id.cardNo_textView);
		balanceTextView = (TextView) findViewById(R.id.purseBalance_textView);
		expiryDateTextView = (TextView) findViewById(R.id.expiryDate_textView);

		Bundle args = getIntent().getExtras();
		cardNo = args.getString("cardNo");
		cardNoTextView.setText(cardNo);
		balanceTextView.setText(args.getString("purseBalance"));
		expiryDateTextView.setText(args.getString("expiryDate"));
		preActivity = args.getString("currentActivity");
		new TransactionHistory().execute();

	}

	@Override
	protected void onResume() {
		super.onResume();
		cd.ensureSensorIsOn();
		mNfcAdapter.enableForegroundDispatch(this, pendingIntent, filters,
				techList);
	}

	@Override
	public void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	public void onBackPressed() {
		Intent in = new Intent(TranxHistoryActivity.this, SecondActivity.class);
		if (preActivity.equals("SecondActivity")) {
			in = new Intent(TranxHistoryActivity.this, SecondActivity.class);
		} else if (preActivity.equals("NFCActivity")) {
			in = new Intent(TranxHistoryActivity.this, NFCActivity.class);
		} else if (preActivity.equals("PaymentActivity")) {
			in = new Intent(TranxHistoryActivity.this, PaymentActivity.class);
		} else {
			in = new Intent(TranxHistoryActivity.this, ConfirmationActivity.class);
		}
		startActivity(in);
		finish();
	}

	class TransactionHistory extends AsyncTask<Void, Void, VDGTranxList> {

		@Override
		protected VDGTranxList doInBackground(Void... params) {
			VDGTranxList tranxList = null;
			try {
				tranxList = wsConnection.getTranxHistory(cardNo);
			} catch (Exception e) {
			}
			return tranxList;
		}

		@Override
		protected void onPostExecute(VDGTranxList tranxList) {
			super.onPostExecute(tranxList);

			if (null == tranxList) {
				tranxHistoryError.setText(StringConstants.ErrorDecription.CONNECTION_ISSUE);
			} else if (tranxList != null && tranxList.size() > 0) {
				VDGTranxHistoryDetail historyDetail;
				list = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> temp;

				for (int i = 0; i < tranxList.size(); i++) {
					temp = new HashMap<String, String>();
					historyDetail = tranxList.get(i);
					temp.put(FIRST_COLUMN, historyDetail.MERCHANTNAME);
					temp.put(SECOND_COLUMN, "$" + historyDetail.AMOUNT);
					temp.put(THIRD_COLUMN, historyDetail.STATUS);
					temp.put(FOURTH_COLUMN, historyDetail.TRANXDATE);
					list.add(temp);
					Log.d("History Detail " + i, historyDetail.MERCHANTNAME
							+ "/" + historyDetail.AMOUNT + "/"
							+ historyDetail.TRANXDATE + "/"
							+ historyDetail.STATUS);
				}

				ListviewAdapter adapter = new ListviewAdapter(
						TranxHistoryActivity.this, list);
				lview.setAdapter(adapter);
			} else {
				tranxHistoryError
						.setText(StringConstants.ErrorRemarks.NO_TRANX_HISTORY);
			}

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}
		}
	}
}
