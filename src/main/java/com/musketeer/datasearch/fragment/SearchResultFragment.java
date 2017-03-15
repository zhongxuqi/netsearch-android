/**   
* @Title: SearchResultFragment.java 
* @Package com.musketeer.datasearch.fragment 
*
* @author musketeer zhongxuqi@163.com  
* @date 2015-3-15 下午3:11:26 
* @version V1.0   
*/
package com.musketeer.datasearch.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.musketeer.baselibrary.Activity.BaseSupportFragment;
import com.musketeer.baselibrary.bean.ParamsEntity;
import com.musketeer.baselibrary.bean.RequestResult;
import com.musketeer.baselibrary.bean.RequestTask;
import com.musketeer.baselibrary.net.UIUpdateTask;
import com.musketeer.baselibrary.paser.IJsonPaser;
import com.musketeer.baselibrary.paser.JsonPaser;
import com.musketeer.baselibrary.util.JSONUtils;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.baselibrary.util.TimeUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.activity.ResultDetailActivity;
import com.musketeer.datasearch.adapter.DirectionaryListAdapter;
import com.musketeer.datasearch.adapter.ResultListAdapter;
import com.musketeer.datasearch.adapter.ResultListAdapter.OnRecordListener;
import com.musketeer.datasearch.common.AppContant;
import com.musketeer.datasearch.common.SharePreferenceConfig;
import com.musketeer.datasearch.entity.SearchFinishEvent;
import com.musketeer.datasearch.entity.SearchResultAndPageEntity;
import com.musketeer.datasearch.entity.SearchResultEntity;
import com.musketeer.datasearch.entity.WebEntity;
import com.musketeer.datasearch.manager.SearchResultManager;
import com.musketeer.datasearch.other.SearchResultComparator;
import com.musketeer.datasearch.request.RequestURLs;
import com.musketeer.datasearch.service.MainService;
import com.musketeer.datasearch.util.Net;
import com.musketeer.datasearch.view.BaseDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;

/**
 * @author zhongxuqi
 */
public class SearchResultFragment extends BaseSupportFragment implements OnItemClickListener {
	public WebEntity mWeb;

	@Bind(R.id.result_list)
	PullToRefreshListView mResultView;
	@Bind(R.id.hint_text)
	TextView mHintTextView;

	private ResultListAdapter mAdapter;
	private List<SearchResultEntity> mDataList;
	private String NextPage;
	private String hintText = null;

