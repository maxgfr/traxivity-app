package com.fanny.traxivity.admin.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alexandre on 22/05/2017.
 */

public class Information implements Parcelable{
    protected String type;
    protected String content;

    public Information(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public Information(){
        type = "";
        content = "";
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Information createFromParcel(Parcel in) {
            return new Information(in);
        }

        public Information[] newArray(int size) {
            return new Information[size];
        }
    };

    // "De-parcel object
    public Information(Parcel in) {
        type = in.readString();
        content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(content);
    }
}
