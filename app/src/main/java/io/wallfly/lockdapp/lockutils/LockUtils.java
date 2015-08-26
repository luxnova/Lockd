package io.wallfly.lockdapp.lockutils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hmkcode.android.recyclerview.R;

import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;

import io.wallfly.lockdapp.activities.EncryptedPhoneActivity;
import io.wallfly.lockdapp.servicesandreceivers.NotificationService;
import io.wallfly.lockdapp.servicesandreceivers.ScreenStatusMonitoringService;

/**
 * Created by JoshuaWilliams on 6/13/15.
 *
 * Class for the methods that handle lock operations.
 *
 * @version 1.0
 */
public class LockUtils extends Utils {

    private static final String LOG_TAG = "LockUtils";

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
        TextView pointView = new TextView(getContext());
        pointView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        pointView.setGravity(Gravity.CENTER);
        pointView.setText(type);
        pointView.setTextSize(50);
        pointView.setX(xCoord - 130); //Offset for the view to be in the center of the touch
        pointView.setY(yCoord - 150); //Offset for the view to be in the center of the touch
        return pointView;
    }


    /**
     * Prints out the lock combination in order.
     */
    public static void printLock() {
        String lockData = "a";
        int sequenceNumber = 0;
        while(!lockData.isEmpty()){
            sequenceNumber++;
            lockData = getStringFromSharedPrefs(Integer.toString(sequenceNumber));
            Log.i("PrintingLockSequence", lockData);
            lockData = getStringFromSharedPrefs(Integer.toString(sequenceNumber + 1));
        }
    }

    /**
     * Takes a number of points and rounds them to the nearest 2 decimal places.
     *
     * @param points - the points whose coordinates will be rounded.
     */
    public static void roundPoints(float[]...points){
        for(float[] point : points){
            point[0] = round(point[0], 2);
            point[1] = round(point[1], 2);
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
            lockData = getStringFromSharedPrefs(getContext().getString(R.string.lock_sequence) + Integer.toString(sequenceNumber));
            Log.i(LOG_TAG, "GET LOCK COMBO ----- From Shared Prefs" + lockData);
            Lock lock = getLock(lockData);
            Log.i(LOG_TAG, "GET LOCK COMBO ----- " + lockData);

            if(lock != null){
                lockCombo.add(getLock(lockData));
            }
            else{
                break;
            }
            lockData = getStringFromSharedPrefs(getContext().getString(R.string.lock_sequence) + Integer.toString(sequenceNumber + 1));
        }
        return lockCombo;
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
        Log.i(LOG_TAG, "LockFromSequence ---- " + getStringFromSharedPrefs(getContext().getString(R.string.lock_sequence) + Integer.toString(sequenceNumber)));
        return Lock.parseLock(getStringFromSharedPrefs(getContext().getString(R.string.lock_sequence) + Integer.toString(sequenceNumber)));
    }

    /**
     * Get the user's setting from the shared prefs.
     *
     * @return - boolean value for if the user's setting is active or not.
     */
    public static boolean getSetting(int stringResource){
        String setting = getStringFromSharedPrefs(getString(stringResource));
        Log.i(LOG_TAG, "Retrieving setting => " + getString(stringResource) + " Value - " + setting);
        return Boolean.parseBoolean(setting);
    }


    /**
     * Get the user's setting from the shared prefs. Only if the value is numerical
     *
     * @return - the numerical value of the setting.
     */
    public static float getNumericalSetting(int stringResource){
        String setting = getString(stringResource);
        Log.i(LOG_TAG, "Numerical Setting ---- " + setting + " Value ---- " + getStringFromSharedPrefs(setting) );
        return (setting.isEmpty()) ? 0 : Float.parseFloat(getStringFromSharedPrefs(setting));
    }

    /**
     * Checks if it's the user's first time opening the app. If it is then it initializes the settings.
     */
    public static boolean checkIfFirstOpen() {
        String initialOpenSetting = getString(R.string.initial_open);

        Log.i(LOG_TAG, "Checking first time ---- Key: " + initialOpenSetting + " Value: " + Utils.getStringFromSharedPrefs(initialOpenSetting));

        if(Utils.getStringFromSharedPrefs(initialOpenSetting).isEmpty()){
            Log.i(LOG_TAG, "User's first time opening the app. Initalizing settings.");
            String militaryClockSetting = getString(R.string.twenty_four_hour_clock);
            String weatherDisplaySetting = getString(R.string.weather_showing);
            String secondsBetweenTapsSetting =  getString(R.string.seconds_between_taps);
            String vibrateOnTouch = getString(R.string.vibrate_on_touch);
            String validTouchBounds = getString(R.string.valid_lock_bounds);
            String lockDialogDisabled = getString(R.string.lock_dialog_disabled);
            String launcher = getString(R.string.launcher_activity);

            saveToSharedPrefsString(militaryClockSetting, "false");
            saveToSharedPrefsString(weatherDisplaySetting, "false");
            saveToSharedPrefsString(initialOpenSetting, "false");
            saveToSharedPrefsString(secondsBetweenTapsSetting, Float.toString(1000));
            saveToSharedPrefsString(vibrateOnTouch, Boolean.toString(true));
            saveToSharedPrefsString(validTouchBounds, Float.toString(150));
            saveToSharedPrefsString(lockDialogDisabled, "false");
            saveToSharedPrefsString(launcher, "1");

            saveToSharedPrefsString(initialOpenSetting, "false");
            return true;
        }
        Log.i(LOG_TAG, "Settings already initialized.");
        return false;
    }

    /**
     * Erases the user's current lock pattern.
     */
    public static void eraseLockPattern(AbstractLockdBaseListener listener) {
        Log.i(LOG_TAG, " ------ Erasing Pattern ------ ");
        ArrayList<Lock> lockCombo = getLockCombo();
        for (Lock lock : lockCombo) {
            saveToSharedPrefsString(getString(R.string.lock_sequence) + lock.getSequenceNumber(), "");
        }
        if(listener != null){
            listener.setSequenceNumber(0);
        }

    }


    /**
     * Checks if the user has created lock pattern.
     * It does so by checking if there is a pattern in the shared prefs.
     *
     * @return - Whether the user already has a lock pattern.
     */
    public static boolean userHasLockPattern() {
        return getLockCombo().size() != 0;
    }


    /**
     * Enables the Lockd service that monitors the screen state
     */
    public static void enableLockd() {
        AbstractLockdBaseListener.setVibrateOnTouch(AbstractLockdBaseListener.getVibrateOnTouch());
        AbstractLockdBaseListener.setValidLockBounds(AbstractLockdBaseListener.getValidLockBounds());
        AbstractLockdBaseListener.setSecondsBetweenTaps(AbstractLockdBaseListener.getSecondsBetweenTaps());
        saveToSharedPrefsString(getString(R.string.enabled), "true");

        if(!isNotificationServiceRunning()){
            Intent intent = new Intent(getContext(), NotificationService.class);
            getContext().startService(intent);
        }

        Intent startIntent = new Intent(getContext(), ScreenStatusMonitoringService.class);
        getContext().startService(startIntent);
    }

    /**
     * Disables the Lockd service that monitors the screen state
     */
    public static void disableLockd() {
        eraseLockPattern(null);
        Intent serviceIntent = new Intent(getContext(), ScreenStatusMonitoringService.class);
        getContext().stopService(serviceIntent);
        serviceIntent = new Intent(getContext(), NotificationService.class);
        getContext().stopService(serviceIntent);

        if(userPhoneIsEncrypted()) {
            Intent i = new Intent(getContext(), EncryptedPhoneActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
            saveToSharedPrefsString(getString(R.string.backup_password), "0");
        }
        saveToSharedPrefsString(getString(R.string.enabled), "false");
    }

    /**
     * Stores a lock in the shared prefs and Logs the details of the lock.
     *
     * @param lock - The lock being stored. Drag, Tap or Hold.
     */
    public static void storeLock(Lock lock){
        saveToSharedPrefsString(getContext().getString(R.string.lock_sequence) + Integer.toString(lock.getSequenceNumber()), lock.toString());
        Log.i("CapturingLock", lock.toString());
        LockUtils.printLock();
        //Toast.makeText(getContext(), lock.print(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks to see if the lock passed matches the appropriate lock in the sequence.
     *
     * @param sequenceNumber - The number the user is currently on in the saved lock sequence.
     * @param lock - the type of lock passed.
     */
    public static boolean checkLock(int sequenceNumber, Lock lock) {
        Log.i(LOG_TAG, "Check Lock " + sequenceNumber);
        Lock sequenceLock = LockUtils.getLockFromSequence(sequenceNumber);

        if(sequenceLock != null && sequenceLock.equals(lock)){
            Log.i(LOG_TAG, "Check Lock " + sequenceLock.toString());
            return true;
        }
        //Toast.makeText(getContext(), "Incorrect Operation", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * Checks if lockd is active by checking if it Lockd is device admin
     *
     * @return - if Lockd is active admin.
     */
    public static boolean isLockdActiveAdmin(){
        ComponentName cn = new ComponentName(getContext(), AdminReceiver.class);
        DevicePolicyManager mgr=(DevicePolicyManager)getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        return mgr.isAdminActive(cn);
    }


    /**
     * Checks if lockd is enabled.
     *
     * @return - if Lockd is enabled (as in the user wants to use it)
     */
    public static boolean isLockdEnabled(){
        return getSetting(R.string.enabled);
    }


    /**
     * Checks to see if the user has a back up password set.
     *
     * @return - if the backup password save location is not empty
     */
    public static boolean userHasBackupPassword() {
        String backupPassword = getStringFromSharedPrefs(getString(R.string.backup_password));
        return !backupPassword.isEmpty() && !backupPassword.equals("0");
    }

    /**
     * Checks to see if the user's back up password save location is empty.
     *
     * @return - whether the user has a password or not.
     */
    public static boolean backupPasswordDialogDisabled() {
        String dialogPreference = getStringFromSharedPrefs(getString(R.string.backup_password_dialog_disabled));
        return !dialogPreference.isEmpty();
    }


    /**
     * Checks to see if the user has enabled Lockd to listen for notificaitons.
     *
     * @return - whether the user has enabled Lockd to listen for notifications.
     */
    public static boolean isNotificationServiceRunning() {
        ContentResolver contentResolver = getContext().getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getContext().getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }

    /**
     * Goes to the user's phone lock screen settings.
     */
    public static void goToUsersLockSettings() {
        Intent intent = new Intent("android.app.action.SET_NEW_PASSWORD");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    /**
     * An encrypted phone is unable to have no password. Tries to set the password to no password
     * then checks the result to see if it is successfully set.
     *
     * @return - whether the password was successfully set or not.
     */
    public static boolean userPhoneIsEncrypted(){
        DevicePolicyManager mgr = (DevicePolicyManager) getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        return BooleanUtils.isFalse(mgr.resetPassword("", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY));
    }
}
