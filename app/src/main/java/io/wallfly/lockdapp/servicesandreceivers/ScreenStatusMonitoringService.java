package io.wallfly.lockdapp.servicesandreceivers;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import io.wallfly.lockdapp.servicesandreceivers.ScreenStatusMonitoringReceiver;


public class ScreenStatusMonitoringService extends Service {
	BroadcastReceiver mReceiver=null;



    public ScreenStatusMonitoringService()
    {
        super();
    }

        @Override
        public void onCreate() {
            super.onCreate();
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            mReceiver = new ScreenStatusMonitoringReceiver();
            registerReceiver(mReceiver, filter);

        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            return START_STICKY;
        }

		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
		
		@Override
		public void onDestroy() {

            Log.i("ScreenOnOff", "Service  destroy");
			if(mReceiver!=null){
                unregisterReceiver(mReceiver);
            }


		}
}