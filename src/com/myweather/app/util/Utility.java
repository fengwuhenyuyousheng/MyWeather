package com.myweather.app.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.myweather.app.R;
import com.myweather.app.model.CityInfo;
import com.myweather.app.model.WeatherInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Utility {

	public static int UICurrentCode;


	// �����ʹ�����������صĳ�����Ϣ������list<CityInfo>
	public static List<CityInfo> handleCityInfo(String responseCity) {
		List<CityInfo> list = new ArrayList<CityInfo>();
		try {
			JSONObject jsonObject = new JSONObject(responseCity);
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			for (int i = 0; i < jsonArray.length(); i++) {
				CityInfo cityInfo = new CityInfo();
				JSONObject jsonObjectInfo = jsonArray.getJSONObject(i);
				String cityName = jsonObjectInfo.getString("name");
				String cityPath = jsonObjectInfo.getString("path");
				// Log.d("Utility", "�������ƣ�"+cityName+" ,�㼶��ϵ��"+cityPath);
				cityInfo.setCityName(cityName);
				cityInfo.setCityPath(cityPath);
				list.add(cityInfo);
			}
			Log.d("Utility", "�����������ݳɹ�");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("Utility", "������������ʧ��");
			e.printStackTrace();
		}
		return list;
	}

	// �����ʹ�����������ص�JSON��������,����WeatherInfoʵ��
	public static WeatherInfo handleWeatherResponse(String responseWeather) {
		WeatherInfo weatherInfo = new WeatherInfo();
		try {
			JSONObject jsonObject = new JSONObject(responseWeather);
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			JSONObject jsonOne = jsonArray.getJSONObject(0);
			JSONObject jsonLocation = jsonOne.getJSONObject("location");
			String cityId = jsonLocation.getString("id");
			String cityName = jsonLocation.getString("name");
			JSONArray jsonDaily = jsonOne.getJSONArray("daily");
			// ���������������Ϣ
			JSONObject jsonDailyToday = jsonDaily.getJSONObject(0);
			String today = jsonDailyToday.getString("date");
			String textToday = jsonDailyToday.getString("text_day");
			int textTodayCode = jsonDailyToday.getInt("code_day");
			String textTonight = jsonDailyToday.getString("text_night");
			int textTonightCode = jsonDailyToday.getInt("code_night");
			String temperatureTodayHigh = jsonDailyToday.getString("high");
			String temperatureTodayLow = jsonDailyToday.getString("low");
			// ���������������Ϣ
			JSONObject jsonDailyTomorrow = jsonDaily.getJSONObject(1);
			String tomorrow = jsonDailyTomorrow.getString("date");
			String textTomorrow = jsonDailyTomorrow.getString("text_day");
			int textTomorrowCode = jsonDailyTomorrow.getInt("code_day");
			String textTomorrowEvening = jsonDailyTomorrow.getString("text_night");
			int textTomorrowEveningCode = jsonDailyTomorrow.getInt("code_night");
			String temperatureTomorrowHigh = jsonDailyTomorrow.getString("high");
			String temperatureTomorrowLow = jsonDailyTomorrow.getString("low");

			String[] weatherTime = jsonOne.getString("last_update").split("\\+");
			String dayUpdate = null, timeUpdate = null;
			if (weatherTime != null && weatherTime.length > 0) {
				dayUpdate = weatherTime[0];
				timeUpdate = weatherTime[1];
			}

			Log.d("Utility", "�����������ݳɹ�");
			// ���������������ݱ��浽weatherInfo��
			weatherInfo.setCityName(cityName);
			weatherInfo.setCityId(cityId);

			weatherInfo.setToday(today);
			weatherInfo.setTextToday(textToday);
			weatherInfo.setTextTodayCode(textTodayCode);
			weatherInfo.setTextTonight(textTonight);
			weatherInfo.setTextTonightCode(textTonightCode);
			weatherInfo.setTemperatureTodayHigh(temperatureTodayHigh);
			weatherInfo.setTemperatureTodayLow(temperatureTodayLow);

			weatherInfo.setTomorrow(tomorrow);
			weatherInfo.setTextTomorrow(textTomorrow);
			weatherInfo.setTextTomorrowCode(textTomorrowCode);
			weatherInfo.setTextTomorrowEvening(textTomorrowEvening);
			weatherInfo.setTextTomorrowEveningCode(textTomorrowEveningCode);
			weatherInfo.setTemperatureTomorrowHigh(temperatureTomorrowHigh);
			weatherInfo.setTemperatureTomorrowLow(temperatureTomorrowLow);

			weatherInfo.setDayUpdate(dayUpdate);
			weatherInfo.setTimeUpdate(timeUpdate);

		} catch (JSONException e) {
			Log.d("Utility", "������������ʧ��");
			e.printStackTrace();

		}
		return weatherInfo;
	}

	// ���ݴ����������Ϣ����View
	public static View createViewFromWeatherInfo(WeatherInfo weatherinfo, Context context) {

		View view = View.inflate(context, R.layout.weather_info, null);
		TextView cityName = (TextView) view.findViewById(R.id.city_name);
		cityName.setText(weatherinfo.getCityName());

		TextView timeUpdate = (TextView) view.findViewById(R.id.time_update);
		timeUpdate.setText("����" + weatherinfo.getTimeUpdate() + "����");

		TextView today = (TextView) view.findViewById(R.id.today);
		today.setText(weatherinfo.getToday());
		TextView textToday = (TextView) view.findViewById(R.id.text_today);
		textToday.setText("����������" + weatherinfo.getTextToday());
		TextView textTonight = (TextView) view.findViewById(R.id.text_tonight);
		textTonight.setText("ҹ��������" + weatherinfo.getTextTonight());
		TextView temperatureTodayHigh = (TextView) view.findViewById(R.id.tempertuer_today_high);
		temperatureTodayHigh.setText("����¶ȣ�" + weatherinfo.getTemperatureTodayHigh());
		TextView temperatureTodayLow = (TextView) view.findViewById(R.id.tempertuer_today_low);
		temperatureTodayLow.setText("����¶ȣ�" + weatherinfo.getTemperatureTodayLow());

		TextView tomorrow = (TextView) view.findViewById(R.id.tomorrow);
		tomorrow.setText(weatherinfo.getTomorrow());
		TextView textTomorrow = (TextView) view.findViewById(R.id.text_tomorrow);
		textTomorrow.setText("����������" + weatherinfo.getTextTomorrow());
		TextView textTomorrowEvening = (TextView) view.findViewById(R.id.text_tomorrow_evening);
		textTomorrowEvening.setText("ҹ��������" + weatherinfo.getTextTomorrowEvening());
		TextView temperatureTomorrowHigh = (TextView) view.findViewById(R.id.tempertuer_tomorrow_high);
		temperatureTomorrowHigh.setText("����¶ȣ�" + weatherinfo.getTemperatureTomorrowHigh());
		TextView temperatureTomorrowLow = (TextView) view.findViewById(R.id.tempertuer_tomorrow_low);
		temperatureTomorrowLow.setText("����¶ȣ�" + weatherinfo.getTemperatureTomorrowLow());

		return view;
	}

	// ����Ҫ���µĳ������������ļ�
	public static void saveUpdateCityName(Context context, String chooseCityName) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString("chooseCityName", chooseCityName);
		editor.commit();
		Log.d("Utility", "����Ҫ�����ĳ�����:" + chooseCityName);
	}

	// ��ȡҪ�����ĳ�����
	public static String readUpdateCityName(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		String chooseCityName = sharedPreferences.getString("chooseCityName", "");
		if(chooseCityName.equals("")){
			chooseCityName=null;
		}else{
			Log.d("Utility", "��ȡҪ�����ĳ�����:" + chooseCityName);
		}
		return chooseCityName;
	}

}
