package io.wallfly.lockdapp;

import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hmkcode.android.recyclerview.R;

import io.wallfly.lockdapp.lockutils.CustomLockListener;


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

        CustomLockListener customLockListener = CustomLockListener.getInstance();
        RelativeLayout lockLayout = (RelativeLayout) findViewById(R.id.lockLayout);
        lockLayout.setOnTouchListener(customLockListener);

        TextView myImage = new TextView(this);
        myImage.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));



        /*
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myImage.setBackground(getDrawable(R.drawable.touch_point_circle));
            myImage.setText("T");
            //myImage.setTypeface(null, Typeface.BOLD);
            myImage.setTextSize(50);
            myImage.setGravity(Gravity.CENTER);
        }
        else{
            myImage.setBackground(getResources().getDrawable(R.drawable.touch_point_circle));
        }

        lockLayout.addView(myImage);
        */

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
