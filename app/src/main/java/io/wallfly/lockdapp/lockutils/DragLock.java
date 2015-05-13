package io.wallfly.lockdapp.lockutils;

import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by JoshuaWilliams on 5/9/15.
 *
 * Creation of a Drag Lock.
 */
public class DragLock extends Lock {
    double[] endPoint;

    public DragLock(double[] startPoint, double[] endPoint, int sequenceNumber){
        setType("D");
        setTimed(false);
        this.point = startPoint;
        this.endPoint = endPoint;
        this.sequenceNumber = sequenceNumber;
    }

    public DragLock(){
        setType("D");
        setTimed(false);
    }



    public void setEndPoint(double[] endPoint){
        this.endPoint = endPoint;
    }


    public double[] getEndPoint() {
        return endPoint;
    }


    public double getEndXCoord(){
        return endPoint[0];
    }


    public double getEndYCoord(){
        return endPoint[1];
    }

    public void setStartPoint(double[] startPoint){
        this.point = startPoint;
    }

    public double[] getStartPoint() {
        return super.getPoint();
    }

    public double getStartXCoord() {
        return super.getxCoord();
    }


    public double getStartYCoord(){
        return this.point[1];
    }

    @Override
    public String print(){
        String data = getType() + " Sequence Number - " + getSequenceNumber() + " isTimed - " + isTimed()
                + " Start Point (" + getStartXCoord() + ", " + getStartYCoord() + ")" +
                " End Point (" + getEndXCoord() + ", " + getEndYCoord() + ")" + " Seconds - " + getSeconds();
        Log.i("Lock", data);
        return data;
    }

    @Override
    public String toString() {
        String[] lockData = new String[]{
                Integer.toString(getSequenceNumber()),
                getType(),
                Double.toString(getStartXCoord()),
                Double.toString(getStartYCoord()),
                Double.toString(getEndXCoord()),
                Double.toString(getEndYCoord())
        };

        TextUtils.join(",", lockData);
        return Arrays.toString(lockData);
    }
}
