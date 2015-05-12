package io.wallfly.lockdapp.lockutils;

import android.util.Log;
import android.text.TextUtils;

import org.w3c.dom.Text;

import java.util.Arrays;


/**
 * Created by JoshuaWilliams on 5/9/15.
 *
 * Base Lock class for the different types of locks.
 *
 * STRATEGY DESIGN PATTERN
 */
public class Lock {
    char type;
    double[] point;
    boolean isTimed = true;
    float seconds;
    int sequenceNumber;

    public void setType(char type) {
        this.type = type;
    }

    public char getType() {
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

    public void print(){
        Log.i("Lock", getType() + " Sequence Number - " + getSequenceNumber() + " isTimed - " + isTimed()
                + " Point (" + getxCoord() + ", " + getYCoord() + ")" + " Seconds - " + getSeconds());
    }

    @Override
    public String toString() {
        String[] lockData = new String[]{Integer.toString(getSequenceNumber()), Character.toString(getType()),
                Double.toString(getxCoord()), Double.toString(getYCoord()), Float.toString(seconds)};

        TextUtils.join(",",lockData);
        return Arrays.toString(lockData);
    }
}
