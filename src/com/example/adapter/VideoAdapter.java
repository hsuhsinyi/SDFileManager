package com.example.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;

import com.example.sdfilemanager.MyImageView.OnMeasureListener;
import com.example.sdfilemanager.NativeImageLoader.NativeImageCallBack;
import com.example.sdfilemanager.R;


public class VideoAdapter extends BaseAdapter {
//	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
//	/**
//	 * 用来存储图片的选中情况
//	 */
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private ListView mListView;
	private List<Map<String, Object>> mlist = null;
	protected LayoutInflater mInflater;

	public VideoAdapter(Context context, List<Map<String, Object>> mVideoInfo, ListView videoListView){
		this.mListView = videoListView;
		this.mlist = mVideoInfo;
		mInflater = LayoutInflater.from(context);
	}
	


	@Override
	public int getCount() {
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		return mlist.get(position);
	}

	public void addVideoList(List<Map<String, Object>> list){
		this.mlist = list;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String videoName =  mlist.get(position).get("videoname").toString();
		Bitmap videoBitmap = (Bitmap) mlist.get(position).get("videoimage");
		
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listview_videocontent, null);
			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.child_video);
			viewHolder.mVideoName = (TextView) convertView.findViewById(R.id.video_name);
//			viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_count);
			
			//用来监听ImageView的宽和高
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		//给ImageView设置路径Tag,这是异步加载图片的小技巧
		//viewHolder.mImageView.setTag(videoPath);
		
		//Bitmap videoBitmap = getVideoThumbnail(videoPath, 100, 100, MediaStore.Images.Thumbnails.MICRO_KIND);
		
		//利用NativeImageLoader类加载本地图片

		
		if(videoBitmap != null){
			viewHolder.mImageView.setImageBitmap(videoBitmap);
			viewHolder.mVideoName.setText(videoName);
		}else{
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		
		return convertView;
	}
	
	
	public static class ViewHolder{
		public ImageView mImageView;
		public TextView mVideoName;
//		public TextView mTextViewCounts;
	}
	



}
