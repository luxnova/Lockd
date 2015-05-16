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


public class CreateLockActivity extends ActionBarActivity {


    private RelativeLayout lockLayout;
    private static final String LOG_TAG = "CreateLockActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lock);
        initiateActivity();
    }

    private void initiateActivity() {
        checkUtilsContext();
        CustomLockListener customLockListener = CustomLockListener.getInstance();
        lockLayout = (RelativeLayout) findViewById(R.id.lockLayout);
        lockLayout.setOnTouchListener(customLockListener);
    }


    public void saveLock(View v){
        displayUserLock();
    }

    /**
     * Displays the user's lock combination that they want to save.
     **/
    private void displayUserLock() {
        ArrayList<Lock> lockCombo = getLockCombo();
        if(lockCombo.size() == 0){
            Toast.makeText(this, "Create a lock sequence to display it.", Toast.LENGTH_SHORT).show();
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
            Lock lock = getLock(lockData);
            if(lock != null){
                lockCombo.add(getLock(lockData));
            }
            else{
                break;
            }
            lockData = Utils.getStringFromSharedPrefs(getString(R.string.package_name) + (sequenceNumber+1));
        }


        return lockCombo;
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


}
