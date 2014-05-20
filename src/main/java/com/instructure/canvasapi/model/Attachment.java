package com.instructure.canvasapi.model;


import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * @author Josh Ruesch
 *
 * Copyright (c) 2014 Instructure. All rights reserved.
 */

public class Attachment extends CanvasModel<Attachment> {

    private long id;
    private String display_name;

    @SerializedName("content-type")
    private String content_type;
    private String filename;
    private String url;
    private String thumbnail_url;

    ///////////////////////////////////////////////////////////////////////////
    // Getters and Setters
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public long getId(){
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getDisplayName() {
        return display_name;
    }
    public String getMimeType() {
        return content_type;
    }
    public String getFilename() {
        return filename;
    }
    public String getUrl() {
        return url;
    }
    public String getThumbnailUrl() {
        return thumbnail_url;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Required Overrides
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public Date getComparisonDate() {
        return null;
    }

    @Override
    public String getComparisonString() {
        return getDisplayName();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Constructors
    ///////////////////////////////////////////////////////////////////////////

    public Attachment() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.display_name);
        dest.writeString(this.content_type);
        dest.writeString(this.filename);
        dest.writeString(this.url);
        dest.writeString(this.thumbnail_url);
    }

    private Attachment(Parcel in) {
        this.id = in.readLong();
        this.display_name = in.readString();
        this.content_type = in.readString();
        this.filename = in.readString();
        this.url = in.readString();
        this.thumbnail_url = in.readString();
    }

    public static Creator<Attachment> CREATOR = new Creator<Attachment>() {
        public Attachment createFromParcel(Parcel source) {
            return new Attachment(source);
        }

        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };
}
