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
	
	private TextView showPathView;
	private ListView listFileView;
	private EditText editTextRename;
	List<Map<String, Object>> filelist = null;
	private boolean isLoad = false;
	private final String orgPath = Environment.getExternalStorageDirectory()+File.separator;
	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST+1;
	private static final int ITEM3 = Menu.FIRST+2;
	private static final int ITEM4 = Menu.FIRST+3;
	private static final int ITEM5 = Menu.FIRST+4;
	private static final int menuNewFold = 1;
	private static final int menuSelectAll = 2;
	private static final int menuRefresh = 3;
	private static final int menuMultiSelect = 4;
	private static final int menuCancelSelect = 5;
	private static final int menuQuit = 6;
	private long mExitTime = 0;
	public static final int TYPE_AUDIO = 1;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_VIDEO = 3;
	public static final int TYPE_APK = 4;
	public CheckBox checkboxlistBox;
	private String currentPath;
	private Boolean selectListMode = false;
	private View mConfirmOperationBar;
	private String getBundleData = null;
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(1024);
	private int currentSelectedItem = 0 ; 
	private MyTask mTask;
	private ProgressBar progressBar;
	private String ACTIONMODE;
	private mediaFileAdapter mImageSimpleAdapter, mVideoSimAdapter, mAudioSimpleAdapter, mApkSimpleAdapter;
	private String currentSelectPath;
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
        	audioListMode = true;
        	view = inflater.inflate(R.layout.fragment_apkcontent, container, false); 
        	System.out.println("audioMode!!!!");
        }
        return view;  
		//return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

