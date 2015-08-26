package io.wallfly.lockdapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.hmkcode.android.recyclerview.R;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;

import io.wallfly.lockdapp.adaptersandlisteners.SettingsAdapter;
import io.wallfly.lockdapp.adaptersandlisteners.ViewGoneAnimationListener;
import io.wallfly.lockdapp.lockutils.AdminReceiver;
import io.wallfly.lockdapp.lockutils.LockUtils;
import io.wallfly.lockdapp.lockutils.Utils;
import io.wallfly.lockdapp.models.SettingsSelection;
import io.wallfly.lockdapp.servicesandreceivers.NotificationService;

/**
 * TODO: Add Change back up password.
 * TODO: Add Notification settings
 * TODO: Add Tutorials.
 */
public class MainActivity extends BaseActivity implements ObservableScrollViewCallbacks,
        View.OnLongClickListener, View.OnClickListener {
    private static final String LOG_TAG = "MainActivity";

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = true;
    private static final int ACTIVATION_REQUEST_CODE = 880;
    private static final int NOTI_ACTIVATION_REQUEST_CODE = 990;

    private View mToolbar;
    private ImageView background, disableLockButton, createLockView, enableLockButton;
    private View mOverlayView, mRecyclerViewBackground;
    private ObservableRecyclerView mRecyclerView;
    private TextView mTitleView;
    private int mActionBarSize, mFlexibleSpaceImageHeight,  mToolbarColor;
    private Handler timerHandler = new Handler();
    private ImageView logo;
    private AlertDialog alert;
    private int scrollBarColor;

    private DevicePolicyManager mgr;
    private ComponentName cn;
    private ArrayList<SettingsSelection> settingsSelections;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);
        }catch (RuntimeException re){
            re.printStackTrace();
            finish();
        }

        checkUtilsContext();
        Utils.loadTypeFaces();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setUpLayout();
        LockScreenActivity.addActivity(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUpLayout() {
        mgr = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        cn = new ComponentName(MainActivity.this, AdminReceiver.class);

        /*
        if(LockUtils.isLockdActiveAdmin() && LockUtils.isLockdEnabled()){
            mgr.resetPassword(Utils.getStringFromSharedPrefs(Utils.getString(R.string.backup_password)), 0);
        }
        */

        setViewBackGround();

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarSize();
        mToolbarColor = getResources().getColor(R.color.lockd_primary);
        scrollBarColor = getResources().getColor(R.color.lockd_status_bar);

        mRecyclerView = (ObservableRecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setScrollViewCallbacks(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(false);
        headerView = LayoutInflater.from(this).inflate(R.layout.recycler_header, null);
        headerView.post(new Runnable() {
            @Override
            public void run() {
                headerView.getLayoutParams().height = mFlexibleSpaceImageHeight;
            }
        });

        addSettings();

        if(Utils.getStringFromSharedPrefs(Utils.getString(R.string.backup_password)).isEmpty()){
            settingsSelections.remove(10);
        }
        mRecyclerView.setAdapter(new SettingsAdapter(this, settingsSelections, headerView));


        mToolbar = findViewById(R.id.toolbar);

        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }


        ImageLoader imageLoader = Utils.getImageLoaderWithConfig();

        logo = (ImageView) findViewById(R.id.logo);
        imageLoader.displayImage("drawable://" + R.drawable.lockd_logo_hires, logo, Utils.getDisplayImageOptions());

        mOverlayView = findViewById(R.id.overlay);


        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(getTitle());
        setTitle(null);


        // mRecyclerViewBackground makes RecyclerView's background except header view.
        mRecyclerViewBackground = findViewById(R.id.list_background);
        final View contentView = getWindow().getDecorView().findViewById(android.R.id.content);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                // mRecylcerViewBackground's should fill its parent vertically
                // but the height of the content view is 0 on 'onCreate'.
                // So we should get it with post().
                mRecyclerViewBackground.getLayoutParams().height = contentView.getHeight();
            }
        });

        //since you cannot programatically add a headerview to a recyclerview we added an empty view as the header
        // in the adapter and then are shifting the views OnCreateView to compensate
        final float scale = 1 + MAX_TEXT_SCALE_DELTA;
        mRecyclerViewBackground.post(new Runnable() {
            @Override
            public void run() {
                ViewHelper.setTranslationY(mRecyclerViewBackground, mFlexibleSpaceImageHeight);
            }
        });
        ViewHelper.setTranslationY(mOverlayView, mFlexibleSpaceImageHeight);
        mTitleView.post(new Runnable() {
            @Override
            public void run() {
                ViewHelper.setTranslationY(mTitleView, (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale));
                ViewHelper.setPivotX(mTitleView, 0);
                ViewHelper.setPivotY(mTitleView, 0);
                ViewHelper.setScaleX(mTitleView, scale);
                ViewHelper.setScaleY(mTitleView, scale);
            }
        });
        mTitleView.setTypeface(Utils.getRoboto());

        disableLockButton = (ImageView) findViewById(R.id.disable_lock_button);
        disableLockButton.setOnClickListener(this);

        createLockView = (ImageView) findViewById(R.id.create_lock_button);
        createLockView.setOnLongClickListener(this);
        createLockView.setOnClickListener(this);

        enableLockButton = (ImageView)findViewById(R.id.enable_lock_button);
        enableLockButton.setOnClickListener(this);
        enableLockButton.setOnLongClickListener(this);

        if(LockUtils.isLockdActiveAdmin()){
            createLockView.setVisibility(View.VISIBLE);
            createLockView.bringToFront();
            disableLockButton.setVisibility(View.INVISIBLE);
            enableLockButton.setVisibility(View.INVISIBLE);
            LockUtils.enableLockd();
        } else {
            createLockView.setVisibility(View.INVISIBLE);
            disableLockButton.setVisibility(View.INVISIBLE);
            enableLockButton.setVisibility(View.VISIBLE);
            enableLockButton.bringToFront();
        }
    }

    private void addSettings() {
        settingsSelections = new ArrayList<>();
        settingsSelections.add(new SettingsSelection());
        settingsSelections.add(new SettingsSelection("Clock", R.drawable.clock_icon));
        settingsSelections.add(new SettingsSelection("Weather", R.drawable.weather_icon));

        settingsSelections.add(new SettingsSelection("Vibrate on touch", R.drawable.vibrate_icon));
        settingsSelections.add(new SettingsSelection("Seconds between taps", R.drawable.seconds_icon));
        settingsSelections.add(new SettingsSelection("Valid touch radius", R.drawable.touch_icon));

        settingsSelections.add(new SettingsSelection("Rate", R.drawable.rate_icon));
        settingsSelections.add(new SettingsSelection("About", R.drawable.about_icon));
        settingsSelections.add(new SettingsSelection("Privacy policy", R.drawable.privacy_policy_icon));
        settingsSelections.add(new SettingsSelection("Help", R.drawable.help_icon));
        if(LockUtils.isLockdActiveAdmin() && LockUtils.userHasLockPattern() && LockUtils.userHasBackupPassword()){
            settingsSelections.add(new SettingsSelection("Change backup password", R.drawable.change_backup_password_icon));
        }
        settingsSelections.add(new SettingsSelection());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setViewBackGround() {
        background = (ImageView) findViewById(R.id.background);
        Bitmap bitmap = Utils.convertToBitmap(Utils.getWallpaper(), 100, 150);
        Utils.blurImage(bitmap);
        background.setImageBitmap(bitmap);
    }

    @Override
    public void onResume(){
        super.onResume();
        checkUtilsContext();
        setViewBackGround();
        if(LockUtils.isLockdActiveAdmin() && LockUtils.isLockdEnabled()){
            mgr.resetPassword(Utils.getStringFromSharedPrefs(Utils.getString(R.string.backup_password)), 0);
        }
        if(LockUtils.userHasLockPattern()){
            addSettings();
            mRecyclerView.setAdapter(new SettingsAdapter(this, settingsSelections, headerView));
        }

    }



    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(background, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(logo, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Translate list background
        ViewHelper.setTranslationY(mRecyclerViewBackground, Math.max(0, -scrollY + mFlexibleSpaceImageHeight));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle();
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);


        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        if (TOOLBAR_IS_STICKY) {
            titleTranslationY = Math.max(0, titleTranslationY);
        }
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);


        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, mToolbarColor));
                Utils.setStatusBarColor(this, ScrollUtils.getColorWithAlpha(1, scrollBarColor));
            } else {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, mToolbarColor));
            }
        } else {
            // Translate Toolbar
            if (scrollY < mFlexibleSpaceImageHeight) {
                ViewHelper.setTranslationY(mToolbar, 0);
            } else {
                ViewHelper.setTranslationY(mToolbar, -scrollY);
            }
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }
    }


    /**
     * Checks if Utitlites context is null.
     */
    private void checkUtilsContext() {
        if(Utils.getContext() == null) Utils.setContext(getApplicationContext());
        Utils.loadTypeFaces();
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "On start");
        if(Utils.getContext() == null){ Utils.setContext(getApplicationContext()); }
        LockUtils.checkIfFirstOpen();
        //initiateActivity();
    }


    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Animator anim = Utils.getAnimateRevealHide(disableLockButton, 1000, disableLockButton.getWidth() / 2, disableLockButton.getHeight() / 2);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    disableLockButton.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();
        }
    };


    private void notifyUserLockEnabled() {
        Utils.showToast(getString(R.string.lock_enabled));
        disableLockButton.bringToFront();
        disableLockButton.setVisibility(View.VISIBLE);
        Animator anim = Utils.getAnimRevealShow(disableLockButton, 400, disableLockButton.getWidth() / 2, disableLockButton.getHeight() / 2);
        anim.addListener(new ViewGoneAnimationListener(enableLockButton)); //Sets view to invisible at the end of animation.
        anim.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createLockView.bringToFront();
                createLockView.setVisibility(View.VISIBLE);
                Animator secondAnim = Utils.getAnimRevealShow(createLockView, 800, createLockView.getWidth() / 2, createLockView.getHeight() / 2);
                //No View Gone listener since the disable lock button needs to be in background,
                secondAnim.start();
            }
        }, 700);
    }


    private void showRemoveLockDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout customlayout = (RelativeLayout) inflater.inflate(R.layout.lockd_custom_alert_dialog_layout, null);
        TextView textView = (TextView) customlayout.findViewById(R.id.textView);
        textView.setText("Seems like you have a system lock enabled. Turn off your system lock to avoid having to unlock " +
                "your device twice.");

        final CheckBox checkBox = (CheckBox) customlayout.findViewById(R.id.checkBox);
        checkBox.setVisibility(View.GONE);

        alert = new AlertDialog.Builder(this)
                .setTitle("Disable System Lock")
                .setView(customlayout)
                .setPositiveButton("Disable lock", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alert.dismiss();
                        LockUtils.enableLockd();
                        notifyUserLockEnabled();
                        LockUtils.goToUsersLockSettings();
                    }
                })
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                        LockUtils.enableLockd();
                        notifyUserLockEnabled();
                    }
                })
                .create();

        alert.show();
    }


    private void showNotificationAccesssDialog() {
        alert = new AlertDialog.Builder(this)
                .setTitle("Notification Access")
                .setMessage("Give Lockd notification access so you can see notifications on your lock screen.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alert.dismiss();
                        final Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                        startActivityForResult(intent, NOTI_ACTIVATION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                })
                .create();

        alert.show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startCreateLockActivity(){
        Intent i = new Intent(MainActivity.this, CreateLockActivity.class);
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this,
                createLockView, createLockView.getTransitionName());
        startActivity(i, transitionActivityOptions.toBundle());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startConfirmLockActivity(){
        Intent i = new Intent(MainActivity.this, ConfirmLockActivity.class);
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this,
                createLockView, createLockView.getTransitionName());
        startActivity(i, transitionActivityOptions.toBundle());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.create_lock_button:
                if(LockUtils.userHasLockPattern()){
                    startConfirmLockActivity();
                    return;
                }
                startCreateLockActivity();
                break;

            case R.id.enable_lock_button:
                LockUtils.showToast(getString(R.string.enable_lock));
                break;

            case R.id.disable_lock_button:
                LockUtils.disableLockd();

                Utils.saveToSharedPrefsString(getString(R.string.enabled), "false");
                timerHandler.removeCallbacks(timerRunnable); //Cancels the timer.
                Utils.showToast(getString(R.string.lock_disabled));
                enableLockButton.bringToFront();
                enableLockButton.setVisibility(View.VISIBLE);
                Animator anim = Utils.getAnimRevealShow(enableLockButton, 400, enableLockButton.getWidth() / 2, enableLockButton.getHeight() / 2);
                view.setTag(createLockView);
                anim.addListener(new ViewGoneAnimationListener(view));
                anim.start();
                addSettings();
                mRecyclerView.setAdapter(new SettingsAdapter(MainActivity.this, settingsSelections, headerView));
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()){
            case R.id.enable_lock_button:
                if(BooleanUtils.isFalse(LockUtils.isLockdActiveAdmin())) {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            getString(R.string.device_admin_explanation));
                    startActivityForResult(intent, ACTIVATION_REQUEST_CODE);
                    return true;
                }

                if(Utils.isDeviceSecured()){
                    showRemoveLockDialog();
                    return true;
                }

                LockUtils.enableLockd();
                notifyUserLockEnabled();

                break;

            case R.id.create_lock_button:
                disableLockButton.bringToFront();
                Animator anim = Utils.getAnimRevealShow(disableLockButton, 400, disableLockButton.getWidth() / 2, disableLockButton.getHeight() / 2);
                anim.start();
                timerHandler.postDelayed(timerRunnable, 3000); //Starts timer to show disable button for 3 seconds (3000 ms)

                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(LOG_TAG, "Administration enabled!");
                    if(LockUtils.isNotificationServiceRunning()){
                        showNotificationAccesssDialog();
                    }
                    if(LockUtils.userPhoneIsEncrypted()){

                    } else {
                        LockUtils.enableLockd();
                    }
                    notifyUserLockEnabled();
                }
                else {
                    Log.i("DeviceAdminSample", "Administration enable FAILED!");
                }
                break;

            case NOTI_ACTIVATION_REQUEST_CODE:
                if(!LockUtils.isNotificationServiceRunning()){
                    Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                    startService(intent);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}