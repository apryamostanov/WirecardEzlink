package com.wirecard.ezlink.fragment;

import static com.wirecard.ezlink.listView.Constant.FIRST_COLUMN;
import static com.wirecard.ezlink.listView.Constant.FOURTH_COLUMN;
import static com.wirecard.ezlink.listView.Constant.SECOND_COLUMN;
import static com.wirecard.ezlink.listView.Constant.THIRD_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.R;
import com.wirecard.ezlink.activity.SecondActivity;
import com.wirecard.ezlink.activity.TranxHistoryActivity;
import com.wirecard.ezlink.activity.WelcomeActivity;
import com.wirecard.ezlink.constants.StringConstants;
import com.wirecard.ezlink.handle.WebserviceConnection;
import com.wirecard.ezlink.listView.ListviewAdapter;
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
import android.widget.ListView;
import android.widget.TextView;

public class PendingUploadTranxFragment extends Fragment {
	private WebserviceConnection wsConnection;
	private Dialog dialog;
	private boolean uploadToHost = false;
	TextView status;
	private ListView lview;
	TextView tranx_label;
	
	public PendingUploadTranxFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_pending_upload_tranx, container, false);
		status = (TextView) rootView.findViewById(R.id.status);
		tranx_label = (TextView) rootView.findViewById(R.id.tranx_label);
		wsConnection = new WebserviceConnection(getActivity());
		lview = (ListView) rootView.findViewById(R.id.listview);
		
		status.setText(SecondActivity.pendingUploadStatus);
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> temp;
		List<ReceiptRequest> pendingUploadList = SecondActivity.pendingUploadList;
		if(!pendingUploadList.isEmpty()) {
			tranx_label.setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < pendingUploadList.size(); i++) {
			temp = new HashMap<String, String>();
			ReceiptRequest receipt = pendingUploadList.get(i);
			temp.put(FIRST_COLUMN, receipt.getMerchantNo());
			temp.put(SECOND_COLUMN, String.valueOf(receipt.getAmount()));
			temp.put(THIRD_COLUMN, receipt.getErrorDescript());
			temp.put(FOURTH_COLUMN, receipt.getTranxDate());
			list.add(temp);
		}

		ListviewAdapter adapter = new ListviewAdapter(
				getActivity(), list);
		lview.setAdapter(adapter);
		return rootView;
	}
}
