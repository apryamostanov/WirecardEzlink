package com.wirecard.ezlink.listView;

import java.util.List;

import com.example.R;
import com.wirecard.ezlink.model.Scanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListScannerAdapter extends BaseAdapter {

	Context context;

	protected List<Scanner> listScanner;
	LayoutInflater inflater;

	public ListScannerAdapter(Context context, List<Scanner> listScanner) {
		this.listScanner = listScanner;
		this.inflater = LayoutInflater.from(context);
		this.context = context;
	}

	public int getCount() {
		return listScanner.size();
	}

	public Scanner getItem(int position) {
		return listScanner.get(position);
	}

	public long getItemId(int position) {
		return listScanner.get(position).getDrawableId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {

			holder = new ViewHolder();
			convertView = this.inflater.inflate(R.layout.listview_item_row_scanner, parent, false);

			holder.function = (TextView) convertView.findViewById(R.id.function);
			holder.functionDetail = (TextView) convertView.findViewById(R.id.function_detail);
			holder.imgScanner   = (ImageView)convertView.findViewById(R.id.img);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Scanner Scanner = listScanner.get(position);
		holder.function.setText(Scanner.getFunction());
		holder.functionDetail.setText(Scanner.getFunctionDetail());
		holder.imgScanner.setImageResource(Scanner.getDrawableId());

		return convertView;
	}

	private class ViewHolder {
		TextView function;
		TextView functionDetail;
		ImageView imgScanner;
	}

}
