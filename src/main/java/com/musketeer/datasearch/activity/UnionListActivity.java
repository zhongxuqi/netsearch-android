package com.musketeer.datasearch.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.musketeer.baselibrary.Activity.BaseActivity;
import com.musketeer.baselibrary.adapter.BaseRecyclerAdapter;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.adapter.UnionListAdapter;
import com.musketeer.datasearch.common.AppContant;
import com.musketeer.datasearch.common.SharePreferenceConfig;
import com.musketeer.datasearch.entity.ListUpdateEvent;
import com.musketeer.datasearch.entity.SearchResultEntity;
import com.musketeer.datasearch.entity.UnionResultResp;
import com.musketeer.datasearch.manager.SearchResultManager;
import com.musketeer.datasearch.view.HeaderLayoutBar;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;

public class UnionListActivity extends BaseActivity {
    public static final String TAG = "UnionListActivity";

    @Bind(R.id.main_headbar)
    HeaderLayoutBar mHeadBar;
    TextView mTitle;
    @Bind(R.id.result_list)
    RecyclerView mListView;

    protected UnionListAdapter mAdapter;
    protected UnionResultResp info;
    protected List<SearchResultEntity> mDataList;

    @Override
    public void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_union_list);
    }

    @Override
    public void initView() {
        mHeadBar.addView(LayoutInflater.from(this).inflate(R.layout.include_common_headbar, null));
        mTitle=(TextView) mHeadBar.findViewById(R.id.title);

        mAdapter = new UnionListAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void initEvent() {
        mHeadBar.findViewById(R.id.back).setOnClickListener(this);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<SearchResultEntity>() {
            @Override
            public void onClick(SearchResultEntity item) {
                switch (SharePreferenceUtils.getInt(UnionListActivity.this, SharePreferenceConfig.LOAD_WAY, AppContant.WEBVIEW_LOAD)) {
                    case AppContant.WEBVIEW_LOAD:
                        Bundle bundle=new Bundle();
                        bundle.putString("entity", "search_result");
                        MainApplication.getInstance().pipeline.put("search_result", item);
                        startActivity(ResultDetailActivity.class, bundle);
                        break;
                    case AppContant.BROWSER_LOAD:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(item.getLink()));
                        startActivity(intent);
                        break;
                }
            }
        });
        mAdapter.setOnRecordListener(new UnionListAdapter.OnRecordListener() {
            @Override
            public void onRecordClick(View v, SearchResultEntity entity, boolean isRecord) {
                SearchResultManager.getInstance().recordSearchResult(UnionListActivity.this, v, entity, isRecord);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        info = MainApplication.getInstance().getUnionRespDao().getDataById(bundle.getInt("id"));
        info.setNew(false);
        MainApplication.getInstance().getUnionRespDao().update(info);
        EventBus.getDefault().post(new ListUpdateEvent(ListUpdateEvent.EventType.UNION_SEARCH));

        if (info == null) {
            finish();
            return;
        }

        mTitle.setText(info.getKeyword().replace("+", " "));

        mDataList = info.getSearchResultData();
        mAdapter.refreshList(mDataList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }
}
