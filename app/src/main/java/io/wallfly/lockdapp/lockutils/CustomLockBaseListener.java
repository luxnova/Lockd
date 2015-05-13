package io.wallfly.lockdapp.lockutils;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by JoshuaWilliams on 4/30/15.
 *
 * Abstract for any Lockd custom lock listener
 */
public abstract class CustomLockBaseListener implements  View.OnTouchListener{
    public int sequenceNumber;
    private long secondsInMillis;
    private boolean dragging = false;
    private static final float TAP_BOUNDS = 650;
    private static final int MINIMUM_DRAG_START_DISTANCE = 30;

    //User settings
    private float SECONDS_BETWEEN_TAPS = 1000;
    private int VALID_TOUCH_BOUNDS = 400;
    private boolean VIBRATE_ON_TOUCH = true;


    private double[] beginningTouchPoint;

    private Context context;

    public static final int x = 0;
    public static final int y = 1;

    /**
     * Method to save a simple tap on the screen.
     *
     * @param point - the x and y coordinates of the tap location on the screen.
     * @param sequenceNumber - The number in the lock sequence that the lock is currently.
     */
    public abstract void captureTouch(double[] point, int sequenceNumber);

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
    public abstract void captureDrag(double[] startingPoint, double[] endingPoint, int sequenceNumber);

    /**
     * Method to capture a hold on the screen
     *
     * @param point - the x and y coordinates of the hold location on the screen.
     * @param seconds - how long the hold was performed.
     * @param sequenceNumber - The number in the lock sequence that the lock is currently.
     */
    public abstract void captureHold(double[] point, float seconds, int sequenceNumber);



    /********************************* On Touch **********************************/

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                beginningTouchPoint = new double[]{motionEvent.getRawX(), motionEvent.getRawY()}; // beginning point for drag lock.
                secondsInMillis = Calendar.getInstance().getTimeInMillis();
                vibrateIfActive();
                Log.i("ACTION DOWN", "Starting...");
                return true;

            case MotionEvent.ACTION_UP:
                double[] endingTouchPoint = new double[]{motionEvent.getRawX(), motionEvent.getRawY()};

                vibrateIfActive();

                Log.i("ACTION UP", "Capture Ending...");
                if(!dragging){
                    // If the point the the user ends at is within the touch buffer zone of the beginning point
                    // and the point is not a valid drag ending point in reference to the beginning point
                    if (!outOfBufferZone(endingTouchPoint)
                            && validDragAction(endingTouchPoint)) {
                        Toast.makeText(context, "Drag was not long enough. Lock not registered.", Toast.LENGTH_SHORT).show();
                        secondsInMillis = 0;
                        return true;
                    }

                    ++sequenceNumber;
                    secondsInMillis = (Calendar.getInstance().getTimeInMillis()) - secondsInMillis;

                    if(secondsInMillis > TAP_BOUNDS){
                        secondsInMillis += SECONDS_BETWEEN_TAPS;
                        captureHold(endingTouchPoint, secondsInMillis, sequenceNumber);
                    }
                    else{
                        captureTouch(endingTouchPoint, sequenceNumber);
                    }

                    secondsInMillis = 0;
                }
                else{
                    ++sequenceNumber;
                    captureDrag(beginningTouchPoint, endingTouchPoint, sequenceNumber);
                    dragging = false;
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
     *
     * Tests to see if the point is within the range of the begining of the drag action's first point.
     * (The point on Action Down).
     *
     * This helps differentiate from holds and attempts to register a drag lock.
     * Measures if a drag attempt was made.
     *
     *
     * @param point - the point being measured.
     * @return - whether the point is within the
     */
    private boolean validDragAction(double[] point) {
        return Math.abs(beginningTouchPoint[x] - point[x]) >= MINIMUM_DRAG_START_DISTANCE
                && Math.abs(beginningTouchPoint[x] - point[x]) < VALID_TOUCH_BOUNDS
                || Math.abs(beginningTouchPoint[y] - point[y]) >= MINIMUM_DRAG_START_DISTANCE
                && Math.abs(beginningTouchPoint[y] - point[y]) < VALID_TOUCH_BOUNDS;
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

    private void vibrateIfActive(){
        if(VIBRATE_ON_TOUCH) ((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
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

    public void setContext(Context context){this.context = context;}

    public Context getContext(){
        return context;
    }

    public void setVibrateOnTouch(boolean vibrate){this.VIBRATE_ON_TOUCH = vibrate;}

    public boolean getVibrateOnTouch(){
        return VIBRATE_ON_TOUCH;
    }

}
