package com.musketeer.datasearch.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.zxing.client.android.CaptureActivity;
import com.musketeer.baselibrary.Activity.BaseFragment;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.activity.MainActivity;
import com.musketeer.datasearch.adapter.DirectionaryListAdapter;
import com.musketeer.datasearch.common.AppContant;
import com.musketeer.datasearch.common.SharePreferenceConfig;
import com.musketeer.datasearch.entity.SearchResultEntity;
import com.musketeer.datasearch.other.SearchResultComparator;
import com.musketeer.datasearch.view.BaseDialog;
import com.musketeer.datasearch.view.HeaderLayoutBar;
import com.musketeer.datasearch.view.NumberProgressBar;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;

public class ScannerFragment extends BaseFragment {
	public static final String TAG = "ScannerFragment";

	@Bind(R.id.main_headbar)
	HeaderLayoutBar mHeadBar;
	ImageView mRecordStatus;
	@Bind(R.id.load_progress)
	NumberProgressBar mResultLoadProgress;
	@Bind(R.id.scanner_result_view)
	public WebView mURIResult;

	private BaseDialog mDirListDialog;

	@Override
	public void setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		BaseView=inflater.inflate(R.layout.fragment_scanner, null);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mHeadBar.addView(LayoutInflater.from(getActivity()).inflate(R.layout.include_scanner_headbar, null));
		mRecordStatus = (ImageView) mHeadBar.findViewById(R.id.status_personal_record);
		mResultLoadProgress.setVisibility(View.GONE);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		mHeadBar.findViewById(R.id.left_image_button).setOnClickListener(this);
		mHeadBar.findViewById(R.id.scanner_button).setOnClickListener(this);
		mRecordStatus.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		//支持javascript
		mURIResult.getSettings().setJavaScriptEnabled(true);
		// 设置可以支持缩放 
		mURIResult.getSettings().setSupportZoom(true); 
		// 设置出现缩放工具 
		mURIResult.getSettings().setBuiltInZoomControls(true);
		//扩大比例的缩放
		mURIResult.getSettings().setUseWideViewPort(true);
		//自适应屏幕
		mURIResult.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mURIResult.getSettings().setLoadWithOverviewMode(true);
		mURIResult.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		mURIResult.setWebChromeClient(new WebChromeClient() {

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
		mURIResult.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// TODO Auto-generated method stub
				Uri uri=Uri.parse(url);
				Intent intent=new Intent(Intent.ACTION_VIEW,uri);
				startActivity(intent);
			}
			
		});
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
			case R.id.left_image_button:
				MainActivity.getInstance().mDrawerLayout.openDrawer(Gravity.LEFT);
				break;
			case R.id.scanner_button:
				startActivityForResult(CaptureActivity.class, MainActivity.SCANNER_CODE);
				break;
			case R.id.status_personal_record:
				if (mURIResult.getUrl()==null||mURIResult.getUrl().length()==0) {
					showCustomToast("内容不能为空");
					return;
				}
				if (mDirListDialog==null) {
					mDirListDialog=new BaseDialog(getActivity());
				}
				mDirListDialog.setDialogContentView(R.layout.include_dialog_scanner_info);
				ListView listView=(ListView) mDirListDialog.findViewById(R.id.dir_list);
				final EditText mTitleText=(EditText) mDirListDialog.findViewById(R.id.url_title);

				//加载数据
				List<SearchResultEntity> list=MainApplication.getInstance()
						.getSearchResultDao().getAllFolderData();
				if (list!=null) {
					Collections.sort(list, new SearchResultComparator());
				}
				final DirectionaryListAdapter adapter=new DirectionaryListAdapter(getActivity(), list);

				//添加根目录
				SearchResultEntity rootDir=new SearchResultEntity();
				rootDir.setTitle("根目录");
				rootDir.setId(0);
				rootDir.setFolder(true);
				adapter.addItemByIndex(rootDir, 0);

				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
						// TODO Auto-generated method stub
						if (mTitleText.getText().toString().length()==0) {
							showCustomToast("标题不能为空");
							return;
						}

						SearchResultEntity resultEntity=new SearchResultEntity();
						resultEntity.setTitle(mTitleText.getText().toString());
						resultEntity.setLink(mURIResult.getUrl());
						resultEntity.setRecorded(true);
						resultEntity.setParentId(adapter.getItem(position).getId());
						MainApplication.getInstance().getSearchResultDao().insert(resultEntity);
						mDirListDialog.dismiss();
					}

				});
				mDirListDialog.show();
				break;
		}
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case MainActivity.SCANNER_CODE:
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				String scanResult = bundle.getString("result");
				switch (SharePreferenceUtils.getInt(getActivity(), SharePreferenceConfig.LOAD_WAY, AppContant.WEBVIEW_LOAD)) {
				case AppContant.WEBVIEW_LOAD:
					mURIResult.loadUrl(scanResult);
					break;
				case AppContant.BROWSER_LOAD:
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(scanResult));
					startActivity(intent);
					break;
				}
			}
			break;
		}
	}

}
