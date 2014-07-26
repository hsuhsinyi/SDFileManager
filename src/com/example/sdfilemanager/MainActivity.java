package com.example.sdfilemanager;

import java.util.ArrayList;
import java.util.List;





import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Build;

public class MainActivity extends FragmentActivity {
	private static final String TAG = "MainActivity";
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private ImageView ivBottomLine;
	private TextView TabSDFile, TabPicFile, TabMusicFile, TabVideoFile,
			TabZipFile;

	private int currIndex = 0;
	private int bottomLineWidth;
	private int offset = 0;
	private int position_one;
	private int position_two;
	private int position_three;
	private int position_four;
	private Resources resources;
	private ScanPagerAdapter adapter;
	private long mExitTime;
	private int screenW;
	Fragment activityfragment;
	Fragment groupFragment;
	Fragment friendsFragment;
	Fragment chatFragment;
	Fragment peopleFragment;
	
	View mConfirmOperationBar;
	private LinearLayout linearLayout;
	private ToggleButton listOrGridButton;
	public final String PRESS_ACTION = "com.example.broadcast.receiver.listbuttonpressed";
	public SharedPreferences userInfo;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		resources = getResources();
		
		InitWidth();
		InitTextView();
		InitViewPager();
		
		userInfo = getSharedPreferences("user_info", 0);  
		userInfo.edit().putString("listOrGrid", "list").commit();
	}



	private void InitTextView() {
		//动态改变viewpager的布局
		LinearLayout viewPagerHead = (LinearLayout)findViewById(R.id.layout_viewpagerhead);
		LayoutParams params = viewPagerHead.getLayoutParams();
		params.width = getScreenWidth() / 2;
		viewPagerHead.setLayoutParams(params);
		
		
		
		TabSDFile = (TextView) findViewById(R.id.category_sd_file);
		TabPicFile = (TextView) findViewById(R.id.category_pic_file);
		TabVideoFile = (TextView) findViewById(R.id.category_video_file);
		TabMusicFile = (TextView) findViewById(R.id.category_music_file);
		TabZipFile = (TextView) findViewById(R.id.category_apk_file);

		TabSDFile.setOnClickListener(new MyOnClickListener(0));
		TabPicFile.setOnClickListener(new MyOnClickListener(1));
		TabVideoFile.setOnClickListener(new MyOnClickListener(2));
		TabMusicFile.setOnClickListener(new MyOnClickListener(3));
		TabZipFile.setOnClickListener(new MyOnClickListener(4));
		
		//listOrGridButton = (ToggleButton) findViewById(R.id.status_listorgrid);
//		listOrGridButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(listOrGridButton.isChecked()){
//					System.out.println("listOrGridButton is checked!!!");
//					Intent intent = new Intent();
//					intent.setAction(PRESS_ACTION);
//					intent.putExtra("msg", "pressed");
//					MainActivity.this.sendBroadcast(intent);
//					userInfo.edit().putString("listOrGrid", "grid").commit(); 
//				}else{
//					System.out.println("listOrGridButton is unchecked!!!");
//					Intent intent = new Intent();
//					intent.setAction(PRESS_ACTION);
//					intent.putExtra("msg", "unpressed");
//					MainActivity.this.sendBroadcast(intent);
//					userInfo.edit().putString("listOrGrid", "list").commit(); 
//				}
//			}
//		});
	}
	
	
	/**初始化viewpager
	
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		// fragmentsList = null;
		fragmentsList = new ArrayList<Fragment>();
		// LayoutInflater mInflater = getLayoutInflater();
		// View activityView = mInflater.inflate(R.layout.c, null);
		System.out.println("isSdCardAvailable" + FileUtil.isSdCardAvailable());
		if (FileUtil.isSdCardAvailable()) {
			activityfragment = new AllFileContentFragment();
			groupFragment = AllMediaContentFragment
					.newInstance(AllMediaContentFragment.TYPE_IMAGE);
			friendsFragment = AllMediaContentFragment
					.newInstance(AllMediaContentFragment.TYPE_VIDEO);
			chatFragment = AllMediaContentFragment
					.newInstance(AllMediaContentFragment.TYPE_AUDIO);
			peopleFragment = AllMediaContentFragment
					.newInstance(AllMediaContentFragment.TYPE_APK);
			fragmentsList.add(activityfragment);
			fragmentsList.add(groupFragment);
			fragmentsList.add(friendsFragment);
			fragmentsList.add(chatFragment);
			fragmentsList.add(peopleFragment);
		} else {
			activityfragment = new StorageNotReadyFragment();
			fragmentsList.add(activityfragment);
		}

		adapter = new ScanPagerAdapter(getSupportFragmentManager(),
				fragmentsList);
		// mPager.setAdapter(new
		// MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
		mPager.setAdapter(adapter);
		// mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	@SuppressLint("NewApi")
	private void InitWidth() {
		ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
		bottomLineWidth = ivBottomLine.getLayoutParams().width;
		Log.d(TAG, "cursor imageview width=" + bottomLineWidth);
		screenW = getScreenWidth()/2;
		int eachWith = (int) (screenW / 5.0);
		ivBottomLine
				.setLayoutParams(new LinearLayout.LayoutParams(eachWith, 5));
		// offset = (int) (screenW /10.0) - (int) (bottomLineWidth/2);
		// ivBottomLine.setX((float)offset);
		offset = (int) ((screenW / 5.0) / 2);
		Log.i("MainActivity", "offset=" + offset);

		position_one = (int) (screenW / 5.0);
		position_two = (int) (screenW / 5.0) * 2;
		position_three = (int) (screenW / 5.0) * 3;
		position_four = (int) (screenW / 5.0) * 4;
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
			System.out.println("index" + index);
		}

		@Override
		public void onClick(View v) {
			System.out.println("index" + index);
			mPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(position_one, 0, 0, 0);
					// tvTabGroups.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two, 0, 0, 0);
					// tvTabFriends.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three, 0, 0, 0);
					// tvTabChat.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 4) {
					animation = new TranslateAnimation(position_four, 0, 0, 0);
				}
				// tvTabActivity.setTextColor(resources.getColor(R.color.white));
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, position_one, 0,
							0);
					// tvTabActivity.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_one, 0, 0);
					// tvTabFriends.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three,
							position_one, 0, 0);
					// tvTabChat.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 4) {
					animation = new TranslateAnimation(position_four,
							position_one, 0, 0);
				}
				// tvTabGroups.setTextColor(resources.getColor(R.color.white));
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, position_two, 0,
							0);
					// tvTabActivity.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_two, 0, 0);
					// tvTabGroups.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three,
							position_two, 0, 0);
					// tvTabChat.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 4) {
					animation = new TranslateAnimation(position_four,
							position_two, 0, 0);
				}

				// tvTabFriends.setTextColor(resources.getColor(R.color.white));
				break;
			case 3:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, position_three,
							0, 0);
					// tvTabActivity.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_three, 0, 0);
					// tvTabGroups.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_three, 0, 0);
					// tvTabFriends.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 4) {
					animation = new TranslateAnimation(position_four,
							position_three, 0, 0);
				}
				// tvTabChat.setTextColor(resources.getColor(R.color.white));
				break;
			case 4:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, position_four,
							0, 0);
					// tvTabActivity.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(position_one,
							position_four, 0, 0);
					// tvTabGroups.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(position_two,
							position_four, 0, 0);
					// tvTabFriends.setTextColor(resources.getColor(R.color.lightwhite));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(position_three,
							position_four, 0, 0);
				}
				// tvTabChat.setTextColor(resources.getColor(R.color.white));
				break;
			}
			currIndex = arg0;
			((BaseFragment) adapter.getItem(arg0)).loadListData();
			animation.setFillAfter(true);
			animation.setDuration(300);
			ivBottomLine.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			System.out.println("now is landscape");
			// screenW = getScreenWidth();
			InitWidth();
			InitTextView();
			InitViewPager();
		}
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			System.out.println("now is portrait");
			// screenW = getScreenWidth();
			InitWidth();
			InitTextView();
			InitViewPager();
		}
		super.onConfigurationChanged(newConfig);
	}

	public int getScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		return screenWidth;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		// TODO Auto-generated method stub
		//此处使用了多态的思想：
		//如果想要调用子类中有而父类中没有的方法，需要进行强制类型转换;
		//因为当用父类的引用指向子类的对象，用父类引用调用方法时，找不到父类中不存在的方法,这时候需要进行向下的类型转换，将父类引用转换为子类引用
		//当使用多态方式调用方法时，首先检查父类中是否有该方法，如果没有，则编译错误；
		
//		return ((BaseFragment) adapter.getItem(mPager.getCurrentItem()))
//				.onKeyUp(keyCode, event);
		Fragment contentFragment = adapter.getItem(mPager.getCurrentItem());
		BaseFragment baseFragment = (BaseFragment)contentFragment;
		return baseFragment.onKeyUp(keyCode, event);
	}
}
