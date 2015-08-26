package io.wallfly.lockdapp.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.hmkcode.android.recyclerview.R;
import com.pwittchen.weathericonview.library.WeatherIconView;

import java.util.ArrayList;
import java.util.List;

import io.wallfly.lockdapp.libs.HomeKeyLocker;
import io.wallfly.lockdapp.lockutils.DragLock;
import io.wallfly.lockdapp.lockutils.HoldLock;
import io.wallfly.lockdapp.lockutils.Lock;
import io.wallfly.lockdapp.lockutils.LockUtils;
import io.wallfly.lockdapp.lockutils.LockdBaseListener;
import io.wallfly.lockdapp.lockutils.LockdListener;
import io.wallfly.lockdapp.lockutils.TapLock;
import io.wallfly.lockdapp.lockutils.Utils;
import io.wallfly.lockdapp.servicesandreceivers.NotificationService;
import io.wallfly.lockdapp.weatherutils.UserLocationUtils;
import io.wallfly.lockdapp.weatherutils.WeatherInfo;
import io.wallfly.lockdapp.weatherutils.YahooWeather;
import io.wallfly.lockdapp.weatherutils.YahooWeatherExceptionListener;
import io.wallfly.lockdapp.weatherutils.YahooWeatherInfoListener;


/**
 * Created by JoshuaWilliams on 6/9/15.
 *
 * The Lock Screen Activity
 *
 * @version 1.0
 *
 *
 * TODO: Disable the soft home keys for phone.
 * TODO: Add Notification Recycler View.
 *
 */
