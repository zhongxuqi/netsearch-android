/**   
* @Title: ResultListAdapter.java 
* @Package com.musketeer.datasearch.adapter 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-15 下午8:08:56 
* @version V1.0   
*/
package com.musketeer.datasearch.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.musketeer.baselibrary.adapter.BaseListAdapter;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.entity.SearchResultEntity;

public class ResultListAdapter extends BaseListAdapter<SearchResultEntity> {
	private OnRecordListener mOnRecordListener;

	public ResultListAdapter(Context context, List<SearchResultEntity> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder mHolder;
		if (convertView==null) {
			mHolder=new Holder();
			convertView=LayoutInflater.from(mContext).inflate(R.layout.item_search_result_list, null);
			mHolder.title=(TextView) convertView.findViewById(R.id.title);
			mHolder.mRecordStatus=(ImageView) convertView.findViewById(R.id.status_personal_record);
			mHolder.summary=(TextView) convertView.findViewById(R.id.summary);
			convertView.setTag(mHolder);
		} else {
			mHolder=(Holder) convertView.getTag();
		}
		
		mHolder.title.setText(mDataList.get(position).getTitle());
		mHolder.summary.setText(mDataList.get(position).getAbstract());
		mDataList.get(position).setStatusView(mHolder.mRecordStatus);
		mHolder.mRecordStatus.setSelected(getItem(position).isRecorded());
		mHolder.mRecordStatus.setTag(getItem(position));
		mHolder.mRecordStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mOnRecordListener!=null) {
					mOnRecordListener.onRecordClick(v, (SearchResultEntity)v.getTag(), !v.isSelected());
				}
			}
		});
		if (getItem(position).isRecorded()) {
			mHolder.mRecordStatus.setSelected(true);
		} else {
			mHolder.mRecordStatus.setSelected(false);
		}
		
		return convertView;
	}
	
	class Holder {
		TextView title;
		ImageView mRecordStatus;
		TextView summary;
	}
	
	public interface OnRecordListener {
		void onRecordClick(View v, SearchResultEntity entity,boolean isRecord);
	}
	
	public void setOnRecordListener(OnRecordListener l) {
		mOnRecordListener=l;
	}

}
