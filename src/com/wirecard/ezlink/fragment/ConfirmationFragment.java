package com.wirecard.ezlink.fragment;

import com.example.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ConfirmationFragment extends Fragment {
	private SharedPreferences sharedPreferences;
	
	public ConfirmationFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_confirmation, container, false);
		sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		
		TextView cardNo_textView = (TextView) rootView.findViewById(R.id.cardNo_textView);
		TextView merchantName_textView = (TextView) rootView.findViewById(R.id.merchantName_textView);
		TextView paymentAmt_textView = (TextView) rootView.findViewById(R.id.paymentAmt_textView);
		TextView prevBal_textView = (TextView) rootView.findViewById(R.id.prevBal_textView);
		TextView currentBal_textView = (TextView) rootView.findViewById(R.id.currentBal_textView);
		// TextView confirm_guide_textView = (TextView)
		// findViewById(R.id.confirm_guide_textView);

		Bundle b = getActivity().getIntent().getExtras();
		cardNo_textView.setText(sharedPreferences.getString("cardNo", null));
		merchantName_textView.setText(sharedPreferences.getString("merchantName", null));
		paymentAmt_textView.setText("$" +sharedPreferences.getString("paymentAmt", null));
		prevBal_textView.setText("$" + sharedPreferences.getString("prevBal", null));
		currentBal_textView.setText("$" + sharedPreferences.getString("currentBal", null));
		return rootView;
	}

}
