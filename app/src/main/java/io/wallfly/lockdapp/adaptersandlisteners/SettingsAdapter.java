package io.wallfly.lockdapp.adaptersandlisteners;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.hmkcode.android.recyclerview.R;
import java.util.ArrayList;
import java.util.List;

import io.wallfly.lockdapp.lockutils.CustomLockBaseListener;
import io.wallfly.lockdapp.lockutils.Utils;
import io.wallfly.lockdapp.models.SettingsSelection;

// must use onItemClickListener but don't know how to implement
public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = "SettingsAdapter";
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<SettingsSelection> settingsSelections = new ArrayList<>();
    private SettingsOnClickListener settingsOnClickListener;
    private View mHeaderView;
    private Activity activity;

    public SettingsAdapter(Activity activity, List<SettingsSelection> itemsData, View headerView) {
        this.settingsSelections = itemsData;
        this.mHeaderView = headerView;
        settingsOnClickListener = new SettingsOnClickListener(activity);
        this.activity = activity;
    }

    // Create new views (invoked by layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_HEADER){
            return new HeaderViewHolder(mHeaderView);
        }
        // create ViewHolder
        return new SettingsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, null));
    }


    // Return the size of your settingsSelections (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return settingsSelections.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof SettingsViewHolder){
            SettingsSelection settingSelection = getItem(position);
            settingSelection.setPosition(position);
            SettingsViewHolder settingsViewHolder = (SettingsViewHolder) viewHolder;

            Utils.getImageLoaderWithConfig().displayImage("drawable://" + settingSelection.getImageResource(), settingsViewHolder.imgViewIcon);
            settingsViewHolder.mainTextView.setText(settingSelection.getTitle());
            settingsViewHolder.mainTextView.setTypeface(Utils.getRoboto());
            settingsViewHolder.subTextView.setTypeface(Utils.getRoboto());

            settingsViewHolder.layout.setOnClickListener(settingsOnClickListener);
            settingsViewHolder.switchView.setOnClickListener(settingsOnClickListener);

            settingsViewHolder.switchView.setTag(settingSelection);
            settingsViewHolder.layout.setTag(settingSelection);

            setUpView(settingsViewHolder, settingSelection);

        }
    }

    private void setUpView(SettingsViewHolder settingsViewHolder, SettingsSelection selection) {
        switch (selection.getPosition()){
            case 1:
                Utils.setViewBackground(R.drawable.ripple_green, settingsViewHolder.layout);
                settingsViewHolder.switchView.setVisibility(View.VISIBLE);
                settingsViewHolder.subTextView.setVisibility(View.VISIBLE);

                if(Utils.getSetting(R.string.twenty_four_hour_clock)){
                    settingsViewHolder.subTextView.setText(Utils.getString(R.string.display_24_clock_text));
                    settingsViewHolder.switchView.setChecked(true);
                }
                else{
                    settingsViewHolder.subTextView.setText(Utils.getString(R.string.display_12_hour_clock_text));
                    settingsViewHolder.switchView.setChecked(false);
                }

                break;

            case 2:
                Utils.setViewBackground(R.drawable.ripple_gray, settingsViewHolder.layout);
                settingsViewHolder.switchView.setVisibility(View.VISIBLE);
                settingsViewHolder.subTextView.setVisibility(View.VISIBLE);

                if(Utils.getSetting(R.string.weather_showing)){
                    settingsViewHolder.subTextView.setText(Utils.getString(R.string.display_weather_text));
                    settingsViewHolder.switchView.setChecked(true);
                }
                else{
                    settingsViewHolder.subTextView.setText(Utils.getString(R.string.weather_not_displayed_text));
                    settingsViewHolder.switchView.setChecked(false);
                }
                break;

            case 3:
                Utils.setViewBackground(R.drawable.ripple_red, settingsViewHolder.layout);
                settingsViewHolder.switchView.setVisibility(View.VISIBLE);
                settingsViewHolder.switchView.setChecked(CustomLockBaseListener.getVibrateOnTouch());
                settingsViewHolder.subTextView.setVisibility(View.GONE);
                settingsViewHolder.mainTextView.setPadding(0, 5, 0, 0);

                break;

            case 4:
                Utils.setViewBackground(R.drawable.ripple_brown, settingsViewHolder.layout);
                settingsViewHolder.switchView.setVisibility(View.GONE);
                settingsViewHolder.subTextView.setVisibility(View.VISIBLE);
                if(CustomLockBaseListener.getSecondsBetweenTaps() > 1999){
                    settingsViewHolder.subTextView.setText(Utils.getFirstCharacter(CustomLockBaseListener.getSecondsBetweenTaps())
                            + " seconds");
                }
                else{
                    settingsViewHolder.subTextView.setText(Utils.getFirstCharacter(CustomLockBaseListener.getSecondsBetweenTaps())
                            + " second");
                }
                break;

            case 5:
                setDefaultWithBackground(settingsViewHolder, R.drawable.ripple_blue);

                break;

            case 6:
                setDefaultWithBackground(settingsViewHolder, R.drawable.ripple_yellow);

                break;

            case 7:
                setDefaultWithBackground(settingsViewHolder, R.drawable.ripple_teal);

                break;

            case 8:
                setDefaultWithBackground(settingsViewHolder, R.drawable.ripple_black);

                break;
            case 9:
                setDefaultWithBackground(settingsViewHolder, R.drawable.ripple_cyan);

                break;
        }
    }




    private void setDefaultWithBackground(SettingsViewHolder settingsViewHolder, int drawable){
        settingsViewHolder.switchView.setVisibility(View.GONE);
        settingsViewHolder.subTextView.setVisibility(View.GONE);
        settingsViewHolder.mainTextView.setPadding(0, 5, 0 ,0);
        if(drawable != 0) Utils.setViewBackground(drawable, settingsViewHolder.layout);
    }

    public SettingsSelection getItem(int position){
        return settingsSelections.get(position);
    }


    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }


    // inner class to hold a reference to each item of RecyclerView
    public static class SettingsViewHolder extends RecyclerView.ViewHolder {
        public TextView mainTextView, subTextView;
        public ImageView imgViewIcon;
        public Switch switchView;
        public RelativeLayout layout;

        public SettingsViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mainTextView = (TextView) itemLayoutView.findViewById(R.id.textView1);
            subTextView = (TextView) itemLayoutView.findViewById(R.id.textView2);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.image);
            switchView = (Switch) itemLayoutView.findViewById(R.id.switch_view);
            layout = (RelativeLayout) itemLayoutView.findViewById(R.id.layout);
        }
    }
}


