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
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isFirstSelect = false;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		//��һ�ν���Ӧ��ʱ��������
		if(isFirstSelect){
			loadListData();
			isFirstSelect = false;
		}
	}
	
	

	public boolean isSelect() {
		return isFirstSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isFirstSelect = isSelect;
	}
	

}
