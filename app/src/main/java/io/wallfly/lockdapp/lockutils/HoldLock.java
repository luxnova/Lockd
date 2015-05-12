package io.wallfly.lockdapp.lockutils;

/**
 * Created by JoshuaWilliams on 5/9/15.
 */
public class HoldLock extends Lock {
    public HoldLock(float seconds, double[] point, int sequenceNumber){
        setType('H');
        this.seconds = seconds;
        this.point = point;
        this.sequenceNumber = sequenceNumber;
    }
    public HoldLock(){
       setType('H');
    }
}
