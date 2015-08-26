package io.wallfly.lockdapp.lockutils;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.hmkcode.android.recyclerview.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;



/**
 * Created by JoshuaWilliams on 4/30/15.
 *
 * Utils class containing commonly used methods.
 */
public class Utils {
    private static Context context;
    private static final String LOG_TAG = "Utils";
    private static ImageLoader imageLoader;
    private static Typeface roboto;
    private static Typeface robotoThin;
    private static GoogleApiClient locationclient;
    private static LocationRequest locationrequest = new LocationRequest();

    static String apiKey = "AIzaSyAiNvQdhFw2c_5JgyuQRUdNeiBBrCHR7gY";

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
        Log.i(LOG_TAG, "Saving -- Code -- " + code + " || Content -- " + content);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.package_name) + code, content);
        editor.apply();
    }

    /**
     * Retrieves a color from the resources
     *
     * @param color - the resource color id
     * @return - the actual color value.
     */
    public static int getColor(int color) {
        return getContext().getResources().getColor(color);
    }


    /**
     * Retrieves a string from the shared preferences.
     * 
     * @param code - code of the string to retrieve from the shared prefs.
     * @return - the appropriate string.
     */
    public static String getStringFromSharedPrefs(String code) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.i(LOG_TAG, "Retrieving From SharedPrefs => ----- Key --- " + code + " || Value --- " + preferences.getString(getString(R.string.package_name) + code, ""));
        return preferences.getString(getString(R.string.package_name) + code, "");
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
    public static float round(double valueToRound, int numberOfDecimalPlaces){
        double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
        double interestedInZeroDPs = valueToRound * multipicationFactor;
        return (float) (Math.round(interestedInZeroDPs) / multipicationFactor);
    }


    /**
     * Sets the passed views to the drawable resource passed.
     *
     * @param drawableResource - the drawable resource to be set on the views.
     * @param views - the views whose background will be set.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
     * Gets display image options from the Universal Imageloader.
     *
     * @return - the DisplayImageOptions object.
     */
    public static DisplayImageOptions getDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
    }

    /**
     * Returns the singleton instance of the {@link ImageLoader} set up with the configs needed for the app.
     *
     * @return - ImageLoader with configs.
     */
    public static ImageLoader getImageLoaderWithConfig() {
        if(imageLoader == null){
            imageLoader = ImageLoader.getInstance();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new WeakMemoryCache())
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .writeDebugLogs() // Remove for release app
                    .build();

            imageLoader.init(config);
        }
        return imageLoader;
    }

    public static void setContext(Context mContext){
        context = mContext;
    }

    public static Context getContext(){
        return context;
    }

    /**
     * Circular reveal on the passed view. For Android-L
     *
     * @param revealView - the view that will be revealed.
     * @param ANIM_DURATION - duration of the animation
     * @param cx - center x point of the hide.
     * @param cy - center y point of the hide.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator getAnimRevealShow(View revealView, long ANIM_DURATION, int cx, int cy) {
        int finalRadius = Math.max(revealView.getWidth(), revealView.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(revealView, cx, cy, 0, finalRadius);
        revealView.setVisibility(View.VISIBLE);
        anim.setDuration(ANIM_DURATION);
        return anim;
    }


    /**
     * Circular hide. Uses circular reveal but sets invokes opposite parameters to give the effect of shrinking rather than
     * revealing.
     *
     * @param viewRoot - view to be hidden.
     * @param ANIM_DURATION - duration of animation.
     * @param cx - center x point of the hide.
     * @param cy - center y point of the hide.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator getAnimateRevealHide(final View viewRoot, long ANIM_DURATION, int cx, int cy) {
        int initialRadius = viewRoot.getWidth();
        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, initialRadius, 0);
        anim.setDuration(ANIM_DURATION);
        return anim;
    }


    /**
     * Returns the first character of a numerical value.
     *
     * @param num - the numerical value whose first character will be extracted.
     * @return - the first characted of the numerical value.
     */
    public static char getFirstCharacter(float num){
        return Float.toString(num).toCharArray()[0];
    }

    /**
     * Shows a toast with the string message passed.
     *
     * @param string - the message to be displayed on the toast.
     */
    public static void showToast(String string) {
        Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
    }


    /**
     * Sets the color of the status bar
     *
     * @param activity - The activity at which the status bar will be set.
     * @param color - the color resource to set the status bar.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Activity activity, int color){
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        try {
            window.setStatusBarColor(activity.getResources().getColor(color));
        }catch (RuntimeException re){
            window.setStatusBarColor(color);
        }
    }


    /**
     * Animation to start the fading in of the data
     *
     * @param v - the view on which the fade animation will be performed.
     */

    public static void fadeInAnimation(final View v, int duration){
        Log.i(LOG_TAG, "Starting Fade In Anim");

        Animation anim = new AlphaAnimation(0, 1);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i(LOG_TAG, "Ending Fade In Anim");
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setFillAfter(true);
        anim.setDuration(duration);
        v.startAnimation(anim);
    }

    /**
     * Animation to start the fading in of the data
     *
     * @param v - the view on which the fade animation will be performed.
     */
    public static void fadeOutAnimation(final View v, int duration){
        Log.i(LOG_TAG, "Starting Fade Out Anim");

        Animation anim = new AlphaAnimation(1, 0);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i(LOG_TAG, "Ending Fade Out Anim");
                v.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setFillAfter(true);
        anim.setDuration(duration);
        v.startAnimation(anim);
    }

    /**
     * Loads the two typefaces used in the application.
     */
    public static void loadTypeFaces(){
        roboto = getTypeface(getString(R.string.roboto_font));
        robotoThin = getTypeface(getString(R.string.roboto_thin_font));
    }


    public static Typeface getRoboto(){
        if(roboto == null){
            Log.e(LOG_TAG, "getRoboto -- Must call loadTypefaces() first.");
            return Typeface.DEFAULT;
        }
        return roboto;
    }


    /**
     * Method that allows simple fonts to be set just by passing the font. It checks to see if the font has the proper format,
     * if it does not, it will return the default font and log error.
     *
     * @param font - the font that needs to be applied.
     * @return Typeface - the new typeface created from the passed font.
     */
    public static Typeface getTypeface(String font){
        if(!font.substring(font.length() - 4, font.length()).equals(".ttf")){
            Log.e(LOG_TAG, "GetTypeFace - Your font does not contain the proper '.ttf' suffix.");
            return Typeface.DEFAULT;
        }
        return Typeface.createFromAsset(getContext().getAssets(), "fonts/" + font);
    }


    /**
     * Blurs the bitamp to a small degree.
     *
     * @param photo - the bitmap to be blurred
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void blurImage(Bitmap photo){
        RenderScript rs = RenderScript.create(getContext());
        Allocation input = Allocation.createFromBitmap( rs, photo, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
        Allocation output = Allocation.createTyped( rs, input.getType() );
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create( rs, Element.U8_4(rs) );
        script.setRadius( 5 /* e.g. 3.f */ );
        script.setInput( input );
        script.forEach(output);
        output.copyTo(photo);
    }

    /**
     * Retrieves a drawable resource from another package.
     *
     * @param packageName - the name of the package.
     * @param resourceID - the id.
     * @return - the drawable from the package.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getDrawableFromApp(String packageName, int resourceID){

        try {
            if(Build.VERSION.SDK_INT >= 21){
                return Utils.getContext().createPackageContext(packageName, 0).getDrawable(resourceID);
            }
            else{
                return Utils.getContext().createPackageContext(packageName, 0).getResources().getDrawable(resourceID);
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Converts drawable to bitmap.
     *
     * @param drawable - the drawable to be turned into a bitmap.
     * @param widthPixels - the width of the bitmap.
     * @param heightPixels - the height of the bitmap.
     * @return - the new bitmap version of the drawable.
     */
    public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);
        return mutableBitmap;
    }


    /**
     * Gets the user's wallpaper.
     *
     * @return - the user's wallpaper drawable.
     */
    public static Drawable getWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
        return wallpaperManager.getDrawable();
    }

    public static Typeface getRobotoThin() {
        return robotoThin;
    }

    /**
     * Stores the location inside the user's shared prefs.
     *
     */
    public static void storeLocation(){
        locationclient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionCallbacks)
                .build();

        if(!locationclient.isConnected()) {
            locationclient.connect();
        }

    }


    /**
     * Retrieves the city name from lattitude and longitude coordinates.
     *
     * @param lat - lattitude of the location
     * @param lng - longitude of the location
     * @return - the city name.
     */
    public static String getCityName(double lat, double lng){
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (addresses != null) ? addresses.get(0).getLocality() : null;
    }



    private static com.google.android.gms.location.LocationListener locationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(LOG_TAG, "Location changing...");
        }
    };

    private static GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle bundle) {
            Log.i(LOG_TAG, "Location client connected");
            locationrequest = LocationRequest.create();
            locationrequest.setInterval(30 * 1000);
            locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.FusedLocationApi.requestLocationUpdates(locationclient,
                    locationrequest, locationListener);



            if(LocationServices.FusedLocationApi.getLastLocation(locationclient) != null){
                double lat = LocationServices.FusedLocationApi.getLastLocation(locationclient).getLatitude();
                double lng = LocationServices.FusedLocationApi.getLastLocation(locationclient).getLongitude();

                String cityName = Utils.getCityName(lat, lng);
                saveToSharedPrefsString(getString(R.string.city_name), cityName);

                locationclient.disconnect();
            }
        }


        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    /**
     * Vibrates phone for 800 ms
     */
    public static void vibrate() {
        ((Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(800);
    }

    /**
     * Checks if use currently has a lock set.
     *
     * @return - whether the lock is set or not.
     */
    public static boolean isDeviceSecured() {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("com.android.internal.widget.LockPatternUtils");
            Constructor<?> constructor = clazz.getConstructor(Context.class);
            constructor.setAccessible(true);
            Object utils = constructor.newInstance(context);
            Method method = clazz.getMethod("isSecure");
            return  (Boolean)method.invoke(utils);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if the user's Gps is enabeld.
     *
     * @return - whether the user's GPS is enabled or not.
     */
    public static boolean isGpsEnabled(){
        final LocationManager manager = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ;
    }
}
