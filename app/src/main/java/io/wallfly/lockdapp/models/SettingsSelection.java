package io.wallfly.lockdapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Joshua Williams on 5/15/15
 *
 * @version 1.0
 *
 * Model class for a settings selection.
 */
public class SettingsSelection implements Parcelable {

    public static final String CLOCK = "Clock";
    public static final String WEATHER = "Weather";
    public static final String VIBRATE_ON_TOUCH = "Vibrate on touch";
    public static final String SECONDS_BETWEEN_TAPS = "Seconds between taps";
    public static final String VALID_TOUCH_RADIUS = "Valid touch radius";
    public static final String RATE = "Rate";
    public static final String ABOUT = "About";
    public static final String PRIVACY_POLICY = "Privacy policy";
    public static final String HELP = "Help";
    public static final String BACKUP_PASSWORD = "Change backup password";

    private String title;
    private int imageResource;
    private int position;

    public SettingsSelection(){
        this.title = "";
        this.imageResource = 0;
    }

    public SettingsSelection(String title, int imageResource) {
        setTitle(title);
        setImageResource(imageResource);
    }

    public SettingsSelection(Parcel in){
        String[] data = new String[3];
        in.readStringArray(data);

        setTitle(data[0]);
        setImageResource(Integer.parseInt(data[1]));
        setPosition(Integer.parseInt(data[2]));
    }

    public void setPosition(int position){
        this.position = position;
    }
    public int getPosition(){return position;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String toString(){
        return "Title = " + getTitle() + " Image Resource = " + getImageResource() + " Position = " + getPosition();
    }




    /************************************* Parcelable Interface *********************************/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {getTitle(),
                Integer.toString(getImageResource()),
                Integer.toString(getPosition())});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SettingsSelection createFromParcel(Parcel in) {
            return new SettingsSelection(in);
        }

        public SettingsSelection[] newArray(int size) {
            return new SettingsSelection[size];
        }
    };

    /************************************* Parcelable Interface *********************************/
}
