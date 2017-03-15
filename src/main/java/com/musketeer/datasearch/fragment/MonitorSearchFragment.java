package com.musketeer.datasearch.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.musketeer.baselibrary.Activity.BaseFragment;
import com.musketeer.baselibrary.bean.ParamsEntity;
import com.musketeer.baselibrary.bean.RequestResult;
import com.musketeer.baselibrary.bean.RequestTask;
import com.musketeer.baselibrary.net.UIUpdateTask;
import com.musketeer.baselibrary.paser.JsonPaser;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.activity.MainActivity;
import com.musketeer.datasearch.activity.MonitorListActivity;
import com.musketeer.datasearch.activity.ResultDetailActivity;
import com.musketeer.datasearch.adapter.MonitorRespListAdapter;
import com.musketeer.datasearch.common.AppContant;
import com.musketeer.datasearch.common.SharePreferenceConfig;
import com.musketeer.datasearch.entity.BaseResp;
import com.musketeer.datasearch.entity.ListUpdateEvent;
import com.musketeer.datasearch.entity.MonitorResultResp;
import com.musketeer.datasearch.entity.SearchResultEntity;
import com.musketeer.datasearch.manager.MonitorTaskManager;
import com.musketeer.datasearch.service.MainService;
import com.musketeer.datasearch.util.Net;
import com.musketeer.datasearch.view.HeaderLayoutBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.Bind;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhongxuqi on 16-5-15.
 */
public class MonitorSearchFragment extends BaseFragment {
    public static final String TAG = "MonitorSearchFragment";

    @Bind(R.id.main_headbar)
    HeaderLayoutBar mHeadBar;
    EditText mSearchText;
    @Bind(R.id.union_result_list)
    RecyclerView mListView;

    protected String OldKeyword="";
    protected MonitorRespListAdapter mAdapter;

    protected AlertDialog mDialog;

