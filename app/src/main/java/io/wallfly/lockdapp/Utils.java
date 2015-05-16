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

import org.w3c.dom.Text;

import io.wallfly.lockdapp.lockutils.DragLock;

/**
 * Created by JoshuaWilliams on 4/30/15.
 *
 * Utils class containing commonly used methods.
 */
public class Utils {
    private static Context context;

    public static String getString(int stringResource){
        return getContext().getString(stringResource);
    }

    public static void saveToSharedPrefsString(String code, String content) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(code, content);
        editor.apply();
    }

    public static String getStringFromSharedPrefs(String code) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(code, "");
    }

    public static void clearSharedPrefs(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static float round(double valueToRound, int numberOfDecimalPlaces)
    {
        double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
        double interestedInZeroDPs = valueToRound * multipicationFactor;
        return (float) (Math.round(interestedInZeroDPs) / multipicationFactor);
    }

    public static TextView getPointView(String type, float xCoord, float yCoord){
        TextView pointView = new TextView(context);
        pointView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        pointView.setGravity(Gravity.CENTER);
        pointView.setText(type);
        pointView.setTextSize(50);
        pointView.setX(xCoord - 150);
        pointView.setY(yCoord - 190);

        return pointView;
    }

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
    public static void setContext(Context mContext){
        context = mContext;
    }

    public static Context getContext(){
        return context;
    }
}
