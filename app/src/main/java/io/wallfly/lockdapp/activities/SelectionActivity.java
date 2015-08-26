package io.wallfly.lockdapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Transition;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.hmkcode.android.recyclerview.R;
import java.util.ArrayList;
import java.util.List;
import io.wallfly.lockdapp.adaptersandlisteners.AboutRecyclerAdapter;
import io.wallfly.lockdapp.lockutils.AbstractLockdBaseListener;
import io.wallfly.lockdapp.lockutils.Utils;
import io.wallfly.lockdapp.models.Library;
import io.wallfly.lockdapp.models.SettingsSelection;

public class SelectionActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String LOG_TAG = "SelectionActivity";
    private static final int ANIMATIONS_DURATION = 200; //All animations have the same length
    private int milliSeconds;
    private Toolbar toolbar;
    private TextView titleTV, radiusTV, accuracyTV, accuracyHeader, radiusHeader;
    private float centerX, centerY, startR, startScale, startX, startY;
    private ImageView scaleView, dragHandle;
    private Handler timerHandler = new Handler();
    private boolean isViewAnimating = false;
    private int statusBarColor;
    private ImageView image;
    private RecyclerView recyclerView;
    private CardView cardView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_activity);

        Bundle data = getIntent().getExtras();

        SettingsSelection selection = data.getParcelable("selection");
        setUpLayout(selection);

    }

    private void setUpLayout(SettingsSelection selection) {
        RelativeLayout validTouchLayout = (RelativeLayout) findViewById(R.id.valid_touch_layout);
        RelativeLayout aboutLayout = (RelativeLayout) findViewById(R.id.about_layout);
        RelativeLayout privacyPolicyLayout = (RelativeLayout) findViewById(R.id.privacy_policy_layout);
        RelativeLayout helpLayout = (RelativeLayout) findViewById(R.id.help_layout);

        //If the selction was valid touch setting
        if(selection.getImageResource() == R.drawable.touch_icon){
            validTouchLayout.setVisibility(View.VISIBLE);
            aboutLayout.setVisibility(View.GONE);
            privacyPolicyLayout.setVisibility(View.GONE);
            helpLayout.setVisibility(View.GONE);

            scaleView = (ImageView) findViewById(R.id.scale_view);
            dragHandle = (ImageView) findViewById(R.id.drag_handle);
            radiusTV = (TextView) findViewById(R.id.radius_tv);
            accuracyTV = (TextView) findViewById(R.id.accuracy_tv);
            radiusHeader = (TextView) findViewById(R.id.radius_header);
            accuracyHeader = (TextView) findViewById(R.id.accuracy_header);
            toolbar = (Toolbar) validTouchLayout.findViewById(R.id.include);
            image = (ImageView) validTouchLayout.findViewById(R.id.image);
            titleTV = (TextView) validTouchLayout.findViewById(R.id.title);


            radiusHeader.setTypeface(Utils.getRoboto());
            radiusTV.setTypeface(Utils.getRoboto());
            radiusTV.setText(convertBoundsToString(AbstractLockdBaseListener.getValidLockBounds()));
            double accuracy = convertBoundsToDouble(AbstractLockdBaseListener.getValidLockBounds());

            accuracyHeader.setTypeface(Utils.getRoboto());
            accuracyTV.setTypeface(Utils.getRoboto());
            accuracyTV.bringToFront();
            accuracyHeader.bringToFront();
            setAccuracyTV(accuracy);

            float scale = AbstractLockdBaseListener.getValidLockBounds() / 220;
            scaleView.setScaleX(scale);
            scaleView.setScaleY(scale);

            //Check to see if the user has already changed this setting, if so then adjust the views appropriately.
            String data = Utils.getStringFromSharedPrefs(getString(R.string.drag_handle_coords));
            if(!data.isEmpty()){
                Log.i(LOG_TAG, "Drag view data -- " + data);
                final String[] dragViewData = data.split(" ");

                ViewTreeObserver vto = dragHandle.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        dragHandle.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        dragHandle.setX(Float.parseFloat(dragViewData[0]));
                        dragHandle.setY(Float.parseFloat(dragViewData[1]));
                    }
                });
            }

            dragHandle.setOnTouchListener(this);
            RelativeLayout parentLayout = (RelativeLayout) findViewById(R.id.parent);
            parentLayout.invalidate();
        }
        else if(selection.getImageResource() == R.drawable.about_icon){
            helpLayout.setVisibility(View.GONE);
            privacyPolicyLayout.setVisibility(View.GONE);
            validTouchLayout.setVisibility(View.GONE);
            aboutLayout.setVisibility(View.VISIBLE);

            image = (ImageView) aboutLayout.findViewById(R.id.image);
            toolbar = (Toolbar) aboutLayout.findViewById(R.id.include);
            titleTV = (TextView) aboutLayout.findViewById(R.id.title);

            recyclerView = (RecyclerView) findViewById(R.id.about_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            List<Library> libraries = new ArrayList<>();
            libraries.add(new Library("Android-ObservableScrollView",
                    "Soichiro Kashima",
                    "Android library to observe scroll events on scrollable views." +
                    "It's easy to interact with the Toolbar introduced in Android 5.0 Lollipop and may be helpful to implement look and feel of Material Design apps.",
                    "https://github.com/ksoichiro/Android-ObservableScrollView"));
            libraries.add(new Library("NineOldAndroids",
                    "Jake Wharton",
                    "Android library for using the Honeycomb (Android 3.0) animation API on all versions of the platform back to 1.0!\n" +
                            "\n" + "Note: LayoutTransition is present and it is not be possible to implement prior to Android 3.0.",
                    "https://github.com/JakeWharton/NineOldAndroids"));
            libraries.add(new Library("DiscreteSeekBar",
                    "Ander Web",
                    "DiscreteSeekbar is my poor attempt to develop an android implementation of the Discrete Slider component from the Google Material Design Guidelines.",
                    "https://github.com/AnderWeb/discreteSeekBar"));
            libraries.add(new Library("UIL",
                    "Sergey Tarasevich",
                    "Android library #1 on GitHub. UIL aims to provide a powerful, flexible and highly customizable instrument for image loading, caching and displaying. It provides a lot of configuration options and good control over the image loading and caching process.",
                    "https://github.com/nostra13/Android-Universal-Image-Loader"));
            libraries.add(new Library("HomeKeyLocker",
                    "Shaobin",
                    "Utility to disable HOME KEY in Android Activity.",
                    "https://github.com/shaobin0604/Android-HomeKey-Locker"));
            libraries.add(new Library("WeatherIconView",
                    "Piotr Wittchen",
                    "Android library providing custom view for displaying weather icon. " +
                            "Weather Icon View is based on Weather Icons project by Erik Flowers.",
                    "https://github.com/pwittchen/WeatherIconView"));


            AboutRecyclerAdapter aboutRecyclerAdapter = new AboutRecyclerAdapter(this, libraries);
            recyclerView.setAdapter(aboutRecyclerAdapter);
        } else if(selection.getImageResource() == R.drawable.privacy_policy_icon){
            helpLayout.setVisibility(View.GONE);
            validTouchLayout.setVisibility(View.GONE);
            aboutLayout.setVisibility(View.GONE);
            privacyPolicyLayout.setVisibility(View.VISIBLE);

            cardView = (CardView) privacyPolicyLayout.findViewById(R.id.card_view);
            image = (ImageView) privacyPolicyLayout.findViewById(R.id.image);
            toolbar = (Toolbar) privacyPolicyLayout.findViewById(R.id.include);
            titleTV = (TextView) privacyPolicyLayout.findViewById(R.id.title);

            TextView textView = (TextView) privacyPolicyLayout.findViewById(R.id.privacy_policy_textview);
            textView.setText(Html.fromHtml(getString(R.string.privacy_policy)));
            textView.setTypeface(Utils.getRoboto());
        } else if(selection.getImageResource() == R.drawable.help_icon){
            helpLayout.setVisibility(View.VISIBLE);
            validTouchLayout.setVisibility(View.GONE);
            aboutLayout.setVisibility(View.GONE);
            privacyPolicyLayout.setVisibility(View.GONE);

            image = (ImageView) helpLayout.findViewById(R.id.image);
            toolbar = (Toolbar) helpLayout.findViewById(R.id.include);
            titleTV = (TextView) helpLayout.findViewById(R.id.title);

            scrollView = (ScrollView) helpLayout.findViewById(R.id.scrollView);
            ImageView touchPic = (ImageView) helpLayout.findViewById(R.id.valid_touch_help_pic);

            Utils.getImageLoaderWithConfig().displayImage("drawable://" + R.drawable.help_valid_touch_picture, touchPic, Utils.getDisplayImageOptions());
        }

        titleTV.setTypeface(Utils.getRoboto());
        titleTV.setVisibility(View.INVISIBLE);
        titleTV.setText(selection.getTitle());

        setToolbarColor(selection.getImageResource());

        Log.i(LOG_TAG, "Selection drawable --- " + selection.getImageResource());
        Utils.getImageLoaderWithConfig().displayImage("drawable://" + selection.getImageResource(), image, Utils.getDisplayImageOptions());
        setupEnterAnimations();
        setupExitAnimations();
        activateTransitionsTimer();
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimations() {
        Transition enterTransition = getWindow().getSharedElementEnterTransition();
        enterTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                showToolbar();
                Log.i(LOG_TAG, "Setting animating to true.");
                isViewAnimating = true;
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupExitAnimations() {
        Transition sharedElementReturnTransition = getWindow().getSharedElementReturnTransition();
        sharedElementReturnTransition.setStartDelay(ANIMATIONS_DURATION);


        Transition returnTransition = getWindow().getReturnTransition();
        returnTransition.setDuration(ANIMATIONS_DURATION);
        returnTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                hideToolbar();
            }

            @Override
            public void onTransitionEnd(Transition transition) {
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }

    /**
     * Animates the toolbar by performing a circular reveal
     */
    private void showToolbar() {
        Animator circularRevealAnim = Utils.getAnimRevealShow(toolbar, ANIMATIONS_DURATION, toolbar.getLeft() + 100, toolbar.getHeight() / 2);
        circularRevealAnim.addListener(circularRevealAnimListener);
        circularRevealAnim.start();
    }

    /**
     * Animates toolbar by performing a circular hide.
     */
    private void hideToolbar() {
        Animator anim = Utils.getAnimateRevealHide(toolbar, ANIMATIONS_DURATION, toolbar.getLeft() + 100, toolbar.getHeight() / 2);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                toolbar.setVisibility(View.INVISIBLE);

            }
        });
        anim.start();
    }


    //Animation for the tool bar when entering.
    //Also begins the animation for the views
    private Animator.AnimatorListener circularRevealAnimListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
            Utils.setStatusBarColor(SelectionActivity.this, getStatusBarColor());

            //If the radiusHeader is equal to null, that means the selection isn't the valid touch setting
            //So there will be no need to load start these animations
            if(radiusHeader != null){
                final Animation pushUpAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_up_in);
                pushUpAnim.setAnimationListener(pushUpAnimListener);

                final Animation slideInFromLeftAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_in_from_left);
                slideInFromLeftAnim.setAnimationListener(slideInLeftAnimationListener);

                final Animation slideInFromRightAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_in_from_right);
                slideInFromRightAnim.setAnimationListener(slideInRightAnimationListener);

                radiusHeader.startAnimation(slideInFromRightAnim);
                radiusTV.startAnimation(slideInFromRightAnim);

                accuracyHeader.startAnimation(slideInFromLeftAnim);
                accuracyTV.startAnimation(slideInFromLeftAnim);

                dragHandle.startAnimation(pushUpAnim);
                scaleView.startAnimation(pushUpAnim);
            }
            else if(recyclerView != null){
                final Animation pushUpAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_up_in);
                pushUpAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                recyclerView.startAnimation(pushUpAnim);
            }
            else if(cardView != null){
                final Animation pushUpAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_up_in);
                pushUpAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        cardView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                cardView.startAnimation(pushUpAnim);
            }
            else if(scrollView != null){
                final Animation pushUpAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_up_in);
                pushUpAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        scrollView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                scrollView.startAnimation(pushUpAnim);
            }

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            Utils.fadeInAnimation(titleTV, ANIMATIONS_DURATION);
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };


    //Listener to make the scaling circle and the drag handle animate.
    private Animation.AnimationListener pushUpAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            dragHandle.setVisibility(View.VISIBLE);
            scaleView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    //Listener to make the scaling circle and the drag handle animate.
    private Animation.AnimationListener slideInLeftAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            accuracyHeader.setVisibility(View.VISIBLE);
            accuracyTV.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    //Listener to make the scaling circle and the drag handle animate.
    private Animation.AnimationListener slideInRightAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            radiusHeader.setVisibility(View.VISIBLE);
            radiusTV.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * Checks if activity started without calling onResume.
     */
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            milliSeconds++;
            if(!isViewAnimating && milliSeconds >= 300){
                showToolbar();
                milliSeconds = 0;
                timerHandler.removeCallbacks(this);
                return;
            }
            else if(isViewAnimating){
                milliSeconds = 0;
                timerHandler.removeCallbacks(this);
                return;
            }
            timerHandler.postDelayed(this, 1);
        }
    };


    /**
     * Checks to see if the activity made the enter transition. It sometimes doesnt. Bug we cant fix as of now
     */
    private void activateTransitionsTimer(){
        timerHandler.postDelayed(timerRunnable,
                ANIMATIONS_DURATION + ANIMATIONS_DURATION); //Starts timer to show disable button for 3 seconds (3000 ms)
    }

    public void setStatusBarColor(int color){
        statusBarColor = color;
    }

    public int getStatusBarColor(){
        return statusBarColor;
    }

    /**
     * Sets color of the toolbar based on the user's selection.
     *
     * @param selection - the settings selection drawable integer.
     */
    public void setToolbarColor(int selection) {
        switch (selection){
            case R.drawable.touch_icon:
                toolbar.setBackgroundResource(R.color.blue);
                setStatusBarColor(R.color.dark_blue);
                break;

            case R.drawable.about_icon:
                toolbar.setBackgroundResource(R.color.teal);
                setStatusBarColor(R.color.dark_teal);
                break;

            case R.drawable.privacy_policy_icon:
                toolbar.setBackgroundResource(R.color.dark_gray);
                setStatusBarColor(R.color.toolbar_gray);
                break;

            case R.drawable.help_icon:
                toolbar.setBackgroundResource(R.color.cyan);
                setStatusBarColor(R.color.dark_cyan);
                break;
        }
    }

    /**
     * Converts the valid touch bounds to a readable string for humans.
     * Is used to set the bounds text view.
     *
     * @param bounds - the user's bounds setting.
     * @return - the string version.
     */
    private String convertBoundsToString(double bounds){
        double readableBounds = Math.round(bounds / 4.2);
        return Double.toString(Utils.round(readableBounds, 0)); //rounds to zero decimal place for simplicity
    }

    /**
     * Converts the valid touch bounds to a double for comparison.
     * Is used to set the bounds text view.
     *
     * @param bounds - the user's bounds setting.
     * @return - the double version.
     */
    private double convertBoundsToDouble(double bounds){
        double readableBounds = Math.round(bounds / 4.2);
        return Utils.round(readableBounds, 0); //rounds to zero decimal place for simplicity
    }

    /**
     * Sets the accuracy text view to the appropriate message.
     *
     * @param accuracy - the user's valid touch bounds that is converted for viewing. (convertToDouble).
     */
    public void setAccuracyTV(double accuracy) {
        if(accuracy < 40 && accuracy > 20){
            accuracyTV.setText("High");
            accuracyTV.setTextColor(getResources().getColor(R.color.yellow));
        }
        else if(accuracy < 25){
            accuracyTV.setText("Extemely high - Consider lowering.");
            accuracyTV.setTextColor(getResources().getColor(R.color.red));
        }
        else if(accuracy <= 60 && accuracy >= 40){
            accuracyTV.setText("Moderate");
            accuracyTV.setTextColor(getResources().getColor(R.color.green));
        }
        else{
            accuracyTV.setText("Low");
            accuracyTV.setTextColor(getResources().getColor(R.color.yellow));
        }
    }

    @Override
    public void onResume(){
        super.onStart();
        if(Utils.getContext() == null){ Utils.setContext(getApplicationContext()); }
    }


    /************************************ ON TOUCH LISTENER FOR DRAG HANDLE ****************************************/

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {

            // calculate center of image
            centerX = (scaleView.getLeft() + scaleView.getRight()) / 2f;
            centerY = (scaleView.getTop() + scaleView.getBottom()) / 2f;

            // recalculate coordinates of starting point
            startX = e.getRawX() - dragHandle.getX() + centerX;
            startY = e.getRawY() - dragHandle.getY() + centerY;


            // get starting distance and scale
            startR = (float) Math.hypot(e.getRawX() - startX, e.getRawY() - startY);
            startScale = scaleView.getScaleX();

        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {

            // calculate new distance
            float newR = (float) Math.hypot(e.getRawX() - startX, e.getRawY() - startY);

            // set new scale
            float newScale = newR / startR * startScale;

            // 220 is the correct x in this formula
            // scale * x = VALID_TOUCH_BOUNDS
            float validTouchBounds = newScale * 220;

            if (validTouchBounds < AbstractLockdBaseListener.getMinimumLockBounds() ||
                    validTouchBounds > AbstractLockdBaseListener.getMaximumLockBounds()) {
                return true;
            }


            //Converts the data into strings to keep precision and stores them.
            String endRawCoords = Float.toString(e.getRawX()) + " " + Float.toString(e.getRawY());
            Utils.saveToSharedPrefsString(getString(R.string.drag_handle_coords), endRawCoords);

            scaleView.setScaleX(newScale);
            scaleView.setScaleY(newScale);

            Log.i(LOG_TAG, "Drag view data VLD: -- " + validTouchBounds);

            setAccuracyTV(convertBoundsToDouble(validTouchBounds));

            //Move handler image
            dragHandle.setX(centerX + scaleView.getWidth() / 2f * newScale);
            dragHandle.setY(centerY + scaleView.getHeight() / 2f * newScale);

            radiusTV.setText(convertBoundsToString(validTouchBounds)); //round to 1 decimal places.

            //Save data
            AbstractLockdBaseListener.setValidLockBounds(validTouchBounds);

            String dragHandleCoords = Float.toString(centerX + scaleView.getWidth() / 2f * newScale) + " "
                    + Float.toString(centerY + scaleView.getHeight() / 2f * newScale);

            Utils.saveToSharedPrefsString(getString(R.string.drag_handle_coords), dragHandleCoords);

        }

        return true;
    }

    /************************************************* END *************************************************/

}
