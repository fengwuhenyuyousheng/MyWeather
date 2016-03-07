package com.myweather.app.db;

import java.util.ArrayList;
import java.util.List;


import com.myweather.app.model.WeatherInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyWeatherDB {
	// �������ݿ�����
	public static final String DB_NAME = "my_weather";
	// �������ݿ�汾
	public static final int VERSION = 1;
	// ����˽�л�����
	private static MyWeatherDB myWeatherDB;
	private SQLiteDatabase db;

	// �����췽��˽�л�
	private MyWeatherDB(Context context) {
		MyWeatherOpenHelper dbHelper = new MyWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	// ��ȡMyWeatherDB��ʵ��(synchronized �߳���)
	public synchronized static MyWeatherDB getInstance(Context context) {
		if (myWeatherDB == null) {
			myWeatherDB = new MyWeatherDB(context);
		}
		return myWeatherDB;
	}

	// ����weatherInfoʵ������ContentValuesʵ��
	public ContentValues createContentValuesFromWeather(WeatherInfo weatherInfo) {
		ContentValues values = new ContentValues();
		values.put("city_id", weatherInfo.getCityId());
		values.put("city_name", weatherInfo.getCityName());

		values.put("today", weatherInfo.getToday());
		values.put("text_today", weatherInfo.getTextToday());
		values.put("text_today_code", weatherInfo.getTextTodayCode());
		values.put("text_tonight", weatherInfo.getTextTonight());
		values.put("text_tonight_code", weatherInfo.getTextTonightCode());
		values.put("temperature_today_high", weatherInfo.getTemperatureTodayHigh());
		values.put("temperature_today_low", weatherInfo.getTemperatureTodayLow());

		values.put("tomorrow", weatherInfo.getTomorrow());
		values.put("text_tomorrow", weatherInfo.getTextTomorrow());
		values.put("text_tomorrow_code", weatherInfo.getTextTomorrowCode());
		values.put("text_tomorrow_evening", weatherInfo.getTextTomorrowEvening());
		values.put("text_tomorrow_evening_code", weatherInfo.getTextTomorrowEveningCode());
		values.put("temperature_tomorrow_high", weatherInfo.getTemperatureTomorrowHigh());
		values.put("temperature_tomorrow_low", weatherInfo.getTemperatureTomorrowLow());

		values.put("time_update", weatherInfo.getTimeUpdate());
		values.put("day_update", weatherInfo.getDayUpdate());
		return values;
	}

	// ��weatherInfoʵ���洢�����ݿ���
	public void saveWeatherInfo(WeatherInfo weatherInfo) {
		if (weatherInfo != null) {
			db.insert("WeatherInfo", null, createContentValuesFromWeather(weatherInfo));
		}
	}

	// ��weatherInfoʵ�����µ����ݿ���
	public void updateWeatherInfo(WeatherInfo weatherInfo) {
		if (weatherInfo != null) {

			db.update("WeatherInfo", createContentValuesFromWeather(weatherInfo), "city_name=?",
					new String[] { weatherInfo.getCityName() });
		}
	}

	// ���ݳ�����ɾ�����ݿ��е���Ϣ
	public Boolean deleteWeatherInfo(String cityName) {
		int result=db.delete("WeatherInfo", "city_name=?", new String[] { cityName });
		if(result!=0){
			return true;
		}else{
			return false;
		}
	}

	// �����ݿ���ɾ��������Ϣ
	public Boolean deleteWeatherInfo() {
		int result=db.delete("WeatherInfo", null, null);
		if(result!=0){
			return true;
		}else{
			return false;
		}
	}

	// �����ݿ��ж�����Ӧ���е�������Ϣ
	public List<WeatherInfo> loadWeatherInfo(String cityName) {
		List<WeatherInfo> weatherList = new ArrayList<WeatherInfo>();
		Cursor cursor;
		if (cityName == null) {
			cursor = db.query("WeatherInfo", null, null, null, null, null, null);
		} else {
			cursor = db.query("WeatherInfo", null, "city_name=?", new String[] { cityName }, null, null, null);
		}

		if (cursor.moveToFirst()) {
			do {
				WeatherInfo weatherInfo = new WeatherInfo();
				weatherInfo.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
				weatherInfo.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));

				weatherInfo.setToday(cursor.getString(cursor.getColumnIndex("today")));
				weatherInfo.setTextToday(cursor.getString(cursor.getColumnIndex("text_today")));
				weatherInfo.setTextTonight(cursor.getString(cursor.getColumnIndex("text_tonight")));
				weatherInfo.setTemperatureTodayHigh(cursor.getString(cursor.getColumnIndex("temperature_today_high")));
				weatherInfo.setTemperatureTodayLow(cursor.getString(cursor.getColumnIndex("temperature_today_low")));

				weatherInfo.setTomorrow(cursor.getString(cursor.getColumnIndex("tomorrow")));
				weatherInfo.setTextTomorrow(cursor.getString(cursor.getColumnIndex("text_tomorrow")));
				weatherInfo.setTextTomorrowEvening(cursor.getString(cursor.getColumnIndex("text_tomorrow_evening")));
				weatherInfo.setTemperatureTomorrowHigh(
						cursor.getString(cursor.getColumnIndex("temperature_tomorrow_high")));
				weatherInfo
						.setTemperatureTomorrowLow(cursor.getString(cursor.getColumnIndex("temperature_tomorrow_low")));

				weatherInfo.setTimeUpdate(cursor.getString(cursor.getColumnIndex("time_update")));
				weatherInfo.setDayUpdate(cursor.getString(cursor.getColumnIndex("day_update")));

				weatherList.add(weatherInfo);

			} while (cursor.moveToNext());
			if (cursor != null) {
				cursor.close();
			}
		}
		return weatherList;
	}
}
