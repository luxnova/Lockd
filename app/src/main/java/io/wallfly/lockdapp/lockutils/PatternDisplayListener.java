package io.wallfly.lockdapp.lockutils;

/**
 * Created by JoshuaWilliams on 6/7/15.
 *
 * The interface for allowing a listener for the custom lock display.
 *
 * @version 1.0
 */
public interface PatternDisplayListener {

    /**
     * When the lock pattern starts to be displayed.
     */
    void onStart();

    /**
     * When the lock pattern is displaying
     *
     * @param lock - the current lock being shown.
     */
    void onDisplaying(Lock lock);
    /**
     * When the lock pattern finished being displayed
     */
    void onEnd();
}
