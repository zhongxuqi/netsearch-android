/**   
* @Title: DirectionaryListAdapter.java 
* @Package com.musketeer.datasearch.adapter 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-28 下午9:25:54 
* @version V1.0   
*/
package com.musketeer.datasearch.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.musketeer.baselibrary.adapter.BaseListAdapter;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.entity.SearchResultEntity;

public class DirectionaryListAdapter extends BaseListAdapter<SearchResultEntity> {
	
	public DirectionaryListAdapter(Context context, List<SearchResultEntity> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder mHolder;
		if (convertView==null) {
			mHolder=new Holder();
			convertView=LayoutInflater.from(mContext).inflate(R.layout.item_directionary_list, null);
			mHolder.Icon=(ImageView) convertView.findViewById(R.id.item_icon);
			mHolder.title=(TextView) convertView.findViewById(R.id.item_title);
			convertView.setTag(mHolder);
		} else {
			mHolder=(Holder) convertView.getTag();
		}
		
		if (getItem(position).isFolder()) {
			mHolder.Icon.setImageResource(R.drawable.ic_directionary);
		} else {
			mHolder.Icon.setImageResource(R.drawable.ic_link);
		}
		mHolder.title.setText(mDataList.get(position).getTitle());
		
		return convertView;
	}
	
	class Holder {
		ImageView Icon;
		TextView title;
	}

}
