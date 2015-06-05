package io.wallfly.lockdapp.adaptersandlisteners;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.hmkcode.android.recyclerview.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import io.wallfly.lockdapp.activities.SelectionActivity;
import io.wallfly.lockdapp.lockutils.CustomLockBaseListener;
import io.wallfly.lockdapp.lockutils.Utils;
import io.wallfly.lockdapp.models.SettingsSelection;

/**
 * Created by JoshuaWilliams on 5/26/15.
 *
 * @version 1.0
 *
 * On click listener for the settings recycler adapter.
 *
 */
public class SettingsOnClickListener implements View.OnClickListener {
    private static final String LOG_TAG = "SettingsItemClick";
    private Activity activity;

    public SettingsOnClickListener(Activity activity){
        this.activity = activity;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        SettingsSelection selection = (SettingsSelection) v.getTag();
        Log.i(LOG_TAG, "Selection ------ " + selection.toString());

        switch (selection.getPosition()) {
            //Clock Setting
            case 1:
                String militaryClockSetting =  Utils.getString(R.string.twenty_four_hour_clock);

                //Toggles the switch and updates views.
                if(Utils.getSetting(R.string.twenty_four_hour_clock)) {
                    Utils.saveToSharedPrefsString(militaryClockSetting, "false");

                    Switch switchView;
                    TextView subTextView;
                    if(v.getId() == R.id.switch_view){
                        RelativeLayout layout = (RelativeLayout) v.getParent();
                        switchView = (Switch) layout.findViewById(R.id.switch_view);
                        subTextView = (TextView) layout.findViewById(R.id.textView2);
                    }
                    else{
                        switchView = (Switch) v.findViewById(R.id.switch_view);
                        subTextView = (TextView) v.findViewById(R.id.textView2);
                    }

                    subTextView.setText(Utils.getString(R.string.display_12_hour_clock_text));
                    switchView.setChecked(false);
                }
                else{
                    Utils.saveToSharedPrefsString(militaryClockSetting, "true");

                    Switch switchView;
                    TextView subTextView;
                    if(v.getId() == R.id.switch_view){
                        RelativeLayout layout = (RelativeLayout) v.getParent();
                        switchView = (Switch) layout.findViewById(R.id.switch_view);
                        subTextView = (TextView) layout.findViewById(R.id.textView2);
                    }
                    else{
                        switchView = (Switch) v.findViewById(R.id.switch_view);
                        subTextView = (TextView) v.findViewById(R.id.textView2);
                    }
                    subTextView.setText(Utils.getString(R.string.display_24_clock_text));
                    switchView.setChecked(true);
                }

                Log.i(LOG_TAG, "Changing clock setting to " + Utils.getSetting(R.string.twenty_four_hour_clock));

                break;

            //Weather Display Setting
            case 2:
                String weatherDisplaySetting = Utils.getString(R.string.weather_showing);

                //Toggles the switch and updates views.
                if(Utils.getSetting(R.string.weather_showing)) {
                    Utils.saveToSharedPrefsString(weatherDisplaySetting, "false");

                    Switch switchView;
                    TextView subTextView;
                    if(v.getId() == R.id.switch_view){
                        RelativeLayout layout = (RelativeLayout) v.getParent();
                        switchView = (Switch) layout.findViewById(R.id.switch_view);
                        subTextView = (TextView) layout.findViewById(R.id.textView2);
                    }
                    else{
                        switchView = (Switch) v.findViewById(R.id.switch_view);
                        subTextView = (TextView) v.findViewById(R.id.textView2);
                    }

                    subTextView.setText(Utils.getString(R.string.weather_not_displayed_text));
                    switchView.setChecked(false);
                }
                else{
                    Utils.saveToSharedPrefsString(weatherDisplaySetting, "true");

                    Switch switchView;
                    TextView subTextView;
                    if(v.getId() == R.id.switch_view){
                        RelativeLayout layout = (RelativeLayout) v.getParent();
                        switchView = (Switch) layout.findViewById(R.id.switch_view);
                        subTextView = (TextView) layout.findViewById(R.id.textView2);
                    }
                    else{
                        switchView = (Switch) v.findViewById(R.id.switch_view);
                        subTextView = (TextView) v.findViewById(R.id.textView2);
                    }
                    subTextView.setText(Utils.getString(R.string.display_weather_text));
                    switchView.setChecked(true);
                }

                Log.i(LOG_TAG, "Changing weather setting to " + Utils.getSetting(R.string.weather_showing));

                break;

            //Vibrate Setting
            case 3:
                Log.i(LOG_TAG, "Vibrate on touch - " + !CustomLockBaseListener.getVibrateOnTouch());
                CustomLockBaseListener.setVibrateOnTouch(!CustomLockBaseListener.getVibrateOnTouch());

                Switch switchView;
                if (v.getId() == R.id.switch_view) {
                    RelativeLayout layout = (RelativeLayout) v.getParent();
                    switchView = (Switch) layout.findViewById(R.id.switch_view);
                } else {
                    switchView = (Switch) v.findViewById(R.id.switch_view);
                }

                switchView.setChecked(CustomLockBaseListener.getVibrateOnTouch());

                break;

            //Seconds between taps Setting
            case 4:

                final TextView secondsTV = (TextView) v.findViewById(R.id.textView2);

                View dialoglayout = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null);

                DiscreteSeekBar seekBar = (DiscreteSeekBar) dialoglayout.findViewById(R.id.seekbar);
                seekBar.setTrackColor(Color.parseColor("#ffffff"));
                seekBar.setScrubberColor(Utils.getColor(R.color.brown));
                seekBar.setThumbColor(Utils.getColor(R.color.brown), Utils.getColor(R.color.brown));
                seekBar.setProgress((int) CustomLockBaseListener.getSecondsBetweenTaps()/1000);
                seekBar.setMin(1);
                seekBar.setMax(5);

                seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                    @Override
                    public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                        if(i > 1){
                            secondsTV.setText(i + " seconds");
                        }
                        else{
                            secondsTV.setText(i + " second");
                        }

                        CustomLockBaseListener.setSecondsBetweenTaps(i * 1000);
                    }

                    @Override
                    public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {

                    }
                });

                Dialog alertDialog = new Dialog(activity);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(dialoglayout);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();
                break;

            //Rate
            case 6:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("market://details?id=" + activity.getPackageName()));
                activity.startActivity(i);

                break;

            //About, Valid Touch Bounds
            default:
                i = new Intent(activity.getApplicationContext(), SelectionActivity.class);
                i.putExtra("selection", selection);
                Log.i(LOG_TAG, "Shared Name --- " + v.findViewById(R.id.image).getTransitionName());
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity,
                        v.findViewById(R.id.image), v.findViewById(R.id.image).getTransitionName());
                activity.startActivity(i, transitionActivityOptions.toBundle());
                break;

        }
    }
}
