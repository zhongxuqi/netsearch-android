/**   
* @Title: WebGroupListAdapter.java 
* @Package com.musketeer.datasearch.adapter 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-4-11 下午2:03:06 
* @version V1.0   
*/
package com.musketeer.datasearch.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.musketeer.baselibrary.adapter.BaseListAdapter;
import com.musketeer.baselibrary.util.ScreenUtils;
import com.musketeer.baselibrary.util.StringUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.entity.WebEntity;
import com.musketeer.datasearch.entity.WebGroupEntity;
import com.musketeer.datasearch.entity.WebsUpdateEvent;
import com.musketeer.datasearch.util.ImageLoader;
import com.musketeer.datasearch.util.Net;

import org.greenrobot.eventbus.EventBus;

public class WebGroupListAdapter extends BaseListAdapter<WebGroupEntity> {
	private WebGroupEntity mSelectedItem;
	
	private OnItemDeleteListener mOnItemDeleteListener;
	private OnItemEditListener mOnItemEditListener;

	public WebGroupListAdapter(Context context, List<WebGroupEntity> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
		if (list!=null) {
			for (WebGroupEntity item:list) {
				if (item.isSelected()) {
					mSelectedItem=item;
					break;
				}
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder mHolder;
		if (convertView==null) {
			mHolder=new Holder();
			convertView=LayoutInflater.from(mContext).inflate(R.layout.item_web_group, null);
			mHolder.groupName=(TextView) convertView.findViewById(R.id.group_name);
			mHolder.delete=(ImageView) convertView.findViewById(R.id.delete);
			mHolder.edit=(ImageView) convertView.findViewById(R.id.edit);
			mHolder.selector=(ImageView) convertView.findViewById(R.id.selector);
			mHolder.itemList=(LinearLayout) convertView.findViewById(R.id.item_list);
			convertView.setTag(mHolder);
		} else {
			mHolder=(Holder) convertView.getTag();
		}
		
		mHolder.groupName.setText(getItem(position).getGroupName());
		mHolder.itemList.removeAllViews();
		List<WebEntity> webList=getItem(position).getWebEntitys();
		for (WebEntity item:webList) {
			View view=LayoutInflater.from(mContext).inflate(R.layout.include_web_item, null);
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins((int) ScreenUtils.dpToPx(mContext, 10), 0, (int)ScreenUtils.dpToPx(mContext, 10), 0);
			view.setLayoutParams(params);
			ImageView mLogo=(ImageView) view.findViewById(R.id.logo);
			if (!StringUtils.isEmpty(item.getLogo())) {
				ImageLoader.loadImage(mLogo, Net.getAbsoluteImageUrl(item.getLogo()));
				mLogo.setVisibility(View.VISIBLE);
			} else {
				mLogo.setVisibility(View.GONE);
			}
			TextView mWebName=(TextView) view.findViewById(R.id.web_name);
			mWebName.setText(item.getName());
			mHolder.itemList.addView(view);
		}
		mHolder.delete.setTag(getItem(position));
		mHolder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mOnItemDeleteListener.onItemDelete((WebGroupEntity) v.getTag());
			}
			
		});
		mHolder.edit.setTag(getItem(position));
		mHolder.edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mOnItemEditListener.onItemEdit((WebGroupEntity) v.getTag());
			}
			
		});
		if (getItem(position)==mSelectedItem) {
			mHolder.selector.setSelected(true);
			mSelectedItem.mSelectedView=mHolder.selector;
		}
		mHolder.selector.setTag(getItem(position));
		mHolder.selector.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.isSelected()) {
					return;
				}
				v.setSelected(true);
				WebGroupEntity entity=(WebGroupEntity) v.getTag();
				entity.setSelected(true);
				MainApplication.getInstance().getWebGroupDao().update(entity);
				if (mSelectedItem!=null) {
					mSelectedItem.setSelected(false);
					if (mSelectedItem.mSelectedView!=null) {
						mSelectedItem.mSelectedView.setSelected(false);
					}
					MainApplication.getInstance().getWebGroupDao().update(mSelectedItem);
				}
				mSelectedItem=entity;
				mSelectedItem.mSelectedView=v;
				EventBus.getDefault().post(new WebsUpdateEvent(WebsUpdateEvent.UpdateType.FROM_SET));
			}
			
		});
		return convertView;
	}
	
	class Holder {
		TextView groupName;
		ImageView delete;
		ImageView edit;
		ImageView selector;
		LinearLayout itemList;
	}
	
	public void setOnItemDeleteListener(OnItemDeleteListener listener) {
		this.mOnItemDeleteListener = listener;
	}

	public void setOnItemEditListener(OnItemEditListener listener) {
		this.mOnItemEditListener = listener;
	}

	public interface OnItemDeleteListener {
		public void onItemDelete(WebGroupEntity item);
	}
	
	public interface OnItemEditListener {
		public void onItemEdit(WebGroupEntity item);
	}

}
