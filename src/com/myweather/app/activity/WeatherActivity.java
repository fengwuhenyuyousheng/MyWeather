package com.myweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.myweather.app.R;
import com.myweather.app.adapter.MyPagerAdapter;
import com.myweather.app.db.MyWeatherDB;
import com.myweather.app.model.WeatherInfo;
import com.myweather.app.service.AutoUpdateService;
import com.myweather.app.util.HttpCallBackListener;
import com.myweather.app.util.HttpUtil;
import com.myweather.app.util.Utility;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener, OnPageChangeListener {

	private Button mBackChoose;
	private Button mUpdate;
	private Button mDeleteAll;
	private Button mAutoUpdate;
	private Button mDeleteCurrent;

	private ViewPager mMyViewPager;
	private MyWeatherDB mMyWeatherDB;

	private MyPagerAdapter mMyPagerAdapter;
	private IntentFilter mIntentFilter;
	private ServiceReceiver mServiceReceiver;
	private String mChooseCityName;
	private boolean mServiceStartOrStop;
	private ProgressDialog mProgressDialog;
	private int mCurrentCode;

	private List<View> mViewList;
	private List<WeatherInfo> mInfoListFromDB;
	private List<String> mViewListName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weather);
		mMyWeatherDB = MyWeatherDB.getInstance(this);
		mAutoUpdate = (Button) findViewById(R.id.auto_update);
		mServiceStartOrStop = serviceRunningOrStop();
		if (mServiceStartOrStop) {
			mAutoUpdate.setText("关闭自动更新");
		} else {
			mAutoUpdate.setText("启动自动更新");
		}
		mDeleteCurrent = (Button) findViewById(R.id.delete_current);
		mBackChoose = (Button) findViewById(R.id.back_choose);
		mUpdate = (Button) findViewById(R.id.update);
		mDeleteAll = (Button) findViewById(R.id.delete_all);
		mMyViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewListName = new ArrayList<String>();
		mViewList = new ArrayList<View>();
		mInfoListFromDB = mMyWeatherDB.loadWeatherInfo(null);
		Log.d("WeatherActivity", "返回数据库信息的长度：" + mInfoListFromDB.size());
		Log.d("WeatherActivity", "mInfoListFromDB:" + mInfoListFromDB.size() + " ,ViewList:" + mViewList.size());
		if (mInfoListFromDB.size() != 0) {
			for (int j = 0; j < mInfoListFromDB.size(); j++) {
				mViewListName.add(mInfoListFromDB.get(j).getCityName());
				mViewList.add(Utility.createViewFromWeatherInfo(mInfoListFromDB.get(j), WeatherActivity.this));
			}
		} else {
			Toast.makeText(WeatherActivity.this, "请选择城市", Toast.LENGTH_SHORT).show();
		}
		Utility.saveUpdateCityName(WeatherActivity.this, null);
		mMyPagerAdapter = new MyPagerAdapter(mViewList);
		mMyViewPager.setAdapter(mMyPagerAdapter);
		mCurrentCode = mMyViewPager.getCurrentItem();
		mMyViewPager.setOffscreenPageLimit(1);

		mBackChoose.setOnClickListener(this);
		mUpdate.setOnClickListener(this);
		mDeleteAll.setOnClickListener(this);
		mAutoUpdate.setOnClickListener(this);
		mDeleteCurrent.setOnClickListener(this);
		mMyViewPager.setOnPageChangeListener(this);

		// 动态注册广播，接收服务的更新信息
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.myweaher.app.service.AutoUpdateService.UPDATE");
		mServiceReceiver = new ServiceReceiver();
		registerReceiver(mServiceReceiver, mIntentFilter);

	}

	@Override
	public void onResume() {
		super.onResume();
		mChooseCityName = Utility.readUpdateCityName(WeatherActivity.this);
		Log.d("WeatherActivity", "查询的城市名:" + mChooseCityName);
		if (mChooseCityName != null) {
			if (!judgeCityName(mViewListName, mChooseCityName)) {
				mViewListName.add(mChooseCityName);
			}
			sendWeatherInfoRequest(mChooseCityName);
		} else if (mViewListName.size() == 0) {
			Toast.makeText(WeatherActivity.this, "请选择城市", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onPause() {
		Utility.saveUpdateCityName(WeatherActivity.this, null);
		super.onPause();
	}

	// 设置点击按钮事件
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_choose:
			Intent backChooseIntent = new Intent(WeatherActivity.this, SearchCityActivity.class);
			startActivity(backChooseIntent);
			break;
		case R.id.auto_update:
			Intent AutoUpdateServiceIntent = new Intent(WeatherActivity.this, AutoUpdateService.class);
			if (mServiceStartOrStop) {
				stopService(AutoUpdateServiceIntent);
				mAutoUpdate.setText("启动自动更新");
			} else {
				startService(AutoUpdateServiceIntent);
				mAutoUpdate.setText("关闭自动更新");
			}
			break;
		case R.id.update:
			if (mInfoListFromDB.size() != 0) {
				Log.d("WeatherActivity", "执行手动更新,更新城市名：" + mViewListName.get(mCurrentCode));
				mChooseCityName=mViewListName.get(mCurrentCode);
				sendWeatherInfoRequest(mViewListName.get(mCurrentCode));
			}
			break;
		case R.id.delete_all:
			if (mInfoListFromDB.size() != 0) {
				Utility.saveUpdateCityName(WeatherActivity.this, null);
				Boolean result = mMyWeatherDB.deleteWeatherInfo();
				mViewListName.clear();
				mViewList.clear();
				if (result) {

					Toast.makeText(WeatherActivity.this, "删除所有城市的信息", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(WeatherActivity.this, "删除失败,历史数据库为空", Toast.LENGTH_SHORT).show();
				}
				mMyPagerAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.delete_current:
			if (mInfoListFromDB.size() != 0) {
				Log.d("WeatherActivity", "执行删除当前城市,删除城市名：" + mViewListName.get(mCurrentCode));
				Boolean result = mMyWeatherDB.deleteWeatherInfo(mViewListName.get(mCurrentCode));
				mViewListName.remove(mCurrentCode);
				mViewList.remove(mCurrentCode);
				if (result) {
					Toast.makeText(WeatherActivity.this, "删除信息成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(WeatherActivity.this, "删除失败,历史数据库为空", Toast.LENGTH_SHORT).show();
				}
				mMyPagerAdapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
	}

	// 发送根据城市名称发送网络请求，获取天气数据并存入数据库中
	public void sendWeatherInfoRequest(String cityName) {
		String address = "https://api.thinkpage.cn/v3/weather/daily.json?key=MT5LUU4K10&location=" + cityName
				+ "&language=zh-Hans&unit=c&start=0&days=5";
		showProgressDialog();
		HttpUtil.sendWeatherRequestWithHttpClient(address, new HttpCallBackListener() {
			public void onFinish(String response) {
				WeatherInfo newWeatherInfo = Utility.handleWeatherResponse(response);
				if (newWeatherInfo != null) {
					List<WeatherInfo> infoList = mMyWeatherDB.loadWeatherInfo(newWeatherInfo.getCityName());
					if (infoList.size() == 0) {
						mMyWeatherDB.saveWeatherInfo(newWeatherInfo);
						Log.d("WeatherActivity", "执行添加操作");
					} else {
						mMyWeatherDB.updateWeatherInfo(newWeatherInfo);
						Log.d("WeatherActivity", "执行更新操作");
					}
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							refreshUI();
						}
					});
				} else {
					Toast.makeText(WeatherActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
				}
			}

			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(WeatherActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	// 判断后台服务是否启动，启动返回true，停止返回false
	public boolean serviceRunningOrStop() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.myweather.app.AutoUpdateService".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	// 显示进度对话框
	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("正在加载城市的天气信息....");
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}

	// 刷新页面
	public void refreshUI() {
		mCurrentCode = mMyViewPager.getCurrentItem();
		Log.d("WeatherActivity", "当前页面：" + mViewListName.get(mCurrentCode));
		mInfoListFromDB = mMyWeatherDB.loadWeatherInfo(null);
		Log.d("WeatherActivity", "返回数据库信息的长度：" + mInfoListFromDB.size());
		Log.d("WeatherActivity", "ViewList:" + mViewList.size() + ",ViewListName:" + mViewListName.size());
		if (mViewList.size() != mViewListName.size()) {
			mViewList.add(Utility.createViewFromWeatherInfo(mInfoListFromDB.get(mInfoListFromDB.size() - 1),
					WeatherActivity.this));
//			mMyViewPager.setCurrentItem(mCurrentCode);
		} else if (mViewListName.size() != 0) {
			for (int i = 0; i < mViewListName.size(); i++) {
				if (mViewListName.get(i) .equals(mChooseCityName) ) {
					mViewList.set(i, Utility.createViewFromWeatherInfo(mInfoListFromDB.get(i), WeatherActivity.this));
					Log.d("WeatherActivity", "更换第"+i+"页面");
				}
			}
		}
		mMyPagerAdapter.notifyDataSetChanged();

		// mMyViewPager.setCurrentItem(mCurrentCode);
	}

	// 判断要添加的城市是否已经存在
	public Boolean judgeCityName(List<String> cityNameList, String cityName) {
		for (int i = 0; i < cityNameList.size(); i++) {
			if (cityNameList.get(i).equals(cityName)) {
				Toast.makeText(WeatherActivity.this, "城市已经添加", Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		return false;
	}

	// 关闭进度对话框
	private void closeProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	private class ServiceReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Log.d("WeatherActivity", "接收广播");
			if (mInfoListFromDB.size() != 0) {
				Log.d("WeatherActivity", "执行手动更新,更新城市名：" + mViewListName.get(mCurrentCode));
				mChooseCityName=mViewListName.get(mCurrentCode);
				sendWeatherInfoRequest(mViewListName.get(mCurrentCode));
			}
		}
	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mServiceReceiver);
	}

	public void onBackPressed() {
		finish();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		mCurrentCode = mMyViewPager.getCurrentItem();
		Log.d("WeatherActivity", "当前页面：" + mViewListName.get(mCurrentCode));
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}

}
