package com.musketeer.datasearch.activity;

import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.baselibrary.util.StringUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.adapter.WebGroupListAdapter;
import com.musketeer.datasearch.adapter.WebGroupListAdapter.OnItemDeleteListener;
import com.musketeer.datasearch.adapter.WebGroupListAdapter.OnItemEditListener;
import com.musketeer.datasearch.common.AppContant;
import com.musketeer.datasearch.common.SharePreferenceConfig;
import com.musketeer.datasearch.entity.WebEntity;
import com.musketeer.datasearch.entity.WebGroupEntity;
import com.musketeer.datasearch.entity.WebsUpdateEvent;
import com.musketeer.datasearch.util.ImageLoader;
import com.musketeer.datasearch.util.Net;
import com.musketeer.datasearch.view.BaseDialog;
import com.musketeer.datasearch.view.HeaderLayoutBar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class SettingActivity extends BaseActivity {
	@Bind(R.id.main_headbar)
	HeaderLayoutBar mHeadBar;
	TextView mTitle;
	@Bind(R.id.load_way)
	RadioGroup mLoadWay;
	@Bind(R.id.notice_type_selector)
	RadioGroup mNoticeTypeSelector;
	@Bind(R.id.web_group_list)
	ListView mWebGroupListView;
	
	private BaseDialog mDialog;

	private WebGroupListAdapter mWebGroupListAdapter;
	private List<WebEntity> tmpSelectedWebList;

	@Override
	public void setContentView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_setting);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mHeadBar.addView(LayoutInflater.from(this).inflate(R.layout.include_common_headbar, null));
		mTitle=(TextView) mHeadBar.findViewById(R.id.title);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		mHeadBar.findViewById(R.id.back).setOnClickListener(this);
		mLoadWay.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.webview_load:
					SharePreferenceUtils.putInt(MainApplication.getInstance(), SharePreferenceConfig.LOAD_WAY, AppContant.WEBVIEW_LOAD);
					break;
				case R.id.browser_load:
					SharePreferenceUtils.putInt(MainApplication.getInstance(), SharePreferenceConfig.LOAD_WAY, AppContant.BROWSER_LOAD);
					break;
				}
			}
		});
		mNoticeTypeSelector.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.notice_open:
						SharePreferenceUtils.putBoolean(MainApplication.getInstance(),
								SharePreferenceConfig.NoticeTypeStatus, true);
						break;
					case R.id.notice_close:
						SharePreferenceUtils.putBoolean(MainApplication.getInstance(),
								SharePreferenceConfig.NoticeTypeStatus, false);
						break;
				}
			}
		});
		findViewById(R.id.add_new_group).setOnClickListener(this);

		EventBus.getDefault().register(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mTitle.setText(R.string.title_activity_setting);
		
		switch (SharePreferenceUtils.getInt(this, SharePreferenceConfig.LOAD_WAY, AppContant.WEBVIEW_LOAD)) {
		case AppContant.WEBVIEW_LOAD:
			mLoadWay.check(R.id.webview_load);
			break;
		case AppContant.BROWSER_LOAD:
			mLoadWay.check(R.id.browser_load);
			break;
		}
		if (SharePreferenceUtils.getBoolean(this, SharePreferenceConfig.NoticeTypeStatus, true)) {
			mNoticeTypeSelector.check(R.id.notice_open);
		} else {
			mNoticeTypeSelector.check(R.id.notice_close);
		}
		
		mWebGroupListAdapter=new WebGroupListAdapter(this,
				MainApplication.getInstance().getWebGroupDao().getAllData());
		mWebGroupListAdapter.setOnItemEditListener(new OnItemEditListener() {

			@Override
			public void onItemEdit(WebGroupEntity item) {
				// TODO Auto-generated method stub
				editWebGroup(item);
			}
			
		});
		mWebGroupListAdapter.setOnItemDeleteListener(new OnItemDeleteListener() {

			@Override
			public void onItemDelete(WebGroupEntity item) {
				// TODO Auto-generated method stub
				final WebGroupEntity mItem=item;
				if (mDialog==null) {
					mDialog=new BaseDialog(SettingActivity.this);
				}
				mDialog.setDialogContentView(R.layout.include_dialog_common_textview);
				TextView textView=(TextView) mDialog.findViewById(R.id.edittext);
				textView.setText("是否删除组");
				mDialog.setButton1(getResources().getString(R.string.cancel), 
						new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
					}
					
				});
				mDialog.setButton2(getResources().getString(R.string.affirm), 
						new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MainApplication.getInstance().getWebGroupDao().delete(mItem);
						mWebGroupListAdapter.refreshList(MainApplication.getInstance().getWebGroupDao().getAllData());
						mDialog.dismiss();
					}
					
				});
				mDialog.show();
			}
			
		});
		mWebGroupListView.setAdapter(mWebGroupListAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.add_new_group:
			editWebGroup(new WebGroupEntity());
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Subscribe
	public void onEvent(WebsUpdateEvent event) {
		if (event.getType() == WebsUpdateEvent.UpdateType.FROM_SET) return;
		initView();
		initEvent();
		initData();
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}
	
	private void editWebGroup(WebGroupEntity entity) {
		final WebGroupEntity mEntity=entity;
		if (mDialog==null) {
			mDialog=new BaseDialog(this);
		}
		mDialog.setDialogContentView(R.layout.include_dialog_web_group_edit);
		final EditText editText=(EditText) mDialog.findViewById(R.id.edittext);
		editText.setHint("请输入组名");
		if (mEntity.getGroupName()!=null&&mEntity.getGroupName().length()>0) {
			editText.setText(mEntity.getGroupName());
		}
		LinearLayout listView=(LinearLayout) mDialog.findViewById(R.id.web_list);
		inflateWebListView(listView, entity.getWebEntitys());
		mDialog.setButton1(getResources().getString(R.string.cancel), 
				new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
			}
			
		});
		mDialog.setButton2(getResources().getString(R.string.affirm), 
				new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (editText.getText().toString().trim().length()==0) {
					showCustomDebug("组名不能为空");
					return;
				}
				mEntity.setWebs(tmpSelectedWebList);
				mEntity.setGroupName(editText.getText().toString().trim());
				MainApplication.getInstance().getWebGroupDao().insertOrUpdate(mEntity);
				mWebGroupListAdapter.refreshList(MainApplication.getInstance().getWebGroupDao().getAllData());
				if (mEntity.isSelected()) {
					EventBus.getDefault().post(new WebsUpdateEvent(WebsUpdateEvent.UpdateType.FROM_SET));
				}
				mDialog.dismiss();
			}
			
		});
		mDialog.show();
	}

	private void inflateWebListView(LinearLayout webListView, List<WebEntity> selectedList) {
		tmpSelectedWebList = new ArrayList<>();
		List<WebEntity> webList = MainApplication.getInstance().getWebDao().getAllData();
		if (webList != null) {
			for (WebEntity web: webList) {
				View itemView = LayoutInflater.from(this).inflate(R.layout.item_web_selector_list, null);
				ImageView webLogo = (ImageView) itemView.findViewById(R.id.web_logo);
				TextView webName = (TextView) itemView.findViewById(R.id.web_name);
				ImageView seletor = (ImageView) itemView.findViewById(R.id.selector);
				if (!StringUtils.isEmpty(web.getLogo())) {
					ImageLoader.loadImage(webLogo, Net.getAbsoluteImageUrl(web.getLogo()));
					webLogo.setVisibility(View.VISIBLE);
				} else {
					webLogo.setVisibility(View.GONE);
				}
				webName.setText(web.getName());
				seletor.setSelected(false);
				for (WebEntity item: selectedList) {
					if (item.getUrl().equals(web.getUrl())) {
						seletor.setSelected(true);
						tmpSelectedWebList.add(web);
						break;
					}
				}
				web.setStatusView(seletor);
				itemView.setTag(web);
				itemView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						WebEntity webItem = (WebEntity)v.getTag();
						webItem.getStatusView().setSelected(!webItem.getStatusView().isSelected());
						if (webItem.getStatusView().isSelected()) {
							tmpSelectedWebList.add(webItem);
						} else {
							for (WebEntity item: tmpSelectedWebList) {
								if (item.getName().equals(webItem.getName())) {
									tmpSelectedWebList.remove(item);
									break;
								}
							}
						}
					}
				});

				// add item view to layout
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				itemView.setLayoutParams(lp);
				webListView.addView(itemView);
			}
		}
	}

}
