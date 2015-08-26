package io.wallfly.lockdapp.lockutils;

import android.util.Log;


/**
 * Created by JoshuaWilliams on 5/9/15.
 * <p/>
 * Base Lock class for the different types of locks.
 * <p/>
 * STRATEGY DESIGN PATTERN
 */

public class Lock {
    private static final String LOG_TAG = "Lock";

    public static final String DRAG = "D";
    public static final String HOLD = "H";
    public static final String TAP = "T";

    String type;
    float[] point;
    boolean isTimed = true;
    int sequenceNumber;
    Lock nextLock;
    int holdSeconds = 2;


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
        String holdSeconds = Integer.toString(getHoldSeconds());
        if(this instanceof HoldLock){
            holdSeconds = ((HoldLock)this).getLowerBuffer() + " " + ((HoldLock)this).getUpperBuffer();
        }
        return getSequenceNumber() + " " + getType() + " " + Double.toString(getxCoord()) + " "
        + Double.toString(getYCoord()) + " " + holdSeconds;
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
     * Hash code method to correctly identify the current lock.
     *
     * @return - the hashcode.
     */
    @Override
    public int hashCode(){
        return (getType().hashCode() * getSequenceNumber());
    }

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
        if(object != null && (object instanceof Lock)){
            Lock lock = ((Lock)object);

            if(this instanceof DragLock && lock instanceof DragLock){
                double startPointDist = Utils.getDistance(((DragLock) lock).getStartPoint(), ((DragLock) this).getStartPoint());
                double endPointDist = Utils.getDistance(((DragLock) lock).getEndPoint(), ((DragLock) this).getEndPoint());

                return startPointDist <= (AbstractLockdBaseListener.getValidLockBounds() + 100) //Added lock validity padding for drag since drag can be kind of tricky
                        && endPointDist <= (AbstractLockdBaseListener.getValidLockBounds() + 100); //Added lock validity padding for drag since drag can be kind of tricky
            }
            else if(this instanceof TapLock && lock instanceof TapLock){
                Log.i(LOG_TAG, "Get Distance ---- " + Utils.getDistance(lock.getPoint(), getPoint()) + " " + AbstractLockdBaseListener.getValidLockBounds());
                return Utils.getDistance(lock.getPoint(), getPoint()) <= AbstractLockdBaseListener.getValidLockBounds();
            }
            else if(this instanceof HoldLock && lock instanceof HoldLock){
                //If the user's hold is generally between the buffers.
                return getHoldSeconds() >=((HoldLock)lock).getLowerBuffer()
                        && getHoldSeconds() <= ((HoldLock)lock).getUpperBuffer()
                        && Utils.getDistance(lock.getPoint(), getPoint()) <= AbstractLockdBaseListener.getValidLockBounds();
            }
        }
        return false;
    }

}
