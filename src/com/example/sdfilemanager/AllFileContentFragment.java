package com.example.sdfilemanager;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.sdfilemanager.AllFileAdapter.ViewHolder;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AllFileContentFragment extends BaseFragment {


	List<Map<String, Object>> filelist = null;
	private static final int ITEM1 = Menu.FIRST;
	private static final int ITEM2 = Menu.FIRST + 1;
	private static final int ITEM3 = Menu.FIRST + 2;
	private static final int ITEM4 = Menu.FIRST + 3;
	private static final int ITEM5 = Menu.FIRST + 4;
	private static final int menuNewFold = 1;
	private static final int menuSelectAll = 2;
	private static final int menuRefresh = 3;
	private static final int menuMultiSelect = 4;
	private static final int menuCancelSelect = 5;
	private static final int menuQuit = 6;
	private static final int menuShowType = 7;
	public static final int TYPE_ALLFILE = 1;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_VIDEO = 3;
	private final static int SCAN_OK = 1;
	
	private String currentPath;
	private AllFileAdapter mSimpleAdapter;
	
	private TextView selectedNumView;
	private View mConfirmOperationBar;
	private View mSelectOperationBar;
	private TextView showPathView;
	private ListView listFileView;
	private GridView gridFileView;
	private EditText editTextRename;
	private ImageButton multiSelectCopyButton;
	private ImageButton multiSelectCutButton;
	private ImageButton multiSelectDeleteButton;
	private ImageButton multiSelectCancelButton; 
	private Button movingConfirmButton;
	private Button movingCancelButton;
	public CheckBox checkboxlistBox;
	private ProgressDialog myProgressDialog;
	

	private long mExitTime = 0;
	private String currentSelectPath;
	private MyCopyOrCutTask mcopyTask;
	private String operateCopyOrCut;
	private String multiSelectPath;
	// 此参数为是否是多选和正常选择状态
	boolean selectListMode = false;
	private File currentfile;
	
	ArrayList<String> strMultiPathArray;

	private String currentSrcPath = null;
	//curentShowType此参数true表示当前是gridview，false表示是listview
	private static boolean curentShowType = true;
	public final int  curentShowList = 1;
	public final int  curentShowGrid = 2;
	
	public static String listOrGridPressed = "unpressed";
	public final String PRESS_ACTION = "com.example.broadcast.receiver.listbuttonpressed";
	public SharedPreferences userInfo;
	private int selectedNum = 0;
	
	
	
	private BroadcastReceiver MyBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(PRESS_ACTION)){  
				//System.out.println("接受到了广播");
				listOrGridPressed = intent.getStringExtra("msg");
				if(listOrGridPressed.equals("pressed")){
				//	System.out.println("按钮按下");
					curentShowType = false;
					userInfo.edit().putString("listOrGrid", "grid").commit(); 
					refreshListItem();
				}else if(listOrGridPressed.equals("unpressed")){
				//	System.out.println("按钮没有按下");
					curentShowType = true;
					userInfo.edit().putString("listOrGrid", "list").commit(); 
					refreshListItem();
				}
            }  
		}
	};

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
		View v = inflater.inflate(R.layout.fragment_allcontent, container,
				false);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		initView();
		
