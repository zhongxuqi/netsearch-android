package com.musketeer.datasearch.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.musketeer.baselibrary.bean.ParamsEntity;
import com.musketeer.baselibrary.bean.RequestResult;
import com.musketeer.baselibrary.bean.RequestTask;
import com.musketeer.baselibrary.net.UIUpdateTask;
import com.musketeer.baselibrary.paser.JsonPaser;
import com.musketeer.baselibrary.util.LogUtils;
import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.datasearch.MainApplication;
import com.musketeer.datasearch.R;
import com.musketeer.datasearch.activity.MonitorListActivity;
import com.musketeer.datasearch.activity.UnionListActivity;
import com.musketeer.datasearch.common.SharePreferenceConfig;
import com.musketeer.datasearch.entity.MonitorResultResp;
import com.musketeer.datasearch.entity.PushMessageBean;
import com.musketeer.datasearch.entity.ListUpdateEvent;
import com.musketeer.datasearch.entity.UnionResultResp;
import com.musketeer.datasearch.util.Net;
import com.musketeer.datasearch.util.NotificationUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhongxuqi on 16-5-12.
 */
public class MessageReceiver extends BroadcastReceiver {
    public static final String TAG = "MessageReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String message = intent.getStringExtra(JPushInterface.EXTRA_MESSAGE);
            String extras = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
            StringBuilder showMsg = new StringBuilder();
            showMsg.append(JPushInterface.EXTRA_MESSAGE + " : " + message + "\n");
            if (!TextUtils.isEmpty(extras)) {
                showMsg.append(JPushInterface.EXTRA_EXTRA + " : " + extras + "\n");
            }
            LogUtils.d("zxq", showMsg.toString());
            PushMessageBean messageBean = JSON.parseObject(message, PushMessageBean.class);
            handleMessage(context, messageBean);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it =  json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " +json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    protected void handleMessage(Context context, PushMessageBean messageBean) {
        switch (messageBean.getType()) {
            case 1:
                getUnionSearchResult(context, messageBean);
                break;
            case 2:
                getMonitorSearchResult(context, messageBean);
                break;
        }
    }

    protected void getUnionSearchResult(final Context context, final PushMessageBean messageBean) {
        RequestTask<UnionResultResp> task = new RequestTask<>(RequestTask.RequestType.GET, false,
                new JsonPaser<UnionResultResp>() {
                    @Override
                    public UnionResultResp BuildModel(JSONObject jsonObject) throws JSONException {
                        LogUtils.d(TAG, jsonObject.toString());
                        return JSON.parseObject(jsonObject.toString(), UnionResultResp.class);
                    }
                }, new UIUpdateTask<UnionResultResp>() {
            @Override
            public void doUIUpdate(RequestResult<UnionResultResp> result) {
                if (!result.isOk) {
                    return;
                }
                MainApplication.getInstance().getUnionRespDao().insert(result.resultObj);
                EventBus.getDefault().post(new ListUpdateEvent(ListUpdateEvent.EventType.UNION_SEARCH));
                if (SharePreferenceUtils.getBoolean(MainApplication.getInstance(),
                        SharePreferenceConfig.NoticeTypeStatus, true)) {
                    NotificationUtil.showNotification(context, context.getString(R.string.search_finish_notify), messageBean,
                            MainApplication.getInstance().getUnionRespDao().getDataByTime(result.resultObj.getTime()).getId(),
                            UnionListActivity.class);
                }
            }
        });
        task.setUrl(Net.getAbsoluteUrl("app/get_union_result"));
        ParamsEntity params=new ParamsEntity();
        params.put("map_key", messageBean.getMapKey());
        task.setParams(params);
        MainService.addTask(task);
    }

    protected void getMonitorSearchResult(final Context context, final PushMessageBean messageBean) {
        RequestTask<MonitorResultResp> task = new RequestTask<>(RequestTask.RequestType.GET, false,
                new JsonPaser<MonitorResultResp>() {
                    @Override
                    public MonitorResultResp BuildModel(JSONObject jsonObject) throws JSONException {
                        LogUtils.d(TAG, jsonObject.toString());
                        return JSON.parseObject(jsonObject.toString(), MonitorResultResp.class);
                    }
                }, new UIUpdateTask<MonitorResultResp>() {
            @Override
            public void doUIUpdate(RequestResult<MonitorResultResp> result) {
                if (!result.isOk || result.resultObj == null || result.resultObj.getUrl().length() == 0) {
                    return;
                }
                MainApplication.getInstance().getMonitorRespDao().updateByNewResp(result.resultObj);
                EventBus.getDefault().post(new ListUpdateEvent(ListUpdateEvent.EventType.MONITOR_SEARCH));
                if (SharePreferenceUtils.getBoolean(MainApplication.getInstance(),
                        SharePreferenceConfig.NoticeTypeStatus, true)) {
                    NotificationUtil.showNotification(context, context.getString(R.string.find_new_notice), messageBean,
                            MainApplication.getInstance().getMonitorRespDao().getDataByTime(result.resultObj.getTime()).getId(),
                            MonitorListActivity.class);
                }
            }
        });
        task.setUrl(Net.getAbsoluteUrl("app/get_monitor_result"));
        ParamsEntity params=new ParamsEntity();
        params.put("map_key", messageBean.getMapKey());
        task.setParams(params);
        MainService.addTask(task);
    }
}
