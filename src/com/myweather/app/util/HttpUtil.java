package com.myweather.app.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {
	
	// 根据传入的地址查询天气数据
	public static void sendWeatherRequestWithHttpClient(final String address,final HttpCallBackListener listener) {
		new Thread(new Runnable() {
			public void run() {
				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(address);
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						Log.d("HttpUtil", "网络请求成功");
						// Log.d("WeatherActivity", response);
						if(listener!=null){
							listener.onFinish(response.toString());
						}
					}else{
						if(listener!=null){
							
							listener.onError(null);
						}
						Log.d("HttpUtil","网络请求失败,请重新选择");
					}
				} catch (Exception e) {
					if(listener!=null){
						listener.onError(e);
					}
				}
			}
		}).start();
	}
	
}
