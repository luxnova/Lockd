package io.wallfly.lockdapp;

import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hmkcode.android.recyclerview.R;

import java.util.ArrayList;

import io.wallfly.lockdapp.lockutils.CustomLockListener;
import io.wallfly.lockdapp.lockutils.DragLock;
import io.wallfly.lockdapp.lockutils.Lock;


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
        ImageButton button = (ImageButton)findViewById(R.id.imageButton);

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


    public void saveLock(View v){
        displayUserLock();
    }

    /**
     * Displays the user's lock combination that they want to save.
     */
    private void displayUserLock() {
        ArrayList<Lock> lockCombo = getLockCombo();
        createLockSequence(lockCombo);
        //showSequence();
    }

    /**
     * Sets each Lock with the next lock in the sequence to faciliate the display animation.
     *
     * @param lockCombo - list of all the user's locks in order.
     */
    private void createLockSequence(ArrayList<Lock> lockCombo) {
        int sequenceNumber = 0;
        while (lockCombo.get(sequenceNumber+1) != null){
            Lock lock = lockCombo.get(sequenceNumber);
            lock.setNextLock(lockCombo.get(sequenceNumber+1));
            sequenceNumber++;
        }
    }


    /**
     * Parses the string lock data into a Lock object of the three types.
     *
     * @param lockData - The string containing the essential information for a lock.
     * @return - the lock object from the data
     */
    private Lock getLock(String lockData) {
        return Lock.parseLock(lockData);
    }

    /**
     * Retrieves a list of the lock combinations in order.
     *
     * @return - list of lock combination.
     */
    public ArrayList<Lock> getLockCombo() {
        String lockData = "0";
        int sequenceNumber = 0;

        ArrayList<Lock> lockCombo = new ArrayList<>();
        while(!lockData.isEmpty()){
            sequenceNumber++;
            lockData = Utils.getStringFromSharedPrefs(getString(R.string.package_name) + sequenceNumber);
            lockCombo.add(getLock(lockData));
        }
        return lockCombo;
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
