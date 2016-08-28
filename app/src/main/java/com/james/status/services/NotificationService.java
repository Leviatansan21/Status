package com.james.status.services;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v7.app.NotificationCompat;

import com.james.status.data.NotificationData;
import com.james.status.utils.PreferenceUtils;
import com.james.status.utils.StaticUtils;

import java.util.ArrayList;
import java.util.Arrays;

@TargetApi(18)
public class NotificationService extends NotificationListenerService {

    public static final String ACTION_GET_NOTIFICATIONS = "com.james.status.ACTION_GET_NOTIFICATIONS";

    private boolean isConnected, shouldSendOnConnect;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;
        String action = intent.getAction();
        if (action == null) return START_STICKY;
        switch (action) {
            case ACTION_GET_NOTIFICATIONS:
                if (isConnected) sendNotifications();
                else shouldSendOnConnect = true;
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        isConnected = true;

        if (shouldSendOnConnect) {
            sendNotifications();
            shouldSendOnConnect = false;
        }
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        isConnected = false;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Boolean enabled = PreferenceUtils.getBooleanPreference(this, PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED);
        if (enabled != null && enabled && !StaticUtils.shouldUseCompatNotifications(this)) {
            Intent intent = new Intent(StatusService.ACTION_NOTIFICATION_ADDED);
            intent.setClass(this, StatusService.class);
            intent.putExtra(StatusService.EXTRA_NOTIFICATION, new NotificationData(sbn));

            startService(intent);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Boolean enabled = PreferenceUtils.getBooleanPreference(this, PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED);
        if (enabled != null && enabled && !StaticUtils.shouldUseCompatNotifications(this)) {
            Intent intent = new Intent(StatusService.ACTION_NOTIFICATION_REMOVED);
            intent.setClass(this, StatusService.class);
            intent.putExtra(StatusService.EXTRA_NOTIFICATION, new NotificationData(sbn));

            startService(intent);
        }
    }

    private ArrayList<StatusBarNotification> getNotifications() {
        ArrayList<StatusBarNotification> activeNotifications = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            activeNotifications.addAll(Arrays.asList(getActiveNotifications()));
        }
        return activeNotifications;
    }

    private void sendNotifications() {
        Boolean enabled = PreferenceUtils.getBooleanPreference(this, PreferenceUtils.PreferenceIdentifier.STATUS_ENABLED);
        if (enabled != null && enabled && !StaticUtils.shouldUseCompatNotifications(this)) {
            for (StatusBarNotification sbn : getNotifications()) {
                NotificationData notification = new NotificationData(sbn);
                notification.priority = NotificationCompat.PRIORITY_DEFAULT;

                Intent intent = new Intent(StatusService.ACTION_NOTIFICATION_ADDED);
                intent.setClass(this, StatusService.class);
                intent.putExtra(StatusService.EXTRA_NOTIFICATION, notification);

                startService(intent);
            }
        }
    }

    private String getKey(StatusBarNotification statusBarNotification) {
        return statusBarNotification.getPackageName() + "/" + String.valueOf(statusBarNotification.getId());
    }
}
