package io.wallfly.lockdapp.lockutils;

import android.util.Log;
import android.widget.Toast;

import io.wallfly.lockdapp.Utils;

/**
 * Created by JoshuaWilliams on 4/30/15.
 *
 * This is the class for a Lock that implements a combined Dragging, touching and holding
 *
 * Singleton Design Pattern
 */
public class CustomLockListener extends CustomLockBaseListener {

    private static final String PACKAGE_NAME = "io.wallfly.lockdapp.lockseq";

    private static CustomLockListener firstInsance = null;


    private CustomLockListener(){
        setContext(Utils.context);
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
    public void captureTouch(double[] point, int sequenceNumber){
        roundPoints(point);
        Lock tapLock = new TapLock(point, sequenceNumber);
        storeLock(tapLock);
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
    public void captureDrag(double[] startingPoint, double[] endingPoint, int sequenceNumber){
        roundPoints(startingPoint, endingPoint);
        Lock dragLock = new DragLock(startingPoint, endingPoint, sequenceNumber);
        storeLock(dragLock);
    }

    /**
     * Method to capture a hold on the screen
     *
     * @param point - the x and y coordinates of the hold location on the screen.
     * @param seconds - how long the hold was performed.
     * @param sequenceNumber - The number in the lock sequence that the lock is currently.
     */
    @Override
    public void captureHold(double[] point, float seconds, int sequenceNumber){
        roundPoints(point);
        Lock holdLock = new HoldLock(seconds, point, sequenceNumber);
        storeLock(holdLock);
    }


    /**
     * Stores a lock in the shared prefs and Logs the details of the lock.
     *
     * @param lock - The lock being stored. Drag, Tap or Hold.
     */
    public void storeLock(Lock lock){
        Utils.saveToSharedPrefsString(PACKAGE_NAME + sequenceNumber, lock.toString());
        Log.i("CapturingLock", lock.toString());
        printLock();
        Toast.makeText(getContext(), lock.print(), Toast.LENGTH_SHORT).show();
    }

    private void roundPoints(double[]...points){
        for(double[] point : points){
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
            lockData = Utils.getStringFromSharedPrefs(PACKAGE_NAME + sequenceNumber);
            Log.i("PrintingLockSequence", lockData);
        }
    }
}


