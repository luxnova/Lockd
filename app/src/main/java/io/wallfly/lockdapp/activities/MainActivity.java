package io.wallfly.lockdapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.List;

import io.wallfly.lockdapp.adaptersandlisteners.SettingsAdapter;
import io.wallfly.lockdapp.adaptersandlisteners.ViewGoneAnimationListener;
import io.wallfly.lockdapp.models.SettingsSelection;
import io.wallfly.lockdapp.lockutils.Utils;
import io.wallfly.lockdapp.libs.HomeKeyLocker;
import io.wallfly.lockdapp.servicesandreceivers.ScreenStatusMonitoringService;

public class MainActivity extends BaseActivity implements ObservableScrollViewCallbacks,
        View.OnLongClickListener, View.OnClickListener {

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = true;

    private View mToolbar;
    private ImageView background, disableLockButton, createLockView, enableLockButton;
    private View mOverlayView, mRecyclerViewBackground;
    private ObservableRecyclerView mRecyclerView;
    private TextView mTitleView;
    private int mActionBarSize, mFlexibleSpaceImageHeight,  mToolbarColor;
    private Handler timerHandler = new Handler();
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUtilsContext();
        Utils.loadTypeFaces();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        setUpLayout();








        //enableLockd();
        //startHomeScreen();

    }

    private void setUpLayout() {
        setViewBackGround();

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarSize();
        mToolbarColor = getResources().getColor(R.color.lockd_primary);

        mRecyclerView = (ObservableRecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setScrollViewCallbacks(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(false);
        final View headerView = LayoutInflater.from(this).inflate(R.layout.recycler_header, null);
        headerView.post(new Runnable() {
            @Override
            public void run() {
                if (headerView != null)
                    headerView.getLayoutParams().height = mFlexibleSpaceImageHeight;
            }
        });

        List<SettingsSelection> settingsSelections = new ArrayList<>();
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
        settingsSelections.add(new SettingsSelection());

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

        enableLockButton = (ImageView)findViewById(R.id.enable_lock_button);
        enableLockButton.setOnClickListener(this);
        enableLockButton.setOnLongClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setViewBackGround() {
        background = (ImageView) findViewById(R.id.background);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        Bitmap bitmap = Utils.convertToBitmap(wallpaperDrawable, 100, 150);
        Utils.blurImage(bitmap);
        background.setImageBitmap(bitmap);
    }


    @Override
    public void onAttachedToWindow(){
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); //dismiss screen lock
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }


    @Override
    public void onResume(){
        super.onResume();
        checkUtilsContext();
        setViewBackGround();
    }



    private void enableLockd() {
        //Lock the home button
        HomeKeyLocker homeKeyLocker = new HomeKeyLocker();
        homeKeyLocker.lock(this);

        Intent startIntent = new Intent(this, ScreenStatusMonitoringService.class);
        this.startService(startIntent);
    }

    private void startHomeScreen() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
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
        if(Utils.getContext() == null){ Utils.setContext(getApplicationContext()); }
        Utils.checkIfFirstOpen();
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

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()){
            case R.id.enable_lock_button:
                Utils.showToast(getString(R.string.lock_enabled));
                disableLockButton.bringToFront();
                disableLockButton.setVisibility(View.VISIBLE);
                Animator anim = Utils.getAnimRevealShow(disableLockButton, 400, disableLockButton.getWidth() / 2, disableLockButton.getHeight() / 2);
                anim.addListener(new ViewGoneAnimationListener(view)); //Sets view to invisible at the end of animation.
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

                break;

            case R.id.create_lock_button:
                disableLockButton.bringToFront();
                anim = Utils.getAnimRevealShow(disableLockButton, 400, disableLockButton.getWidth() / 2, disableLockButton.getHeight() / 2);
                anim.start();

                timerHandler.postDelayed(timerRunnable, 3000); //Starts timer to show disable button for 3 seconds (3000 ms)

                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.enable_lock_button:
                Utils.showToast(getString(R.string.enable_lock));
                break;

            case R.id.disable_lock_button:
                timerHandler.removeCallbacks(timerRunnable); //Cancels the timer.
                Utils.showToast(getString(R.string.lock_disabled));
                enableLockButton.bringToFront();
                enableLockButton.setVisibility(View.VISIBLE);
                Animator anim = Utils.getAnimRevealShow(enableLockButton, 400, enableLockButton.getWidth() / 2, enableLockButton.getHeight() / 2);
                view.setTag(createLockView);
                anim.addListener(new ViewGoneAnimationListener(view));
                anim.start();

                break;
        }
    }
}