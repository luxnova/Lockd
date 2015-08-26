package io.wallfly.lockdapp.lockutils;

import android.app.Activity;
import android.util.Log;

/**
 * Created by JoshuaWilliams on 6/13/15.
 *
 * Lockd listener for any layout that wants to implement.
 *
 * Uses State Design Pattern for simple implementation.
 *
 * @version 1.6
 */
public class LockdListener extends AbstractLockdBaseListener {

    private static final String LOG_TAG = "LockdListener";
    LockdBaseListener lockState;
    LockdBaseListener createLockState;
    LockdBaseListener compareLockState;


    /**
     * Constructor for creating a lockd listener with your own compare and create lock listener.
     * Allows developer to create their own alogrithms for comparing and creating locks using Lockd Listener.
     *
     * @param callingActivity - the context of the lock.
     * @param createLockListener - The base lockd listener implementation for when the user creates a lock pattern.
     * @param compareLockListener - the base lockd listener implementation for when the user is comparing. testing or using their saved lock pattern.
     */
    public LockdListener(Activity callingActivity, LockdBaseListener createLockListener,
                         LockdBaseListener compareLockListener){
        createLockState = createLockListener;
        compareLockState = compareLockListener;

        setCallingActivity(callingActivity);
        setContext(callingActivity);

        setCreatingLock(createLockListener != null);
    }

    /**
     * Sets whether the user is creating a lock or not. Changes the state
     * based off of if the user is or is not.
     *
     * @param creatingLock - whether the user is creating a lock or not.
     */
    @Override
    public void setCreatingLock(boolean creatingLock){
        //sets the variable in the super class
        super.setCreatingLock(creatingLock);
        //Changes the state.
        lockState = (creatingLock) ? createLockState : compareLockState;

        Log.i(LOG_TAG, "Creating lock ------ " + creatingLock);
    }

    @Override
    public void onHoldPerformed() {
        lockState.onHoldPerformed();
    }


    @Override
    public void captureTouch(float[] point, int sequenceNumber) {
        Log.i(LOG_TAG, "Sequence number ------ " + sequenceNumber);
        lockState.captureTouch(point, sequenceNumber);
    }

    @Override
    public void captureDrag(float[] startingPoint, float[] endingPoint, int sequenceNumber) {
        Log.i(LOG_TAG, "Sequence number ------ " + sequenceNumber);
        lockState.captureDrag(startingPoint, endingPoint, sequenceNumber);
    }

    @Override
    public void captureHold(float[] point, float seconds, int sequenceNumber) {
        Log.i(LOG_TAG, "Sequence number ------ " + sequenceNumber);
        lockState.captureHold(point, seconds, sequenceNumber);
    }

    @Override
    public void onCaptureStart() {
        lockState.onCaptureStart();
    }

    @Override
    public void onCaptureEnd() {
        lockState.onCaptureEnd();
    }

}
