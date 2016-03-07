package com.myweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import com.myweather.app.db.MyWeatherDB;
import com.myweather.app.model.WeatherInfo;
import com.myweather.app.receiver.AlarmReceiver;
import com.myweather.app.util.HttpCallBackListener;
import com.myweather.app.util.HttpUtil;
import com.myweather.app.util.Utility;

public class AutoUpdateService extends Service {

	private MyWeatherDB myWeatherDB;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		
		myWeatherDB = MyWeatherDB.getInstance(this);
		
		new Thread(new Runnable() {
			public void run() {
				upWeatherInfo();
			}
		}).start();
		AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
		int time=60*1000;
		long triggerAtTime=SystemClock.elapsedRealtime()+time;
		Intent i=new Intent(this,AlarmReceiver.class);
		PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	private void upWeatherInfo() {
		SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
		String currentCityName=sharedPreferences.getString("currentCityName", "");
		String address = "https://api.thinkpage.cn/v3/weather/now.json?key=MT5LUU4K10&location=" +currentCityName 
				+ "&language=zh-Hans&unit=c";
		Log.d("AutoUpdateService", "启动更新服务，更新的城市名称："+currentCityName);
		HttpUtil.sendWeatherRequestWithHttpClient(address, new HttpCallBackListener() {
			public void onFinish(String response) {
				WeatherInfo newWeatherInfo =Utility.handleWeatherResponse(response);
				if (newWeatherInfo != null) {
					List<WeatherInfo> infoList = myWeatherDB.loadWeatherInfo(newWeatherInfo.getCityName());
					if (infoList.size() == 0) {
						myWeatherDB.saveWeatherInfo(newWeatherInfo);
						Log.d("Utility", "执行添加操作");
					} else {
						myWeatherDB.updateWeatherInfo(newWeatherInfo);
						Log.d("Utility", "执行更新操作");
					}
				}
				Intent serviceIntent=new Intent();
				serviceIntent.setAction("com.myweaher.app.service.AutoUpdateService.UPDATE");
				sendBroadcast(serviceIntent);
				Log.d("AutoUpdateService", "发送广播");
			}

			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
		
	}
}
