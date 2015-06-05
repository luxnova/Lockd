package io.wallfly.lockdapp.servicesandreceivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class ScreenStatusMonitoringReceiver extends BroadcastReceiver {

	    private static boolean screenOff;

	    @Override
	    public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                screenOff = true;
                //showLock(context);
                Log.i("State", "Off");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                screenOff = false;
                Log.i("State", "On");

            }
            Intent i = new Intent(context, ScreenStatusMonitoringService.class);
            i.putExtra("screen_state", screenOff);
            context.startService(i);

	    }

    private void showLock(Context context) {
        //Check if lock screen showing already
        /*
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        */

    }

}
