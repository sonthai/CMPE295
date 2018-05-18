package com.quangpham.drs.dto;

import android.provider.BaseColumns;

/**
 * Created by quangpham on 4/1/18.
 */

public class UserInfo {
    private String fullName;
    private String gender;
    private String email;
    private String birthDate;
    private String avatarBase64;
    private String yearJoined;

    public UserInfo() {}

    public UserInfo(String mEmail, String year) {
        email = mEmail;
        fullName = gender = birthDate = avatarBase64 = null;
        yearJoined = year;
    }

    public UserInfo(String mEmail, String mFullName, String mGender, String mBirthDate) {
        fullName = mFullName;
        gender = mGender;
        email = mEmail;
        birthDate = mBirthDate;
        avatarBase64 = yearJoined = null;
    }

    public String getAvatar() {
        return avatarBase64;
    }

    public void setAvatar(String avatar) {
        this.avatarBase64 = avatar;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String mBirthDate) {
        birthDate = mBirthDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String mFulName) {
        fullName = mFulName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mEmail) {
        email = mEmail;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getYearJoined() {
        return yearJoined;
    }

    public void setYearJoined(String yearJoined) {
        this.yearJoined = yearJoined;
    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_info";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_FULLNAME = "full_name";
        public static final String COLUMN_NAME_BIRTHDATE = "birthdate";
        public static final String COLUMN_NAME_AVATAR_BASE64 = "avatar";
        public static final String COLUMN_NAME_YEAR_JOINED = "year_joined";

    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    FeedEntry.COLUMN_NAME_FULLNAME + " TEXT," +
                    FeedEntry.COLUMN_NAME_GENDER + " TEXT," +
                    FeedEntry.COLUMN_NAME_BIRTHDATE + " TEXT," +
                    FeedEntry.COLUMN_NAME_AVATAR_BASE64 + " TEXT," +
                    FeedEntry.COLUMN_NAME_YEAR_JOINED + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
}