//		userInfo = getActivity().getSharedPreferences("user_info", 0);
//		String getInfoString = userInfo.getString("user_info", "");
//		//System.out.println("user_info"+getInfoString);
//		if(getInfoString.equals("grid")){
//			curentShowType = false;
//		}else if(getInfoString.equals("list")){
//			curentShowType = true;
//		}
		currentPath = Environment.getExternalStorageDirectory() + "";
		refreshListItem();
		strMultiPathArray = new ArrayList<String>();
		listFileView.setOnCreateContextMenuListener(this);
		gridFileView.setOnCreateContextMenuListener(this);
		
        IntentFilter filter_press = new IntentFilter();  
        filter_press.addAction(PRESS_ACTION);  
        getActivity().registerReceiver(MyBroadcastReceiver, filter_press); 
		
	}
	
	public void initView(){
		//初始化listview、gridview以及路径显示pathview
		listFileView = (ListView) getActivity().findViewById(R.id.allfile_list);
		gridFileView = (GridView) getActivity().findViewById(R.id.allgrid_list);
		showPathView = (TextView) getActivity().findViewById(R.id.showpathview);
		selectedNumView = (TextView) getActivity().findViewById(R.id.selected_num);

		// 初始化单选复制、粘贴按钮
		mConfirmOperationBar = getActivity().findViewById(
				R.id.moving_operation_bar);
		movingConfirmButton = (Button) getActivity().findViewById(
				R.id.button_moving_confirm);
		movingCancelButton = (Button) getActivity().findViewById(
				R.id.button_moving_cancel);

		// 初始化多选以及全选时的操作按钮
		mSelectOperationBar = getActivity().findViewById(
				R.id.multiselect_operation_bar);
	    multiSelectCopyButton = (ImageButton) getActivity()
				.findViewById(R.id.multiselect_button_copy);
		multiSelectCutButton = (ImageButton) getActivity()
				.findViewById(R.id.multiselect_button_cut);
		multiSelectDeleteButton = (ImageButton) getActivity()
				.findViewById(R.id.multiselect_button_delete);
		multiSelectCancelButton = (ImageButton) getActivity()
				.findViewById(R.id.multiselect_button_cancel);
//		multiSelectDeleteButton.setPadding((int) (getScreenWidth()/5.0),  multiSelectDeleteButton.getPaddingTop(),  
//				multiSelectDeleteButton.getPaddingRight(),  multiSelectDeleteButton.getPaddingBottom());
//		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ImageView.getLayoutParams();
//		params.setMargins(left, top, right, bottom));// 通过自定义坐标来放置你的控件
//		mTextView .setLayoutParams(params);
		
		//初始化线程操作时的progressdialog
		myProgressDialog = new ProgressDialog(getActivity());
		myProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		myProgressDialog.setTitle("文件操作");
		myProgressDialog.setMessage("正在处理中....");
		myProgressDialog.setIndeterminate(false);
		myProgressDialog.setCancelable(false);
	}

	public void setCurrentFileView(){
		if(curentShowType){
			listFileView.setVisibility(View.VISIBLE);
			gridFileView.setVisibility(View.GONE);
			mSimpleAdapter.setShowType(curentShowList);
		}else{
			gridFileView.setVisibility(View.VISIBLE);
			listFileView.setVisibility(View.GONE);
			mSimpleAdapter.setShowType(curentShowGrid);
		}
	}
	
	private void refreshListItem() {
//		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//			getActivity().Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
//			return;
//		}
		
		//显示进度条
		//mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
		//防止viewpager滑动时childlist会有重复的数据
//		if(childList != null){
//			childList.clear();
//		}
//	
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			filelist = buildListForSimpleAdapter(currentPath);

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
			//mProgressDialog.dismiss();
			mSimpleAdapter = new AllFileAdapter(getActivity(), filelist,
					R.layout.listview_allfile, new String[] { "image", "name",
							"modifytime", "path" }, new int[] { R.id.imageDir,
							R.id.name, R.id.mdtime });
			setCurrentFileView();
			showPathView.setText(currentPath);
			listFileView.setAdapter(mSimpleAdapter);
			listFileView.setOnItemClickListener(mItemClickListener);
			listFileView.setSelection(0);
			gridFileView.setAdapter(mSimpleAdapter);
			gridFileView.setOnItemClickListener(mItemClickListener);
			gridFileView.setSelection(0);
			//adapter = new GroupAdapter(MainActivity.this, list, mGroupGridView);
			//mGroupGridView.setAdapter(adapter);
			break;
		}
	}
	
};
	
