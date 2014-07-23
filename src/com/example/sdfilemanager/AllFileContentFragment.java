package com.example.sdfilemanager;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.sdfilemanager.AllFileAdapter.ViewHolder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AllFileContentFragment extends BaseFragment {

	private TextView showPathView;
	private ListView listFileView;
	private EditText editTextRename;
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
	private long mExitTime = 0;
	public static final int TYPE_ALLFILE = 1;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_VIDEO = 3;
	public CheckBox checkboxlistBox;
	private String currentPath;
	private AllFileAdapter mSimpleAdapter;
	private View mConfirmOperationBar;
	private View mSelectOperationBar;
	private String currentSelectPath;
	private MyCopyOrCutTask mcopyTask;
	private String operateCopyOrCut;
	private String multiSelectPath;
	// 此参数为是否是多选和正常选择状态
	boolean selectListMode = false;
	File currentfile;
	ProgressDialog myProgressDialog;
	ArrayList<String> strMultiPathArray;
	Button movingConfirmButton;
	Button movingCancelButton;
	private String currentSrcPath = null;

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

		listFileView = (ListView) getActivity().findViewById(R.id.allfile_list);
		showPathView = (TextView) getActivity().findViewById(R.id.showpathview);
		movingConfirmButton = (Button) getActivity().findViewById(
				R.id.button_moving_confirm);
		movingCancelButton = (Button) getActivity().findViewById(
				R.id.button_moving_cancel);
		refreshListItem(Environment.getExternalStorageDirectory()
				+ File.separator);
		currentPath = Environment.getExternalStorageDirectory() + "";
		strMultiPathArray = new ArrayList<String>();
		listFileView.setOnCreateContextMenuListener(this);
	}

	public void refreshListItem(String path) {
		showPathView.setText(path);
		filelist = buildListForSimpleAdapter(path);
		mSimpleAdapter = new AllFileAdapter(getActivity(), filelist,
				R.layout.listrow_allfile, new String[] { "image", "name",
						"modifytime", "path" }, new int[] { R.id.imageDir,
						R.id.name, R.id.mdtime });
		listFileView.setAdapter(mSimpleAdapter);
		listFileView.setOnItemClickListener(mItemClickListener);
		listFileView.setSelection(0);
	}

	OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			System.out.println("current selectmode is:" + selectListMode);
			if (selectListMode) {
				if (mSimpleAdapter.isSelected.get(position)) {
					mSimpleAdapter.isSelected.put(position, false);
					listFileView.setAdapter(mSimpleAdapter);
				} else if (!mSimpleAdapter.isSelected.get(position)) {
					mSimpleAdapter.isSelected.put(position, true);
					listFileView.setAdapter(mSimpleAdapter);
				}
			} else {
				currentPath = (String) filelist.get(position).get("path");
				File file = new File(currentPath);
				if (file.isDirectory()) {
					refreshListItem(currentPath);
				} else if (file.isFile()) {
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
						intent.setDataAndType(Uri.fromFile(file),
								"application/vnd.android.package-archive");
						startActivity(intent);
//						Intent intent = new Intent("android.intent.action.VIEW");
//						intent.addCategory("android.intent.category.DEFAULT");
//						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						Uri uri = Uri.fromFile(file);
//						intent.setDataAndType(uri, "text/plain");
					}
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
				map.put("image", R.drawable.ic_dir);
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
					map.put("image", R.drawable.file_icon_picture);
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
					map.put("image", R.drawable.file_icon_video);
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
					map.put("image", R.drawable.file_icon_mp3);
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
					map.put("image", R.drawable.file_icon_txt);
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
					map.put("image", R.drawable.file_icon_apk);
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
		refreshListItem(currentPath);

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
			copyContextMenuOperate();
			break;
		// 剪切选项
		case ITEM4:
			operateCopyOrCut = "cut";
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
		movingCancelButton.setOnClickListener(listener);
		movingConfirmButton.setOnClickListener(listener);
		strMultiPathArray.add(currentSelectPath);
	}

	private class MyCopyOrCutTask extends AsyncTask<String, Integer, String> {

		public MyCopyOrCutTask() {
			myProgressDialog = new ProgressDialog(getActivity());
			myProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			myProgressDialog.setTitle("文件操作");
			myProgressDialog.setMessage("正在处理中....");
			myProgressDialog.setIndeterminate(false);
			myProgressDialog.setCancelable(false);
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
			refreshListItem(currentPath);
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
									refreshListItem(currentPath);

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
									refreshListItem(currentPath);
								}
							}).setNegativeButton("取消", null).create();
			return renameDialog;

		case 3:
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
									refreshListItem(currentPath);
								}
							}).setNegativeButton("取消", null).create();
			return newFileFoldDialog;

		case 4:
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
	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == 1) {
			Dialog newFoldDialog = onCreatDialog(3);
			newFoldDialog.show();
		} else if (id == 2) {
			for (int i = 0; i < filelist.size(); i++) {
				mSimpleAdapter.isSelected.put(i, true);
				listFileView.setAdapter(mSimpleAdapter);
			}
			selectListMode = true;
			multiSelectOption();
		} else if (id == 3) {
			refreshListItem(currentPath);
		} else if (id == 4) {
			selectListMode = true;
			multiSelectOption();
		} else if (id == 5) {
			for (int i = 0; i < filelist.size(); i++) {
				mSimpleAdapter.isSelected.put(i, false);
				listFileView.setAdapter(mSimpleAdapter);
			}
			selectListMode = false;
		} else if (id == 6) {
			getActivity().finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void multiSelectOption() {
		// TODO Auto-generated method stub

		mSelectOperationBar = getActivity().findViewById(
				R.id.multiselect_operation_bar);
		mConfirmOperationBar = getActivity().findViewById(
				R.id.moving_operation_bar);
		mSelectOperationBar.setVisibility(View.VISIBLE);
		movingConfirmButton = (Button) getActivity().findViewById(
				R.id.button_moving_confirm);
		movingCancelButton = (Button) getActivity().findViewById(
				R.id.button_moving_cancel);
		Button multiSelectCopyButton = (Button) getActivity().findViewById(
				R.id.multiselect_button_copy);
		Button multiSelectCutButton = (Button) getActivity().findViewById(
				R.id.multiselect_button_cut);
		Button multiSelectDeleteButton = (Button) getActivity().findViewById(
				R.id.multiselect_button_delete);
		Button multiSelectCancelButton = (Button) getActivity().findViewById(
				R.id.multiselect_button_cancel);

		multiSelectCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mSelectOperationBar.setVisibility(View.GONE);
				for (int i = 0; i < filelist.size(); i++) {
					mSimpleAdapter.isSelected.put(i, false);
					listFileView.setAdapter(mSimpleAdapter);
				}
				selectListMode = false;
			}
		});
		multiSelectCopyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
							System.out.println("multiSelectPath:::"
									+ multiSelectPath);
						}
					}
					mSelectOperationBar.setVisibility(View.GONE);
					selectListMode = false;
					for (int i = 0; i < filelist.size(); i++) {
						mSimpleAdapter.isSelected.put(i, false);
						listFileView.setAdapter(mSimpleAdapter);
					}
					mConfirmOperationBar.setVisibility(View.VISIBLE);
					movingConfirmButton.setOnClickListener(listener);
					movingCancelButton.setOnClickListener(listener);
					operateCopyOrCut = "copy";
				}
			}
		});

	}

	Button.OnClickListener listener = new Button.OnClickListener() {// 创建监听对象
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_moving_confirm:
				if (currentSrcPath.equals(currentPath)) {
					Toast.makeText(getActivity(), "处于同一目录中，请选择其他目录",
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
				strMultiPathArray.clear();
				break;
			default:
				break;
			}
		}
	};

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
	public void loadListData() {
		// TODO Auto-generated method stub

	}

}
