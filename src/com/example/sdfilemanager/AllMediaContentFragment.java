package com.example.sdfilemanager;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;







import com.example.sdfilemanager.AllFileAdapter.ViewHolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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

public class AllMediaContentFragment extends BaseFragment{
	
	private ListView listFileView;
	List<Map<String, Object>> filelist = null;
	private final String orgPath = Environment.getExternalStorageDirectory()+File.separator;
	private long mExitTime = 0;
	public static final int TYPE_AUDIO = 1;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_VIDEO = 3;
	public static final int TYPE_APK = 4;
	public CheckBox checkboxlistBox;
	private String currentPath;
	private Boolean selectListMode = false;
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(1024);
	private MyTask mTask;
	ChildAdapter mImageSimpleAdapter;
	private mediaFileAdapter mVideoSimAdapter, mAudioSimpleAdapter, mApkSimpleAdapter;
	private boolean imageListMode = false;
	private boolean videoListMode = false;
	private boolean audioListMode = false;
	private boolean apkListMode = false;
	private ProgressDialog mProgressDialog;
	private final static int SCAN_OK = 1;

	public static AllMediaContentFragment newInstance(int index) {
		AllMediaContentFragment fragment = new AllMediaContentFragment();
		Bundle b = new Bundle();
		b.putInt("index", index);
		fragment.setArguments(b);
		return fragment;
	}

	private int getIndex() {
		int index = getArguments().getInt("index");
		System.out.println("get from main:index" + index);
		return index;
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = null;
        if(getIndex() == TYPE_IMAGE){
        	imageListMode = true;
        	view = inflater.inflate(R.layout.fragment_imagecontent, container, false); 
        	System.out.println("imageMode!!!!");
        }
        if(getIndex() == TYPE_VIDEO){
        	videoListMode = true;
        	view = inflater.inflate(R.layout.fragment_videocontent, container, false); 
        	System.out.println("videoMode!!!!");
        }
        if(getIndex() == TYPE_AUDIO){
        	audioListMode = true;
        	view = inflater.inflate(R.layout.fragment_audiocontent, container, false); 
        	System.out.println("audioMode!!!!");
        }
        if(getIndex() == TYPE_APK){
        	apkListMode = true;
        	view = inflater.inflate(R.layout.fragment_apkcontent, container, false); 
        	System.out.println("apkMode!!!!");
        }
        return view;  
	
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		
	}
	
	public void startloadListData(){
//		refreshListItem(orgPath);
//		currentPath = Environment.getExternalStorageDirectory()+"";
		getImages();
	}
	
