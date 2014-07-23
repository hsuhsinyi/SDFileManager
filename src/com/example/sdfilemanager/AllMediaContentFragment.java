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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ContextMenu;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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
	private mediaFileAdapter mImageSimpleAdapter, mVideoSimAdapter, mAudioSimpleAdapter, mApkSimpleAdapter;
	private boolean imageListMode = false;
	private boolean videoListMode = false;
	private boolean audioListMode = false;
	private boolean apkListMode = false;

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
		refreshListItem(orgPath);
		currentPath = Environment.getExternalStorageDirectory()+"";
	}
	
	public void refreshListItem(String path){
		//showPathView.setText(path);
		mTask = new MyTask();
		mTask.execute(path);
	}
	
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
	        	listFileView = (ListView) getActivity().findViewById(R.id.imagefile_list);
	        	imageListMode = true;
				mImageSimpleAdapter = new mediaFileAdapter(getActivity(), filelist, R.layout.listrow_media, 
						new String[] {"image", "name", "path"}, 
						new int[] {R.id.imageDir, R.id.name, R.id.mpath});
			listFileView.setAdapter(mImageSimpleAdapter);
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
			listFileView.setOnItemClickListener(mItemClickListener);
			listFileView.setSelection(0);

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
						map.put("path", file.getPath());
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
						map.put("path", file.getPath());
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
						map.put("path", file.getPath());
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
						map.put("path", file.getPath());
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
