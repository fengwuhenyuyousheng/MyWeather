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
			mAutoUpdate.setText("�ر��Զ�����");
		} else {
			mAutoUpdate.setText("�����Զ�����");
		}
		mDeleteCurrent = (Button) findViewById(R.id.delete_current);
		mBackChoose = (Button) findViewById(R.id.back_choose);
		mUpdate = (Button) findViewById(R.id.update);
		mDeleteAll = (Button) findViewById(R.id.delete_all);
		mMyViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewListName = new ArrayList<String>();
		mViewList = new ArrayList<View>();
		mInfoListFromDB = mMyWeatherDB.loadWeatherInfo(null);
		Log.d("WeatherActivity", "�������ݿ���Ϣ�ĳ��ȣ�" + mInfoListFromDB.size());
		Log.d("WeatherActivity", "mInfoListFromDB:" + mInfoListFromDB.size() + " ,ViewList:" + mViewList.size());
		if (mInfoListFromDB.size() != 0) {
			for (int j = 0; j < mInfoListFromDB.size(); j++) {
				mViewListName.add(mInfoListFromDB.get(j).getCityName());
				mViewList.add(Utility.createViewFromWeatherInfo(mInfoListFromDB.get(j), WeatherActivity.this));
			}
		} else {
			Toast.makeText(WeatherActivity.this, "��ѡ�����", Toast.LENGTH_SHORT).show();
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

		// ��̬ע��㲥�����շ���ĸ�����Ϣ
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.myweaher.app.service.AutoUpdateService.UPDATE");
		mServiceReceiver = new ServiceReceiver();
		registerReceiver(mServiceReceiver, mIntentFilter);

	}

	@Override
	public void onResume() {
		super.onResume();
		mChooseCityName = Utility.readUpdateCityName(WeatherActivity.this);
		Log.d("WeatherActivity", "��ѯ�ĳ�����:" + mChooseCityName);
		if (mChooseCityName != null) {
			if (!judgeCityName(mViewListName, mChooseCityName)) {
				mViewListName.add(mChooseCityName);
			}
			sendWeatherInfoRequest(mChooseCityName);
		} else if (mViewListName.size() == 0) {
			Toast.makeText(WeatherActivity.this, "��ѡ�����", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onPause() {
		Utility.saveUpdateCityName(WeatherActivity.this, null);
		super.onPause();
	}

	// ���õ����ť�¼�
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
				mAutoUpdate.setText("�����Զ�����");
			} else {
				startService(AutoUpdateServiceIntent);
				mAutoUpdate.setText("�ر��Զ�����");
			}
			break;
		case R.id.update:
			if (mInfoListFromDB.size() != 0) {
				Log.d("WeatherActivity", "ִ���ֶ�����,���³�������" + mViewListName.get(mCurrentCode));
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

					Toast.makeText(WeatherActivity.this, "ɾ�����г��е���Ϣ", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(WeatherActivity.this, "ɾ��ʧ��,��ʷ���ݿ�Ϊ��", Toast.LENGTH_SHORT).show();
				}
				mMyPagerAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.delete_current:
			if (mInfoListFromDB.size() != 0) {
				Log.d("WeatherActivity", "ִ��ɾ����ǰ����,ɾ����������" + mViewListName.get(mCurrentCode));
				Boolean result = mMyWeatherDB.deleteWeatherInfo(mViewListName.get(mCurrentCode));
				mViewListName.remove(mCurrentCode);
				mViewList.remove(mCurrentCode);
				if (result) {
					Toast.makeText(WeatherActivity.this, "ɾ����Ϣ�ɹ�", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(WeatherActivity.this, "ɾ��ʧ��,��ʷ���ݿ�Ϊ��", Toast.LENGTH_SHORT).show();
				}
				mMyPagerAdapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
	}

	// ���͸��ݳ������Ʒ����������󣬻�ȡ�������ݲ��������ݿ���
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
						Log.d("WeatherActivity", "ִ����Ӳ���");
					} else {
						mMyWeatherDB.updateWeatherInfo(newWeatherInfo);
						Log.d("WeatherActivity", "ִ�и��²���");
					}
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							refreshUI();
						}
					});
				} else {
					Toast.makeText(WeatherActivity.this, "��ѯʧ��", Toast.LENGTH_SHORT).show();
				}
			}

			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(WeatherActivity.this, "��ѯʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	// �жϺ�̨�����Ƿ���������������true��ֹͣ����false
	public boolean serviceRunningOrStop() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.myweather.app.AutoUpdateService".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	// ��ʾ���ȶԻ���
	private void showProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("���ڼ��س��е�������Ϣ....");
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}

	// ˢ��ҳ��
	public void refreshUI() {
		mCurrentCode = mMyViewPager.getCurrentItem();
		Log.d("WeatherActivity", "��ǰҳ�棺" + mViewListName.get(mCurrentCode));
		mInfoListFromDB = mMyWeatherDB.loadWeatherInfo(null);
		Log.d("WeatherActivity", "�������ݿ���Ϣ�ĳ��ȣ�" + mInfoListFromDB.size());
		Log.d("WeatherActivity", "ViewList:" + mViewList.size() + ",ViewListName:" + mViewListName.size());
		if (mViewList.size() != mViewListName.size()) {
			mViewList.add(Utility.createViewFromWeatherInfo(mInfoListFromDB.get(mInfoListFromDB.size() - 1),
					WeatherActivity.this));
//			mMyViewPager.setCurrentItem(mCurrentCode);
		} else if (mViewListName.size() != 0) {
			for (int i = 0; i < mViewListName.size(); i++) {
				if (mViewListName.get(i) .equals(mChooseCityName) ) {
					mViewList.set(i, Utility.createViewFromWeatherInfo(mInfoListFromDB.get(i), WeatherActivity.this));
					Log.d("WeatherActivity", "������"+i+"ҳ��");
				}
			}
		}
		mMyPagerAdapter.notifyDataSetChanged();

		// mMyViewPager.setCurrentItem(mCurrentCode);
	}

	// �ж�Ҫ��ӵĳ����Ƿ��Ѿ�����
	public Boolean judgeCityName(List<String> cityNameList, String cityName) {
		for (int i = 0; i < cityNameList.size(); i++) {
			if (cityNameList.get(i).equals(cityName)) {
				Toast.makeText(WeatherActivity.this, "�����Ѿ����", Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		return false;
	}

	// �رս��ȶԻ���
	private void closeProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	private class ServiceReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			Log.d("WeatherActivity", "���չ㲥");
			if (mInfoListFromDB.size() != 0) {
				Log.d("WeatherActivity", "ִ���ֶ�����,���³�������" + mViewListName.get(mCurrentCode));
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
		Log.d("WeatherActivity", "��ǰҳ�棺" + mViewListName.get(mCurrentCode));
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}

}
