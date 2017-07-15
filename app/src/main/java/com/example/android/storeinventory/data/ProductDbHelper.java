package com.example.android.storeinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for Products table. Manages database creation and version management.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "inventory.db";

    /** Database version */
    private static final int DATABASE_VERSION = 1;


    /**
     * Default Constructor
     * @param context
     */
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Function - CREATE table
     * This method is called when the database is created for the first time
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String TYPE_TEXT = " TEXT";
        final String TYPE_INTEGER = " INTEGER";
        final String TYPE_FLOAT = " REAL";
        final String TYPE_TIMESTAMP = " TIMESTAMP";
        final String NOT_NULL = " NOT NULL";
        final String COMMA_SEP = ", ";

        // Create a String that contains the SQL statement to create the table
        String SQL_CREATE_TABLE =  "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ("
                + ProductContract.ProductEntry._ID + TYPE_INTEGER + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + TYPE_TEXT + NOT_NULL + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + TYPE_FLOAT + NOT_NULL + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_PRODUCT_DISCOUNT_PRICE + TYPE_FLOAT + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK + TYPE_INTEGER + NOT_NULL + " DEFAULT 0" + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_PRODUCT_OFFER + TYPE_INTEGER + NOT_NULL + " DEFAULT 0" + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE + TYPE_TEXT + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + TYPE_TEXT + NOT_NULL + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE + TYPE_TEXT + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL + TYPE_TEXT + NOT_NULL + COMMA_SEP
                + ProductContract.ProductEntry.COLUMN_LAST_UPDATED + TYPE_TIMESTAMP + NOT_NULL + " DEFAULT CURRENT_TIMESTAMP"
                + ")";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
