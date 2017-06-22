package com.example.android.inventoryapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.Data.InvContract.InvEntry;

/**
 * Created by nalin on 16-Jun-17.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "surbhi.db";

    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_INV_TABLE = "CREATE TABLE " + InvEntry.TABLE_NAME + "(" +
                InvEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InvEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL," +
                InvEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL," +
                InvEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                InvEntry.COLUMN_PRODUCT_IMAGE + " TEXT);" ;
        db.execSQL(SQL_CREATE_INV_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
