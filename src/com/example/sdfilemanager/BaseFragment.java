package com.example.sdfilemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

public abstract class BaseFragment extends Fragment {
	private boolean isFirstSelect = false;
	
	/**
	 * 加载数据
	 */
	public abstract void loadListData();
	
	/**
	 * 接收Activity按键
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public abstract boolean onKeyUp(int keyCode, KeyEvent event);

}
