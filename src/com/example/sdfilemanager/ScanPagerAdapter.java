package com.example.sdfilemanager;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ScanPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragmentsList;

	public ScanPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	public ScanPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragmentsList) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.fragmentsList = fragmentsList;
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return fragmentsList.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return null != fragmentsList ? fragmentsList.size() : 0;
	}

}
