package com.myweather.app.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {
	
	// ���ݴ���ĵ�ַ��ѯ��������
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
						Log.d("HttpUtil", "��������ɹ�");
						// Log.d("WeatherActivity", response);
						if(listener!=null){
							listener.onFinish(response.toString());
						}
					}else{
						if(listener!=null){
							
							listener.onError(null);
						}
						Log.d("HttpUtil","��������ʧ��,������ѡ��");
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