	public void refreshListItem(String path){
		//showPathView.setText(path);
		mTask = new MyTask();
		mTask.execute(path);
	}
	
	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	private void getImages() {

		//显示进度条
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage("正在加载中");
		mProgressDialog.show();
		if(list != null){
			list.clear();
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = getActivity().getContentResolver();

				//只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				//List<String> chileList = new ArrayList<String>();
				
				while (mCursor.moveToNext()) {
					//获取图片的路径
					Map<String, Object> map = new HashMap<String, Object>();
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					System.out.println("imageUrl"+path);
					map.put("imageUrl", path);
					list.add(map);
				}
				
				mCursor.close();
				
				//通知Handler扫描图片完成
				mHandler.sendEmptyMessage(SCAN_OK);
				
			}
		}).start();
		
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				//关闭进度条
				mProgressDialog.dismiss();
	        	GridView mGridView = (GridView)getActivity().findViewById(R.id.child_grid);
	        	mImageSimpleAdapter = new ChildAdapter(getActivity(), list, mGridView);
				mGridView.setAdapter(mImageSimpleAdapter);
				//adapter = new GroupAdapter(MainActivity.this, list, mGroupGridView);
				//mGroupGridView.setAdapter(adapter);
				break;
			}
		}
		
	};
	
	
	private class MyTask extends AsyncTask<String, Integer, String>{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			if(filelist != null){
				filelist.clear();
			}
			filelist = buildListForSimpleAdapter(params[0]);

			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
	        if(getIndex() == TYPE_IMAGE){
	        	GridView mGridView = (GridView)getActivity().findViewById(R.id.child_grid);
	        	mImageSimpleAdapter = new ChildAdapter(getActivity(), filelist, mGridView);
				mGridView.setAdapter(mImageSimpleAdapter);
	        	
//	        	int count = 0;
//	        	int COLUMNS = 3;
//	        	int viewId = 0x7F24FFF0;
//	        	int verticalSpacing, horizontalSpacing;
//	        	verticalSpacing = horizontalSpacing = 4;
//	            Display display = getActivity().getWindowManager().getDefaultDisplay();
//	            int imageWidth = (display.getWidth() - (3 + 1) * horizontalSpacing) / 3;
//	        	RelativeLayout mySrcollView = (RelativeLayout)getActivity().findViewById(R.id.image_cache_parent_layout);
//
//	        	for(int i=0; i<filelist.size(); i++){
//	        		  ImageView imageView = new ImageView(getActivity());
//	                  //imageView.setId(++viewId);
//	                  imageView.setScaleType(ScaleType.CENTER);
//	                  imageView.setBackgroundResource((Integer) filelist.get(i).get("image"));
//	                 // imageView.setBackgroundResource(R.drawable.image_border);
//	                  mySrcollView.addView(imageView);
//
//	                  // set imageView layout params
//	                  LayoutParams layoutParams = (RelativeLayout.LayoutParams)imageView.getLayoutParams();
//	                  layoutParams.width = imageWidth;
//	                  layoutParams.topMargin = verticalSpacing;
//	                  layoutParams.rightMargin = horizontalSpacing;
//	                  int column = count % COLUMNS;
//	                  int row = count / COLUMNS;
//	                  if (row > 0) {
//	                      layoutParams.addRule(RelativeLayout.BELOW, viewId - COLUMNS);
//	                  }
//	                  if (column > 0) {
//	                      layoutParams.addRule(RelativeLayout.RIGHT_OF, viewId - 1);
//	                  }
//	                  layoutParams.height = 400;
//
//	                  // get image
//	                  //IMAGE_CACHE.get(imageUrl, imageView);
//	                  count++;
//	        	}
//	        	System.out.println("hhhhhhhhhh"+count);
//	        	listFileView = (ListView) getActivity().findViewById(R.id.imagefile_list);
//	        	imageListMode = true;
//				mImageSimpleAdapter = new mediaFileAdapter(getActivity(), filelist, R.layout.listrow_media, 
//						new String[] {"image", "name", "path"}, 
//						new int[] {R.id.imageDir, R.id.name, R.id.mpath});
//			listFileView.setAdapter(mImageSimpleAdapter);
	        	//System.out.println("imageMode!!!!");
	        }else if(getIndex() == TYPE_VIDEO){
	        	listFileView = (ListView) getActivity().findViewById(R.id.videofile_list);
	        	videoListMode = true;
				mVideoSimAdapter = new mediaFileAdapter(getActivity(), filelist, R.layout.listrow_media, 
						new String[] {"image", "name", "path"}, 
						new int[] {R.id.imageDir, R.id.name, R.id.mpath});
			listFileView.setAdapter(mVideoSimAdapter);
	        	//System.out.println("videoMode!!!!");
	        }else if(getIndex() == TYPE_AUDIO){
	        	listFileView = (ListView) getActivity().findViewById(R.id.audiofile_list);
	        	audioListMode = true;
				mAudioSimpleAdapter = new mediaFileAdapter(getActivity(), filelist, R.layout.listrow_media, 
						new String[] {"image", "name", "path"}, 
						new int[] {R.id.imageDir, R.id.name, R.id.mpath});
			listFileView.setAdapter(mAudioSimpleAdapter);
	        	//System.out.println("audioMode!!!!");
	        }else if(getIndex() == TYPE_APK){
	        	listFileView = (ListView) getActivity().findViewById(R.id.apkfile_list);
	        	audioListMode = true;
	        	mApkSimpleAdapter = new mediaFileAdapter(getActivity(), filelist, R.layout.listrow_media, 
						new String[] {"image", "name", "path"}, 
						new int[] {R.id.imageDir, R.id.name, R.id.mpath});
			listFileView.setAdapter(mApkSimpleAdapter);
	        	//System.out.println("audioMode!!!!");
	        }
			//listFileView.setOnItemClickListener(mItemClickListener);
			//listFileView.setSelection(0);

			super.onPostExecute(result);
		}
	}
	
	OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			System.out.println("current selectmode is:" + selectListMode);
			currentPath = (String) filelist.get(position).get("path");
			File file = new File(currentPath);
			if (JudgeMediaFileType.isImageFileType(file)) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Uri uri = Uri.fromFile(file);
				intent.setDataAndType(uri, "image/*");
				startActivity(intent);
			} else if (JudgeMediaFileType.isAudioFileType(file)) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("oneshot", 0);
				intent.putExtra("configchange", 0);
				Uri uri = Uri.fromFile(file);
				intent.setDataAndType(uri, "audio/*");
				startActivity(intent);
			} else if (JudgeMediaFileType.isVideoFileType(file)) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("oneshot", 0);
				intent.putExtra("configchange", 0);
				Uri uri = Uri.fromFile(file);
				intent.setDataAndType(uri, "video/*");
				startActivity(intent);
			} else if (JudgeMediaFileType.isTxtFileType(file)) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Uri uri = Uri.fromFile(file);
				intent.setDataAndType(uri, "text/plain");

			}
		}
	};

	private List<Map<String, Object>> buildListForSimpleAdapter(String path){
		File[] files = new File(path).listFiles();
		long time = 0;
			for(File file: files){
				if(file.getName().equals(".android_secure")){
					continue;
				}else if(file.isDirectory()){
					list = buildListForSimpleAdapter(file.getAbsolutePath() + File.separator);		
				}else if (imageListMode) {
					if(JudgeMediaFileType.isImageFileType(file)){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("image", R.drawable.file_icon_picture);
						map.put("name", file.getName());
						time = file.lastModified();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						Date date = new Date(time);
						//map.put("modifytime", formatter.format(date));
						map.put("path", file.getParentFile().getAbsolutePath());
						list.add(map);
					}
				}else if(videoListMode){
					if(JudgeMediaFileType.isVideoFileType(file)){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("image", R.drawable.file_icon_video);
						map.put("name", file.getName());
						time = file.lastModified();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						Date date = new Date(time);
						//map.put("modifytime", formatter.format(date));
						map.put("path", file.getParentFile().getAbsolutePath());
						list.add(map);
					}
				}else if(audioListMode){
					if(JudgeMediaFileType.isAudioFileType(file)){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("image", R.drawable.file_icon_mp3);
						map.put("name", file.getName());
						time = file.lastModified();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						Date date = new Date(time);
						//map.put("modifytime", formatter.format(date));
						map.put("path", file.getParentFile().getAbsolutePath());
						list.add(map);	
					}
				}else if(apkListMode){
					if(JudgeMediaFileType.isApkFileType(file)){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("image", R.drawable.file_icon_apk);
						map.put("name", file.getName());
						time = file.lastModified();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						Date date = new Date(time);
						//map.put("modifytime", formatter.format(date));
						map.put("path", file.getParentFile().getAbsolutePath());
						list.add(map);	
					}
				}
			}

		return list;
	}
    
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			String currPath = currentPath + File.separator;
			System.out.println(currPath);
				if((System.currentTimeMillis() - mExitTime) > 2000){
					Toast.makeText(getActivity(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();
				}else{
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
