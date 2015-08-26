package io.wallfly.lockdapp.lockutils;

/**
 * Created by JoshuaWilliams on 6/14/15.
 *
 * The base interface for the listener for responding the the touch captures.
 *
 * @version 1.0
 */
public interface LockdBaseListener {

    /**
     * Method to save a simple tap on the screen.
     *
     * @param point - the x and y coordinates of the tap location on the screen.
     * @param sequenceNumber - The number in the lock sequence that the lock is currently.
     */
    void captureTouch(float[] point, int sequenceNumber);

    /**
     * Method to save a drag on the screen.
     * This works by capturing 2 points, the beginning point of the drag and the ending point of the drag.
     * The user can drag how ever and wherever they want, as long as they initially start at the beginning point
     * and end at the ending point.
     *
     * @param startingPoint - Where the user taps to begin the drag.
     * @param endingPoint - Where the user picks their finger up off the screen at.
     * @param sequenceNumber - The number in the lock sequence that the lock is currently.
     */
    void captureDrag(float[] startingPoint, float[] endingPoint, int sequenceNumber);

    /**
     * Method to capture a hold on the screen
     *
     * @param point - the x and y coordinates of the hold location on the screen.
     * @param seconds - how long the hold was performed.
     * @param sequenceNumber - The number in the lock sequence that the lock is currently.
     */
    void captureHold(float[] point, float seconds, int sequenceNumber);

    /**
     * Call back for when a hold is performed.
     */
    void onHoldPerformed();

    /**
     * Call back for when the Lock capture starts.
     */
    void onCaptureStart();

    /**
     * Call back for when the Lock capture ends.
     */
    void onCaptureEnd();
}
