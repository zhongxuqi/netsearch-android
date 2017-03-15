package com.musketeer.datasearch.activity;

import java.util.Collections;
import java.util.List;

import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.adapter.DirectionaryListAdapter;
import com.musketeer.datasearch.entity.SearchResultEntity;
import com.musketeer.datasearch.manager.SearchResultManager;
import com.musketeer.datasearch.other.SearchResultComparator;
import com.musketeer.datasearch.view.BaseDialog;
import com.musketeer.datasearch.view.HeaderLayoutBar;
import com.musketeer.datasearch.view.NumberProgressBar;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import butterknife.Bind;

public class ResultDetailActivity extends BaseActivity {
	@Bind(R.id.main_headbar)
	HeaderLayoutBar mHeadBar;
	TextView mTitle;
	ImageView mRecordStatus;
	@Bind(R.id.load_progress)
	NumberProgressBar mResultLoadProgress;
	@Bind(R.id.result_detail_view)
	WebView mResultDetail;
	
	private BaseDialog mDirListDialog;
	
	//实体信息
	private SearchResultEntity resultEntity;

	@Override
	public void setContentView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_result_detail);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mHeadBar.addView(LayoutInflater.from(this).inflate(R.layout.include_search_result_headbar, null));
		mTitle=(TextView) mHeadBar.findViewById(R.id.title);
		mRecordStatus = (ImageView) mHeadBar.findViewById(R.id.status_personal_record);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		mHeadBar.findViewById(R.id.back).setOnClickListener(this);
		mRecordStatus.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mTitle.setText(R.string.title_activity_result_detail);
		
		resultEntity=(SearchResultEntity) MainApplication.getInstance().pipeline.get(
				getIntent().getExtras().getString("entity", ""));
		mRecordStatus.setSelected(resultEntity.isRecorded());
		
		//支持javascript
		mResultDetail.getSettings().setJavaScriptEnabled(true);
		// 设置可以支持缩放 
		mResultDetail.getSettings().setSupportZoom(true); 
		// 设置出现缩放工具 
		mResultDetail.getSettings().setBuiltInZoomControls(true);
		//扩大比例的缩放
		mResultDetail.getSettings().setUseWideViewPort(true);
		//自适应屏幕
		mResultDetail.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mResultDetail.getSettings().setLoadWithOverviewMode(true);
		mResultDetail.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		mResultDetail.loadUrl(resultEntity.getLink());
		mResultDetail.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				if (newProgress==100) {
					mResultLoadProgress.setVisibility(View.GONE);
	            } else {
	            	if (mResultLoadProgress.getVisibility()==View.GONE) {
	            		mResultLoadProgress.setVisibility(View.VISIBLE);
	            	}
	                mResultLoadProgress.setProgress(newProgress);
	            }
				super.onProgressChanged(view, newProgress);
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.status_personal_record:
			SearchResultManager.getInstance().recordSearchResult(this, null, resultEntity, !resultEntity.isRecorded());
			v.setSelected(!v.isSelected());
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode==KeyEvent.KEYCODE_BACK&&mResultDetail.canGoBack()) {
			mResultDetail.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mResultDetail.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mResultDetail.onPause();
	}
}
