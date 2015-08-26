package io.wallfly.lockdapp.servicesandreceivers;

/**
 * Created by JoshuaWilliams on 6/22/15.
 */
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hmkcode.android.recyclerview.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.wallfly.lockdapp.activities.LockScreenActivity;
import io.wallfly.lockdapp.lockutils.Utils;
import io.wallfly.lockdapp.models.LockScreenNotification;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {
    private static final String LOG_TAG = "NotificationService";
    Context context;
    private static List<LockScreenNotification>  notifications = new ArrayList<>();;
    private static NotificationService notificationService;

    @Override

    public void onCreate() {
        Log.i(LOG_TAG, "Starting Notification Service");
        super.onCreate();
        notificationService = this;
        context = getApplicationContext();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if(notificationService == null) notificationService = this;

        if(sbn == null) return;


        LockScreenNotification lockScreenNotification = getLockScreenNotification(sbn);
        notifications.add(lockScreenNotification);
        Intent msgrcv = new Intent("Msg");

        //Log.i(LOG_TAG, "Message ---- " + text);
        if(LockScreenActivity.isShowing()){
            showLock();
        }

        Log.i(LOG_TAG, "------ Count ------ " + notifications.size());

        String message = lockScreenNotification.getText().toLowerCase();



        LocalBroadcastManager.getInstance(context).registerReceiver(LockScreenActivity.receiver, new IntentFilter("Msg"));

        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
        notifications.remove(getLockScreenNotification(sbn));
    }

    /**
     * Allows
     * @return
     */
    public static List<LockScreenNotification> getNotifications(){
        if(notificationService == null) return new ArrayList<>();
        notifications.clear();

        StatusBarNotification[] notis = notificationService.getActiveNotifications();
        if(notis != null) {
            List<StatusBarNotification> sbns = Arrays.asList(notis);
            for(StatusBarNotification sbs : sbns){
                notifications.add(getLockScreenNotification(sbs));
            }
        }
        return notifications;
    }

    private void showLock() {
        //Check if lock screen showing already
        //Sets the back up password to the user's password.
        DevicePolicyManager  mgr = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        mgr.resetPassword(Utils.getStringFromSharedPrefs(Utils.getString(R.string.backup_password)), 0);
        Utils.saveToSharedPrefsString(Utils.getString(R.string.launcher_activity), "2");
        Intent i = new Intent(context, LockScreenActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static LockScreenNotification getLockScreenNotification(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString(Notification.EXTRA_TITLE);
        String text = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
        int icon = sbn.getNotification().icon;

        Log.i("Package", "------- " + pack);
        Log.i("Title", "------- " + title);
        Log.i("Text", "------- " + text);
        Log.i("Icon", "------- " +  icon);

        return new LockScreenNotification(pack, title, text, icon);
    }
}