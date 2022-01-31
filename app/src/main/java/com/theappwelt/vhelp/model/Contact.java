package com.theappwelt.vhelp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "contactId")
    public int id;

    @ColumnInfo(name = "contactName")
    public String name;

    @ColumnInfo(name = "contactMobile")
    public String mobile;
    @ColumnInfo(name = "isCall")
    public boolean isCall;
    @ColumnInfo(name = "isSms")
    public boolean isSms;
    @ColumnInfo(name = "relationship")
    public String relationship;

    @ColumnInfo(name = "message")
    public String Message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isCall() {
        return isCall;
    }

    public void setCall(boolean call) {
        isCall = call;
    }

    public boolean isSms() {
        return isSms;
    }

    public void setSms(boolean sms) {
        isSms = sms;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Contact() {
    }

    public Contact(String name, String mobile, String relationship, boolean isCall, boolean isSms, String message) {
        this.name = name;
        this.mobile = mobile;
        this.relationship = relationship;
        this.isCall = isCall;
        this.isSms = isSms;
        this.Message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(mobile);
        dest.writeBoolean(isCall);
        dest.writeBoolean(isSms);
        dest.writeString(relationship);
        dest.writeString(Message);
    }

    protected Contact(Parcel in) {
        id = in.readInt();
        name = in.readString();
        mobile = in.readString();
        isCall = in.readByte() != 0;
        isSms = in.readByte() != 0;
        relationship = in.readString();
        Message = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

}
