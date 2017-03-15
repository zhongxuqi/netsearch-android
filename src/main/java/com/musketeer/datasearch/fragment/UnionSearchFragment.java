package com.musketeer.datasearch.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.musketeer.baselibrary.Activity.BaseFragment;
import com.musketeer.baselibrary.bean.ParamsEntity;
import com.musketeer.baselibrary.bean.RequestResult;
import com.musketeer.baselibrary.bean.RequestTask;
import com.musketeer.baselibrary.net.UIUpdateTask;
import com.musketeer.baselibrary.paser.JsonPaser;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.activity.MainActivity;
import com.musketeer.datasearch.activity.UnionListActivity;
import com.musketeer.datasearch.adapter.UnionRespListAdapter;
import com.musketeer.datasearch.common.AppContant;
import com.musketeer.datasearch.entity.BaseResp;
import com.musketeer.datasearch.entity.ListUpdateEvent;
import com.musketeer.datasearch.entity.UnionResultResp;
import com.musketeer.datasearch.entity.WebEntity;
import com.musketeer.datasearch.entity.WebGroupEntity;
import com.musketeer.datasearch.entity.WebsUpdateEvent;
import com.musketeer.datasearch.service.MainService;
import com.musketeer.datasearch.util.Net;
import com.musketeer.datasearch.view.BaseDialog;
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
 * Created by zhongxuqi on 16-5-12.
 */
public class UnionSearchFragment extends BaseFragment {
    public static final String TAG = "UnionSearchFragment";

    @Bind(R.id.main_headbar)
    HeaderLayoutBar mHeadBar;
    EditText mSearchText;
    @Bind(R.id.union_result_list)
    RecyclerView mListView;

    protected String OldKeyword="";
    protected UnionRespListAdapter mAdapter;

    protected BaseDialog mDialog;

    @Override
    public void setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BaseView = inflater.inflate(R.layout.fragment_union_search, null);
    }

    @Override
    public void initView() {
        mHeadBar.addView(LayoutInflater.from(getActivity()).inflate(R.layout.include_mainpage_headbar, null));
        mSearchText=(EditText) mHeadBar.findViewById(R.id.search_keyword);
        mSearchText.setHint(R.string.search_bar_input_hint);

        mAdapter = new UnionRespListAdapter(getActivity());
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
                try {
                    return submitSearchTask();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return false;
            }

        });
        EventBus.getDefault().register(this);

        mAdapter.setOnItemClickListener(new UnionRespListAdapter.OnItemClickListener<UnionResultResp>() {
            @Override
            public void onClick(UnionResultResp item) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", item.getId());
                startActivity(UnionListActivity.class, bundle);
            }
        });
        mAdapter.setOnItemDeleteListener(new UnionRespListAdapter.OnItemDeleteListener<UnionResultResp>() {
            @Override
            public void onItemDelete(final UnionResultResp item) {
                if (mDialog==null) {
                    mDialog=new BaseDialog(getActivity());
                }
                mDialog.setDialogContentView(R.layout.include_dialog_common_textview);
                TextView textView=(TextView) mDialog.findViewById(R.id.edittext);
                textView.setText(R.string.affirm_to_delete);
                mDialog.setButton1(getResources().getString(R.string.cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            mDialog.dismiss();
                        }
                    });
                mDialog.setButton2(getResources().getString(R.string.affirm),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            boolean result = MainApplication.getInstance().getUnionRespDao().delete(item);
                            LogUtils.d(TAG, ""+ result);
                            mAdapter.deleteItem(item);
                            mAdapter.notifyDataSetChanged();
                            mDialog.dismiss();
                        }
                    });
                mDialog.show();
            }
        });
    }

    @Override
    public void initData() {
        List<UnionResultResp> list = MainApplication.getInstance().getUnionRespDao().getAllDataOrderByTime();
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
                try {
                    submitSearchTask();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    protected boolean submitSearchTask() throws UnsupportedEncodingException {
        if (mSearchText.getText().toString().trim().length() <= 0 ||
                mSearchText.getText().toString().trim().equals(OldKeyword)) {
            return false;
        }
        String keyword = mSearchText.getText().toString().trim().replace(" ", "+");
        OldKeyword=mSearchText.getText().toString().trim();
        if (AppContant.BanKeyWords.contains(keyword)) {
            showCustomToast(getString(R.string.ban_word_hint));
            return false;
        }
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
                if (result.resultObj == null || !result.resultObj.getStatus().equals("200")) {
                    showCustomToast(getString(R.string.submit_task_fail));
                } else {
                    showCustomToast(getString(R.string.submit_task_success));
                }
            }
        });
        task.setUrl(Net.getAbsoluteUrl("app/submit_union_task"));
        ParamsEntity params=new ParamsEntity();
        params.put("keyword", URLEncoder.encode(keyword, "UTF-8"));
        params.put("registration_id", JPushInterface.getRegistrationID(MainApplication.getInstance()));
        WebGroupEntity groupEntity = MainApplication.getInstance().getWebGroupDao().getSelectedWebGroup();
        if (groupEntity == null) {
            showCustomToast(getString(R.string.select_web_group));
            return false;
        }
        String parser_names = "";
        for (WebEntity web: groupEntity.getWebEntitys()) {
            if (parser_names.length() > 0) {
                parser_names += ",";
            }
            parser_names += web.getParserName();
        }
        params.put("parser_names", URLEncoder.encode(parser_names, "UTF-8"));
        task.setParams(params);
        MainService.addTask(task);
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(ListUpdateEvent event) {
        if (event.getType() != ListUpdateEvent.EventType.UNION_SEARCH) return;
        initData();
    }

    @Subscribe
    public void onEvent(WebsUpdateEvent event) {
        OldKeyword = "";
    }
}
