package io.wallfly.lockdapp.lockutils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hmkcode.android.recyclerview.R;
import java.util.ArrayList;


/**
 * Created by JoshuaWilliams on 4/30/15.
 *
 * Abstract for any Lockd custom lock listener
 *
 * @version 1.4
 */
public abstract class AbstractLockdBaseListener implements View.OnTouchListener, LockdBaseListener {
    private static final String LOG_TAG = "CustomLockBaseListener";

    //Developer Setting
    private static int ANIMATION_DISPLAY_SECONDS = 2; //Will show the display of the lock animation.

    private int sequenceNumber;
    private int holdSeconds;
    private boolean dragging = false;
    private boolean userIsHolding = false;
    private boolean creatingLock = true;
    private static final int TAP_BOUNDS = 500; //milliseconds to track if touch is a tap or hold.
    private static final int MINIMUM_DRAG_START_DISTANCE = 30; //density pixels
    private static final int VALID_DRAG_DISTANCE = 500; //density pixels

    //DO NOT MODIFY
    private static final float MINIMUM_LOCK_BOUNDS = 70; //Never set minimum less that 70. This will make it extremely hard for user to unlock device.
    private static final float MAXIMUM_LOCK_BOUNDS = 415;//Never set maximum about 415. This will make it extremely easy for anyone to unlock device.

    //User settings
    private static float SECONDS_BETWEEN_TAPS; //milliseconds
    private static float VALID_TOUCH_BOUNDS; //density pixels
    private static boolean VIBRATE_ON_TOUCH;

    private float[] beginningTouchPoint;

    private Context context;

    //Convenience values for classes that implement this listener
    static final int x = 0;
    static final int y = 1;

    private PatternDisplayListener patternDisplayListener;

    private Handler timerHandler = new Handler();
    private Handler displayHandler = new Handler();

    private int animationSeconds = 0;

    //View for the pattern display
    private RelativeLayout parent;
    private TextView pointView;
    private TextView endPointView;
    private Lock currentLock;

    //Settings for Custom Lock Listener Developers
    private boolean showPatternViews = true;

    //This is for developers implementing this Lockd lock. setting the calling activity
    //allows for them to cast to the correct activity to use methods inside the listener they create.
    private Activity callingActivity;


    /**
     * Allows user to set a listener for when the lock pattern is being performed.
     *
     * @param listener - the pattern listener
     */
    public void setPatternListener(PatternDisplayListener listener){
        patternDisplayListener = listener;
    }




    /********************************* On Touch **********************************/

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                beginningTouchPoint = new float[]{motionEvent.getRawX(), motionEvent.getRawY()}; // beginning point for drag lock.
                onCaptureStart();
                vibrateIfActive();
                activateHoldTimer();
                Log.i("ACTION DOWN", "Starting..." + holdSeconds);
                return true;

            case MotionEvent.ACTION_UP:
                onCaptureEnd();
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


