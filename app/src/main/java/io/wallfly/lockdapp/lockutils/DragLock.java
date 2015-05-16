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
    float[] endPoint;

    public DragLock(float[] startPoint, float[] endPoint, int sequenceNumber){
        setType("D");
        setTimed(false);
        setStartPoint(startPoint);
        setEndPoint(endPoint);
        setSequenceNumber(sequenceNumber);
    }

    public DragLock(){
        setType("D");
        setTimed(false);
    }


    public void setEndPoint(float[] endPoint){
        this.endPoint = endPoint;
    }


    public float[] getEndPoint() {
        return endPoint;
    }


    public float getEndXCoord(){
        return endPoint[0];
    }


    public float getEndYCoord(){
        return endPoint[1];
    }

    public void setStartPoint(float[] startPoint){
        setPoint(startPoint);
    }

    public float[] getStartPoint() {
        return super.getPoint();
    }

    public float getStartXCoord() {
        return super.getxCoord();
    }


    public float getStartYCoord(){
        return super.getYCoord();
    }

    @Override
    public String print(){
        String data = getType() + " Sequence Number - " + getSequenceNumber() + " isTimed - " + isTimed()
                + " Start Point (" + getStartXCoord() + ", " + getStartYCoord() + ")" +
                " End Point (" + getEndXCoord() + ", " + getEndYCoord() + ")" + " Seconds - " + getHoldSeconds();
        Log.i("Lock", data);
        return data;
    }

    @Override
    public String toString() {
        return getSequenceNumber() + " " + getType() + " " + getStartXCoord() + " "
                + getStartYCoord() + " " + " " + getEndXCoord() + " " + getEndYCoord();
    }
}
