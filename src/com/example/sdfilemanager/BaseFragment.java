package com.example.sdfilemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

public abstract class BaseFragment extends Fragment {
	private boolean isFirstSelect = false;
	
	/**
	 * ��������
	 */
	public abstract void loadListData();
	
	/**
	 * ����Activity����
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public abstract boolean onKeyUp(int keyCode, KeyEvent event);

}
