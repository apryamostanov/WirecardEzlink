package com.wirecard.ezlink.fragment;

import com.example.R;
import com.wirecard.ezlink.activity.PaymentActivity;
import com.wirecard.ezlink.activity.PaymentActivity.IsoDepReaderTask;
import com.wirecard.ezlink.constants.StringConstants;
import com.wirecard.ezlink.handle.WebserviceConnection;
import com.wirecard.ezlink.model.QRCode;
import com.wirecard.ezlink.webservices.receipt.RecieptReqError;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PaymentFragment extends Fragment {
	public static TextView error_textView;
	public static TextView error_content;
	private TextView guide_textView;
	private String paymentAmt;
	private String merchantName;
	private String purseBalance;
	private TextView cardNo_textView;
	private TextView purseBalance_textView;
	private TextView paymentAmt_textView;
	private TextView merchantName_textView;
	private TextView merchantRef_textView;
	private Button payButton;
	private QRCode qrCode;
	private SharedPreferences sharedPreferences;
	private String cardNo;
	private WebserviceConnection wsConnection;
	public static ProgressDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_payment, container, false);

		sharedPreferences = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
		wsConnection = new WebserviceConnection(getActivity());
		cardNo_textView = (TextView) rootView.findViewById(R.id.cardNo_textView1);
		purseBalance_textView = (TextView) rootView.findViewById(R.id.purseBalance_textView);
		paymentAmt_textView = (TextView) rootView.findViewById(R.id.paymentAmt_textView1);
		merchantName_textView = (TextView) rootView.findViewById(R.id.merchantName_textView1);
		merchantRef_textView = (TextView) rootView.findViewById(R.id.merchantRef_textView);
		error_content = (TextView) rootView.findViewById(R.id.error_content1);
		error_textView = (TextView) rootView.findViewById(R.id.error1);
		payButton = (Button) rootView.findViewById(R.id.payButton);
		guide_textView = (TextView) rootView.findViewById(R.id.guide_textView);
		showInfo();
		return rootView;
	}
	
	private void showInfo() {
		qrCode = QRCode.getInstance();
		paymentAmt = qrCode.getQR_AMOUNT();
		merchantName = qrCode.getQR_MER_NAME();
		String merchantRef = qrCode.getQR_MER_TRAX_REF_NO();
		
		cardNo = sharedPreferences.getString("cardNo", null);
		purseBalance = sharedPreferences.getString("balance", null);
		
		cardNo_textView.setText(cardNo);
		purseBalance_textView.setText("$"+purseBalance);
		paymentAmt_textView.setText("$"+paymentAmt);
		merchantName_textView.setText(merchantName);
		merchantRef_textView.setText(merchantRef);
		
		if(Double.parseDouble(purseBalance) < Double.parseDouble(paymentAmt)) {
			error_textView.setVisibility(View.VISIBLE);
			error_content.setText(StringConstants.ErrorDecription.INSUFFICIENT_BALANCE);
			payButton.setVisibility(View.INVISIBLE);
			wsConnection.uploadReceiptData("", new RecieptReqError(StringConstants.ErrorCode.ERROR_CODE_20, StringConstants.ErrorDecription.INSUFFICIENT_BALANCE));
		}
		
		payButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog = ProgressDialog.show(getActivity(), "Processing", "Please wait while your transaction is being processed...", true);
				// Request : Debit Command
				IsoDep isoDep = com.wirecard.ezlink.handle.IsoDepReaderTask.isoDepStatic;
				PaymentActivity paymentActivity = (PaymentActivity) getActivity();
				PaymentActivity.IsoDepReaderTask asyncTask = paymentActivity.new IsoDepReaderTask();
				asyncTask.execute(isoDep);
			}
		});
	}

}