	@Override
	public void setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		BaseView=inflater.inflate(R.layout.fragment_search_result, null);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mResultView.setEmptyView(mHintTextView);
		mHintTextView.setText(R.string.input_keyword_hint);
		if (hintText != null) mHintTextView.setText(hintText);
	}

	@Override
	public void initEvent() {
		// TODO Auto-generated method stub
		mResultView.setOnItemClickListener(this);
		mResultView.setMode(Mode.PULL_FROM_END);
		mResultView.getLoadingLayoutProxy(false, true).setPullLabel("上拉刷新");
		mResultView.getLoadingLayoutProxy(false, true).setRefreshingLabel("加载中...");
		mResultView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开刷新");
		mResultView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(TimeUtils.getDateENNotSecond(System.currentTimeMillis()));
				
				if (NextPage==null||NextPage.length()==0) {
					refreshView.onRefreshComplete();
					showCustomToast("已无更多数据");
					return;
				}
				CustomSearch(NextPage,mWeb.getParserName());
			}
		});
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		mAdapter=new ResultListAdapter(getActivity(),null);
		mResultView.setAdapter(mAdapter);
		mAdapter.setOnRecordListener(new OnRecordListener() {
			@Override
			public void onRecordClick(View v, SearchResultEntity entity, boolean isRecord) {
				// TODO Auto-generated method stub
				SearchResultManager.getInstance().recordSearchResult(getActivity(), v, entity, isRecord);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mAdapter.refreshList(mDataList);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		switch (SharePreferenceUtils.getInt(getActivity(), SharePreferenceConfig.LOAD_WAY, AppContant.WEBVIEW_LOAD)) {
		case AppContant.WEBVIEW_LOAD:
			Bundle bundle=new Bundle();
			bundle.putString("entity", "search_result");
			MainApplication.getInstance().pipeline.put("search_result", mAdapter.getItem(position-1));
			startActivity(ResultDetailActivity.class, bundle);
			break;
		case AppContant.BROWSER_LOAD:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(mAdapter.getItem(position-1).getLink()));
			startActivity(intent);
			break;
		}
	}
	
	public void search(String keyword) throws UnsupportedEncodingException{
		// TODO Auto-generated method stub
		if (AppContant.BanKeyWords.contains(keyword)) {
			setHintText(MainApplication.getInstance().getString(R.string.ban_word_hint));
			return;
		}
		RequestTask<SearchResultAndPageEntity> task = new RequestTask<>(RequestTask.RequestType.GET, false,
			new JsonPaser<SearchResultAndPageEntity>() {
				@Override
				public SearchResultAndPageEntity BuildModel(JSONObject jsonObject) throws JSONException {
					LogUtils.d(TAG, jsonObject.toString());
					return JSON.parseObject(jsonObject.toString(), SearchResultAndPageEntity.class);
				}
			}, new UIUpdateTask<SearchResultAndPageEntity>() {
				@Override
				public void doUIUpdate(RequestResult<SearchResultAndPageEntity> result) {
					if (!result.isOk) {
						if (isAdded()) {
							showCustomToast(getResources().getString(R.string.net_status_error));
							setHintText(MainApplication.getInstance().getString(R.string.net_status_error));
						}
						return;
					}
					if (result.resultObj == null || result.resultObj.getResultData() == null ||
							result.resultObj.getResultData().size() == 0) {
						setHintText(MainApplication.getInstance().getString(R.string.search_list_empty));
						return;
					}
					SearchResultAndPageEntity resultEntity = result.resultObj;
					mDataList=resultEntity.getResultData();
					if (mDataList!=null) {
						if (mAdapter!=null) {
							mAdapter.refreshList(SearchResultManager.getInstance().UpdateListInfo(mDataList));
							EventBus.getDefault().post(new SearchFinishEvent(mWeb));
						}
					}
					NextPage=resultEntity.getNextPage();
				}
		});
		task.setUrl(Net.getAbsoluteUrl(mWeb.getUrl()));
		ParamsEntity params=new ParamsEntity();
		params.put("keyword", URLEncoder.encode(keyword, "UTF-8"));
		task.setParams(params);
		MainService.addTask(task);
	}
	
	private void CustomSearch(String url,String parser_name) {
		// TODO Auto-generated method stub
		//RequestTask task=new RequestTask(this, CUSTOM_SEARCH, RequestTask.RequestType.GET, new WebSearchBuilder(), false);
		RequestTask<SearchResultAndPageEntity> task = new RequestTask<>(RequestTask.RequestType.GET, false,
			new JsonPaser<SearchResultAndPageEntity>() {
				@Override
				public SearchResultAndPageEntity BuildModel(JSONObject jsonObject) throws JSONException {
					LogUtils.d(TAG, jsonObject.toString());
					return JSON.parseObject(jsonObject.toString(), SearchResultAndPageEntity.class);
				}
			}, new UIUpdateTask<SearchResultAndPageEntity>() {
				@Override
				public void doUIUpdate(RequestResult<SearchResultAndPageEntity> result) {
					if (!result.isOk) {
						if (isAdded()) {
							showCustomToast(getResources().getString(R.string.net_status_error));
							EventBus.getDefault().post(mWeb);
						}
						return;
					}

					mResultView.onRefreshComplete();
					SearchResultAndPageEntity resultEntity = result.resultObj;
					mDataList.addAll(resultEntity.getResultData());
					if (mDataList!=null) {
						if (mAdapter!=null) {
							mAdapter.refreshList(mDataList);
							EventBus.getDefault().post(new SearchFinishEvent(mWeb));
						}
					}
					NextPage=resultEntity.getNextPage();
				}
		});
		task.setUrl(RequestURLs.CUSTOM_SEARCH);
		ParamsEntity params=new ParamsEntity();
		params.put("url", url);
		params.put("parser_name", parser_name);
		task.setParams(params);
		MainService.addTask(task);
	}

	protected void setHintText(String text) {
		if (mHintTextView != null) {
			mHintTextView.setText(text);
		} else {
			hintText = text;
		}
	}

}
