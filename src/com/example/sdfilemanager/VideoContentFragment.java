package com.example.sdfilemanager;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class VideoContentFragment extends BaseFragment{
	
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
	private List<ImageBean> list = new ArrayList<ImageBean>();
	ImageGroupAdapter mImageSimpleAdapter;
	private boolean imageListMode = false;
	private boolean videoListMode = false;
	private boolean audioListMode = false;
	private boolean apkListMode = false;
	private ProgressDialog mProgressDialog;
	private final static int SCAN_OK = 1;
	List<String> childList = null;
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();


    
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
        	imageListMode = true;
        	view = inflater.inflate(R.layout.fragment_imagecontent, container, false); 
        	//System.out.println("imageMode!!!!");
        return view;  
        
	
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		
	}
	
	public void startloadListData(){
		getImagesList();
	}
	

	/**
	 * ����ContentProviderɨ���ֻ��е�ͼƬ���˷��������������߳���
	 */
	private void getImagesList() {
//		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//			getActivity().Toast.makeText(this, "�����ⲿ�洢", Toast.LENGTH_SHORT).show();
//			return;
//		}
		
		//��ʾ������
		//mProgressDialog = ProgressDialog.show(this, null, "���ڼ���...");
		//��ֹviewpager����ʱchildlist�����ظ�������
		if(childList != null){
			childList.clear();
		}
//		if(mGruopMap != null){
//			mGruopMap.clear();
//		}
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = getActivity().getContentResolver();

				//ֻ��ѯjpeg��png��ͼƬ
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				
				while (mCursor.moveToNext()) {
					//��ȡͼƬ��·��
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					
					//��ȡ��ͼƬ�ĸ�·����
					String parentName = new File(path).getParentFile().getName();

					
					//���ݸ�·������ͼƬ���뵽mGruopMap��
					if (!mGruopMap.containsKey(parentName)) {
						childList = new ArrayList<String>();
						childList.add(path);
						mGruopMap.put(parentName, childList);
						System.out.println("i am here");
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}
				
				mCursor.close();
				
				//֪ͨHandlerɨ��ͼƬ���
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
				//�رս�����
				//mProgressDialog.dismiss();
	        	GridView mGridView = (GridView)getActivity().findViewById(R.id.child_grid);
	        	
	        	mImageSimpleAdapter = new ImageGroupAdapter(getActivity(), list = subGroupOfImage(mGruopMap), mGridView);
				mGridView.setAdapter(mImageSimpleAdapter);
				mGridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						List<String> childList = mGruopMap.get(list.get(position).getFolderName());			
						Intent mIntent = new Intent(getActivity(), ShowAllImage.class);
						mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
						startActivity(mIntent);


						
					}
				});
				//adapter = new GroupAdapter(MainActivity.this, list, mGroupGridView);
				//mGroupGridView.setAdapter(adapter);
				break;
			}
		}
		
	};
	

	/**
	 * ��װ�������GridView������Դ����Ϊ����ɨ���ֻ���ʱ��ͼƬ��Ϣ����HashMap��
	 * ������Ҫ����HashMap��������װ��List
	 * 
	 * @param mGruopMap
	 * @return
	 */
	private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap){
		if(mGruopMap.size() == 0){
			return null;
		}
		List<ImageBean> list = new ArrayList<ImageBean>();
		
		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			
			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));//��ȡ����ĵ�һ��ͼƬ
			
			list.add(mImageBean);
		}
		
		return list;
		
	}
	
	

    
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			String currPath = currentPath + File.separator;
			System.out.println(currPath);
				if((System.currentTimeMillis() - mExitTime) > 2000){
					Toast.makeText(getActivity(), "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
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
