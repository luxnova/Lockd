package io.wallfly.lockdapp.adaptersandlisteners;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.hmkcode.android.recyclerview.R;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.apache.commons.lang3.StringUtils;

import io.wallfly.lockdapp.activities.SelectionActivity;
import io.wallfly.lockdapp.lockutils.AbstractLockdBaseListener;
import io.wallfly.lockdapp.lockutils.LockUtils;
import io.wallfly.lockdapp.lockutils.Utils;
import io.wallfly.lockdapp.models.SettingsSelection;

/**
 * Created by JoshuaWilliams on 5/26/15.
 *
 * @version 1.0
 *
 * On click listener for the settings recycler adapter.
 */
public class SettingsOnClickListener implements View.OnClickListener {
    private static final String LOG_TAG = "SettingsItemClick";
    private Activity activity;
    private android.support.v7.app.AlertDialog alertDialog;
    private String password, backupPassword;
    private boolean cancel;

    public SettingsOnClickListener(Activity activity){
        this.activity = activity;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(final View v) {
        SettingsSelection selection = (SettingsSelection) v.getTag();
        Log.i(LOG_TAG, "Selection ------ " + selection.toString());

        switch (selection.getTitle()) {
            //Clock Setting
            case SettingsSelection.CLOCK:
                String militaryClockSetting =  Utils.getString(R.string.twenty_four_hour_clock);

                //Toggles the switch and updates views.
                if(LockUtils.getSetting(R.string.twenty_four_hour_clock)) {
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
                } else {
                    Utils.saveToSharedPrefsString(militaryClockSetting, "true");

                    Switch switchView;
                    TextView subTextView;
                    if(v.getId() == R.id.switch_view){
                        RelativeLayout layout = (RelativeLayout) v.getParent();
                        switchView = (Switch) layout.findViewById(R.id.switch_view);
                        subTextView = (TextView) layout.findViewById(R.id.textView2);
                    } else {
                        switchView = (Switch) v.findViewById(R.id.switch_view);
                        subTextView = (TextView) v.findViewById(R.id.textView2);
                    }
                    subTextView.setText(Utils.getString(R.string.display_24_clock_text));
                    switchView.setChecked(true);
                }

                Log.i(LOG_TAG, "Changing clock setting to " + LockUtils.getSetting(R.string.twenty_four_hour_clock));

                break;

            //Weather Display Setting
            case SettingsSelection.WEATHER:
                String weatherDisplaySetting = Utils.getString(R.string.weather_showing);

                //Toggles the switch and updates views.
                if(LockUtils.getSetting(R.string.weather_showing)) {
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
                } else {
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

                Log.i(LOG_TAG, "Changing weather setting to " + LockUtils.getSetting(R.string.weather_showing));

                break;

            //Vibrate Setting
            case SettingsSelection.VIBRATE_ON_TOUCH:
                Log.i(LOG_TAG, "Vibrate on touch - " + !AbstractLockdBaseListener.getVibrateOnTouch());
                AbstractLockdBaseListener.setVibrateOnTouch(!AbstractLockdBaseListener.getVibrateOnTouch());

                Switch switchView;
                if (v.getId() == R.id.switch_view) {
                    RelativeLayout layout = (RelativeLayout) v.getParent();
                    switchView = (Switch) layout.findViewById(R.id.switch_view);
                } else {
                    switchView = (Switch) v.findViewById(R.id.switch_view);
                }

                switchView.setChecked(AbstractLockdBaseListener.getVibrateOnTouch());

                if(AbstractLockdBaseListener.getVibrateOnTouch()){
                    ((Vibrator)Utils.getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
                }


                break;

            //Seconds between taps Setting
            case SettingsSelection.SECONDS_BETWEEN_TAPS:
                final TextView secondsTV = (TextView) v.findViewById(R.id.textView2);

                View dialoglayout = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null);

                DiscreteSeekBar seekBar = (DiscreteSeekBar) dialoglayout.findViewById(R.id.seekbar);
                seekBar.setTrackColor(Color.parseColor("#ffffff"));
                seekBar.setScrubberColor(Utils.getColor(R.color.brown));
                seekBar.setThumbColor(Utils.getColor(R.color.brown), Utils.getColor(R.color.brown));
                seekBar.setProgress((int) AbstractLockdBaseListener.getSecondsBetweenTaps()/1000);
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

                        AbstractLockdBaseListener.setSecondsBetweenTaps(i * 1000);
                    }

                    @Override
                    public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {

                    }
                });

                final Dialog seekbarDialog = new Dialog(activity);
                seekbarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                seekbarDialog.setContentView(dialoglayout);
                seekbarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                seekbarDialog.show();
                break;

            //Rate
            case SettingsSelection.RATE:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("market://details?id=" + activity.getPackageName()));
                activity.startActivity(i);

                break;

            case SettingsSelection.BACKUP_PASSWORD:
                final View view = LayoutInflater.from(activity).inflate(R.layout.confirm_password_layout, null);
                final EditText backupPasswordTV = (EditText) view.findViewById(R.id.backup_password_tv);

                alertDialog = new android.support.v7.app.AlertDialog.Builder(v.getContext())
                        .setTitle("Enter Password")
                        .setView(view)
                        .setPositiveButton(Utils.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                password = backupPasswordTV.getText().toString();
                                cancel = false;
                            }
                        })
                        .setNegativeButton(Utils.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                                cancel = true;
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                if(cancel) return;

                                if(Utils.getStringFromSharedPrefs(Utils.getString(R.string.backup_password))
                                        .equals(password)) {
                                    showNewPasswordDialog(view.getContext());
                                }
                                else {
                                    Utils.showToast("Incorrect password.");
                                    alertDialog.show();
                                }
                            }
                        })
                        .create();

                alertDialog.show();

                break;
            //About, Valid Touch Bounds, TODO: Notifications
            default:
                if(StringUtils.isNotBlank(selection.getTitle())){
                    i = new Intent(activity.getApplicationContext(), SelectionActivity.class);
                    i.putExtra("selection", selection);
                    Log.i(LOG_TAG, "Shared Name --- " + v.findViewById(R.id.image).getTransitionName());
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity,
                            v.findViewById(R.id.image), v.findViewById(R.id.image).getTransitionName());
                    activity.startActivity(i, transitionActivityOptions.toBundle());
                }
                break;

        }
    }

    /**
     * Shows the dialog for the user to select a new password.
     *
     * @param context - the current activity context.
     */
    private void showNewPasswordDialog(Context context) {
        final View view = LayoutInflater.from(activity).inflate(R.layout.confirm_password_layout, null);

        final EditText backupPasswordTV = (EditText) view.findViewById(R.id.backup_password_tv);
        final EditText confirmBackupPasswordTV = (EditText) view.findViewById(R.id.confirm_backup_password_tv);

        confirmBackupPasswordTV.setVisibility(View.VISIBLE);

        alertDialog = new android.support.v7.app.AlertDialog.Builder(context)
                .setTitle("Enter Password")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton(Utils.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        password = backupPasswordTV.getText().toString();
                        backupPassword = confirmBackupPasswordTV.getText().toString();
                        cancel = false;
                    }
                })
                .setNegativeButton(Utils.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancel = true;
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(cancel) return;

                        if(!password.isEmpty() && password.equals(backupPassword)){
                            Utils.saveToSharedPrefsString(Utils.getString(R.string.backup_password), password);
                        }
                        else{
                            Utils.showToast("Passwords do not match.");
                            alertDialog.show();
                        }
                    }
                })
                .create();

        alertDialog.show();
    }

}
