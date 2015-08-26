package io.wallfly.lockdapp.servicesandreceivers;


import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hmkcode.android.recyclerview.R;

import io.wallfly.lockdapp.activities.LockScreenActivity;
import io.wallfly.lockdapp.lockutils.LockUtils;
import io.wallfly.lockdapp.lockutils.Utils;


public class ScreenStatusMonitoringReceiver extends BroadcastReceiver {

    private DevicePolicyManager mgr;

    @Override
	    public void onReceive(Context context, Intent intent) {
            mgr = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

            Utils.setContext(context);
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i("State", "Off");
                showLock(context);

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.i("State", "On");

            }  else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                showLock(context);
                context.startService(new Intent(context, ScreenStatusMonitoringService.class));
                context.startService(new Intent(context, NotificationService.class));
            }

	    }

    private void showLock(Context context) {
        if(LockUtils.isLockdActiveAdmin() && LockUtils.isLockdEnabled() && LockUtils.userHasLockPattern()) {
            //Check if lock screen showing already
            //Sets the back up password to the user's password.
            mgr.resetPassword(Utils.getStringFromSharedPrefs(Utils.getString(R.string.backup_password)), 0);
            Utils.saveToSharedPrefsString(Utils.getString(R.string.launcher_activity), "2");
            Intent i = new Intent(context, LockScreenActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }

}
