package com.myweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyWeatherOpenHelper extends SQLiteOpenHelper {
	// 建表语句
	public static final String CITY_INFO = "create table CityInfo(" + "id integer primary key autoincrement,"
			+ "city_name text," + "city_path text)";
	// 建表语句
	public static final String WEATHER_INFO = "create table WeatherInfo (" + "id integer primary key autoincrement,"
			+ "city_id text," + "city_name text," 
			+ "today text," + "text_today text," + "text_today_code integer,"
			+ "text_tonight text," + "text_tonight_code integer," 
			+ "temperature_today_high text,"+ "temperature_today_low text," 
			+ "tomorrow text," + "text_tomorrow text," + "text_tomorrow_code integer,"
			+ "text_tomorrow_evening text," + "text_tomorrow_evening_code integer," 
			+ "temperature_tomorrow_high text,"+ "temperature_tomorrow_low text," 
			+ "time_update text," + "day_update text)";

	// 重写构造方法
	public MyWeatherOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 建立表格
		db.execSQL(CITY_INFO);
		db.execSQL(WEATHER_INFO);

	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
