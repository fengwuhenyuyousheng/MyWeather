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

	// ����ҳ��������
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mViewList.size();
	}

	// view�Ƿ����Զ���
	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == (View)object;
	}

	// ʵ����һ��ҳ��
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
//		mViewList.get(position).setTag(position);
		container.addView(mViewList.get(position));
		Log.d("MyPagerAdapter", "ʵ������"+position+"ҳ��");
		Log.d("MyPagerAdapter", "ʵ������һ����"+container.getChildCount()+"��ҳ��");
		return mViewList.get(position);
	}

	// ����һ��ҳ��
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		object=(View)object;
		((ViewPager)container).removeView(mViewList.get(position));
		Log.d("MyPagerAdapter", "���ٵ�"+position+"ҳ��");
		Log.d("MyPagerAdapter", "���ٺ�һ����"+container.getChildCount()+"��View");
	}

	// ����һ��ҳ��
	@Override
	public int getItemPosition(Object object) {
//		View v=(View)object;
//		if((Integer)v.getTag()==Utility.UICurrentCode){
//			Log.d("MyPagerAdapter", "ˢ��"+Utility.UICurrentCode+"ҳ��");
//			return POSITION_NONE;
//		}else{
//			return POSITION_UNCHANGED;
//		}
//		return super.getItemPosition(object);
		Log.d("MyPagerAdapter", "ˢ��ҳ��");
		return POSITION_NONE;
	}
}
