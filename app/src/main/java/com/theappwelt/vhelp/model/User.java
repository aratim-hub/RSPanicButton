package com.theappwelt.vhelp.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "userId")
    public int id;

    @ColumnInfo(name = "userMobile")
    public String mobile;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    @ColumnInfo(name = "userName")
    public String username;

    @ColumnInfo(name = "userOccupation")
    public String occupation;

    public User() {
    }

    public User(int id, String mobile, String username,String occupation) {
        this.id = id;
        this.mobile = mobile;
        this.occupation = occupation;
        this.username = username;

    }
    public User(String mobile) {
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
