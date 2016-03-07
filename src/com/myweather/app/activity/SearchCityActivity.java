package com.myweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.myweather.app.R;
import com.myweather.app.adapter.MySearchResultAdapter;
import com.myweather.app.model.CityInfo;
import com.myweather.app.util.HttpCallBackListener;
import com.myweather.app.util.HttpUtil;
import com.myweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SearchCityActivity extends Activity implements OnClickListener {

	private EditText cityNameEditText;
	private Button searchButton;
	private ListView searchResultList;
	private MySearchResultAdapter cityInfoAdapter;
	private List<CityInfo> cityInfoList;
	private ProgressDialog progressDialog;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_city);
		cityNameEditText = (EditText) findViewById(R.id.city_name_eidttext);
		searchButton = (Button) findViewById(R.id.search_button);
		searchResultList = (ListView) findViewById(R.id.search_result_listview);
		cityInfoList = new ArrayList<CityInfo>();
		cityInfoAdapter = new MySearchResultAdapter(SearchCityActivity.this, R.layout.city_info_item, cityInfoList);
		searchResultList.setAdapter(cityInfoAdapter);
		searchButton.setOnClickListener(this);
		searchResultList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String chooseCityName=cityInfoList.get(position).getCityName();
				Utility.saveUpdateCityName(SearchCityActivity.this, chooseCityName);
				finish();
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		String address = "https://api.thinkpage.cn/v3/location/search.json?key=MT5LUU4K10&q="
				+ cityNameEditText.getText().toString() + "&limit=20";
		// TODO Auto-generated method stub
		Log.d("SearchCity", cityNameEditText.getText().toString());
		showProgressDialog();
		
		HttpUtil.sendWeatherRequestWithHttpClient(address, new HttpCallBackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				
				List<CityInfo> newCityInfoList = Utility.handleCityInfo(response);
				cityInfoList.clear();
				Log.d("SearchCityActivity", "搜索结果长度" + newCityInfoList.size());
				for(int i=0;i<newCityInfoList.size();i++){
					cityInfoList.add(newCityInfoList.get(i));
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						if (cityInfoList.size() == 0) {
							Toast.makeText(SearchCityActivity.this, "未搜索到相关城市", Toast.LENGTH_SHORT).show();
						}
						cityInfoAdapter.notifyDataSetChanged();
					}
				});

			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(SearchCityActivity.this, "查询失败", Toast.LENGTH_SHORT).show();

					}
				});
			}

		});
	}

	// 显示进度对话框
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在搜索城市信息.....");
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}

	// 关闭进度对话框
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	//设置返回键返回天气信息页面
	public void onBackPress(){
		finish();
	}

}
