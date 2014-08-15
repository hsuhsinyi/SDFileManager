package com.example.sdfilemanager;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.adapter.ImageGroupAdapter;
import com.example.adapter.VideoAdapter;
import com.example.adapter.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class VideoContentFragment extends BaseFragment {

	List<Map<String, Object>> filelist = null;
	private long mExitTime = 0;
	public static final int TYPE_AUDIO = 1;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_VIDEO = 3;
	public static final int TYPE_APK = 4;
	public CheckBox checkboxlistBox;
	private String currentPath;
	ImageGroupAdapter mImageSimpleAdapter;
	VideoAdapter mVideoSimpleAdapter;
	private final static int SCAN_OK = 1;
	List<String> childList = null;
	ListView videoListView;
	List<Map<String, Object>> mVideoInfo = new ArrayList<Map<String, Object>>();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = null;
		view = inflater.inflate(R.layout.fragment_videocontent, container,
				false);
		// System.out.println("imageMode!!!!");
		return view;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (null == mVideoInfo) {
			mVideoInfo =  new ArrayList<Map<String, Object>>();
		}
		videoListView = (ListView) getActivity().findViewById(
				R.id.video_list);
		if(mVideoInfo == null){
			System.out.println("mvideo is null");
		}
		mVideoSimpleAdapter = new VideoAdapter(getActivity(), mVideoInfo, videoListView);
		videoListView.setAdapter(mVideoSimpleAdapter);
		super.onActivityCreated(savedInstanceState);

	}

	public void startloadListData() {

		getVideoList();

	}

	public void getVideoList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 获取视频文件：
				ContentResolver mContentResolver = getActivity()
						.getContentResolver();
				// ContentResolver contentResolver =
				// mContext.getContentResolver();
				String[] projection = new String[] { MediaStore.Video.Media.DATA };
				Cursor mCursor = mContentResolver.query(
						MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
						projection, null, null,
						MediaStore.Video.Media.DEFAULT_SORT_ORDER);
				mCursor.moveToFirst();

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					Map<String, Object> map = new HashMap<String, Object>();
					String videoPath = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Video.Media.DATA));
					File videoFile = new File(videoPath);
					String videoNameString = videoFile.getName();
					// String videoNameString = mCursor.getString(mCursor
					// .getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
					System.out.println("video path" + videoNameString);

					Bitmap videoBitmap = getVideoThumbnail(videoPath, 100, 100,
							MediaStore.Images.Thumbnails.MICRO_KIND);
					map.put("videoimage", videoBitmap);
					map.put("videoname", videoNameString);
					mVideoInfo.add(map);
					mVideoSimpleAdapter.addVideoList(mVideoInfo);
				}

				mCursor.close();
				mHandler.sendEmptyMessage(SCAN_OK);
			}
		}).start();

	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:

				mVideoSimpleAdapter.notifyDataSetChanged();
				videoListView.setSelection(0);
				break;
			}
		}

	};

	/**
	 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	private Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}



	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			String currPath = currentPath + File.separator;
			System.out.println(currPath);
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(getActivity(), "再按一次退出程序", Toast.LENGTH_SHORT)
						.show();
				mExitTime = System.currentTimeMillis();
			} else {
				System.exit(0);
			}
		}
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void loadListData() {
		// TODO Auto-generated method stub
		startloadListData();
	}

}
