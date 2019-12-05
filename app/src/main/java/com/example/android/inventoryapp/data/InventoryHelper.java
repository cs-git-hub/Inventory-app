package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.android.inventoryapp.data.InventoryContract.InventoryItems;
import java.io.ByteArrayOutputStream;

public class InventoryHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 3;

    public InventoryHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE =  "CREATE TABLE " + InventoryItems.TABLE_NAME + " ("
                + InventoryItems._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + InventoryItems.COLUMN_NAME + " TEXT NOT NULL,"
                + InventoryItems.COLUMN_PRICE + " INTEGER,"
                + InventoryItems.COLUMN_QUANTITY + " INTEGER,"
                + InventoryItems.COLUMN_IMAGE + " BLOB,"
                + InventoryItems.COLUMN_SUP_NAME + " TEXT NOT NULL,"
                + InventoryItems.COLUMN_SUP_PHONE + " TEXT,"
                + InventoryItems.COLUMN_SUP_EMAIL + " TEXT)";
        db.execSQL(SQL_CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InventoryItems.TABLE_NAME);
        onCreate(db);
    }
    public static byte[] getAsByteArray(Bitmap bm){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
    public static Bitmap getBitmap(byte[] image){
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