//	public void loadData(String path) {
//		showPathView.setText(path);
//		filelist = buildListForSimpleAdapter(path);
//		mSimpleAdapter = new AllFileAdapter(getActivity(), filelist,
//				R.layout.listview_allfile, new String[] { "image", "name",
//						"modifytime", "path" }, new int[] { R.id.imageDir,
//						R.id.name, R.id.mdtime });
//		setCurrentFileView();
//		listFileView.setAdapter(mSimpleAdapter);
//		listFileView.setOnItemClickListener(mItemClickListener);
//		listFileView.setSelection(0);
//		gridFileView.setAdapter(mSimpleAdapter);
//		gridFileView.setOnItemClickListener(mItemClickListener);
//		gridFileView.setSelection(0);
//	}

	OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if (selectListMode) {
				if (mSimpleAdapter.isSelected.get(position)) {
					mSimpleAdapter.isSelected.put(position, false);
					selectedNum--;
					selectedNumView.setText("已选择:"+selectedNum+"个");
					mSimpleAdapter.notifyDataSetChanged();
				} else if (!mSimpleAdapter.isSelected.get(position)) {
					mSimpleAdapter.isSelected.put(position, true);
					selectedNum++;
					selectedNumView.setText("已选择:"+selectedNum+"个");
					mSimpleAdapter.notifyDataSetChanged();
				}
			} else {
				currentPath = (String) filelist.get(position).get("path");
				File file = new File(currentPath);
				if (file.isDirectory()) {
					refreshListItem();
				} else if (file.isFile()) {
					startOnFileTypes(file);
					currentPath = file.getParentFile().getAbsolutePath();
				}
			}
		}
	};

	private List<Map<String, Object>> buildListForSimpleAdapter(String path) {
		File[] files = new File(path).listFiles();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(
				files.length);
		long time = 0;
		for (File file : files) {
			if (file.getName().equals(".android_secure")) {
				continue;
			} else if (file.isDirectory()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("image",  getActivity().getResources().getDrawable(R.drawable.ic_dir));
				map.put("name", file.getName());
				time = file.lastModified();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date(time);
				map.put("modifytime", formatter.format(date));
				map.put("path", file.getPath());
				list.add(map);
			} else if (file.isFile()) {
				if (JudgeMediaFileType.isImageFileType(file)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("image", getActivity().getResources().getDrawable(R.drawable.file_icon_picture));
					map.put("name", file.getName());
					time = file.lastModified();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					Date date = new Date(time);
					map.put("modifytime", formatter.format(date));
					map.put("path", file.getPath());
					list.add(map);
				} else if (JudgeMediaFileType.isVideoFileType(file)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("image",getActivity().getResources().getDrawable(R.drawable.file_icon_video));
					map.put("name", file.getName());
					time = file.lastModified();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					Date date = new Date(time);
					map.put("modifytime", formatter.format(date));
					map.put("path", file.getPath());
					list.add(map);
				} else if (JudgeMediaFileType.isAudioFileType(file)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("image", getActivity().getResources().getDrawable(R.drawable.file_icon_mp3));
					map.put("name", file.getName());
					time = file.lastModified();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					Date date = new Date(time);
					map.put("modifytime", formatter.format(date));
					map.put("path", file.getPath());
					list.add(map);
				} else if (JudgeMediaFileType.isTxtFileType(file)) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("image", getActivity().getResources().getDrawable(R.drawable.file_icon_txt));
					map.put("name", file.getName());
					time = file.lastModified();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					Date date = new Date(time);
					map.put("modifytime", formatter.format(date));
					map.put("path", file.getPath());
					list.add(map);
				} else if (JudgeMediaFileType.isApkFileType(file)) {
					Map<String, Object> map = new HashMap<String, Object>();
					try {
						map.put("image", getApkIcon(file.getAbsolutePath()));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					map.put("name", file.getName());
					time = file.lastModified();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					Date date = new Date(time);
					map.put("modifytime", formatter.format(date));
					map.put("path", file.getPath());
					list.add(map);
				}
			}
		}
		return list;
	}

	/**
	 * TODO<进入到当前目录的父目录，当到"/"目录时，退出当前activity>
	 * 
	 * @throw
	 * @return void
	 */
	void gotoParentDir() {
		File file = new File(currentPath);
		File str_pa = file.getParentFile();
		currentPath = str_pa.getAbsolutePath();
		refreshListItem();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		menu.setHeaderTitle("文件夹操作");
		menu.add(0, ITEM1, 0, "删除");
		menu.add(0, ITEM2, 0, "重命名");
		menu.add(0, ITEM3, 0, "复制");
		menu.add(0, ITEM4, 0, "剪切");
		menu.add(0, ITEM5, 0, "文件详情");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int index = itemInfo.position;
		strMultiPathArray.clear();
		currentSelectPath = (String) filelist.get(index).get("path");
		currentfile = new File(currentSelectPath);
		// 由于currentSelectPath是选中的文件/目录所在路径，所以要跳转到上一级
		currentSrcPath = currentfile.getParentFile().getPath();
		switch (item.getItemId()) {
		// 删除选项
		case ITEM1:
			Dialog delDialog = onCreatDialog(1);
			delDialog.show();
			break;
		// 重命名选项
		case ITEM2:
			Dialog renameDialog = onCreatDialog(2);
			renameDialog.show();
			break;
		// 复制选项
		case ITEM3:
			operateCopyOrCut = "copy";
			selectListMode = false;
			copyContextMenuOperate();
			break;
		// 剪切选项
		case ITEM4:
			operateCopyOrCut = "cut";
			selectListMode = false;
			copyContextMenuOperate();
			break;
		case ITEM5:
			Dialog detailDialog = onCreatDialog(4);
			detailDialog.show();
			break;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * TODO 长按ContextMenu时的复制选项动作 mode 判断是复制还是剪切模式
	 * 
	 * @throw
	 * @return void
	 */
	public void copyContextMenuOperate() {
		mConfirmOperationBar = getActivity().findViewById(
				R.id.moving_operation_bar);
		mConfirmOperationBar.setVisibility(View.VISIBLE);
		unregisterForContextMenu(listFileView);
		movingCancelButton.setOnClickListener(selectListener);
		movingConfirmButton.setOnClickListener(selectListener);
		strMultiPathArray.add(currentSelectPath);
	}

	private class MyCopyOrCutTask extends AsyncTask<String, Integer, String> {

		public MyCopyOrCutTask() {
			myProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			if (operateCopyOrCut.equals("copy")) {
				for (int i = 0; i < strMultiPathArray.size(); i++) {
					String descPathName = FileUtil.parseSrcPathToDesc(
							strMultiPathArray.get(i), params[0]);
					FileUtil.copyFileOrDirectory(strMultiPathArray.get(i),
							descPathName);
				}
			} else if (operateCopyOrCut.equals("cut")) {
				for (int i = 0; i < strMultiPathArray.size(); i++) {
					String descPathName = FileUtil.parseSrcPathToDesc(
							strMultiPathArray.get(i), params[0]);
					FileUtil.cut(strMultiPathArray.get(i), descPathName);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			myProgressDialog.dismiss();
			refreshListItem();
			Toast.makeText(getActivity(), "操作完成!", Toast.LENGTH_SHORT).show();
			registerForContextMenu(listFileView);
			strMultiPathArray.clear();
			super.onPostExecute(result);
		}
	}

	/**
	 * TODO<根据不同的ID值创建不同的dialog >
	 * 
	 * @throw
	 * @return Dialog
	 */
	protected Dialog onCreatDialog(int id) {
		switch (id) {
		case 1:
			// 创建删除对话框
			Dialog deleteDialog = new AlertDialog.Builder(getActivity())
					.setTitle("删除文件")
					.setMessage("文件删除后不能恢复，确认删除？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									File file = new File(currentSelectPath);
									FileUtil.deleteFileOrDir(file);
									refreshListItem();

								}
							}).setNegativeButton("取消", null).create();
			return deleteDialog;

		case 2:
			// 创建重命名对话框
			View dialogRenameView = LayoutInflater.from(getActivity()).inflate(
					R.layout.dialog_fileoption, null);
			editTextRename = (EditText) dialogRenameView
					.findViewById(R.id.dialog_filename_input);
			File file = new File(currentSelectPath);
			String fileName = file.getName();
			editTextRename.setText(fileName);
			editTextRename.setSelection(fileName.length());
			Dialog renameDialog = new AlertDialog.Builder(getActivity())
					.setTitle("重命名")
					.setView(dialogRenameView)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									File file = new File(currentSelectPath);
									String fileName = file.getName();
									String getFileName = editTextRename
											.getText().toString().trim();
									if (TextUtils.isEmpty(getFileName)) {
										Toast.makeText(getActivity(),
												"文件名不能为空", Toast.LENGTH_SHORT)
												.show();
										return;
									}
									File newFile = null;
									newFile = FileUtil.renameFile(file,
											getFileName);
									refreshListItem();
								}
							}).setNegativeButton("取消", null).create();
			return renameDialog;

		case 3:
			//创建新建文件夹dialog
			View dialogNewFoldView = LayoutInflater.from(getActivity())
					.inflate(R.layout.dialog_fileoption, null);
			editTextRename = (EditText) dialogNewFoldView
					.findViewById(R.id.dialog_filename_input);
			Dialog newFileFoldDialog = new AlertDialog.Builder(getActivity())
					.setTitle("创建新文件夹")
					.setView(dialogNewFoldView)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

									String getFileName = editTextRename
											.getText().toString().trim();
									File file = new File(currentPath
											+ File.separator + getFileName);
									String fileName = file.getName();
									if (TextUtils.isEmpty(getFileName)) {
										Toast.makeText(getActivity(),
												"目录名不能为空", Toast.LENGTH_SHORT)
												.show();
										return;
									} else if (file.exists()) {
										Toast.makeText(getActivity(), "目录已经存在",
												Toast.LENGTH_SHORT).show();
										return;
									}
									file.mkdirs();
									refreshListItem();
								}
							}).setNegativeButton("取消", null).create();
			return newFileFoldDialog;

		case 4:
			//创建文件详细信息dialog
			View dialogFileDetailView = LayoutInflater.from(getActivity())
					.inflate(R.layout.dialog_filedetail, null);
			File currentFile = new File(currentSelectPath);
			TextView informationPath = (TextView) dialogFileDetailView
					.findViewById(R.id.information_location);
			TextView informationSize = (TextView) dialogFileDetailView
					.findViewById(R.id.information_size);
			TextView informationModified = (TextView) dialogFileDetailView
					.findViewById(R.id.information_modified);
			TextView informationCanRead = (TextView) dialogFileDetailView
					.findViewById(R.id.information_canread);
			TextView informationCanWrite = (TextView) dialogFileDetailView
					.findViewById(R.id.information_canwrite);
			TextView informationHidden = (TextView) dialogFileDetailView
					.findViewById(R.id.information_ishidden);
			informationPath.setText(currentSelectPath);
			if (currentFile.isDirectory()) {
				try {
					informationSize.setText(FileUtil.getFolderSize(currentFile)
							+ "");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (currentFile.isFile()) {
				informationSize.setText(currentFile.length() / 1048576 + "");
			}

			long time;
			time = currentFile.lastModified();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date(time);
			informationModified.setText(formatter.format(date));
			Dialog fileDetailDialog = new AlertDialog.Builder(getActivity())
					.setTitle("文件详情")
					.setView(dialogFileDetailView)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									return;
								}
							}).create();
			return fileDetailDialog;

		default:
			return null;
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		if (selectListMode) {
			menu.add(0, menuCancelSelect, 0, "取消选择");
		} else {
			menu.add(0, menuNewFold, 0, "新建文件夹");
			menu.add(0, menuSelectAll, 0, "全选");
			menu.add(0, menuRefresh, 0, "刷新");
			menu.add(0, menuMultiSelect, 0, "多选");
			menu.add(0, menuQuit, 0, "退出");
			menu.add(0, menuShowType, 0, "列表方式");
		}
		return;
	}

	/*
	 * (non-Javadoc) 向optionitem添加内容，注意只能在activity的基础上增加，不能自行创立
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem
	 * )
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == 1) {
			Dialog newFoldDialog = onCreatDialog(3);
			newFoldDialog.show();
		} else if (id == 2) {
			for (int i = 0; i < filelist.size(); i++) {
				mSimpleAdapter.isSelected.put(i, true);
				mSimpleAdapter.notifyDataSetChanged();
			}
			selectedNum = filelist.size();
			selectedNumView.setText("已选择:"+selectedNum+"个");
			selectListMode = true;
			multiSelectOption();
		} else if (id == 3) {
			refreshListItem();
		} else if (id == 4) {
			selectedNum = 0;
			selectedNumView.setText("已选择:"+selectedNum+"个");
			selectListMode = true;
			multiSelectOption();
		} else if (id == 5) {
			for (int i = 0; i < filelist.size(); i++) {
				mSimpleAdapter.isSelected.put(i, false);
				mSimpleAdapter.notifyDataSetChanged();
			}
			selectListMode = false;
			mSelectOperationBar.setVisibility(View.GONE);
			mConfirmOperationBar.setVisibility(View.GONE);
		} else if (id == 6) {
			getActivity().finish();
		} else if (id == 7) {
			final CharSequence[] items = {"列表视图", "网格视图"}; 
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("选择查看方式");
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	if(item == 0){
						curentShowType = true;
						refreshListItem();
			    	}else if(item == 1){
						curentShowType = false;
						refreshListItem();
			    	}

			    }
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 多选或者全选时 的操作
	 */
	private void multiSelectOption() {
		mSelectOperationBar.setVisibility(View.VISIBLE);
		multiSelectCopyButton.setOnClickListener(multiSelectListener);
		multiSelectCancelButton.setOnClickListener(multiSelectListener);
	}
	
    OnClickListener multiSelectListener = new OnClickListener() {//创建多选或者全选时的剪切、复制..按钮的监听对象
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.multiselect_button_copy:
				operateCopyOrCut = "copy";
				multiCopyOrCutClick();
				break;
			case R.id.multiselect_button_cut:
				operateCopyOrCut = "cut";
				multiCopyOrCutClick();
				break;
			case R.id.multiselect_button_cancel:
				multiCancelClick();
				break;
			case R.id.multiselect_button_delete:
				
				break;
				
			default:
				break;
			}
		}
	};

	OnClickListener selectListener = new OnClickListener() {// 创建粘贴、取消按钮的监听对象
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_moving_confirm:
				if (currentSrcPath.equals(currentPath)) {
					Toast.makeText(getActivity(), "处于同一目录中，请选择其他目录",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if(currentfile.isFile()){
					Toast.makeText(getActivity(), "请选择目录！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				mConfirmOperationBar.setVisibility(View.GONE);
				mcopyTask = new MyCopyOrCutTask();
				mcopyTask.execute(currentPath);
				break;
			case R.id.button_moving_cancel:
				mConfirmOperationBar.setVisibility(View.GONE);
				registerForContextMenu(listFileView);
				registerForContextMenu(gridFileView);
				strMultiPathArray.clear();
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 多选或者全选时的"复制"、"剪切"按钮处理流程
	 */
	public void multiCopyOrCutClick(){
		currentSrcPath = currentPath;
		// TODO Auto-generated method stub
		boolean isSelectFile = false;
		for (int i = 0; i < filelist.size(); i++) {
			isSelectFile = mSimpleAdapter.isSelected.get(i);
			if (isSelectFile) {
				break;
			}
		}
		if (!isSelectFile) {
			Toast.makeText(getActivity(), "请选择至少一个文件",
					Toast.LENGTH_SHORT).show();
		} else {
			for (int i = 0; i < filelist.size(); i++) {
				isSelectFile = mSimpleAdapter.isSelected.get(i);
				if (isSelectFile) {
					multiSelectPath = (String) filelist.get(i).get(
							"path");
					strMultiPathArray.add(multiSelectPath);
					//System.out.println("multiSelectPath:::"+ multiSelectPath);
				}
			}
			mSelectOperationBar.setVisibility(View.GONE);
			selectListMode = false;
			for (int i = 0; i < filelist.size(); i++) {
				mSimpleAdapter.isSelected.put(i, false);
				mSimpleAdapter.notifyDataSetChanged();
			}
			mConfirmOperationBar.setVisibility(View.VISIBLE);
			movingConfirmButton.setOnClickListener(selectListener);
			movingCancelButton.setOnClickListener(selectListener);
		}
	}
	
	/**
	 * 多选或者全选时的“取消”按钮处理流程
	 */
	public void multiCancelClick(){
		mSelectOperationBar.setVisibility(View.GONE);
		for (int i = 0; i < filelist.size(); i++) {
			mSimpleAdapter.isSelected.put(i, false);
			mSimpleAdapter.notifyDataSetChanged();
		}
		selectListMode = false;
	}

	public void startOnFileTypes(File file){
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
		} else if (JudgeMediaFileType.isApkFileType(file)) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
			startActivity(intent);
		}
	}
	
	public Drawable getApkIcon(String path) throws Exception {
		Drawable icon2;
		Drawable icon1;
		PackageManager pm = getActivity().getPackageManager();
		PackageInfo pkgInfo = pm.getPackageArchiveInfo(path,
				PackageManager.GET_ACTIVITIES);
		//if (pkgInfo != null) {
			ApplicationInfo appInfo = pkgInfo.applicationInfo;
			/* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
			appInfo.sourceDir = path;
			appInfo.publicSourceDir = path;
			String appName = pm.getApplicationLabel(appInfo).toString();// 得到应用名
			String packageName = appInfo.packageName; // 得到包名
			String version = pkgInfo.versionName; // 得到版本信息
			/* icon1和icon2其实是一样的 */
			icon1 = pm.getApplicationIcon(appInfo);// 得到图标信息
			icon2 = appInfo.loadIcon(pm);
			String pkgInfoStr = String.format(
					"PackageName:%s, Vesion: %s, AppName: %s", packageName,
					version, appName);
			//return icon1;
		//}
		return icon1;

	}
	
	public int getScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		return screenWidth;
	}
	
	/* (non-Javadoc)在fragment中重写onKeyUp事件
	 * @see com.example.sdfilemanager.BaseFragment#onKeyUp(int, android.view.KeyEvent)
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			String currPath = currentPath + File.separator;
			// System.out.println(currPath);
			if (currPath.equals(FileUtil.getSDPath())) {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					Toast.makeText(getActivity(), "再按一次退出程序",
							Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();
				} else {
					System.exit(0);
				}
			} else {
				gotoParentDir();
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
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(MyBroadcastReceiver);
	}

	@Override
	public void loadListData() {
		// TODO Auto-generated method stub
		refreshListItem();
	}
}
