package io.wallfly.lockdapp.lockutils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.hmkcode.android.recyclerview.R;

import io.wallfly.lockdapp.activities.CreateLockActivity;

/**
 * Created by JoshuaWilliams on 4/30/15.
 *
 * This is the class for a Lock that implements a combined Dragging, touching and holding
 *
 * Singleton Design Pattern
 */
public class CustomLockListener extends CustomLockBaseListener {
    private static final String LOG_TAG = "CustomLockListener";
    private static CustomLockListener firstInsance = null;
    private CreateLockActivity callingActivity;

    /**
     * Constructor, initializes the context of the instance by default with the current context of the
     * Utils class.
     */
    private CustomLockListener(){
        setContext(Utils.getContext());
        Utils.clearSharedPrefs();
    }

    /**
     * Retrieve singleton instance.
     *
     * @return - CustomLock instance
     */
    public static CustomLockListener getInstance(){
        if(firstInsance == null){
            firstInsance = new CustomLockListener();
        }
        return firstInsance;
    }

    /**
     * Method to save a simple tap on the screen.
     *
     * @param point - the x and y coordinates of the tap location on the screen.
     * @param sequenceNumber - The number in the lock sequence that the lock is currently.
     */
    @Override
    public void captureTouch(float[] point, int sequenceNumber){
        roundPoints(point);
        Lock tapLock = new TapLock(point, sequenceNumber);

        if(isCreatingLock()){
            storeLock(tapLock);
        }
        else{
            checkLock(sequenceNumber, tapLock);
        }
    }

    /**
     * Method to save a drag on the screen.
     * This works by capturing 2 points, the beginning point of the drag and the ending point of the drag.
     * The user can drag how ever and wherever they want, as long as they initially start at the beginning point
     * and end at the ending point.
     *
     * @param startingPoint - Where the user taps to begin the drag.
     * @param endingPoint - Where the user picks their finger up off the screen at.
     * @param sequenceNumber - The number in the lock sequence that the lock is currently.
     */
    @Override
    public void captureDrag(float[] startingPoint, float[] endingPoint, int sequenceNumber){
        roundPoints(startingPoint, endingPoint);
        Lock dragLock = new DragLock(startingPoint, endingPoint, sequenceNumber);

        if(isCreatingLock()) {
            storeLock(dragLock);
        }
        else{
            checkLock(sequenceNumber, dragLock);
        }
    }

    /**
     * Method to capture a hold on the screen
     *
     * @param point - the x and y coordinates of the hold location on the screen.
     * @param seconds - how long the hold was performed.
     * @param sequenceNumber - The number in the lock sequence that the lock is currently.
     */
    @Override
    public void captureHold(float[] point, float seconds, int sequenceNumber){
        roundPoints(point);
        Lock holdLock = new HoldLock(seconds, point, sequenceNumber);

        if(isCreatingLock()){
            storeLock(holdLock);
        }
        else{
            checkLock(sequenceNumber, holdLock);
        }
    }

    /**
     * Checks to see if the lock passed matches the appropriate lock in the sequence.
     *
     * @param sequenceNumber - The number the user is currently on in the saved lock sequence.
     * @param lock - the type of lock passed.
     */
    private boolean checkLock(int sequenceNumber, Lock lock) {
        Log.i(LOG_TAG, "Check Lock " + sequenceNumber);
        Lock sequenceLock = Utils.getLockFromSequence(sequenceNumber);
        if(sequenceLock.equals(lock)){
            Toast.makeText(getContext(), "Correct Lock", Toast.LENGTH_SHORT).show();
            if(Utils.getLockFromSequence((sequenceNumber + 1)) == null) {
                Toast.makeText(getContext(), "Correct Pattern", Toast.LENGTH_LONG).show();
                getCallingActivity().setLockConfirmed(true);
                setSequenceNumber(0);
            }
            return true;
        }
        Toast.makeText(getContext(), "Incorrect Operation", Toast.LENGTH_SHORT).show();
        setSequenceNumber(0);
        return false;
    }

    /**
     * Stores a lock in the shared prefs and Logs the details of the lock.
     *
     * @param lock - The lock being stored. Drag, Tap or Hold.
     */
    public void storeLock(Lock lock){
        Utils.saveToSharedPrefsString(Integer.toString(getSequenceNumber()), lock.toString());
        Log.i("CapturingLock", lock.toString());
        printLock();
        Toast.makeText(getContext(), lock.print(), Toast.LENGTH_SHORT).show();
    }


    /**
     * Takes a number of points and rounds them to the nearest 2 decimal places.
     * 
     * @param points - the points whose coordinates will be rounded.
     */
    private void roundPoints(float[]...points){
        for(float[] point : points){
            point[x] = Utils.round(point[x], 2);
            point[y] = Utils.round(point[y], 2);
        }
    }

    /**
     * Prints out the lock combination in order.
     */
    private void printLock() {
        String lockData = "a";
        int sequenceNumber = 0;
        while(!lockData.isEmpty()){
            sequenceNumber++;
            lockData = Utils.getStringFromSharedPrefs(Integer.toString(sequenceNumber));
            Log.i("PrintingLockSequence", lockData);
            lockData = Utils.getStringFromSharedPrefs(Integer.toString(sequenceNumber + 1));
        }
    }

    public void setCallingActivity(Activity activity){this.callingActivity = (CreateLockActivity) activity;}
    private CreateLockActivity getCallingActivity(){return callingActivity;}


}


