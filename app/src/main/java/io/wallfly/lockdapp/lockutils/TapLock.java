package io.wallfly.lockdapp.lockutils;

/**
 * Created by JoshuaWilliams on 5/9/15.
 *
 * Creation of a Tap Lock
 *
 * @version 1.0
 */
public class TapLock extends Lock {

    public TapLock(float[] point, int sequenceNumber){
        setType("T");
        setTimed(false);
        setPoint(point);
        setSequenceNumber(sequenceNumber);
    }
    public TapLock(){
        setType("T");
        setTimed(false);
    }

}
