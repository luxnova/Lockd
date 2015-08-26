package io.wallfly.lockdapp.activities;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hmkcode.android.recyclerview.R;
import java.util.ArrayList;
import io.wallfly.lockdapp.lockutils.DragLock;
import io.wallfly.lockdapp.lockutils.HoldLock;
import io.wallfly.lockdapp.lockutils.LockUtils;
import io.wallfly.lockdapp.lockutils.LockdBaseListener;
import io.wallfly.lockdapp.lockutils.LockdListener;
import io.wallfly.lockdapp.lockutils.PatternDisplayListener;
import io.wallfly.lockdapp.lockutils.TapLock;
import io.wallfly.lockdapp.lockutils.Utils;
import io.wallfly.lockdapp.lockutils.Lock;

/**
 * Created by Joshua Williams
 *
 * Activity for allowing the use to create a lock pattern.
 *
 * @version 1.0
 */
public class CreateLockActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int FADE_ANIM_DURATION = 200;
    private RelativeLayout lockLayout;
    private static final String LOG_TAG = "CreateLockActivity";
    private boolean lockConfirmed = false;
    private boolean confirmingLock = false;
    private boolean sessionCancelled = false;
    private boolean later;
    private LockdListener lockdListener;
    private Handler timerHandler = new Handler();
    private Handler animationHandler = new Handler();
    private Handler holdHandler = new Handler();
    private int seconds = 5;
    private int animationSeconds = 0;
    private int holdSeconds = 0;
    private TextView timerTV, holdSecondsTV, sequenceNumberTV;
    private ImageView timerBorder;
    private AlertDialog alert;
    private boolean resumeCalledFirst = true;
    private boolean userIsHolding = false;
    private String email;
    private String password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lock);
        initiateActivity();
    }

    private void initiateActivity() {
        checkUtilsContext();
        timerBorder = (ImageView) findViewById(R.id.timer_border);
        timerBorder.setVisibility(View.VISIBLE);
        timerBorder.bringToFront();

        Utils.setStatusBarColor(this, R.color.theme_background_status_color);

        final TextView operationTV = (TextView) findViewById(R.id.operation_tv);
        final TextView dragStartTV = (TextView) findViewById(R.id.drag_start_tv);
        final TextView dragEndTV = (TextView) findViewById(R.id.drag_end_tv);
        sequenceNumberTV = (TextView) findViewById(R.id.sequence_num_tv);
        sequenceNumberTV.setVisibility(View.VISIBLE);
        holdSecondsTV = (TextView) findViewById(R.id.hold_seconds_tv);

        lockdListener = new LockdListener(this, createLockListener, compareLockListener);
        lockdListener.setPatternListener(new PatternDisplayListener() {
            @Override
            public void onStart() {
                Utils.fadeOutAnimation(timerBorder, FADE_ANIM_DURATION);
                Utils.fadeOutAnimation(timerTV, FADE_ANIM_DURATION);
            }


            @Override
            public void onDisplaying(Lock lock) {
                operationTV.setVisibility(View.VISIBLE);
                holdSecondsTV.setVisibility(View.GONE);
                sequenceNumberTV.setText("" + lock.getSequenceNumber());

                if(lock.getType().equals(Lock.DRAG)) {
                    operationTV.setText("Drag");
                    dragStartTV.setVisibility(View.VISIBLE);
                    dragEndTV.setVisibility(View.VISIBLE);
                }
                else if (lock.getType().equals(Lock.HOLD)){
                    operationTV.setText("Hold");
                    holdSecondsTV.setVisibility(View.VISIBLE);
                    holdSecondsTV.setText("Hold for " +
                            Integer.toString(lock.getHoldSeconds()).charAt(0) + " seconds."); // gets the first character of hold seconds millisecond value.
                    dragStartTV.setVisibility(View.GONE);
                    dragEndTV.setVisibility(View.GONE);
                }
                else{
                    operationTV.setText("Tap");
                    dragStartTV.setVisibility(View.GONE);
                    dragEndTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onEnd() {
                sequenceNumberTV.setText("");
                timerTV.setText("Tap to confirm");
                timerTV.setOnClickListener(CreateLockActivity.this);
                timerBorder.setOnClickListener(CreateLockActivity.this);
                Utils.fadeInAnimation(timerBorder, FADE_ANIM_DURATION);
                Utils.fadeInAnimation(timerTV, FADE_ANIM_DURATION);
                timerTV.setTextSize(20);

                operationTV.setVisibility(View.INVISIBLE);
                dragStartTV.setVisibility(View.GONE);
                dragEndTV.setVisibility(View.GONE);
                holdSecondsTV.setVisibility(View.GONE);
            }
        });


        lockLayout = (RelativeLayout) findViewById(R.id.lockLayout);
        timerTV = (TextView) findViewById(R.id.timer_text_view);
        timerTV.setText(Integer.toString(5));
        timerTV.bringToFront();

        if(LockUtils.getSetting(R.string.lock_dialog_disabled)){
            timerTV.setText("Tap to start");
            timerTV.setTextSize(20);
            timerTV.setOnClickListener(this);
            timerBorder.setOnClickListener(this);
        }
        else{
            timerTV.setTextSize(100);
        }

        lockLayout.bringToFront();

        animationHandler.postDelayed(animationRunnable, 1);
    }

    private void showStartDialog(){
        resumeCalledFirst = false;
        String message = "<font color='#6699FF'>" + "Tap " +"</font> <font color='brown'>, </font>" +
                "<font color='#6699FF'> hold </font>" +
                "</font> <font color='brown'>or</font>" +
                "</font> <font color='#6699FF'> drag </font>" +
                "</font> <font color='brown'> anywhere on the screen. When you are finished creating your lock pattern," +
                " wait until the timer is over then select the option" +
                " to show your pattern! </font>";


        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout customlayout = (RelativeLayout) inflater.inflate(R.layout.lockd_custom_alert_dialog_layout, null);

        TextView textView = (TextView) customlayout.findViewById(R.id.textView);
        textView.setText(Html.fromHtml(message), TextView.BufferType.SPANNABLE);
        textView.setTypeface(Utils.getRoboto());

        final CheckBox checkBox = (CheckBox) customlayout.findViewById(R.id.checkBox);

        alert = new AlertDialog.Builder(CreateLockActivity.this)
                .setTitle("Get Lockd!")
                .setView(customlayout)
                .setCancelable(false)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(checkBox.isChecked()){
                            Utils.saveToSharedPrefsString(getString(R.string.lock_dialog_disabled), "true");
                        }
                        startTimer();
                        lockLayout.bringToFront();
                        lockLayout.setOnTouchListener(lockdListener);
                    }
                })
                .show();
    }



    /**
     * On click listener method for the saveLockView.
     */
    public void saveLock(){
        displayUserLock();
        confirmLock();
    }

    /**
     * Make the user to confirm the lock inputed.
     */
    private void confirmLock() {
        lockdListener.setCreatingLock(false); //Allows the listener to act as a test rather than creation.
    }

    /**
     * Displays the user's lock combination that they want to save.
     **/
    private void displayUserLock() {
        ArrayList<Lock> lockCombo = LockUtils.getLockCombo();
        Log.i(LOG_TAG, "Display ---- ");

        if(lockCombo.size() == 0){
            showNoLockDialog();
            return;
        }
        showSequence();
    }


    private void showSequence() {
        lockdListener.showLockSequence(lockLayout);
    }

    private void checkUtilsContext() {
        if(Utils.getContext() == null){
            Utils.setContext(getApplicationContext());
        }
    }


    /**
     * Shows a dialog for no lock being saved.
     * */
    public void showNoLockDialog(){
        alert = new AlertDialog.Builder(CreateLockActivity.this)
                .setTitle("Create a Pattern")
                .setMessage(getString(R.string.no_lock_message))
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sequenceNumberTV.setText("");
                        alert.dismiss();
                        resetTimer();
                    }
                })
                .setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                })
                .setCancelable(false)
                .create();

        alert.show();
    }

    private void showFinishedDialog(){

        //Cancel any previous dialog
        if(alert != null){
            alert.dismiss();
        }

        alert =  new AlertDialog.Builder(CreateLockActivity.this)
                .setTitle("Finished?")
                .setMessage(getString(R.string.finished_with_pattern))
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        resetTimer();
                    }
                })
                .setNeutralButton(getString(R.string.start_over), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sequenceNumberTV.setText("");
                        Log.i(LOG_TAG, "Erase -- Start Over ------ Finished");
                        lockLayout.setOnTouchListener(lockdListener);
                        LockUtils.eraseLockPattern(lockdListener);
                        resetTimer();
                    }
                })
                .setPositiveButton(getString(R.string.show_pattern), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelTimer();
                        lockLayout.setOnTouchListener(null);
                        confirmingLock = true;
                        saveLock();
                    }
                })
                .setCancelable(false)
                .create();

        alert.show();
    }

    public void showCorrectPatternDialog() {
        cancelTimer();
        //Cancel any previous dialog
        if(alert != null){
            alert.dismiss();
        }
        alert =  new AlertDialog.Builder(CreateLockActivity.this)
                .setTitle("Correct Pattern!")
                .setMessage("Looks like you remembered your pattern! Are you sure you want this pattern?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (LockUtils.userHasBackupPassword()) {
                            onBackPressed();
                            return;
                        }

                        Log.i(LOG_TAG, "back up dialog ------- " + LockUtils.backupPasswordDialogDisabled() + " ");
                        if (!LockUtils.backupPasswordDialogDisabled()) {
                            showBackUpPasswordDialog();
                        }

                    }
                })
                .setNegativeButton("No, one more time please", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sequenceNumberTV.setText("");
                        LockUtils.eraseLockPattern(lockdListener);
                        lockLayout.setOnTouchListener(lockdListener);
                        lockdListener.setCreatingLock(true);
                        confirmingLock = false;
                        resetTimer();
                    }
                })
                .setCancelable(false)
                .create();

        alert.show();
    }


    private void showBackUpPasswordDialog() {
        String message = "<font color='brown'>" + "Almost finished! Set a backup password and the email" +
                " you want it sent to just in case you forget."
                + "</font> <font color='#CF7809'> <br><br>Not setting a backup password means you will have the " +
                "default password which is : 0.</font>";


        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout customlayout = (RelativeLayout) inflater.inflate(R.layout.backup_password_dialog, null);

        TextView textView = (TextView) customlayout.findViewById(R.id.textView);
        textView.setText(Html.fromHtml(message), TextView.BufferType.SPANNABLE);
        textView.setTypeface(Utils.getRoboto());

        final EditText passwordTV = (EditText) customlayout.findViewById(R.id.backupPasswordTV);
        final EditText emailTV = (EditText) customlayout.findViewById(R.id.emailTV);

        if(!Utils.getStringFromSharedPrefs(Utils.getString(R.string.email)).isEmpty()){
            emailTV.setText(Utils.getStringFromSharedPrefs(Utils.getString(R.string.email)));
        }


        final CheckBox checkBox = (CheckBox) customlayout.findViewById(R.id.checkBox);

        alert = new AlertDialog.Builder(CreateLockActivity.this)
                .setTitle("Set Backup Password")
                .setView(customlayout)
                .setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        later = false;

                        email = emailTV.getText().toString();
                        password = passwordTV.getText().toString();

                        if (checkBox.isChecked()) Utils.saveToSharedPrefsString(getString(R.string.backup_password_dialog_disabled), "true");

                    }
                })
                .setNegativeButton(getString(R.string.later), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        later = true;
                        Utils.saveToSharedPrefsString(getString(R.string.backup_password), "0");
                        if (checkBox.isChecked()) Utils.saveToSharedPrefsString(getString(R.string.backup_password_dialog_disabled), "true");

                        alert.dismiss();
                        onBackPressed();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (!later) {
                            if (checkEmail(email)) {
                                Utils.saveToSharedPrefsString(getString(R.string.email), emailTV.getText().toString());
                            } else {
                                Utils.showToast("Something seems wrong with your email address.");
                                alert.show();
                                return;
                            }

                            if (!password.isEmpty() && password.length() >= 4) {
                                alert.setOnDismissListener(null);
                                Utils.saveToSharedPrefsString(getString(R.string.backup_password), passwordTV.getText().toString());
                            } else {
                                Utils.showToast("Password needs to be more than four character if you are going to set it.");
                                alert.show();
                                return;
                            }
                        }

                        onBackPressed();
                    }
                })
                .setCancelable(false)
                .create();

        alert.show();
    }

    private void showIncorrectLockDialog() {
        alert = new AlertDialog.Builder(CreateLockActivity.this)
                .setTitle("Incorrect Lock")
                .setMessage(getString(R.string.incorrect_lock_pattern))
                .setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sequenceNumberTV.setText("");
                        alert.dismiss();
                        resetTimer();
                    }
                })
                .setNegativeButton(getString(R.string.start_over), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(LOG_TAG, "Erase -- Start Over ------ Incorrect");
                        sequenceNumberTV.setText("");
                        LockUtils.eraseLockPattern(lockdListener);
                        lockdListener.setCreatingLock(true);
                        lockLayout.setOnTouchListener(lockdListener);
                        confirmingLock = false;
                        resetTimer();
                    }
                })
                .setCancelable(false)
                .create();

        alert.show();
    }

    private void showSessionCancelledDialog() {
        alert = new AlertDialog.Builder(CreateLockActivity.this)
                .setTitle("Session Lost")
                .setMessage("Looks like you left while you were making a lock pattern. To keep your data safe, we erased the previous pattern. Just select \"Start over\" when you want to start again!")
                .setPositiveButton(getString(R.string.start_over), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sequenceNumberTV.setText("");
                        lockLayout.setOnTouchListener(lockdListener);
                        lockdListener.setCreatingLock(true);
                        alert.dismiss();
                        resetTimer();
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(LOG_TAG, "Erase --- Cancel ------ ");
                        LockUtils.eraseLockPattern(lockdListener);
                        confirmingLock = false;
                        onBackPressed();
                    }
                })
                .setCancelable(false)
                .create();

        alert.show();
    }


    public void setLockConfirmed(boolean lockConfirmed){
        this.lockConfirmed = lockConfirmed;
    }

    /**
     * checks if the email has the appropriate formatting.
     *
     * @param email - the email addresss being checked.
     * @return - if the email has appropriate formatting or not.
     */
    private boolean checkEmail(String email){
        email = email.trim();
        if(email.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$") && !email.isEmpty()
                && email.contains("@") && email.contains(".")){
            return true;
        }

        return false;
    }


    @Override
    public void onStart() {
        super.onStart();
        LockUtils.checkIfFirstOpen();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Utils.getContext() == null) Utils.setContext(getApplicationContext());
        if(lockdListener != null) lockdListener.setCallingActivity(this);

        if(sessionCancelled && !resumeCalledFirst) {
            showSessionCancelledDialog();
        }
    }



    //Timer to smoothly show animation.
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds--;
            Log.i(LOG_TAG, "Seconds ---- " + seconds);
            timerTV.setText(Integer.toString(seconds));
            if(seconds <= 0){
                cancelTimer();

                Log.i(LOG_TAG, "Confirming Lock --- " + confirmingLock + " Combo Size ---- " + LockUtils.getLockCombo().size());
                if(!confirmingLock && LockUtils.getLockCombo().size() == 0){
                    showNoLockDialog();
                    return;
                }
                else if(!confirmingLock) {
                    showFinishedDialog();
                }
                else if(confirmingLock){
                    showIncorrectLockDialog();
                }
                return;
            }
            timerHandler.postDelayed(this, 1000);
        }
    };



    //Timer to track if user is finished with their lock pattern.
    Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            animationSeconds++;
            if(animationSeconds == 10){
                Bundle extras = getIntent().getExtras();
                if(extras != null && extras.get(getString(R.string.calling_activity)).equals("A")){
                    if(LockUtils.getSetting(R.string.lock_dialog_disabled)){
                        startTimer();
                        timerTV.setVisibility(View.VISIBLE);
                        timerBorder.bringToFront();
                        timerTV.bringToFront();
                        lockLayout.bringToFront();
                        lockLayout.setOnTouchListener(lockdListener);
                    }
                    else{
                        timerTV.setVisibility(View.VISIBLE);
                        timerBorder.bringToFront();
                        timerTV.bringToFront();
                        lockLayout.bringToFront();
                        lockLayout.setOnTouchListener(lockdListener);
                        showStartDialog();
                    }
                }
                else {
                    Animation scaleAnim = AnimationUtils.loadAnimation(CreateLockActivity.this, R.anim.scale_anim);
                    scaleAnim.setDuration(200);
                    scaleAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            timerTV.setVisibility(View.VISIBLE);
                            timerBorder.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (LockUtils.getSetting(R.string.lock_dialog_disabled)) {
                            } else {
                                showStartDialog();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    timerBorder.startAnimation(scaleAnim);
                    timerTV.startAnimation(scaleAnim);
                }
                animationHandler.removeCallbacks(this);
                return;
            }
            animationHandler.postDelayed(this, 1);
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

    /**
     * Starts the timer for the tracking the users touch.
     */
    private void startTimer(){
        timerTV.setTextSize(100);
        timerTV.setText("5");
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
    private void resetTimer(){
        cancelTimer();
        seconds = 5;
        startTimer();
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        cancelTimer();
    }

    @Override
    public void onPause(){
        super.onPause();
        cancelTimer();
        if(alert != null){
            alert.dismiss();
        }
        confirmingLock = false;
        sessionCancelled = true;
        Log.i(LOG_TAG, "Erase -------- ON PAUSE ------ ");
        if(!lockConfirmed) LockUtils.eraseLockPattern(lockdListener);
    }

    @Override
    public void onBackPressed(){
        cancelTimer();
        ConfirmLockActivity.finishActivity();
        super.onBackPressed();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.timer_border:
                Log.i(LOG_TAG, "Timer Border Clicked ------ " + lockdListener.isCreatingLock());
                if(!lockdListener.isCreatingLock()){
                    confirmLock();
                }
                timerBorder.setOnClickListener(null);
                timerTV.setOnClickListener(null);
                lockLayout.setOnTouchListener(lockdListener);
                timerTV.setTextSize(100);
                resetTimer();
                break;

            case R.id.timer_text_view:
                timerBorder.performClick();
                break;
        }
    }

    LockdBaseListener createLockListener = new LockdBaseListener() {
        @Override
        public void captureTouch(float[] point, int sequenceNumber) {
            sequenceNumberTV.setText("" + sequenceNumber);
            resetTimer();

            LockUtils.roundPoints(point);
            Lock tapLock = new TapLock(point, sequenceNumber);
            LockUtils.storeLock(tapLock);
        }

        @Override
        public void captureDrag(float[] startingPoint, float[] endingPoint, int sequenceNumber) {
            sequenceNumberTV.setText("" + sequenceNumber);
            resetTimer();

            LockUtils.roundPoints(startingPoint, endingPoint);
            Lock dragLock = new DragLock(startingPoint, endingPoint, sequenceNumber);
            LockUtils.storeLock(dragLock);
        }

        @Override
        public void captureHold(float[] point, float seconds, int sequenceNumber) {
            sequenceNumberTV.setText("" + sequenceNumber);
            resetTimer();

            LockUtils.roundPoints(point);
            Lock holdLock = new HoldLock(seconds, point, sequenceNumber);
            LockUtils.storeLock(holdLock);
        }

        @Override
        public void onHoldPerformed() {
            cancelTimer();
            startHoldTimer();
        }

        @Override
        public void onCaptureStart() {
            cancelTimer();
        }

        @Override
        public void onCaptureEnd() {
            if(userIsHolding){
                cancelHoldTimer();
                userIsHolding = false;
            }
            startTimer();
        }
    };

    LockdBaseListener compareLockListener = new LockdBaseListener() {
        @Override
        public void captureTouch(float[] point, int sequenceNumber) {
            sequenceNumberTV.setText("" + sequenceNumber);
            resetTimer();

            LockUtils.roundPoints(point);
            Lock tapLock = new TapLock(point, sequenceNumber);
            checkIfPatternCompleted(sequenceNumber, tapLock);
        }

        @Override
        public void captureDrag(float[] startingPoint, float[] endingPoint, int sequenceNumber) {
            sequenceNumberTV.setText("" + sequenceNumber);
            resetTimer();

            LockUtils.roundPoints(startingPoint, endingPoint);
            Lock dragLock = new DragLock(startingPoint, endingPoint, sequenceNumber);
            checkIfPatternCompleted(sequenceNumber, dragLock);
        }

        @Override
        public void captureHold(float[] point, float seconds, int sequenceNumber) {
            sequenceNumberTV.setText("" + sequenceNumber);
            resetTimer();

            LockUtils.roundPoints(point);
            Lock holdLock = new HoldLock(seconds, point, sequenceNumber);
            checkIfPatternCompleted(sequenceNumber, holdLock);
        }

        @Override
        public void onHoldPerformed() {
            startHoldTimer();
        }

        @Override
        public void onCaptureStart() {
            cancelTimer();
        }

        @Override
        public void onCaptureEnd() {
            if(userIsHolding){
                cancelHoldTimer();
                userIsHolding = false;
            }
            startTimer();
        }
    };


    /**
     * Checks if the pattern is completed.
     *
     * @param sequenceNumber - the sequence number of the lock.
     * @param lock - the lock being checked.
     */
    private void checkIfPatternCompleted(int sequenceNumber, Lock lock) {
        if(LockUtils.checkLock(sequenceNumber, lock)){
            if(LockUtils.getLockFromSequence(sequenceNumber + 1) == null){
                showCorrectPatternDialog();
                setLockConfirmed(true);
                lockdListener.setSequenceNumber(0);
            }
        }
        else{
            if(!lockdListener.isCreatingLock()){
                sequenceNumberTV.setText("" + 1);
                lockdListener.setSequenceNumber(0); //resets the sequence number.
            }
        }
    }


}
