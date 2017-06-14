package com.fanny.traxivity.admin.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alexandre on 18/04/2017.
 */

public class Quote implements Parcelable {

    protected String author;
    protected String content;

    // Default constructor required for calls to DataSnapshot.getValue(User.class) (Firebase)
    public Quote() {
    }

    public Quote(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "<b>" + author + "</b> : <br>" + "<p>\"" + content + "\" </p>";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };

    // "De-parcel object
    public Quote(Parcel in) {
        author = in.readString();
        content = in.readString();
    }
}