    /**
     * Will vibrate if the user has the setting active for 100 milliseconds
     */
    private void vibrateIfActive(){
        if(VIBRATE_ON_TOUCH) ((Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
    }


    /**
     * Displays the lock sequence starting from the lock that the method was called on. That is, if the
     * method gets called on by the 3rd lock in the sequence, then the lock sequence will display from the 3rd
     * until the end of the combination. The sequence will be displayed on the parent layout.
     *
     * @param parent  - the layout the lock sequence will be displayed on.
     */
    public void showLockSequence(RelativeLayout parent){
        ArrayList<Lock> lockCombo = LockUtils.getLockCombo();
        createLockSequence(lockCombo);

        setParent(parent);

        if(patternDisplayListener != null){
            patternDisplayListener.onStart();
        }

        displayPoints(lockCombo.get(0));
    }

    /**
     * Shows the next lock in the sequence on the parent layout.
     *
     * @param lock - The next lock to be displayed in the sequence.
     */
    private void showLockNextLock(Lock lock){
        displayPoints(lock);
    }

    /**
     * Displays the points of the lock along with the type.
     *
     * @param lock - The lock to whose points will be displayed.
     */
    private void displayPoints(Lock lock) {
        setCurrentLock(lock);

        if(showPatternViews){
            TextView pointView = LockUtils.getPointView(lock.getType(), lock.getxCoord(), lock.getYCoord());
            pointView.setVisibility(View.VISIBLE);
            Utils.setViewBackground(R.drawable.touch_point_circle, pointView);

            if(lock instanceof DragLock){
                pointView.setTextColor(Utils.getColor(R.color.green));
                Utils.setViewBackground(R.drawable.drag_start_circle, pointView);

                TextView endPointView = LockUtils.getPointView(lock.getType(),
                        ((DragLock)lock).getEndXCoord() ,((DragLock)lock).getEndYCoord());

                endPointView.setTextColor(Utils.getColor(R.color.red));

                Utils.setViewBackground(R.drawable.drag_end_circle, endPointView);

                endPointView.setVisibility(View.VISIBLE);
                getParent().addView(endPointView);
                setEndPointView(endPointView);
            }

            getParent().addView(pointView);
            setPointView(pointView);
        }

        patternDisplayListener.onDisplaying(getCurrentLock());
        startAnimation();

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

    private void startAnimation() {
        displayHandler.postDelayed(displayRunnable, 0);
    }

    private void setParent(RelativeLayout parent){this.parent = parent;}
    private RelativeLayout getParent(){return parent;}


    /**
     * This view that shows the lock operation. Also operates as the start point view for drag locks.
     *
     * T - Tap
     * D - Drag
     * H - Hold
     *
     * @param pointView - The view which will show the lock operation.
     */
    private void setPointView(TextView pointView){this.pointView = pointView;}
    private void setEndPointView(TextView endPointView){this.endPointView = endPointView;}

    /**
     * Only used for Drag Locks
     *
     * @return - The text view with the lock operation showing
     */
    private TextView getPointView(){return pointView;}
    private TextView getEndPointView(){return endPointView;}

    /**
     * Sets and gets current lock for displaying locks.
     *
     * @param currentLock - the current lock.
     */
    private void setCurrentLock(Lock currentLock) {this.currentLock = currentLock;}
    private Lock getCurrentLock() {return currentLock;}

    /**
     * Setting for developers to decide if the default views will be shown when the pattern is displaying.
     * This allows developers to set their own views to display pattern, this is done by setting a PatternDisplayListener
     * and using the Lock object's points to set as the X and Y of the view you want to use to display the pattern.
     *
     * @param show - whether the pattern views will be shown or not.
     */
    public void showPatternViews(boolean show){
        this.showPatternViews = show;
    }



    /*************************************** USER SETTINGS ********************************************/

    /**
     * Sets the lock bounds for a valid touch.
     * Works as a circle's radius from the point in reference.
     * Used when comparing locks ( Lock.equals() )
     *
     * The higher the the bounds, the less security.
     *
     * @param lockBounds - the "radius" of the circle being calculated.
     */
    public static void setValidLockBounds(float lockBounds){
        String setting =  Utils.getString(R.string.valid_lock_bounds);
        Log.i(LOG_TAG, "SETTING VALID LOCK BOUNDS --- " + lockBounds);
        Utils.saveToSharedPrefsString(setting, Float.toString(lockBounds));
        VALID_TOUCH_BOUNDS = lockBounds;
    }

    public static float getValidLockBounds(){
        float vlb = LockUtils.getNumericalSetting(R.string.valid_lock_bounds);
        Log.i(LOG_TAG, "RETRIEVING VALID LOCK BOUNDS --- " + vlb);
        VALID_TOUCH_BOUNDS = (vlb == 0) ? VALID_TOUCH_BOUNDS : vlb;
        return VALID_TOUCH_BOUNDS;
    }

    public static void setVibrateOnTouch(boolean vibrate){
        Log.i(LOG_TAG, "SETTING VIBRATE ON TOUCH --- " + vibrate);
        String setting = Utils.getString(R.string.vibrate_on_touch);
        Utils.saveToSharedPrefsString(setting, Boolean.toString(vibrate));
        VIBRATE_ON_TOUCH = vibrate;
    }

    public static boolean getVibrateOnTouch(){
        VIBRATE_ON_TOUCH = LockUtils.getSetting(R.string.vibrate_on_touch);
        Log.i(LOG_TAG, "RETRIEVING VIBRATE ON TOUCH --- " + VIBRATE_ON_TOUCH);
        return VIBRATE_ON_TOUCH;
    }

    /**
     * Setting number for home many seconds between each lock sequence action.
     * Works Like a buffer, the higher the seconds, the less security.
     *
     * @param seconds - milliseconds for the buffer.
     */
    public static void setSecondsBetweenTaps(float seconds){
        Log.i(LOG_TAG, "SETTING SECONDS BETWEEN TAPS --- " + seconds);
        String setting = Utils.getString(R.string.seconds_between_taps);
        Utils.saveToSharedPrefsString(setting, Float.toString(seconds));
        SECONDS_BETWEEN_TAPS = seconds;
    }

    public static float getSecondsBetweenTaps(){
        float sbt = LockUtils.getNumericalSetting(R.string.seconds_between_taps);
        SECONDS_BETWEEN_TAPS = (sbt == 0) ? SECONDS_BETWEEN_TAPS : sbt;
        Log.i(LOG_TAG, "RETRIEVING SECONDS BETWEEN TAPS --- " + SECONDS_BETWEEN_TAPS);
        return SECONDS_BETWEEN_TAPS;
    }


    /*********************************** END OF SETTINGS ********************************************/

    public static float getMinimumLockBounds() {
        return MINIMUM_LOCK_BOUNDS;
    }

    public static float getMaximumLockBounds() {
        return MAXIMUM_LOCK_BOUNDS;
    }


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


    public void setCallingActivity(Activity activity){this.callingActivity = activity;}
    public Activity getCallingActivity(){
        return callingActivity;
    }

    //Timer to track if user is performing a hold.
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            holdSeconds++;
            if(holdSeconds == TAP_BOUNDS) {
                Log.i(LOG_TAG, "...Holding...");
                userIsHolding = true;
                onHoldPerformed();
                vibrateIfActive();
            }
            timerHandler.postDelayed(this, 1);
        }
    };

    Runnable displayRunnable = new Runnable() {

        @Override
        public void run() {
            animationSeconds++;

            ANIMATION_DISPLAY_SECONDS = (getCurrentLock().getType().equals(Lock.HOLD)) ? 3 : 2;

            if(animationSeconds == ANIMATION_DISPLAY_SECONDS){
                animationSeconds = 0;
                displayHandler.removeCallbacks(displayRunnable);
                getPointView().setVisibility(View.GONE);

                if(getEndPointView() != null){
                    getEndPointView().setVisibility(View.GONE);
                }

                if(getCurrentLock().getNextLock() != null){
                    showLockNextLock(getCurrentLock().getNextLock());
                }
                else{

                    if(patternDisplayListener != null){
                        patternDisplayListener.onEnd();
                    }

                    Log.i(LOG_TAG, "End of sequence");
                    setParent(null);
                }
                return;
            }

            displayHandler.postDelayed(this, 1000);
        }
    };
}
