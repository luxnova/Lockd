package io.wallfly.lockdapp.lockutils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hmkcode.android.recyclerview.R;


/**
 * Created by JoshuaWilliams on 5/9/15.
 *
 * Base Lock class for the different types of locks.
 *
 * STRATEGY DESIGN PATTERN
 */
public class Lock {
    private static final String LOG_TAG = "Lock";

    String type;
    float[] point;
    boolean isTimed = true;
    int sequenceNumber;
    Lock currentLock;
    Lock nextLock;
    int holdSeconds = 2;

    private int animationSeconds = 0;


    RelativeLayout parent;
    Context context;
    TextView pointView;
    TextView endPointView;

    Handler timerHandler = new Handler();

    /*********************************** Lock Attribute Methods *********************************************/

    public void setType(String type) {this.type = type;}

    public String getType() {return type;}

    public void setPoint(float[] point) {this.point = point;}

    public float[] getPoint() {return point;}

    public void setxCoord(float xCoord) {this.point[0] = xCoord;}

    public float getxCoord() {return this.point[0];}

    public void setYCoord(float yCoord) {this.point[1] = yCoord;}

    public float getYCoord() {return this.point[1];}

    public void setTimed(boolean isTimed) {this.isTimed = isTimed;}

    public boolean isTimed() {return isTimed;}

    public void setSequenceNumber(int sequenceNumber) {this.sequenceNumber = sequenceNumber;}

    public int getSequenceNumber() { return sequenceNumber;  }

    public void setHoldSeconds(int holdSeconds) { this.holdSeconds = holdSeconds; }

    public int getHoldSeconds() { return holdSeconds; }

    public void setNextLock(Lock lock){this.nextLock = lock;}

    public Lock getNextLock(){return nextLock;}

    /****************************************************************************************************/


    /**
     * Prints the lock data in a readable format.
     *
     * @return - returns the string of readable lock data.
     */
    public String print(){
        String data = getType() + " Sequence Number - " + getSequenceNumber() + " isTimed - " + isTimed()
                + " Point (" + getxCoord() + ", " + getYCoord() + ")" + " Seconds - " + getHoldSeconds();
        Log.i("Lock", data);
        return data;
    }

    /**
     * Turns lock data into an array of data and joins it by a comma to be easily parsed.
     *
     * @return - Lock data array string.
     */
    @Override
    public String toString() {
        return getSequenceNumber() + " " + getType() + " " + Double.toString(getxCoord()) + " "
        + Double.toString(getYCoord()) + " " + Integer.toString(getHoldSeconds());
    }

    /**
     * Reads lock data from a string (string must be from lock.toString() or contain appropriate data)
     * and creates the appropriate lock.
     *
     * @param lockData - the lock.toString() data.
     * @return - the lock created from the data.
     */
    public static Lock parseLock(String lockData){
        Log.i(LOG_TAG, lockData);
        String[] lockArray = lockData.split(("\\s+"));

        Lock lock = null;
        if(isValidLock(lockArray)) {
            int sequenceNumber = Integer.parseInt(lockArray[0]);
            String type = lockArray[1];
            float[] point = new float[]{Float.parseFloat(lockArray[2]), Float.parseFloat(lockArray[3])};

            if (type.equals("D")) {
                float[] endPoint = new float[]{Float.parseFloat(lockArray[4]), Float.parseFloat(lockArray[5])};
                lock = new DragLock(point, endPoint, sequenceNumber);
            } else if (type.equals("H")) {
                float seconds = Float.parseFloat(lockArray[4]);
                lock = new HoldLock(seconds, point, sequenceNumber);
            } else {
                lock = new TapLock(point, sequenceNumber);
            }
        }
        return lock;
    }

    private static boolean isValidLock(String[] lockArray) {
        try{
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(lockArray[0]);
            //noinspection ResultOfMethodCallIgnored
            Float.parseFloat(lockArray[2]);
            //noinspection ResultOfMethodCallIgnored
            Float.parseFloat(lockArray[3]);

            if(lockArray[1].equals("D") || lockArray[1].equals("H") || lockArray[1].equals("T")){
                if(lockArray[1].equals("D")){
                    //noinspection ResultOfMethodCallIgnored
                    Float.parseFloat(lockArray[4]);
                    //noinspection ResultOfMethodCallIgnored
                    Float.parseFloat(lockArray[5]);
                }
                return true;
            }
        }catch (Exception e){
            Log.e(LOG_TAG, "Improper lock format.");
        }
        return false;
    }

