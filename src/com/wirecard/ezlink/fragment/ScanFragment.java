package com.wirecard.ezlink.fragment;

import java.util.ArrayList;
import java.util.List;

import com.example.R;
import com.wirecard.ezlink.activity.NFCActivity;
import com.wirecard.ezlink.activity.QRCodeScannerActivity;
import com.wirecard.ezlink.listView.ListScannerAdapter;
import com.wirecard.ezlink.model.ReceiptRequest;
import com.wirecard.ezlink.model.Scanner;
import com.wirecard.ezlink.sqlite.DBHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ScanFragment extends Fragment implements OnItemClickListener{
	ArrayList<Scanner> scannerList;
	ListView scanListView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_qrcode_scanner, container, false);

		scannerList = new ArrayList<Scanner>();
		
		Scanner scan = new Scanner(R.drawable.scan_barcode, "Scan QRCode", "Start Scanning with the camera");
//		Scanner manual = new Scanner(R.drawable.manual_key, "Manual Key-in", "Manually type in barcode");
//		Scanner decodeFromFile = new Scanner(R.drawable.file_decode, "Decode from File", "Choose a QR Code File");
//		Scanner decodeFromUrl = new Scanner(R.drawable.url_decode, "Decode from Url", "Insert the url of the qr code");
		
		scannerList.add(scan);
//		scannerList.add(manual);
//		scannerList.add(decodeFromFile);
//		scannerList.add(decodeFromUrl);
		
		scanListView = (ListView) rootView.findViewById(R.id.listview);
		ListScannerAdapter adapter = new ListScannerAdapter(getActivity(), scannerList);
		scanListView.setAdapter(adapter);
		scanListView.setOnItemClickListener(this);
		return rootView;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		DBHelper db = new DBHelper(getActivity());
		List<ReceiptRequest> list = db.getAllReceiptRequest();
		boolean uploadToHost = list.isEmpty()? false:true;
		if(!uploadToHost) {
			if(position == 0) {
				Intent in = new Intent(getActivity(), QRCodeScannerActivity.class);
				startActivity(in);
				getActivity().finish();
			}
		} else {
			Toast.makeText(getActivity(), "Can not scan qrcode because needing upload pending transaction", Toast.LENGTH_LONG).show();
		}
	}
}
