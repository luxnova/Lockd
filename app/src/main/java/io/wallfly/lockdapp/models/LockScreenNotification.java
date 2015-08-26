package io.wallfly.lockdapp.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JoshuaWilliams on 6/27/15.
 */
public class LockScreenNotification {
    String packageName;
    String text;
    int picture;
    String title;

    public LockScreenNotification(String packageName, String title, String text, int picture){
        setPackageName(packageName);
        setTitle(title);
        setText(text);
        setIconResource(picture);
    }


    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName(){
        return packageName;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }

    public void setIconResource(int picture){
        this.picture = picture;
    }

    public int getIcon(){
        return picture;
    }

    @Override
    public String toString() {
        return "Title : " + title + "  Text : " +  "  Icon : " + picture;
    }

}
