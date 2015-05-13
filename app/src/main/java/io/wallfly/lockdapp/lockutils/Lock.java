package io.wallfly.lockdapp.lockutils;

import android.util.Log;
import android.text.TextUtils;

import java.util.Arrays;


/**
 * Created by JoshuaWilliams on 5/9/15.
 *
 * Base Lock class for the different types of locks.
 *
 * STRATEGY DESIGN PATTERN
 */
public class Lock {
    String type;
    double[] point;
    boolean isTimed = true;
    float seconds;
    int sequenceNumber;
    Lock nextLock;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setPoint(double[] point) {
        this.point = point;
    }

    public double[] getPoint() {
        return point;
    }

    public void setxCoord(double xCoord) {
        this.point[0] = xCoord;
    }

    public double getxCoord() {
        return this.point[0];
    }

    public void setYCoord(double yCoord) {
        this.point[1] = yCoord;
    }

    public double getYCoord() {
        return this.point[1];
    }

    public void setTimed(boolean isTimed) {
        this.isTimed = isTimed;
    }

    public boolean isTimed() {
        return isTimed;
    }

    public void setSeconds(float seconds) {
        this.seconds = seconds;
    }

    public float getSeconds() {
        return seconds;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String print(){
        String data = getType() + " Sequence Number - " + getSequenceNumber() + " isTimed - " + isTimed()
                + " Point (" + getxCoord() + ", " + getYCoord() + ")" + " Seconds - " + getSeconds();
        Log.i("Lock", data);
        return data;
    }

    @Override
    public String toString() {
        String[] lockData = new String[]{Integer.toString(getSequenceNumber()), getType(),
                Double.toString(getxCoord()), Double.toString(getYCoord()), Float.toString(seconds)};

        TextUtils.join(",", lockData);
        return Arrays.toString(lockData);
    }

    public static Lock parseLock(String lockData){
        String[] lockArray = lockData.split(",");

        int sequenceNumber = Integer.parseInt(lockArray[0]);
        String type = lockArray[1];
        double[] point = new double[]{Double.parseDouble(lockArray[2]), Double.parseDouble(lockArray[3])};

        Lock lock;
        if(type.equals("D")){
            double[] endPoint = new double[]{Double.parseDouble(lockArray[4]), Double.parseDouble(lockArray[5])};
            lock = new DragLock(point, endPoint, sequenceNumber);
        }
        else if(type.equals("H")){
            float seconds = Float.parseFloat(lockArray[4]);
            lock = new HoldLock(seconds, point, sequenceNumber);
        }
        else{
            lock = new TapLock(point, sequenceNumber);
        }

        return lock;
    }

    public void setNextLock(Lock lock){this.nextLock = lock;}
    public Lock getNextLock(){return nextLock;}

}
