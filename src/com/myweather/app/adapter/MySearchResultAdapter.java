package com.myweather.app.adapter;

import java.util.List;

import com.myweather.app.R;
import com.myweather.app.model.CityInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MySearchResultAdapter extends ArrayAdapter<CityInfo> {
	
	private int resourceId;

	public MySearchResultAdapter(Context context, int textViewResourceId, List<CityInfo> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		resourceId=textViewResourceId;
	}
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		CityInfo cityInfo=getItem(position);
		View view;
		
		ViewHolder viewHolder;
		
		if(convertView==null){
			view=LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder=new ViewHolder();
			viewHolder.cityNameResult=(TextView) view.findViewById(R.id.city_name_result);
			viewHolder.cityPathResult=(TextView) view.findViewById(R.id.city_path_result);
			view.setTag(viewHolder);
		}else{
			view=convertView;
			viewHolder=(ViewHolder) view.getTag();
		}
		viewHolder.cityNameResult.setText(cityInfo.getCityName());
		viewHolder.cityPathResult.setText(cityInfo.getCityPath());
		return view;
	}
	class ViewHolder{
		TextView cityNameResult;
		TextView cityPathResult;
	}
}
