package com.wirecard.ezlink.fragment;

import com.example.R;
import com.wirecard.ezlink.activity.NFCActivity;
import com.wirecard.ezlink.model.QRCode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class QRCodeFragment extends Fragment {
	private TextView tv_merchantNo;
	private TextView tv_merchantName;
	private TextView tv_merchantRefNo;
	private TextView tv_orderNo;
	private TextView tv_paymentAmt;
	
	public QRCodeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_qrcode, container, false);
		
		Button nfcButton = (Button) rootView.findViewById(R.id.nfcButton);
		nfcButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				FragmentManager fragmentManager = getFragmentManager();
//				Fragment faq = new FAQFragment();
//	            fragmentManager.beginTransaction().replace(R.id.content_frame, faq).commit();
				
				Intent in = new Intent(getActivity(), NFCActivity.class);
				getActivity().startActivity(in);
				getActivity().finish();
			}
		});
		
		tv_merchantNo = (TextView) rootView.findViewById(R.id.tv_merchantNo);
		tv_merchantName = (TextView) rootView.findViewById(R.id.tv_merchantName);
		tv_merchantRefNo = (TextView) rootView.findViewById(R.id.tv_merchantRefNo);
		tv_orderNo = (TextView) rootView.findViewById(R.id.tv_orderNo);
		tv_paymentAmt = (TextView) rootView.findViewById(R.id.tv_paymentAmt);
		
		QRCode qrCode = QRCode.getInstance();
		tv_merchantNo.setText(qrCode.getQR_MER_ID());
		tv_merchantName.setText(qrCode.getQR_MER_NAME());
		tv_merchantRefNo.setText(qrCode.getQR_MER_TRAX_REF_NO());
		tv_orderNo.setText(qrCode.getQR_ORDER_NO());
		tv_paymentAmt.setText("$" + qrCode.getQR_AMOUNT());
		return rootView;
	}

}
