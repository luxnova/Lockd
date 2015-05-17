package io.wallfly.lockdapp.lockutils;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hmkcode.android.recyclerview.R;

import java.util.Calendar;

import io.wallfly.lockdapp.Utils;

/**
 * Created by JoshuaWilliams on 4/30/15.
 *
 * Abstract for any Lockd custom lock listener
 */
public abstract class CustomLockBaseListener implements View.OnTouchListener{
    private static final String LOG_TAG = "CustomLockBaseListener";

    private int sequenceNumber;
    private int holdSeconds;
    private boolean dragging = false;
    private boolean userIsHolding = false;
    private boolean creatingLock = true;
    private static final int TAP_BOUNDS = 1000; //milliseconds to track if touch is a tap or hold.
    private static final int MINIMUM_DRAG_START_DISTANCE = 30; //density pixels
    private static final int VALID_DRAG_DISTANCE = 500; //density pixels

    //User settings
    private static float SECONDS_BETWEEN_TAPS = 1000; //milliseconds
    private static int VALID_TOUCH_BOUNDS = 150; //density pixels
    private static boolean VIBRATE_ON_TOUCH = true;

    private float[] beginningTouchPoint;

    private Context context;

    static final int x = 0;
    static final int y = 1;

    Handler timerHandler = new Handler();


    /**
     * Method to save a simple tap on the screen.
     *
     * @param point - the x and y coordinates of the tap location on the screen.
     * @param sequenceNumber - The number in the lock sequence that the lock is currently.
     */
    public abstract void captureTouch(float[] point, int sequenceNumber);

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
    public abstract void captureDrag(float[] startingPoint, float[] endingPoint, int sequenceNumber);

    /**
     * Method to capture a hold on the screen
     *
     * @param point - the x and y coordinates of the hold location on the screen.
     * @param seconds - how long the hold was performed.
     * @param sequenceNumber - The number in the lock sequence that the lock is currently.
     */
    public abstract void captureHold(float[] point, float seconds, int sequenceNumber);



    /********************************* On Touch **********************************/

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                beginningTouchPoint = new float[]{motionEvent.getRawX(), motionEvent.getRawY()}; // beginning point for drag lock.
                vibrateIfActive();
                activateHoldTimer();
                Log.i("ACTION DOWN", "Starting..." + holdSeconds);
                return true;

            case MotionEvent.ACTION_UP:
                float[] endingTouchPoint = new float[]{motionEvent.getRawX(), motionEvent.getRawY()};

                Log.i("ACTION UP", "Capture Ending...");
                if(!dragging){
                    // If the point the the user ends at is within the touch buffer zone of the beginning point
                    // and the point is not a valid drag ending point in reference to the beginning point
                    if (!validDragAction(endingTouchPoint) && userAttemptedDrag(endingTouchPoint)) {
                        cancelHoldTimer();
                        if(isCreatingLock()){
                            Toast.makeText(getContext(), Utils.getString(R.string.drag_not_registered), Toast.LENGTH_SHORT).show();
                        }
                        holdSeconds = 0;
                        return true;
                    }

                    if(userIsHolding){
                        captureHold(endingTouchPoint, holdSeconds, ++sequenceNumber);
                    }
                    else{
                        captureTouch(endingTouchPoint, ++sequenceNumber);
                    }
                    holdSeconds = 0;
                }
                else{
                    vibrateIfActive();
                    captureDrag(beginningTouchPoint, endingTouchPoint, ++sequenceNumber);
                    dragging = false;
                }
                cancelHoldTimer();
                return true;

            case MotionEvent.ACTION_MOVE:{
                if(movedOutOfBufferZone(new float[]{motionEvent.getRawX(), motionEvent.getRawY()})){
                    dragging = true;
                    cancelHoldTimer();
                }
                break;
            }
        }
        return false;
    }

    /**
     * Test to see if the user attempted to register a drag but the distance was not long enough.
     *
     * @param endingTouchPoint - the point being measured to get the distance
     * @return - whether the drag was valid or not.
     */
    private boolean userAttemptedDrag(float[] endingTouchPoint) {
        return Utils.getDistance(beginningTouchPoint, endingTouchPoint) > MINIMUM_DRAG_START_DISTANCE;
    }

    /**
     * Starts the timer to see if user is performing a hold.
     */
    private void activateHoldTimer() {
        timerHandler.postDelayed(timerRunnable, 0);
    }

    /**
     * Cancels the hold timer by removing callbacks from the handler.
     */
    private void cancelHoldTimer(){
        userIsHolding = false;
        holdSeconds = 0;
        timerHandler.removeCallbacks(timerRunnable);
    }


    /**
     * For Dragging
     *
     * User has moved finger far enough from the touch point on the screen that they began at.
     *
     * @param point - The point being measured against.
     * @return - whether the user has moved far enough from the beginning point.
     */
    private boolean movedOutOfBufferZone(float[] point) {
        return Utils.getDistance(beginningTouchPoint, point) >= VALID_DRAG_DISTANCE;
    }

    /**
     * Tests to see if the point is within the range of the begining of the drag action's first point.
     * (The point on Action Down).
     *
     * This helps differentiate from holds and attempts to register a drag lock.
     * Measures if a drag attempt was made.
     *
     * @param point - the point being measured.
     * @return - whether the point is within the
     */
    private boolean validDragAction(float[] point) {
        return movedOutOfBufferZone(point) && userAttemptedDrag(point);
    }



    private void vibrateIfActive(){
        if(VIBRATE_ON_TOUCH) ((Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
    }



    /*************************************** USER SETTINGS ********************************************/

    /**
     * Sets the lock bounds for a valid touch.
     * Works as a circle's radius from the point in reference.
     *
     * The higher the the bounds, the less security.
     *
     * @param lockBounds - the "radius" of the circle being calculated.
     */
    public static void setValidLockBounds(int lockBounds){VALID_TOUCH_BOUNDS = lockBounds;}

    public static float getValidLockBounds(){return VALID_TOUCH_BOUNDS;}

    public static void setVibrateOnTouch(boolean vibrate){VIBRATE_ON_TOUCH = vibrate;}

    public static boolean getVibrateOnTouch(){return VIBRATE_ON_TOUCH;}

    /**
     * Setting number for home many seconds between each lock sequence action.
     * Works Like a buffer, the higher the seconds, the less security.
     *
     * @param seconds - milliseconds for the buffer.
     */
    public static void setSecondsBetweenTaps(float seconds){
        SECONDS_BETWEEN_TAPS = seconds;
    }

    public static float getSecondsBetweenTaps(){
        return SECONDS_BETWEEN_TAPS;
    }


    /*********************************** END OF SETTINGS ********************************************/


    public void setContext(Context context){this.context = context;}

    public Context getContext(){return context;}

    public boolean isCreatingLock() {
        return creatingLock;
    }

    public void setCreatingLock(boolean creatingLock) {
        if(!creatingLock) setSequenceNumber(0);
        this.creatingLock = creatingLock;
    }

    public void setSequenceNumber(int mSequenceNumber){sequenceNumber = mSequenceNumber;}

    public int getSequenceNumber(){return sequenceNumber;}

    //Timer to track if user is performing a hold.
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            holdSeconds++;
            if(holdSeconds == TAP_BOUNDS) {
                Log.i(LOG_TAG, "...Holding...");
                userIsHolding = true;
                vibrateIfActive();
            }
            timerHandler.postDelayed(this, 1);
        }
    };

}
