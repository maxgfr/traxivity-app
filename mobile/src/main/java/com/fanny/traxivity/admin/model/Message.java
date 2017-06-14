package com.fanny.traxivity.admin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanny.traxivity.admin.view.activities.MessagesManager;


/**
 * Created by Alexandre on 18/04/2017.
 */

public class Message implements Parcelable{

    String content;

    protected Quote quote;

    //protected MessagesManager.BCTCategory bctCategory;
    protected String category;

    protected MessagesManager.Achievement achievement;

    protected String dayWeek;

    public String getDayWeek() {
        return dayWeek;
    }

    public void setDayWeek(String dayWeek) {
        this.dayWeek = dayWeek;
    }

    public Quote getQuote() {
        return quote;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public MessagesManager.Achievement getAchievement() {
        return achievement;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAchievement(MessagesManager.Achievement achievement) {
        this.achievement = achievement;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeParcelable(quote, flags);
        dest.writeString(category);
        dest.writeString(achievement.name());
        dest.writeString(dayWeek);
    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    // "De-parcel object
    public Message(Parcel in) {
        content = in.readString();
        quote = in.readParcelable(Quote.class.getClassLoader());
        category = in.readString();
        achievement = MessagesManager.Achievement.valueOf(in.readString());
        dayWeek = in.readString();
    }

    public Message(){
        quote = null;
        content = "";
        category = "";
        achievement = MessagesManager.Achievement.Low;
        dayWeek = "";
    }

    public Message(Message message){
        this.content = message.getContent();
        if(message.getQuote() != null)
            this.quote = new Quote(message.getQuote().getAuthor(), message.getQuote().getContent());
        this.achievement = message.getAchievement();
        this.category = message.getCategory();
    }

    public Message(String content, Quote quote, String category, MessagesManager.Achievement achievement, String dayWeek) {
        this.content = content;
        this.quote = quote;
        this.category = category;
        this.achievement = achievement;
        this.dayWeek = dayWeek;
    }
}

