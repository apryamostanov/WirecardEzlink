package com.wirecard.ezlink.fragment;

import com.example.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ConfirmationFragment extends Fragment {
	
	public ConfirmationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_confirmation, container, false);
		TextView cardNo_textView = (TextView) rootView.findViewById(R.id.cardNo_textView);
		TextView merchantName_textView = (TextView) rootView.findViewById(R.id.merchantName_textView);
		TextView paymentAmt_textView = (TextView) rootView.findViewById(R.id.paymentAmt_textView);
		TextView prevBal_textView = (TextView) rootView.findViewById(R.id.prevBal_textView);
		TextView currentBal_textView = (TextView) rootView.findViewById(R.id.currentBal_textView);
		// TextView confirm_guide_textView = (TextView)
		// findViewById(R.id.confirm_guide_textView);

		Bundle b = getActivity().getIntent().getExtras();
		cardNo_textView.setText(b.getString("cardNo"));
		merchantName_textView.setText(b.getString("merchantName"));
		paymentAmt_textView.setText("$" + b.getString("paymentAmt"));
		prevBal_textView.setText("$" + b.getString("prevBal"));
		currentBal_textView.setText("$" + b.getString("currentBal"));
		return rootView;
	}

}
