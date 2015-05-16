package io.wallfly.lockdapp;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.hmkcode.android.recyclerview.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

    private Toolbar mToolbar;
    private ObservableRecyclerView mRecyclerView;

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = true;



    private int mActionBarSize;
    private int mFlexibleSpaceImageHeight;
    private int mToolbarColor;

    private View mImageView;
    private View mOverlayView;
    private View mRecyclerViewBackground;
    private TextView mTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initiateActivity();
    }

    private void initiateActivity() {

        TypedValue tv = new TypedValue();
        mActionBarSize = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mToolbarColor = getResources().getColor(R.color.abc_input_method_navigation_guard);

        mRecyclerView = (ObservableRecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setScrollViewCallbacks(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(false);

        checkUtilsContext();


        final View headerView = LayoutInflater.from(this).inflate(R.layout.recycler_header, null);

        ViewTreeObserver vto = headerView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                headerView.getLayoutParams().height = mFlexibleSpaceImageHeight;
            }
        });

        setDummyDataWithHeader(mRecyclerView, headerView);


        List<ItemData> itemsData = new ArrayList<>();

        // R.drawable.ic_launcher is used as a blank icon template for now
        // until custom icons are created
        itemsData.add(new ItemData("Enable Lockd", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Notification Settings", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Privacy Settings", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Reset Lock", R.drawable.ic_launcher));
        //position of Other ItemData title is 4, this is used for the
        //partition used to separate settings
        itemsData.add(new ItemData("Other", R.drawable.ic_launcher));
        itemsData.add(new ItemData("About", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Privacy Policy", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Help", R.drawable.ic_launcher));

        itemsData.add(new ItemData("Other", R.drawable.ic_launcher));
        itemsData.add(new ItemData("About", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Privacy Policy", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Help", R.drawable.ic_launcher));

        itemsData.add(new ItemData("Other", R.drawable.ic_launcher));
        itemsData.add(new ItemData("About", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Privacy Policy", R.drawable.ic_launcher));
        itemsData.add(new ItemData("Help", R.drawable.ic_launcher));




        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);




        mToolbarColor = getResources().getColor(R.color.abc_input_method_navigation_guard);

        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }

        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);

        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(getTitle());



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
    }

    private void checkUtilsContext() {
        if(Utils.getContext() == null)
        {
            Utils.setContext(getApplicationContext());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initiateActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        initiateActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanUpActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        cleanUpActivity();
    }

    @Override
    public void onStop() {
        super.onStop();
        cleanUpActivity();
    }

    /**
     * Clean up method, set global vars to null
     */
    private void cleanUpActivity() {

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

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
            // Change alpha of mToolbar background
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
        Log.e("DEBUG", "onUpOrCancelMotionEvent: " + scrollState);
        ActionBar ab = getSupportActionBar();
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }
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

    public static ArrayList<String> getDummyData() {
        return getDummyData(100);
    }

    public static ArrayList<String> getDummyData(int num) {
        ArrayList<String> items = new ArrayList<String>();
        for (int i = 1; i <= num; i++) {
            items.add("Item " + i);
        }
        return items;
    }


    protected void setDummyDataWithHeader(RecyclerView recyclerView, View headerView) {
        recyclerView.setAdapter(new SimpleHeaderRecyclerAdapter(this, getDummyData(), headerView));
    }
}
