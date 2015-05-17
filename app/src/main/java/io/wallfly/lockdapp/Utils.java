package io.wallfly.lockdapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hmkcode.android.recyclerview.R;
import java.util.ArrayList;
import io.wallfly.lockdapp.lockutils.Lock;

/**
 * Created by JoshuaWilliams on 4/30/15.
 *
 * Utils class containing commonly used methods.
 */
public class Utils {
    private static Context context;
    private static final String LOG_TAG = "Utils";

    /**
     * Retrieves a string from the strings.xml.
     * Mainly for classes that do not have context.
     *
     * @param stringResource - the resource of the desired string.
     * @return - the string from the resource.
     */
    public static String getString(int stringResource){
        return getContext().getString(stringResource);
    }

    /**
     * Stores a string inside the user's shared preferences.
     *
     * @param code - the code of the string.
     * @param content - the string being saved.
     */
    public static void saveToSharedPrefsString(String code, String content) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(code, content);
        editor.apply();
    }

    /**
     * Retrieves a string from the shared preferences.
     * 
     * @param code - code of the string to retrieve from the shared prefs.
     * @return - the appropriate string.
     */
    public static String getStringFromSharedPrefs(String code) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(code, "");
    }

    /**
     * Erases all data from shared prefs.
     */
    public static void clearSharedPrefs(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Rounds a double value to the nearest number of passed decimal places.
     *
     * @param valueToRound - the value that will be rounded.
     * @param numberOfDecimalPlaces - The number of decimal places to round to.
     * @return - the rounded value.
     */
    public static float round(double valueToRound, int numberOfDecimalPlaces)
    {
        double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
        double interestedInZeroDPs = valueToRound * multipicationFactor;
        return (float) (Math.round(interestedInZeroDPs) / multipicationFactor);
    }

    /**
     * Returns the appropriate TextView with modifications based off of the type.
     * Used to get the visual for locks.
     *
     * @param type - type of lock
     * @param xCoord - x Coordinate of lock.
     * @param yCoord- y Coordinate of lock.
     * @return - the view that represents the lock's position on the screen.
     */
    public static TextView getPointView(String type, float xCoord, float yCoord){
        TextView pointView = new TextView(context);
        pointView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        pointView.setGravity(Gravity.CENTER);
        pointView.setText(type);
        pointView.setTextSize(50);
        pointView.setX(xCoord - 130); //Offset for the view to be in the center of the touch
        pointView.setY(yCoord - 140); //Offset for the view to be in the center of the touch

        return pointView;
    }

    /**
     * Sets the passed views to the drawable resource passed.
     *
     * @param drawableResource - the drawable resource to be set on the views.
     * @param views - the views whose background will be set.
     */
    public static void setViewBackground(int drawableResource, View...views){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for(View view : views){
                view.setBackground(getContext().getDrawable(drawableResource));
            }
        }
        else{
            for(View view : views){
                view.setBackground(getContext().getResources().getDrawable(drawableResource));
            }
        }
    }

    /**
     * Retrieves a list of the lock combinations in order.
     *
     * @return - list of lock combination.
     */
    public static ArrayList<Lock> getLockCombo() {
        String lockData = "0";
        int sequenceNumber = 0;


        ArrayList<Lock> lockCombo = new ArrayList<>();
        while(!lockData.isEmpty()){
            sequenceNumber++;
            lockData = Utils.getStringFromSharedPrefs(getString(R.string.package_name) + sequenceNumber);
            Lock lock = getLock(lockData);
            if(lock != null){
                lockCombo.add(getLock(lockData));
            }
            else{
                break;
            }
            lockData = Utils.getStringFromSharedPrefs(getString(R.string.package_name) + (sequenceNumber+1));
        }
        return lockCombo;
    }

    /**
     * Gets the distance of one point from another.
     *
     * @param point1 - point being measured.
     * @param point2 - point being measured.
     * @return - The distance the points are from each other.
     */
    public static double getDistance(float[] point1, float[] point2) {
        return Math.sqrt(Math.pow(point1[0] - point2[0], 2) + Math.pow(point1[1] - point2[1], 2));
    }

    /**
     * Parses the string lock data into a Lock object of the three types.
     *
     * @param lockData - The string containing the essential information for a lock.
     * @return - the lock object from the data
     */
    public static Lock getLock(String lockData) {
        return Lock.parseLock(lockData);
    }

    /**
     * Returns the appropriate lock from the user's saved lock combo.
     *
     * @param sequenceNumber - The lock's sequence number.
     * @return - the appropriate lock.
     */
    public static Lock getLockFromSequence(int sequenceNumber) {
        return Lock.parseLock(getStringFromSharedPrefs(getString(R.string.package_name) + sequenceNumber));
    }

    public static void setContext(Context mContext){
        context = mContext;
    }

    public static Context getContext(){
        return context;
    }


}
