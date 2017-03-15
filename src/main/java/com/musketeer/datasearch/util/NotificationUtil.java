package com.musketeer.datasearch.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.musketeer.datasearch.R;
import com.musketeer.datasearch.activity.UnionListActivity;
import com.musketeer.datasearch.entity.ListUpdateEvent;
import com.musketeer.datasearch.entity.PushMessageBean;
import com.musketeer.datasearch.entity.UnionResultResp;

/**
 * Created by zhongxuqi on 16-5-13.
 */
public class NotificationUtil {
    public static void showNotification(Context context, String title, PushMessageBean messageBean, int id, Class<? extends Activity> cls) {
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, cls);
        intent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification mNotification = new Notification.Builder(context)
            .setTicker(title)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(title)
            .setContentText(messageBean.getKeyword())
            .setContentIntent(pendingIntent)
            .build();
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(0, mNotification);
    }
}
