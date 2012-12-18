package com.example.devicehive.android.client.sample;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class TabsAdapter extends FragmentPagerAdapter implements
		ActionBar.TabListener, ViewPager.OnPageChangeListener {

	private final Context mContext;
	private final ActionBar mActionBar;
	private final ArrayList<ActionBar.Tab> mTabs = new ArrayList<ActionBar.Tab>();
	private final ViewPager mViewPager;

	public TabsAdapter(SherlockFragmentActivity activity, ViewPager viewPager) {
		super(activity.getSupportFragmentManager());
		mContext = activity;
		mActionBar = activity.getSupportActionBar();
		mViewPager = viewPager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(ActionBar.Tab tab, Fragment fragment) {
		tab.setTag(fragment);
		tab.setTabListener(this);
		mTabs.add(tab);
		mActionBar.addTab(tab);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}

	@Override
	public Fragment getItem(int position) {
		ActionBar.Tab tab = mTabs.get(position);
		return (Fragment) tab.getTag();
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		for (int i = 0; i < mTabs.size(); i++) {
			if (mTabs.get(i) == tab) {
				mViewPager.setCurrentItem(i);
			}
		}
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		mActionBar.setSelectedNavigationItem(position);
	}
}