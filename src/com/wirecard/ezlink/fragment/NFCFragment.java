package com.wirecard.ezlink.fragment;

import com.example.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NFCFragment extends Fragment {
	public static TextView error;
	public static TextView error_content;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_tapcard, container, false);
		error = (TextView) rootView.findViewById(R.id.error);
		error_content = (TextView) rootView.findViewById(R.id.error_content);
		return rootView;
	}
}
