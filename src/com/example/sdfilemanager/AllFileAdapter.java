package com.example.sdfilemanager;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AllFileAdapter extends SimpleAdapter {
	List<Map<String, Object>> mlist = null;
	HashMap<String, Object> map;
	LayoutInflater mInflater;
	Map<Integer, Boolean> isSelected;
	private LayoutInflater mLayoutInflater;
	public final int SHOW_LIST = 1;
	public final int SHOW_GRID = 2;
	public int show_type = SHOW_GRID;

	public AllFileAdapter(Context context, List<Map<String, Object>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		mLayoutInflater = LayoutInflater.from(context);
		mlist = data;
		isSelected = new HashMap<Integer, Boolean>();
		for (int i = 0; i < data.size(); i++) {
			isSelected.put(i, false);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void setShowType(int show_type){
		this.show_type =show_type;
	}

	public final class ViewHolder {
		private ImageView listImageView;
		private TextView listFileName;
		private TextView listMdTime;
		public CheckBox listcheckbox;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			switch (show_type) {
			case SHOW_LIST:
				convertView = mLayoutInflater.inflate(R.layout.listrow_allfile, null);
				System.out.println("show_list");
				break;
				
			case SHOW_GRID:
				convertView = mLayoutInflater.inflate(R.layout.gridrow_allfile, null);
				System.out.println("show_grid");
				break;

			default:
				break;
			}
			holder = new ViewHolder();
			
			holder.listImageView = (ImageView) convertView
					.findViewById(R.id.imageDir);
			holder.listFileName = (TextView) convertView
					.findViewById(R.id.name);
			holder.listMdTime = (TextView) convertView
					.findViewById(R.id.mdtime);
			holder.listcheckbox = (CheckBox) convertView
					.findViewById(R.id.listcheckbox);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setTag(holder);
		holder.listImageView.setBackgroundResource((Integer) mlist
				.get(position).get("image"));
		holder.listFileName.setText(mlist.get(position).get("name").toString());
		holder.listMdTime.setText(mlist.get(position).get("modifytime")
				.toString());
		holder.listcheckbox.setChecked(isSelected.get(position));
		final CheckBox checkBox = holder.listcheckbox;
		final int arg2 = position;
		checkBox.setChecked(isSelected.get(position));
		checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isSelected.get(arg2)) {
					isSelected.put(arg2, false);
				} else {
					isSelected.put(arg2, true);
					System.out.println(arg2 + "is selected!!!!");
				}
				notifyDataSetChanged();
			}
		});
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub

			}
		});
		return convertView;
	}

}
