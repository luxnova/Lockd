package io.wallfly.lockdapp.lockutils;

/**
 * Created by JoshuaWilliams on 5/9/15.
 *
 * Creation of a Tap Lock
 */
public class TapLock extends Lock {

    public TapLock(double[] point, int sequenceNumber){
        setType('T');
        setTimed(false);
        this.point = point;
        this.sequenceNumber = sequenceNumber;
    }
    public TapLock(){
        setType('T');
        setTimed(false);
    }
}