public class LockScreenActivity extends Activity implements YahooWeatherInfoListener,
        YahooWeatherExceptionListener{

    private static boolean isShowing = false;
    private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, true);
    private static String LOG_TAG = "LockScreenActivity";
    private TextView tempTV;
    private TextView incorrectLockTV;
    private static LinearLayout notificationLayout;
    private WeatherIconView weatherIconView;
    private LockdListener lockdListener;
    private Handler timerHandler = new Handler();
    private Handler finishHandler = new Handler();
    private int seconds = 0;
    private int finishSeconds = 0;
    private double maxSeconds = 1;
    private int backDoorCount = 0;
    private static final int VISIBLE_NOTIFICATIONS = 4;
    private HomeKeyLocker homeKeyLocker;
    private static List<Activity> activeActivities = new ArrayList<>();
    private WindowManager manager;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShowing = true;
        setUpSystemViews();
        setContentView(R.layout.activity_lock_screen);
        Utils.setContext(getApplicationContext());
        Utils.loadTypeFaces();
        UserLocationUtils.setActivity(this);
        initiate();
        addActivity(this);
    }

    /**
     * Sets up the status bar
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setUpSystemViews() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

        // this is to enable the notification to recieve touch events
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

        // Draws over status bar
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        Utils.setStatusBarColor(this, R.color.transparent);
        //manager.addView(view, localLayoutParams);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initiate() {
        Intent startIntent = new Intent(this, NotificationService.class);
        startService(startIntent);

        notificationLayout = (LinearLayout) findViewById(R.id.linear_layout);
        addNotifications(this);
        lockHomeButton();

        incorrectLockTV = (TextView) findViewById(R.id.incorrect_attempt_tv);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(incorrectLockTV.getWindowToken(), 0);

        maxSeconds = Double.parseDouble(Utils.getStringFromSharedPrefs(Utils.getString(R.string.seconds_between_taps)));

        lockdListener = new LockdListener(this, null, compareLockListener);
        RelativeLayout lockField = (RelativeLayout) findViewById(R.id.lock_field);
        lockField.setOnTouchListener(lockdListener);

        ImageView logo = (ImageView) findViewById(R.id.lockd_logo);
        weatherIconView = (WeatherIconView) findViewById(R.id.weather_icon);

        Utils.getImageLoaderWithConfig().displayImage("drawable://" + R.drawable.lockd_logo_hires,
                logo, Utils.getDisplayImageOptions());
        setUpClocks();

        Log.i(LOG_TAG, "Weather Showing Setting ----- " + LockUtils.getSetting(R.string.weather_showing));
        //If user has the weather setting enabled show the weather.
        //Since this is is a network request, run it in a background thread.
        if(LockUtils.getSetting(R.string.weather_showing)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setUpWeather();
                }
            }).start();
        }
        else{
            tempTV.setVisibility(View.GONE);
            weatherIconView.setVisibility(View.GONE);
        }


        Log.i(LOG_TAG, "LOCK COMBO SIZE --- " + LockUtils.getLockCombo().size());
        //If user does not have a lock pattern set, confirm the lock whenever the screen
        //is touched.
        if(!LockUtils.userHasLockPattern()){
            lockField.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    lockConfirmed();
                    return true;
                }
            });
        }
    }

    private static void addNotifications(Context context) {
        /*
        int i = 0;
        if(notificationLayout == null) return;
        notificationLayout.removeAllViews();
        List<LockScreenNotification> lockScreenNotifications = NotificationService.getNotifications();

        List<LockScreenNotification> copyList = new ArrayList<>();

        for(LockScreenNotification lockScreenNotification : lockScreenNotifications){
            copyList.add(lockScreenNotification);
        }


            for (LockScreenNotification lockScreenNotification : copyList) {
                Log.i(LOG_TAG, "Notification ------ " + lockScreenNotification.toString());

                View view = LayoutInflater.from(context).inflate(R.layout.notification_view, null);
                TextView appNameTV = (TextView) view.findViewById(R.id.appNameTV);
                TextView detailsTV = (TextView) view.findViewById(R.id.detailsTV);
                ImageView icon = (ImageView) view.findViewById(R.id.iconIV);

                appNameTV.setText(lockScreenNotification.getTitle());
                detailsTV.setText(lockScreenNotification.getText());
                icon.setImageDrawable(Utils.getDrawableFromApp(lockScreenNotification.getPackageName(), lockScreenNotification.getIcon()));

                if (i < VISIBLE_NOTIFICATIONS) {
                    notificationLayout.addView(view);
                }
                i++;
            }
            */
    }

    /**
     * Allow user to access their device.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void lockConfirmed() {
        DevicePolicyManager mgr = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mgr.resetPassword("", 0);
        homeKeyLocker.unlock();
        Utils.saveToSharedPrefsString(getString(R.string.launcher_activity), "1");
        clearAllActivities();
        finishHandler.postDelayed(finishRunnable, 1);
        isShowing = false;
    }

    /**
     * Sets up the TextClock views.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setUpClocks() {
        TextClock timeTV = (TextClock) findViewById(R.id.timer_clock_tv);
        TextClock merideimTV = (TextClock) findViewById(R.id.meridiem_tv);

        //Is the clock 24 hours?
        Log.i(LOG_TAG, "24 Hour Clock ---- " + LockUtils.getSetting(R.string.twenty_four_hour_clock));

        if(LockUtils.getSetting(R.string.twenty_four_hour_clock)){
            timeTV.setFormat24Hour("HH:mm");
            timeTV.setFormat12Hour(null);
            merideimTV.setVisibility(View.GONE);
        }
        else{
            timeTV.setFormat24Hour("h:mm");
        }
        
        TextClock dateTV = (TextClock) findViewById(R.id.date_tv);

        TextView protectedTV = (TextView) findViewById(R.id.protected_tv);
        tempTV = (TextView) findViewById(R.id.temp_tv);

        String temp = Utils.getStringFromSharedPrefs(getString(R.string.last_temp));

        if(!temp.isEmpty()) {
            tempTV.setText(temp);
            tempTV.setVisibility(View.VISIBLE);
        }

        TextClock[] textClockList = new TextClock[]{
                timeTV,
                merideimTV,
                dateTV
        };

        for(TextClock textClock : textClockList){
            textClock.setTypeface(Utils.getRobotoThin());
        }

        protectedTV.setTypeface(Utils.getRobotoThin());
    }


    /**
     * Sets up the weather views.
     */
    private void setUpWeather() {
        Log.i(LOG_TAG, "Set up weather ---- Start");
        mYahooWeather.setExceptionListener(LockScreenActivity.this);

        if(Utils.isGpsEnabled()){
            searchByGPS();
            Utils.storeLocation();
        }


        String cityName = Utils.getStringFromSharedPrefs(Utils.getString(R.string.city_name));
        if(!cityName.isEmpty()){
            Log.i(LOG_TAG, "Set up weather ---- City Name ---- " + cityName);
            searchByPlaceName(cityName);
        }

        if(!Utils.isGpsEnabled() && cityName.isEmpty()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tempTV.setTypeface(Utils.getRobotoThin());
                    tempTV.setText("Need location.");
                }
            });
        }
    }

    /**
     * Locks the home button
     */
    private void lockHomeButton(){
        //Lock the home button
        homeKeyLocker = new HomeKeyLocker();
        homeKeyLocker.lock(this);
    }

    /**
     * Starts the timer for the tracking the users touch.
     */
    private void startTimer(){
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    /**
     * Cancels the timer for the tracking the users touch.
     */
    private void cancelTimer(){
        timerHandler.removeCallbacks(timerRunnable);
    }

    /**
     * Resets the timer for the tracking the users touch.
     */
    public void resetTimer(){
        incorrectLockTV.setVisibility(View.INVISIBLE);
        cancelTimer();
        seconds = 0;
        startTimer();
    }


    //Timer to smoothly show animation.
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;

            if(seconds % 1000 == 0) Log.i(LOG_TAG, "Seconds ---- " + seconds/1000);

            if(seconds == maxSeconds){
                cancelTimer();
                showInCorrectLockTextView();
                lockdListener.setSequenceNumber(0);
                Utils.showToast("Out of time. Attempt cancelled.");
                Utils.vibrate();
                return;
            }
            timerHandler.postDelayed(this, 1);
        }
    };

    //This timer is for initiating the system lock (the user's back up lock) when they successfully put in their lock.
    //This prevents the the lock screen not showing if the user quickly taps the power button.
    Runnable finishRunnable = new Runnable() {
        @Override
        public void run() {
            finishSeconds++;
            Log.i(LOG_TAG, "Finish Seconds ----- " + finishSeconds);
            if(finishSeconds == 100){
                finishSeconds = 0;
                DevicePolicyManager mgr = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                mgr.resetPassword("0", 0);
                finishHandler.removeCallbacks(this);
                return;
            }
            finishHandler.postDelayed(this, 1);
        }
    };

    @Override
    public void onPause(){
        super.onPause();
        Log.i(LOG_TAG, "On Pause -------- ");
        cancelTimer();
    }

    /**
     * Searches for the weather by name of the location.
     *
     * @param location - the name of the place to get weather info about.
     */
    private void searchByPlaceName(String location) {
        mYahooWeather.setNeedDownloadIcons(true);
        mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
        mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.PLACE_NAME);
        mYahooWeather.queryYahooWeatherByPlaceName(getApplicationContext(), location, LockScreenActivity.this);
    }

    /**
     * Searches for the weather by GPS.
     */
    private void searchByGPS() {
        mYahooWeather.setNeedDownloadIcons(true);
        mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
        mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.GPS);
        mYahooWeather.queryYahooWeatherByGPS(getApplicationContext(), this);
    }

    /*********************************/
    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }


    /**
     * Adds an activity to the activity list so that it may be cancelled
     * when the lock is confirmed. The goal is to clear all active activities
     * so that the user does not see the Lockd menu screen when it finishes.
     *
     * @param activity - the activity to be added.
     */
    public static void addActivity(Activity activity){
        //Activity that is added is the faux launcher activity.
        activeActivities.add(activity);
    }

    /**
     * Finishes all activities in the list and then clears the list.
     * This prevents Lockd from popping up after the lock screen is dismissed.
     */
    private void clearAllActivities(){
        for(Activity activity : activeActivities){
            Log.i(LOG_TAG, "Finishing Activity ---- " + activity.getClass());
            activity.finish();
        }
        activeActivities.clear();
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    @Override
    public void onFailConnection(Exception e) {
        e.printStackTrace();
        Log.e(LOG_TAG, "FAIL CONNECTING");
    }

    @Override
    public void onFailParsing(Exception e) {
        e.printStackTrace();
        Log.e(LOG_TAG, "FAIL PARSING");
    }

    @Override
    public void onFailFindLocation(Exception e) {
        e.printStackTrace();
        Log.e(LOG_TAG, "FAIL FINDING");
    }

    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {
        if(weatherInfo == null){
            Log.i(LOG_TAG, "Weather null");
            tempTV.setText("Need location.");
            return;
        }
        weatherIconView.setIconResource(weatherInfo.getCurrentConditionIconURL());
        Log.i(LOG_TAG, "Weather ----- " + weatherInfo.getCurrentCode());

        tempTV.setTypeface(Utils.getRoboto());
        int temp = weatherInfo.getForecastInfo1().getForecastTempHigh() + 33;
        tempTV.setText(temp + "˚");
        Utils.saveToSharedPrefsString(getString(R.string.last_temp), Integer.toString(temp));
    }

    @Override
    public void onBackPressed(){
        Log.i(LOG_TAG, "Backdoor count ---- " + backDoorCount);
        if(backDoorCount < 10){
            backDoorCount++;
        }
        else{
            lockConfirmed();
        }
    }

    /**
     * Show the textview for an incorrect lock. This is called by the custom lock listener class.
     */
    public void showInCorrectLockTextView() {
        incorrectLockTV.setVisibility(View.VISIBLE);
    }

    LockdBaseListener compareLockListener = new LockdBaseListener() {
        @Override
        public void captureTouch(float[] point, int sequenceNumber) {
            resetTimer();

            LockUtils.roundPoints(point);
            Lock tapLock = new TapLock(point, sequenceNumber);
            checkLock(sequenceNumber, tapLock);
        }

        @Override
        public void captureDrag(float[] startingPoint, float[] endingPoint, int sequenceNumber) {
            resetTimer();

            LockUtils.roundPoints(startingPoint, endingPoint);
            Lock dragLock = new DragLock(startingPoint, endingPoint, sequenceNumber);
            checkLock(sequenceNumber, dragLock);
        }

        @Override
        public void captureHold(float[] point, float seconds, int sequenceNumber) {
            resetTimer();

            LockUtils.roundPoints(point);
            Lock holdLock = new HoldLock(seconds, point, sequenceNumber);
            checkLock(sequenceNumber, holdLock);
        }

        @Override
        public void onHoldPerformed() {

        }


        @Override
        public void onCaptureStart() {
            cancelTimer();
        }

        @Override
        public void onCaptureEnd() {
            startTimer();
        }
    };

    /**
     * Checks if the pattern is completed.
     *
     * @param sequenceNumber - the sequence number of the lock.
     * @param lock - the lock being checked.
     */
    private void checkLock(int sequenceNumber, Lock lock) {
        if(LockUtils.checkLock(sequenceNumber, lock)){
            if(LockUtils.getLockFromSequence(sequenceNumber + 1) == null){
                lockConfirmed();
            }
        }
    }

    public static boolean isShowing() {
        return isShowing;
    }

    public static BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            addNotifications(context);

            if(notificationLayout != null){
                Log.i(LOG_TAG, "Noti layout count ----- " + notificationLayout.getChildCount());
            }
        }
    };

}
