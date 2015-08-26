package io.wallfly.lockdapp.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.hmkcode.android.recyclerview.R;

import io.fabric.sdk.android.Fabric;
import io.wallfly.lockdapp.lockutils.LockUtils;
import io.wallfly.lockdapp.lockutils.Utils;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        LockUtils.setContext(getApplicationContext());
        LockUtils.checkIfFirstOpen();
        int choice = Integer.parseInt(Utils.getStringFromSharedPrefs(Utils.getString(R.string.launcher_activity)));

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey("brandy")){
            Intent i = new Intent(LauncherActivity.this, ViewText.class);
            startActivity(i);
            return;
        }

        Log.i("Lock screen", "Choice ------ " + choice);
        if(choice == 1){
            startActivity(new Intent(this, MainActivity.class));
        }
        else{
            startActivity(new Intent(this, LockScreenActivity.class));
        }

        //Check again if Lockd is enabled, if so then make sure the service is started
        //by trying to start it again. The service enables the lock screen to be shown when
        //the user's screen is turned off.
        if(LockUtils.getSetting(R.string.enabled)){
            LockUtils.enableLockd();
        }

        LockScreenActivity.addActivity(this);

        finish();
    }

}
