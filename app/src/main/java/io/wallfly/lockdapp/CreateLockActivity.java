package io.wallfly.lockdapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hmkcode.android.recyclerview.R;

import java.util.ArrayList;

import io.wallfly.lockdapp.lockutils.CustomLockListener;
import io.wallfly.lockdapp.lockutils.Lock;


public class CreateLockActivity extends ActionBarActivity  {


    private RelativeLayout lockLayout;
    private static final String LOG_TAG = "CreateLockActivity";
    private boolean lockConfirmed = false;
    private CustomLockListener customLockListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lock);
        initiateActivity();
    }

    private void initiateActivity() {
        checkUtilsContext();
        customLockListener = CustomLockListener.getInstance();
        customLockListener.setCallingActivity(this);
        lockLayout = (RelativeLayout) findViewById(R.id.lockLayout);
        lockLayout.setOnTouchListener(customLockListener);
    }


    /**
     * On click listener method for the saveLockView.
     *
     * @param v - the view on which the method will operate.
     */
    public void saveLockOnClick(View v){
        displayUserLock();
        confirmLock();
    }

    /**
     * Make the user to confirm the lock inputed.
     */
    private void confirmLock() {
        Toast.makeText(this, "Confirm Lock.", Toast.LENGTH_SHORT).show();
        if(Utils.getLockFromSequence(1) == null) {
            showNoLockToast();
            return;
        }
        customLockListener.setCreatingLock(false); //Allows the listener to act as a test rather than creation.
    }

    /**
     * Displays the user's lock combination that they want to save.
     **/
    private void displayUserLock() {
        ArrayList<Lock> lockCombo = Utils.getLockCombo();
        if(lockCombo.size() == 0){
            showNoLockToast();
            return;
        }
        createLockSequence(lockCombo);
        showSequence(lockCombo);
    }


    /**
     * Sets each Lock with the next lock in the sequence to faciliate the display animation.
     *
     * @param lockCombo - list of all the user's locks in order.
     */
    private void createLockSequence(ArrayList<Lock> lockCombo) {
        int sequenceNumber = 0;
        while (sequenceNumber+1 != lockCombo.size() && lockCombo.size() != 0){
            Lock lock = lockCombo.get(sequenceNumber);
            lock.setNextLock(lockCombo.get(sequenceNumber+1));
            sequenceNumber++;
        }
    }


    private void showSequence(ArrayList<Lock> lockCombo) {
        lockCombo.get(0).showLockSequence(this, lockLayout);
    }

    private void checkUtilsContext() {
        if(Utils.getContext() == null)
        {
            Utils.setContext(getApplicationContext());
        }
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

    /**
     * Shows a toast for no lock being saved.
     */
    public void showNoLockToast(){
        Toast.makeText(this, "Create a lock sequence to display it.", Toast.LENGTH_SHORT).show();
    }

    public void setLockConfirmed(boolean lockConfirmed){
        displayUserLock();
        this.lockConfirmed = lockConfirmed;
    }



}