    /**
     * Displays the lock sequence starting from the lock that the method was called on. That is, if the
     * method gets called on by the 3rd lock in the sequence, then the lock sequence will display from the 3rd
     * until the end of the combination. The sequence will be displayed on the parent layout.
     *
     * @param context - the context of the class calling the method on the lock.
     * @param parent  - the layout the lock sequence will be displayed on.
     */
    public void showLockSequence(Context context, RelativeLayout parent){
        setContext(context);
        setParent(parent);
        displayPoints(this);
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

        TextView pointView = Utils.getPointView(lock.getType(), lock.getxCoord(), lock.getYCoord());
        pointView.setVisibility(View.VISIBLE);
        Utils.setViewBackground(R.drawable.touch_point_circle, pointView);

        //TODO: Differentiate beginning of drag point from end of drag point.

        if(lock instanceof DragLock){
            TextView endPointView = Utils.getPointView(lock.getType(),
                    ((DragLock)lock).getEndXCoord() ,((DragLock)lock).getEndYCoord());
            Utils.setViewBackground(R.drawable.touch_point_circle, endPointView);

            endPointView.setVisibility(View.VISIBLE);
            getParent().addView(endPointView);
            setEndPointView(endPointView);
        }

        getParent().addView(pointView);
        setPointView(pointView);
        startAnimation();
    }


     Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            animationSeconds++;

            //Gets the first digit of the float value for the hold seconds.
            int firstDigit = Character.getNumericValue(String.valueOf(getCurrentLock().getHoldSeconds()).charAt(0));
            Log.i(LOG_TAG, animationSeconds + " | " + firstDigit);

            if(animationSeconds == firstDigit && firstDigit != 1|| firstDigit == 1 && animationSeconds == ++firstDigit){
                animationSeconds = 0;
                timerHandler.removeCallbacks(timerRunnable);
                getPointView().setVisibility(View.GONE);

                if(getEndPointView() != null){
                    getEndPointView().setVisibility(View.GONE);
                }

                if(getCurrentLock().getNextLock() != null){
                    showLockNextLock(getCurrentLock().getNextLock());
                }
                else{
                    Log.i(LOG_TAG, "End of sequence");
                    setContext(null);
                    setParent(null);
                }
                return;
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    private void startAnimation() {
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void setParent(RelativeLayout parent){this.parent = parent;}
    private RelativeLayout getParent(){return parent;}

    private void setContext(Context context){this.context = context;}
    private Context getContext(){return context;}

    private void setPointView(TextView pointView){this.pointView = pointView;}
    private void setEndPointView(TextView endPointView){this.endPointView = endPointView;}

    private TextView getPointView(){return pointView;}
    private TextView getEndPointView(){return endPointView;}

    private void setCurrentLock(Lock currentLock) {this.currentLock = currentLock;}

    private Lock getCurrentLock() {return currentLock;}

    /**
     * Tests to see if the lock is equal to another lock by testing it's attributes.
     * The test generally consists of 'if the distance of the user's lock point/points are are within the
     * original lock point/points radius based off of the user's valid lock bounds settings'.
     * Basically if the user is in the same general area of their original lock sequence's point.
     *
     * @param object - the lock object.
     * @return - if the lock is equal.
     */
    @Override
    public boolean equals(Object object){
        if((object instanceof Lock)){
            Lock lock = ((Lock)object);

            if(this instanceof DragLock && lock instanceof DragLock){
                double startPointDist = Utils.getDistance(((DragLock) lock).getStartPoint(), ((DragLock) this).getStartPoint());
                double endPointDist = Utils.getDistance(((DragLock) lock).getEndPoint(), ((DragLock) this).getEndPoint());

                return startPointDist <= (CustomLockBaseListener.getValidLockBounds() + 100) //Added lock validity padding for drag since drag can be kind of tricky
                        && endPointDist <= (CustomLockBaseListener.getValidLockBounds() + 100); //Added lock validity padding for drag since drag can be kind of tricky
            }
            else if(this instanceof TapLock && lock instanceof TapLock){
                Log.i(LOG_TAG, "Get Distance ---- " + Utils.getDistance(lock.getPoint(), getPoint()) + " " + CustomLockBaseListener.getValidLockBounds());
                return Utils.getDistance(lock.getPoint(), getPoint()) <= CustomLockBaseListener.getValidLockBounds();
            }
            else if(this instanceof HoldLock && lock instanceof HoldLock){
                //If the user's hold is generally between the buffers.
                return getHoldSeconds() >=((HoldLock)lock).getLowerBuffer()
                        && getHoldSeconds() <= ((HoldLock)lock).getUpperBuffer()
                        && Utils.getDistance(lock.getPoint(), getPoint()) <= CustomLockBaseListener.getValidLockBounds();
            }
        }
        return false;
    }
}
