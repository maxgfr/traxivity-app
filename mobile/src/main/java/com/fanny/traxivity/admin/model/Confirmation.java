package com.fanny.traxivity.admin.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alexandre on 25/05/2017.
 */

public class Confirmation extends Information implements Parcelable {

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

    public Confirmation(String type, String content) {
        super(type, content);
    }

    // "De-parcel object
    public Confirmation(Parcel in) {
        type = in.readString();
        content = in.readString();
    }

    public Confirmation() {
        super();
    }

    public Confirmation(Information information){
        this.type = information.getType();
        this.content = information.getContent();
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