//		ViewHolder vHollder = (ViewHolder) view.getTag();
//		vHollder.listcheckbox.setChecked(true);
//        for (int i = 0; i < filelist.size(); i++) {    
//        	mSimpleAdapter.isSelected.put(i, true);    
//        }    
//        boolean check = vHollder.listcheckbox.isChecked();
//        mSimpleAdapter.isSelected.put(position, check); 
//		startloadListData();
		
	}
	
	public void startloadListData(){
        if(getIndex() == TYPE_IMAGE){
        	imageListMode = true;
        	listFileView = (ListView) getActivity().findViewById(R.id.imagefile_list);  
        }else if(getIndex() == TYPE_VIDEO){
        	videoListMode = true;
        	listFileView = (ListView) getActivity().findViewById(R.id.videofile_list);
        }else if(getIndex() == TYPE_AUDIO){
        	audioListMode = true;
        	listFileView = (ListView) getActivity().findViewById(R.id.audiofile_list);
        }else if (getIndex() == TYPE_APK) {
        	apkListMode = true;
        	listFileView = (ListView) getActivity().findViewById(R.id.apkfile_list);
		} 
		
//		progressBar = (ProgressBar) getActivity().findViewById(R.id.progressbar_load);
//		showPathView = (TextView) getActivity().findViewById(R.id.showpathview);
//		showPathView.setVisibility(View.GONE);
		refreshListItem(orgPath);

		currentPath = Environment.getExternalStorageDirectory()+"";
		//listFileView.setOnCreateContextMenuListener(this);
		
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
			
			if(filelist != null){
				filelist.clear();
			}
			filelist = buildListForSimpleAdapter(params[0]);
			// TODO Auto-generated method stub
//			System.out.println("params[0]"+params[0]);

//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			//progressBar.setVisibility(View.GONE);
	        if(getIndex() == TYPE_IMAGE){
	        	imageListMode = true;
				mImageSimpleAdapter = new mediaFileAdapter(getActivity(), filelist, R.layout.listrow_media, 
						new String[] {"image", "name", "path"}, 
						new int[] {R.id.imageDir, R.id.name, R.id.mpath});
			listFileView.setAdapter(mImageSimpleAdapter);
	        	//System.out.println("imageMode!!!!");
	        }else if(getIndex() == TYPE_VIDEO){
	        	videoListMode = true;
				mVideoSimAdapter = new mediaFileAdapter(getActivity(), filelist, R.layout.listrow_media, 
						new String[] {"image", "name", "path"}, 
						new int[] {R.id.imageDir, R.id.name, R.id.mpath});
			listFileView.setAdapter(mVideoSimAdapter);
	        	//System.out.println("videoMode!!!!");
	        }else if(getIndex() == TYPE_AUDIO){
	        	audioListMode = true;
				mAudioSimpleAdapter = new mediaFileAdapter(getActivity(), filelist, R.layout.listrow_media, 
						new String[] {"image", "name", "path"}, 
						new int[] {R.id.imageDir, R.id.name, R.id.mpath});
			listFileView.setAdapter(mAudioSimpleAdapter);
	        	//System.out.println("audioMode!!!!");
	        }else if(getIndex() == TYPE_APK){
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
			if(selectListMode){
	        	if(mImageSimpleAdapter.isSelected.get(position)){
	        		mImageSimpleAdapter.isSelected.put(position, false);
	        		listFileView.setAdapter(mImageSimpleAdapter);
	        	}else if(!mImageSimpleAdapter.isSelected.get(position)){
	        		mImageSimpleAdapter.isSelected.put(position, true);   
	            	listFileView.setAdapter(mImageSimpleAdapter);
	        	}
			}else{
				currentPath = (String) filelist.get(position).get("path");
				File file = new File(currentPath);
				if(JudgeMediaFileType.isImageFileType(file)){
				    Intent intent = new Intent("android.intent.action.VIEW");
				    intent.addCategory("android.intent.category.DEFAULT");
				    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    Uri uri = Uri.fromFile(file);
				    intent.setDataAndType(uri, "image/*");
				    startActivity(intent);
				}else if(JudgeMediaFileType.isAudioFileType(file)){
				    Intent intent = new Intent("android.intent.action.VIEW");
				    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				    intent.putExtra("oneshot", 0);
				    intent.putExtra("configchange", 0);
				    Uri uri = Uri.fromFile(file);
				    intent.setDataAndType(uri, "audio/*");
					startActivity(intent);
				}else if(JudgeMediaFileType.isVideoFileType(file)){
					Intent intent = new Intent("android.intent.action.VIEW");
				    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				    intent.putExtra("oneshot", 0);
				    intent.putExtra("configchange", 0);
				    Uri uri = Uri.fromFile(file);
				    intent.setDataAndType(uri, "video/*");
					startActivity(intent);
				}else if(JudgeMediaFileType.isTxtFileType(file)) {
					Intent intent = new Intent("android.intent.action.VIEW");
					intent.addCategory("android.intent.category.DEFAULT");
				    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    Uri uri = Uri.fromFile(file);
				    intent.setDataAndType(uri, "text/plain");
				}
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
						map.put("image", R.drawable.file_icon_mp3);
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
    
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.setHeaderTitle("文件夹操作");
		menu.add(0, ITEM1, 0, "删除");
		menu.add(0, ITEM2, 0, "重命名");
		menu.add(0, ITEM3, 0, "文件详情");
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo)item.getMenuInfo();
		int index = itemInfo.position;
	//	System.out.println(index);
		if(filelist == null){
			System.out.println("fileist is null");
		}
		currentSelectPath = (String) filelist.get(index).get("path");
		System.out.println("onContextItemSelected"+currentSelectPath);
		File currentfile = new File(currentSelectPath);
		//由于currentSelectPath是选中的文件/目录所在路径，所以要跳转到上一级
		currentPath = currentfile.getParentFile().getPath();
		//System.out.println("currentPath,,hhy" + currentPath);
		switch(item.getItemId()){
		//删除选项
		case ITEM1:
			Dialog delDialog =  onCreatDialog(1);
			delDialog.show();
			break;
		//重命名选项
		case ITEM2:
			Dialog renameDialog = onCreatDialog(2);
			renameDialog.show();
			break;
		}	
		return super.onContextItemSelected(item);
	}
	
	/**  
	 * TODO<?ù?Y2?í?μ?ID?μ′′?¨2?í?μ?dialog > 
	 * 
	 * @throw 
	 * @return Dialog 
	 */
	protected Dialog onCreatDialog(int id){
		switch (id){
		case 1:
			//′′?¨é?3y???°?ò
			Dialog deleteDialog = new AlertDialog.Builder(getActivity()).setTitle("é?3y???t")
			.setMessage("???té?3yoó2??ü???′￡?è·è?é?3y￡?")
			.setPositiveButton("è·?¨", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				//	System.out.println("é?3y"+path);
					File file = new File(currentSelectPath);
					System.out.println("é?3y"+currentSelectPath);
					FileUtil.deleteFileOrDir(file);
					refreshListItem(orgPath);
					
				}
			}).setNegativeButton("取消", null)
			.create();
			return deleteDialog;
		
		case 2:
			//′′?¨???ü?????°?ò
			View dialogRenameView = LayoutInflater.from(getActivity())
			.inflate(R.layout.dialog_fileoption, null);
			editTextRename = (EditText) dialogRenameView.findViewById(R.id.dialog_filename_input);
			File file = new File(currentSelectPath);
			String fileName = file.getName();
			editTextRename.setText(fileName);
			editTextRename.setSelection(fileName.length());
			Dialog renameDialog = new AlertDialog.Builder(getActivity()).setTitle("重命名")
			.setView(dialogRenameView)
			.setPositiveButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					File file = new File(currentSelectPath);
					String fileName = file.getName();
					System.out.println("onClick"+fileName);
					//System.out.println("onContextItemSelected"+path);
					String getFileName = editTextRename.getText().toString().trim();
					if(TextUtils.isEmpty(getFileName)){
						Toast.makeText(getActivity(), "???t??2??ü?a??", Toast.LENGTH_SHORT).show();
						return;
					}
					refreshListItem(orgPath);
				}
			}).setNegativeButton("è???", null)
			.create();
			return renameDialog;
			
		default:
			return null;
		}
	}
	

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		if (selectListMode){
			menu.add(0, menuCancelSelect, 0, "取消选择");
		}else{
			menu.add(0, menuSelectAll, 0, "全选");
			menu.add(0, menuRefresh, 0, "刷新");
			menu.add(0, menuMultiSelect, 0, "多选");
		}
		return;
	}
	
	/* (non-Javadoc) ?òoptionitemìí?ó?úèY￡?×￠òa???ü?úactivityμ??ù′?é????ó￡?2??ü×?DD′′á￠
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == 2){
	        for (int i = 0; i < filelist.size(); i++) {    
	        	mImageSimpleAdapter.isSelected.put(i, true);   
	        	listFileView.setAdapter(mImageSimpleAdapter);
	        }    
	        selectListMode = true;
	        
		}else if(id == 3){
			refreshListItem(currentPath);
		}else if(id == 4){
			selectListMode = true;
		}else if(id == 5){
	        for (int i = 0; i < filelist.size(); i++) {    
	        	mImageSimpleAdapter.isSelected.put(i, false);   
	        	listFileView.setAdapter(mImageSimpleAdapter);
	        }    
			selectListMode = false;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			String currPath = currentPath + File.separator;
			System.out.println(currPath);
				if((System.currentTimeMillis() - mExitTime) > 2000){
					Toast.makeText(getActivity(), "?ù°′ò?′?í?3?3ìDò", Toast.LENGTH_SHORT).show();
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
