package io.wallfly.lockdapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.hmkcode.android.recyclerview.R;

import io.wallfly.lockdapp.lockutils.CustomLock;


public class CreateLockActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lock);

        initiateActivity();
    }

    private void initiateActivity() {
        if(Utils.context == null)
        {
            Utils.context = getApplicationContext();
        }

        CustomLock customLock = CustomLock.getInstance();
        RelativeLayout lockLayout = (RelativeLayout) findViewById(R.id.lockLayout);
        lockLayout.setOnTouchListener(customLock);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_lock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
