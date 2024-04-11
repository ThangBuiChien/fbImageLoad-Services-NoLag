package com.example.w10up_firebase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DataClass implements Parcelable {
    private String imageURL, caption;

    public DataClass(){

    }

    protected DataClass(Parcel in) {
        imageURL = in.readString();
        caption = in.readString();
    }

    public static final Creator<DataClass> CREATOR = new Creator<DataClass>() {
        @Override
        public DataClass createFromParcel(Parcel in) {
            return new DataClass(in);
        }

        @Override
        public DataClass[] newArray(int size) {
            return new DataClass[size];
        }
    };

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public DataClass(String imageURL, String caption) {
        this.imageURL = imageURL;
        this.caption = caption;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(imageURL);
        dest.writeString(caption);
    }
}