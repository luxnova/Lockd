package io.wallfly.lockdapp.lockutils;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import io.wallfly.lockdapp.Utils;

/**
 * Created by JoshuaWilliams on 4/30/15.
 *
 * This is the class for a Lock that implements a combined Dragging, touching and holding
 *
 * Singleton Design Pattern
 */
public class CustomLock implements CustomLockListener, View.OnTouchListener {

    private static CustomLock firstInsance = null;
    private int sequenceNumber;
    private static long secondsInMillis;
    private boolean hold = false;
    private boolean dragging = false;

    private static float SECONDS_BETWEEN_TAPS = 1000;
    private static int VALID_TOUCH_BOUNDS = 400;
    private static final String PACKAGE_NAME = "io.wallfly.lockdapp.lockseq";

    private double[] beginningTouchPoint;
    private double[] endingTouchPoint;

    private Context context;

    private CustomLock(){
        setContext(context);
        Utils.clearSharedPrefs();
    }

    /**
     * Retrieve singleton instance.
     *
     * @return - CustomLock instance
     */
    public static CustomLock getInstance(){
        if(firstInsance == null){
            firstInsance = new CustomLock();


        }
        return firstInsance;
    }


    /************************** Lock Interface *****************************************/

    @Override
    public void captureTouch(double[] point,  int sequenceNumber) {
        point[x] = Math.round((point[x]*10)/10);
        point[y]= Math.round((point[y]*10)/10);

        Lock tap = new TapLock(point, sequenceNumber);
        Utils.saveToSharedPrefsString(PACKAGE_NAME + sequenceNumber, tap.toString());
        Log.i("CapturingLock", tap.toString());
        printLock();
    }


    @Override
    public void captureDrag(double[] startingPoint, double[] endingPoint, int sequenceNumber){
        double startXCoord = Math.round((startingPoint[x]*10)/10);
        double startYCoord = Math.round((startingPoint[y]*10)/10);
        double endXCoord = Math.round((endingPoint[x]*10)/10);
        double endYCoord = Math.round((endingPoint[y]*10)/10);

        startingPoint = new double[]{startXCoord, startYCoord};
        endingPoint = new double[]{endXCoord, endYCoord};

        Lock dragLock = new DragLock(startingPoint, endingPoint, sequenceNumber);

        Utils.saveToSharedPrefsString(PACKAGE_NAME + sequenceNumber, dragLock.toString());
        Log.i("CapturingLock", dragLock.toString());
        printLock();

        dragging = false;
    }


    @Override
    public void captureHold(double[] point, float seconds, int sequenceNumber) {
        point[x] = Math.round((point[x]*10)/10);
        point[y]= Math.round((point[y]*10)/10);

        Lock holdLock = new HoldLock(seconds, point, sequenceNumber);
        Utils.saveToSharedPrefsString(PACKAGE_NAME + sequenceNumber, holdLock.toString());
        Log.i("CapturingLock", holdLock.toString());
        printLock();
        hold = false;

    }




    /********************************* On Touch **********************************/

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                beginningTouchPoint = new double[]{motionEvent.getRawX(), motionEvent.getRawY()};
                secondsInMillis = Calendar.getInstance().getTimeInMillis();
                Log.i("ACTION DOWN", "Starting...");
                return true;

            case MotionEvent.ACTION_UP:
                Log.i("ACTION UP", "Capture Ending...");
                if(!dragging){
                    secondsInMillis = (Calendar.getInstance().getTimeInMillis()) - secondsInMillis;
                    Log.i("Seconds", "Hold " + secondsInMillis);
                    if(secondsInMillis > 1100){
                        hold = true;
                    }
                    secondsInMillis += SECONDS_BETWEEN_TAPS;
                    ++sequenceNumber;
                    if(hold){
                        captureHold(new double[] {motionEvent.getRawX(), motionEvent.getRawY()}, secondsInMillis, sequenceNumber);
                    }
                    else{
                        captureTouch(new double[] {motionEvent.getRawX(), motionEvent.getRawY()},  sequenceNumber);
                    }
                    secondsInMillis = 0;
                }
                else{
                    if (outOfBufferZone(new double[]{motionEvent.getRawX(), motionEvent.getRawY()})){
                        endingTouchPoint = new double[]{motionEvent.getRawX(), motionEvent.getRawY()};
                        ++sequenceNumber;
                        captureDrag(beginningTouchPoint, endingTouchPoint, sequenceNumber);
                    }
                    else{
                        Toast.makeText(context, "Drag was not long enough. Lock not registered.", Toast.LENGTH_SHORT);
                    }
                }
                return true;

            case MotionEvent.ACTION_MOVE:{

                if(beginningTouchPoint[x] != motionEvent.getRawX()
                        && beginningTouchPoint[y] != motionEvent.getRawY()
                        && outOfBufferZone(new double[]{motionEvent.getRawX(), motionEvent.getRawY()})){
                    dragging = true;
                    Log.i("DRAG EVENT", "" + motionEvent.getRawX() + " " + motionEvent.getRawY());
                }
                break;
            }
        }
        return false;
    }

    /**
     * Checks to see if the point being registered is within the the circle of the at which the action started.
     * This prevents very close and indistinguishable touches from being created and saved.
     *
     * @param point - point being measured against the starting point and the bounds.
     * @return - whether or not the point is out of the buffer zone.
     */
    private boolean outOfBufferZone(double[] point) {
        //equation for testing if point is inside a circle.
        double D = Math.sqrt(Math.pow(beginningTouchPoint[x] - point[x], 2) + Math.pow(beginningTouchPoint[y] - point[y], 2));
        return D >= VALID_TOUCH_BOUNDS;
    }

    /**
     * Setting number for home many seconds between each lock sequence action.
     * Works Like a buffer, the higher the seconds, the less security.
     *
     * @param seconds - milliseconds for the buffer.
     */
    public void setSecondsBetweenTaps(float seconds){
        SECONDS_BETWEEN_TAPS = seconds;
    }

    public float getSecondsBetweenTaps(){
        return SECONDS_BETWEEN_TAPS;
    }


    /**
     * Sets the lock bounds for a valid touch.
     * Works as a circle's radius from the point in reference.
     *
     * The higher the the bounds, the less security.
     *
     * @param lockBounds - the "radius" of the circle being calculated.
     */
    public void setValidLockBounds(int lockBounds){
        VALID_TOUCH_BOUNDS = lockBounds;
    }

    public float getValidLockBounds(){
        return VALID_TOUCH_BOUNDS;
    }

    private void printLock() {
        String lockData = "a";
        int sequenceNumber = 0;
        while(!lockData.isEmpty()){
            sequenceNumber++;
            lockData = Utils.getStringFromSharedPrefs(PACKAGE_NAME + sequenceNumber);
            Log.i("PrintingLockSequence", lockData);
        }
    }

    public void setContext(Context context){
        this.context = context;
    }




    /**
     *
     */
}


