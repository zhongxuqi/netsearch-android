package com.musketeer.datasearch.manager;

import android.util.Base64;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.musketeer.baselibrary.bean.ParamsEntity;
import com.musketeer.baselibrary.bean.RequestResult;
import com.musketeer.baselibrary.bean.RequestTask;
import com.musketeer.baselibrary.net.UIUpdateTask;
import com.musketeer.baselibrary.paser.JsonPaser;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.entity.BaseResp;
import com.musketeer.datasearch.entity.MonitorResultResp;
import com.musketeer.datasearch.service.MainService;
import com.musketeer.datasearch.util.Net;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhongxuqi on 16-5-15.
 */
public class MonitorTaskManager {
    public static final String TAG = "MonitorTaskManager";

    private static MonitorTaskManager instance;

    public static synchronized MonitorTaskManager getInstance() {
        if (instance == null) {
            instance = new MonitorTaskManager();
        }
        return instance;
    }

    private MonitorTaskManager() {

    }

    public void initMonitorTask() {
        List<MonitorResultResp> MonitorTaskList = MainApplication.getInstance().getMonitorRespDao().getAllData();
        if (MonitorTaskList != null) {
            for (MonitorResultResp item: MonitorTaskList) {
                try {
                    submitMonitorTask(item.getUrl(), item.getKeyword(), false);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void submitMonitorTask(String targetUrl, String keyword, final boolean isShowSuccess) throws UnsupportedEncodingException {
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
                    Toast.makeText(MainApplication.getInstance(), R.string.net_status_error, Toast.LENGTH_LONG).show();
                    return;
                }
                if (result.resultObj == null || !result.resultObj.getStatus().equals("200")) {
                    Toast.makeText(MainApplication.getInstance(), R.string.submit_task_fail, Toast.LENGTH_LONG).show();
                } else if (isShowSuccess) {
                    Toast.makeText(MainApplication.getInstance(), R.string.submit_task_success, Toast.LENGTH_LONG).show();
                }
            }
        });
        task.setUrl(Net.getAbsoluteUrl("app/submit_monitor_task"));
        ParamsEntity params=new ParamsEntity();
        params.put("target_url", Base64.encodeToString(targetUrl.getBytes(), Base64.URL_SAFE).trim());
        params.put("keyword", URLEncoder.encode(keyword, "UTF-8"));
        params.put("registration_id", JPushInterface.getRegistrationID(MainApplication.getInstance()));
        task.setParams(params);
        MainService.addTask(task);
    }
}
