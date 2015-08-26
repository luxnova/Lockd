package io.wallfly.lockdapp.activities;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hmkcode.android.recyclerview.R;
import io.wallfly.lockdapp.lockutils.DragLock;
import io.wallfly.lockdapp.lockutils.HoldLock;
import io.wallfly.lockdapp.lockutils.Lock;
import io.wallfly.lockdapp.lockutils.LockUtils;
import io.wallfly.lockdapp.lockutils.LockdBaseListener;
import io.wallfly.lockdapp.lockutils.LockdListener;
import io.wallfly.lockdapp.lockutils.TapLock;
import io.wallfly.lockdapp.lockutils.Utils;

public class ConfirmLockActivity extends AppCompatActivity {
    private static final String LOG_TAG = "ConfirmLockActivity";

    private TextView holdSecondsTV;
    private LockdListener lockdListener;
    private RelativeLayout lockLayout;
    private boolean userIsHolding = false;
    private int holdSeconds;
    private Handler timerHandler = new Handler();
    private Handler holdHandler = new Handler();
    private int seconds = 0;
    private double maxSeconds;
    private TextView operationTV, sequenceNumberTV;
    private AlertDialog alert;
    private static ConfirmLockActivity thisInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_lock);
        initiateActivity();
    }

    private void initiateActivity() {
        checkUtilsContext();
        maxSeconds = Double.parseDouble(Utils.getStringFromSharedPrefs(Utils.getString(R.string.seconds_between_taps)));

        Utils.setStatusBarColor(this, R.color.theme_background_status_color);

        operationTV = (TextView) findViewById(R.id.operation_tv);
        operationTV.setVisibility(View.INVISIBLE);
        operationTV.setTextColor(Utils.getColor(R.color.red));
        operationTV.setText("Incorrect attempt");

        holdSecondsTV = (TextView) findViewById(R.id.hold_seconds_tv);
        sequenceNumberTV = (TextView) findViewById(R.id.sequence_num_tv);

        lockLayout = (RelativeLayout) findViewById(R.id.lockLayout);
        lockdListener = new LockdListener(this, null, compareLockListener);
        lockLayout.setOnTouchListener(lockdListener);

    }

    LockdBaseListener compareLockListener = new LockdBaseListener() {
        @Override
        public void captureTouch(float[] point, int sequenceNumber) {
            sequenceNumberTV.setText("" + sequenceNumber);
            resetTimer();

            LockUtils.roundPoints(point);
            Lock tapLock = new TapLock(point, sequenceNumber);
            checkLock(sequenceNumber, tapLock);
        }

        @Override
        public void captureDrag(float[] startingPoint, float[] endingPoint, int sequenceNumber) {
            sequenceNumberTV.setText("" + sequenceNumber);
            resetTimer();

            LockUtils.roundPoints(startingPoint, endingPoint);
            Lock dragLock = new DragLock(startingPoint, endingPoint, sequenceNumber);
            checkLock(sequenceNumber, dragLock);
        }

        @Override
        public void captureHold(float[] point, float seconds, int sequenceNumber) {
            sequenceNumberTV.setText("" + sequenceNumber);
            resetTimer();

            LockUtils.roundPoints(point);
            Lock holdLock = new HoldLock(seconds, point, sequenceNumber);
            checkLock(sequenceNumber, holdLock);
        }

        @Override
        public void onHoldPerformed() {
            startHoldTimer();
        }


        @Override
        public void onCaptureStart() {
            operationTV.setVisibility(View.INVISIBLE);
            cancelTimer();
        }

        @Override
        public void onCaptureEnd() {
            if(userIsHolding){
                cancelHoldTimer();
            }
            startTimer();
        }
    };

    private void showResetLockDialog() {
        String message = "<font color='brown'>" + "Seems like you already have a lock pattern stored. Do you want to create a new one?"
                + "</font> <font color='#CF7809'> <br><br>Selecting \"Yes\" will erase your current pattern. </font>";

        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout customlayout = (RelativeLayout) inflater.inflate(R.layout.lockd_custom_alert_dialog_layout, null);
        TextView textView = (TextView) customlayout.findViewById(R.id.textView);
        textView.setText(Html.fromHtml(message), TextView.BufferType.SPANNABLE);

        final CheckBox checkBox = (CheckBox) customlayout.findViewById(R.id.checkBox);
        checkBox.setVisibility(View.GONE);

        alert = new AlertDialog.Builder(this)
                .setTitle("Create new lock")
                .setView(customlayout)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alert.dismiss();
                        LockUtils.eraseLockPattern(null);
                        lockConfirmed();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                })
                .create();

        alert.show();
    }


    /**
     * Checks if the pattern is completed.
     *
     * @param sequenceNumber - the sequence number of the lock.
     * @param lock - the lock being checked.
     */
    private void checkLock(int sequenceNumber, Lock lock) {
        if(LockUtils.checkLock(sequenceNumber, lock)){
            if(LockUtils.getLockFromSequence(sequenceNumber + 1) == null){
                if(LockUtils.userHasLockPattern()){
                    sequenceNumberTV.setText("");
                    cancelTimer();
                    operationTV.setVisibility(View.INVISIBLE);
                    showResetLockDialog();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void lockConfirmed() {
        View sharedView = findViewById(R.id.view);
        View confirmLockTV = findViewById(R.id.confirmLockTV);
        Intent i = new Intent(getApplicationContext(), CreateLockActivity.class);
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create(sharedView, sharedView.getTransitionName()),
                Pair.create(confirmLockTV, confirmLockTV.getTransitionName()));
        i.putExtra(getString(R.string.calling_activity), "A");

        thisInstance = this;
        startActivity(i, transitionActivityOptions.toBundle());

    }

    private void checkUtilsContext() {
        if(Utils.getContext() == null){
            Utils.setContext(getApplicationContext());
        }
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
     * Starts the hold timer that tracks how long the user is peforming a hold.
     */
    private void startHoldTimer(){
        userIsHolding = true;
        holdHandler.postDelayed(holdRunnable, 1000);
        holdSeconds = 0;
    }

    /**
     * Cancels the hold timer.
     */
    private void cancelHoldTimer(){
        holdHandler.removeCallbacks(holdRunnable);
        holdSecondsTV.setVisibility(View.GONE);
        userIsHolding = false;
    }
    /**
     * Resets the timer for the tracking the users touch.
     */
    public void resetTimer(){
        operationTV.setVisibility(View.INVISIBLE);
        cancelTimer();
        seconds = 0;
        startTimer();
    }

    private void showInCorrectLockTextView() {
        operationTV.setVisibility(View.VISIBLE);
    }

    //Timer to smoothly show animation.
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;

            if(seconds % 1000 == 0) Log.i(LOG_TAG, "Seconds ---- " + seconds/1000);

            if(seconds == maxSeconds){
                sequenceNumberTV.setText("");
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



    Runnable holdRunnable = new Runnable() {
        @Override
        public void run() {
            holdSeconds++;
            holdSecondsTV.setText("Seconds: " + holdSeconds);
            if(holdSecondsTV.getVisibility() == View.GONE){
                holdSecondsTV.setVisibility(View.VISIBLE);
            }
            holdHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onBackPressed(){
        cancelTimer();
        super.onBackPressed();
    }

    public static void finishActivity() {
        if(thisInstance != null) thisInstance.finish();
    }
}