    @Override
    public void setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseView = inflater.inflate(R.layout.fragment_monitor_search, null);
    }

    @Override
    public void initView() {
        mHeadBar.addView(LayoutInflater.from(getActivity()).inflate(R.layout.include_mainpage_headbar, null));
        mSearchText=(EditText) mHeadBar.findViewById(R.id.search_keyword);
        mSearchText.setHint(R.string.please_input_the_target_url);

        mAdapter = new MonitorRespListAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void initEvent() {
        mHeadBar.findViewById(R.id.left_image_button).setOnClickListener(this);
        mHeadBar.findViewById(R.id.search_button).setOnClickListener(this);

        mSearchText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode != KeyEvent.KEYCODE_ENTER) return false;
                editMonitorTask(mSearchText.getText().toString().trim());
                return true;
            }

        });
        EventBus.getDefault().register(this);

        mAdapter.setRootClicklistener(new MonitorRespListAdapter.OnRootClickListener() {
            @Override
            public void onRootClick(MonitorResultResp item) {
                switch (SharePreferenceUtils.getInt(getActivity(), SharePreferenceConfig.LOAD_WAY, AppContant.WEBVIEW_LOAD)) {
                    case AppContant.WEBVIEW_LOAD:
                        SearchResultEntity urlInfo = MainApplication.getInstance().getSearchResultDao()
                                .getDataByLink(item.getUrl());
                        if (urlInfo == null) {
                            urlInfo = new SearchResultEntity();
                            urlInfo.setTitle(item.getTitle());
                            urlInfo.setLink(item.getUrl());
                            urlInfo.setRecorded(false);
                        }
                        Bundle bundle=new Bundle();
                        bundle.putString("entity", "search_result");
                        MainApplication.getInstance().pipeline.put("search_result", urlInfo);
                        startActivity(ResultDetailActivity.class, bundle);
                        break;
                    case AppContant.BROWSER_LOAD:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(item.getUrl()));
                        startActivity(intent);
                        break;
                }
            }
        });
        mAdapter.setOnItemClickListener(new MonitorRespListAdapter.OnItemClickListener<MonitorResultResp>() {
            @Override
            public void onClick(MonitorResultResp item) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", item.getId());
                startActivity(MonitorListActivity.class, bundle);
            }
        });
        mAdapter.setOnItemDeleteListener(new MonitorRespListAdapter.OnItemDeleteListener<MonitorResultResp>() {
            @Override
            public void onItemDelete(final MonitorResultResp item) {
                if (mDialog!=null) {
                    mDialog.dismiss();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.affirm_to_delete);
                builder.setNegativeButton(getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDialog.dismiss();
                        }
                    });
                builder.setPositiveButton(getResources().getString(R.string.affirm),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                deleteMoniterTask(item);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                mDialog = builder.create();
                mDialog.show();
            }
        });
    }

    @Override
    public void initData() {
        List<MonitorResultResp> list = MainApplication.getInstance().getMonitorRespDao().getAllDataOrderByTime();
        mAdapter.refreshList(list);
        mAdapter.notifyDataSetChanged();
        LogUtils.d(TAG, ""+list.size());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.left_image_button:
                MainActivity.getInstance().mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.search_button:
                editMonitorTask(mSearchText.getText().toString().trim());
                break;
        }
    }

    protected void editMonitorTask(String targetUrl) {
        if (targetUrl.length() == 0) return;
        final String formatTargetUrl;
        if (targetUrl.startsWith("http://")) {
            formatTargetUrl = targetUrl;
        } else {
            formatTargetUrl = "http://" + targetUrl;
        }
        if (mDialog!=null) {
            mDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.include_dialog_common_edittext, null);
        builder.setView(view);
        final EditText editText = (EditText) view.findViewById(R.id.edittext);
        editText.setHint("请输入关键字");
        builder.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialog.dismiss();
                    }
                });
        builder.setPositiveButton(getResources().getString(R.string.affirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().trim().length()==0) {
                            showCustomToast("关键字不能为空");
                            return;
                        }
                        try {
                            submitMonitorTask(formatTargetUrl, editText.getText().toString().trim());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mDialog.dismiss();
                    }
                });
        mDialog = builder.create();
        mDialog.show();
    }

    protected void submitMonitorTask(String targetUrl, String keyword) throws UnsupportedEncodingException {
        if (keyword.length() <= 0 || keyword.equals(OldKeyword)) {
            return;
        }
        String formatKeyword = keyword.replace(" ", "+");
        if (MainApplication.getInstance().getMonitorRespDao().hasUrlAndKeyWord(targetUrl, keyword)) {
            showCustomToast(getString(R.string.task_exists));
            return;
        }
        OldKeyword=mSearchText.getText().toString().trim();
        if (AppContant.BanKeyWords.contains(formatKeyword)) {
            showCustomToast(getString(R.string.ban_word_hint));
            return;
        }
        MonitorTaskManager.getInstance().submitMonitorTask(targetUrl, formatKeyword, true);
    }

    public void deleteMoniterTask(final MonitorResultResp monitorTask) throws UnsupportedEncodingException {
        RequestTask<BaseResp> task = new RequestTask<>(RequestTask.RequestType.GET, false,
                new JsonPaser<BaseResp>() {
                    @Override
                    public BaseResp BuildModel(JSONObject jsonObject) throws JSONException {
                        LogUtils.d(TAG, jsonObject.toString());
                        return JSON.parseObject(jsonObject.toString(), BaseResp.class);
                    }
                }, new UIUpdateTask<BaseResp>() {
            @Override
            public void doUIUpdate(RequestResult<BaseResp> result) {
                if (!result.isOk) {
                    if (isAdded()) {
                        showCustomToast(getResources().getString(R.string.net_status_error));
                    }
                    return;
                }
                MainApplication.getInstance().getMonitorRespDao().delete(monitorTask);
                mAdapter.deleteItem(monitorTask);
                mAdapter.notifyDataSetChanged();
                mDialog.dismiss();
            }
        });
        task.setUrl(Net.getAbsoluteUrl("app/delete_monitor_task"));
        ParamsEntity params=new ParamsEntity();
        params.put("target_url", Base64.encodeToString(monitorTask.getUrl().getBytes(), Base64.URL_SAFE).trim());
        params.put("keyword", URLEncoder.encode(monitorTask.getKeyword(), "UTF-8"));
        params.put("registration_id", JPushInterface.getRegistrationID(MainApplication.getInstance()));
        task.setParams(params);
        MainService.addTask(task);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(ListUpdateEvent event) {
        if (event.getType() != ListUpdateEvent.EventType.MONITOR_SEARCH) return;
        initData();
    }
}
