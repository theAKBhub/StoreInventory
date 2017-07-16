package com.example.android.storeinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.android.storeinventory.R;


/**
 * {@link ContentProvider} for Inventory App.
 */

public class ProductProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the PRODUCTS table */
    private static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single item in the PRODUCTS table */
    private static final int PRODUCT_ID = 101;

    /** Database Helper Object */
    private ProductDbHelper mDbHelper;

    /** Column Tags used only for validation purpose */
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_DISCOUNT = "discount";
    private static final String TAG_STOCK = "stock";
    private static final String TAG_SUPPLIER = "supplier";
    private static final String TAG_EMAIL = "email";

    /** UriMatcher object to match a content URI to a corresponding code */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    /** Static initializer - run the first time anything is called from this class */
    static {
        // Content URI of the form "content://package-name/products"
        // This URI is used to provide access to multiple table rows.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);

        // Content URI of the form "content://package-name/products/#", where # is the ID value
        // This is used to provide access to single row of the table.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    /**
     * Function - READ from table
     * Peform query for given URI and load the cursor with results fetched from table.
     * The returned result can have multiple rows or a single row, depending on given URI.
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return cursor
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get instance of readable database
        SQLiteDatabase sqLiteDBReadable = mDbHelper.getReadableDatabase();

        // Cursor to hold the query result
        Cursor cursor;

        // Check if the uri matches to a specific URI CODE
        int match =  sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                cursor =  sqLiteDBReadable.query(ProductContract.ProductEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor =  sqLiteDBReadable.query(ProductContract.ProductEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException(getContext().getString(R.string.exception_unknown_uri, uri));
        }

        // Set notification URI on Cursor so it knows when to update in the event the data in cursor changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    /**
     * Function - INSERT into table
     * This method inserts records in the table
     * @param uri
     * @param contentValues
     * @return uri
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id;
        String columnsToValidate = TAG_NAME + "|" + TAG_PRICE + "|" + TAG_DISCOUNT + "|"
                                    + TAG_STOCK + "|" + TAG_SUPPLIER + "|" + TAG_EMAIL;

        boolean isValidInput = validateInput(contentValues, columnsToValidate);

        if (isValidInput) {
            SQLiteDatabase sqLiteDBWritable = mDbHelper.getWritableDatabase();
            id = sqLiteDBWritable.insert(ProductContract.ProductEntry.TABLE_NAME,
                    null, contentValues);
        } else {
            id = -1;
        }

        // Check if ID is -1, which means record insert has failed
        if (id == -1) {
            Log.e(LOG_TAG, (getContext().getString(R.string.error_insert, uri)));
            return null;
        }

        // Notify all listeners that the data has changed
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID of the newly inserted row appended at the end
        return ContentUris.withAppendedId(uri, id);
    }


    /**
     * Function - UPDATE table
     * This method updates products
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return int
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);

            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException(getContext().getString(R.string.exception_unknown_uri, uri));
        }
    }


    /**
     * This method validates input data for updates and applies the updates
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return
     */
    public int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        String columnsToValidate = null;
        StringBuilder stringBuilder = new StringBuilder();
        final String SEPARATOR = "|";
        int rowsUpdated = 0;

        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        } else {
            if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)) {
                stringBuilder.append(TAG_NAME);
            } else if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)) {
                stringBuilder.append(SEPARATOR).append(TAG_PRICE);
            } else if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_DISCOUNT_PRICE)) {
                stringBuilder.append(SEPARATOR).append(TAG_DISCOUNT);
            } else if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK)) {
                stringBuilder.append(SEPARATOR).append(TAG_STOCK);
            } else if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME)) {
                stringBuilder.append(SEPARATOR).append(TAG_SUPPLIER);
            } else if (contentValues.containsKey(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL)) {
                stringBuilder.append(SEPARATOR).append(TAG_EMAIL);
            }

            columnsToValidate = stringBuilder.toString();
            boolean isValidInput = validateInput(contentValues, columnsToValidate);

            if (isValidInput) {
                SQLiteDatabase sqLiteDBWritable = mDbHelper.getWritableDatabase();

                // Perform the update on the database and get the number of rows affected
                rowsUpdated = sqLiteDBWritable.update(ProductContract.ProductEntry.TABLE_NAME,
                        contentValues, selection, selectionArgs);

                // If 1 or more rows were updated, then notify all listeners that the data at the
                // given URI has changed
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
            }

            return rowsUpdated;
        }
    }


    /**
     * This method validates the inputs used in INSERT and UPDATE queries
     * @param values - ContentValues
     * @param columns - Tags for columns that need to be validated
     * @return true/false
     */
    public boolean validateInput(ContentValues values, String columns) {

        String [] columnArgs = columns.split("|");
        String productName = null;
        Double productPrice = null;
        Double productDiscountPrice = null;
        Integer flagOnOffer = null;
        Integer productStock = null;
        String supplier = null;
        String supplierEmail = null;


        for (int i = 0; i < columnArgs.length; i++ ) {

            if (columnArgs[i].equals(TAG_NAME)) {
                // Check if Product Name is not null
                productName = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
                if (productName == null || productName.trim().length() == 0) {
                    throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_name));
                }
            }
            else if (columnArgs[i].equals(TAG_PRICE)) {
                // Check if Price is provided
                productPrice = values.getAsDouble(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
                if (productPrice == null || productPrice < 0) {
                    throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_price));
                }
            }
            else if (columnArgs[i].equals(TAG_DISCOUNT)) {
                productDiscountPrice = values.getAsDouble(ProductContract.ProductEntry.COLUMN_PRODUCT_DISCOUNT_PRICE);
                flagOnOffer = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_OFFER);

                // Check if Discount Price, if provided, is valid
                if (productDiscountPrice != null && productDiscountPrice < 0) {
                    throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_discount_price));
                }

                // Check if On-Offer Flag is valid
                if (flagOnOffer == null || (!ProductContract.ProductEntry.isValidOfferFlag(flagOnOffer))) {
                    throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_stock));
                }

                // Check if Discount Price is entered, then On-Offer is not switched on
                if (productDiscountPrice!= null && productDiscountPrice > 0
                        && (flagOnOffer != ProductContract.ProductEntry.PRODUCT_ON_OFFER)) {
                    throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_discount_offer));
                }

                // Check if On-Offer is switched on, then Discount is > 0
                if ((productDiscountPrice == null || productDiscountPrice == 0) &&
                        (flagOnOffer == ProductContract.ProductEntry.PRODUCT_ON_OFFER)) {
                    throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_discount_offer));
                }

            }
            else if (columnArgs[i].equals(TAG_STOCK)) {
                productStock = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK);
                if (productStock == null || productStock < 0) {
                    throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_stock));
                }
            }
            else if (columnArgs[i].equals(TAG_SUPPLIER)) {
                supplier = values.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
                if (supplier == null || supplier.trim().length() == 0) {
                    throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_supplier));
                }
            }
            else if (columnArgs[i].equals(TAG_EMAIL)) {
                supplierEmail = values.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL);
                if (supplierEmail == null || supplierEmail.trim().length() == 0) {
                    throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_supplier_email));
                }
            }
        }

        return true;
    }


    /**
     * Function - DELETE from table
     * This method deletes records from the table
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return number of rows deleted
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase sqLiteDBWritable = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = sqLiteDBWritable.delete(ProductContract.ProductEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = sqLiteDBWritable.delete(ProductContract.ProductEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException(getContext().getString(R.string.exception_unknown_uri, uri));
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Method to determine type of URI used to query the table
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch(match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;

            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException(getContext().getString(R.string.exception_unknown_uri, uri));
        }
    }

}
