package com.quangpham.drs;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by quangpham on 4/4/18.
 */

public class FeedReaderDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "FeedReader.db";

    public FeedReaderDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserInfo.SQL_CREATE_ENTRIES);
        db.execSQL(ProductInfo.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(UserInfo.SQL_DELETE_ENTRIES);
        db.execSQL(ProductInfo.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static void resetTable(SQLiteDatabase db) {

        db.execSQL(UserInfo.SQL_DELETE_ENTRIES);
        db.execSQL(ProductInfo.SQL_DELETE_ENTRIES);
        db.execSQL(UserInfo.SQL_CREATE_ENTRIES);
        db.execSQL(ProductInfo.SQL_CREATE_ENTRIES);
        db.close();
    }

    public Cursor getAllUserEntry(SQLiteDatabase db) {
        Cursor result = db.rawQuery("SELECT * FROM " + UserInfo.FeedEntry.TABLE_NAME, null);
        return result;
    }

    public Cursor getUserEntryByEmail(SQLiteDatabase db, String email) {
        Cursor result = db.rawQuery("SELECT * FROM " + UserInfo.FeedEntry.TABLE_NAME + " " +
                        "WHERE " + UserInfo.FeedEntry.COLUMN_NAME_EMAIL + " = '" + email + "'", null);
        return result;
    }

    public Cursor getAllProductEntry(SQLiteDatabase db) {
        Cursor result = db.rawQuery("SELECT * FROM " + ProductInfo.FeedEntry.TABLE_NAME, null);
        return result;
    }

    public Cursor getProductEntry(SQLiteDatabase db, ProductInfo productInfo) {
        Cursor result = db.rawQuery("SELECT * FROM " + ProductInfo.FeedEntry.TABLE_NAME +
                                    " WHERE " + ProductInfo.FeedEntry.COLUMN_NAME_IMAGE_NAME + "='" +
                                    productInfo.getProductImageName() + "'", null);
        return result;
    }

    public boolean insertProductEntry(ProductInfo productInfo) {
        boolean flag = false;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("INSERT INTO " + ProductInfo.FeedEntry.TABLE_NAME +
                        " VALUES('" + productInfo.getProductImageName() + "','" +
                                productInfo.getProductName() + "','" +
                                productInfo.getProductPrice() + "','" +
                                productInfo.getBitmapBase64() + "')");
            flag = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return flag;
    }

    public boolean removeProductEntry(ProductInfo productInfo) {
        boolean flag = false;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + ProductInfo.FeedEntry.TABLE_NAME +
                    " WHERE " + ProductInfo.FeedEntry.COLUMN_NAME_IMAGE_NAME + "='" +
                    productInfo.getProductImageName() + "'");
            flag = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return flag;
    }

    public boolean updateAvatarByID(UserInfo entry) {
        boolean flag = false;

        if (entry.getAvatar() != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            try {
                db.execSQL("UPDATE " + UserInfo.FeedEntry.TABLE_NAME +
                        " SET " + UserInfo.FeedEntry.COLUMN_NAME_AVATAR_BASE64 + " = '" + entry.getAvatar() + "' " +
                        " WHERE " + UserInfo.FeedEntry.COLUMN_NAME_EMAIL + " = '" + entry.getEmail() + "'"
                );
                flag = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            db.close();
        }
        return flag;
    }

    public boolean updateUserEntryByID(UserInfo entry) {
        boolean flag = false;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("UPDATE " + UserInfo.FeedEntry.TABLE_NAME +
                    " SET " + UserInfo.FeedEntry.COLUMN_NAME_FULLNAME + " = '" + entry.getFullName() + "', " +
                    UserInfo.FeedEntry.COLUMN_NAME_GENDER + " = '" + entry.getGender() + "', " +
                    UserInfo.FeedEntry.COLUMN_NAME_BIRTHDATE + " = '" + entry.getBirthDate() + "' " +
                    " WHERE " + UserInfo.FeedEntry.COLUMN_NAME_EMAIL + " = '" + entry.getEmail() + "'"
            );
            flag = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        return flag;
    }
}
