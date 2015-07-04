package com.wirecard.ezlink.fragment;

import static com.wirecard.ezlink.listView.Constant.FIRST_COLUMN;
import static com.wirecard.ezlink.listView.Constant.FOURTH_COLUMN;
import static com.wirecard.ezlink.listView.Constant.SECOND_COLUMN;
import static com.wirecard.ezlink.listView.Constant.THIRD_COLUMN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.R;
import com.wirecard.ezlink.handle.WebserviceConnection;
import com.wirecard.ezlink.listView.ListviewAdapter;
import com.wirecard.ezlink.model.ErrorCode;
import com.wirecard.ezlink.webservices.tranxHistory.VDGTranxHistoryDetail;
import com.wirecard.ezlink.webservices.tranxHistory.VDGTranxList;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class TransactionHistoryFragment extends Fragment {
	private WebserviceConnection wsConnection;
	public static ArrayList<HashMap<String, String>> list;
	private ListView lview;
	private TextView tranxHistoryError;
	private Dialog dialog;
	
	public TransactionHistoryFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_transaction_history,
				container, false);
		
		dialog = ProgressDialog.show(getActivity(), "Processing", "Please wait while your transaction history is being processed...", true);
		wsConnection = new WebserviceConnection(getActivity());
		lview = (ListView) rootView.findViewById(R.id.listview);
		tranxHistoryError = (TextView) rootView
				.findViewById(R.id.tranxHistoryError);
		if (list != null) {
			ListviewAdapter adapter = new ListviewAdapter(getActivity(), list);
			lview.setAdapter(adapter);
		} else {
			new TransactionHistory().execute();
		}

		return rootView;
	}

	class TransactionHistory extends AsyncTask<Void, Void, VDGTranxList> {

		@Override
		protected VDGTranxList doInBackground(Void... params) {
			VDGTranxList tranxList = null;
			try {
				Bundle args = getArguments();
				 if (args != null && args.containsKey("cardNo")) {
				     String cardNo = args.getString("cardNo");
				     tranxList = wsConnection.getTranxHistory(cardNo);
				 }
			} catch (Exception e) {

			}
			return tranxList;
		}

		@Override
		protected void onPostExecute(VDGTranxList tranxList) {
			super.onPostExecute(tranxList);

			if (tranxList == null) {
				tranxHistoryError.setText(ErrorCode.getConnectionIssue());
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

				ListviewAdapter adapter = new ListviewAdapter(getActivity(),
						list);
				lview.setAdapter(adapter);
			} else {
				tranxHistoryError.setText("There is no transaction history");
			}
			
			if (dialog != null && dialog.isShowing())
	        {
				dialog.dismiss();
				dialog = null;
	        }
		}
	}

}
