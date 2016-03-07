package com.myweather.app.adapter;

import java.util.List;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MyPagerAdapter extends PagerAdapter {

	List<View> mViewList;

	public MyPagerAdapter(List<View> viewList) {
		this.mViewList = viewList;

	}

	// 返回页卡的数量
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mViewList.size();
	}

	// view是否来自对象
	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == (View)object;
	}

	// 实例化一个页卡
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
//		mViewList.get(position).setTag(position);
		container.addView(mViewList.get(position));
		Log.d("MyPagerAdapter", "实例化第"+position+"页卡");
		Log.d("MyPagerAdapter", "实例化后，一共有"+container.getChildCount()+"个页面");
		return mViewList.get(position);
	}

	// 销毁一个页卡
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		object=(View)object;
		((ViewPager)container).removeView(mViewList.get(position));
		Log.d("MyPagerAdapter", "销毁第"+position+"页卡");
		Log.d("MyPagerAdapter", "销毁后，一共有"+container.getChildCount()+"个View");
	}

	// 更新一个页卡
	@Override
	public int getItemPosition(Object object) {
//		View v=(View)object;
//		if((Integer)v.getTag()==Utility.UICurrentCode){
//			Log.d("MyPagerAdapter", "刷新"+Utility.UICurrentCode+"页面");
//			return POSITION_NONE;
//		}else{
//			return POSITION_UNCHANGED;
//		}
//		return super.getItemPosition(object);
		Log.d("MyPagerAdapter", "刷新页面");
		return POSITION_NONE;
	}
}
