package com.example.android.storeinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Database schema for Products Database
 */

public class ProductContract {

    /**
     * Empty Constructor
     */
    private ProductContract() {}


    /** ContentProvider Name */
    public static final String CONTENT_AUTHORITY = "com.example.android.storeinventory";

    /** ContentProvider Base Uri */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** Path appended to base URI for possible URI's */
    public static final String PATH_PRODUCTS = "products";


    /**
     * Inner class that defines constant values for the Products table.
     * Each entry in the table represents a single product.
     */
    public static class ProductEntry implements BaseColumns {

        /** The content URI to access the data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /** MIME type of the {@link #CONTENT_URI} for a list of items */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /** MIME type of the {@link #CONTENT_URI} for a single item */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


        /** Name of database table */
        public static final String TABLE_NAME = "products";

        /** Unique ID - Type: INTEGER */
        public final static String _ID = BaseColumns._ID;

        /** Product Name - Type: TEXT */
        public final static String COLUMN_PRODUCT_NAME ="product_name";

        /** Product Price - Type: REAL */
        public final static String COLUMN_PRODUCT_PRICE ="product_price";

        /** Product Discount Price - Type: REAL */
        public final static String COLUMN_PRODUCT_DISCOUNT_PRICE ="product_discount_price";

        /** Product Stock - Type: INTEGER */
        public final static String COLUMN_PRODUCT_STOCK ="product_stock";

        /** Product On Offer Flag - Type: INTEGER */
        public final static String COLUMN_PRODUCT_OFFER ="product_offer_flag";

        /** Product Image - Type: TEXT */
        public final static String COLUMN_PRODUCT_IMAGE ="product_image";

        /** Supplier Name - Type: TEXT */
        public final static String COLUMN_SUPPLIER_NAME ="product_supplier_name";

        /** Supplier Phone - Type: TEXT */
        public final static String COLUMN_SUPPLIER_PHONE ="product_supplier_phone";

        /** Supplier Email - Type: TEXT */
        public final static String COLUMN_SUPPLIER_EMAIL ="product_supplier_email";

        /** Last Updated - Type: TIMESTAMP */
        public final static String COLUMN_LAST_UPDATED ="product_last_updated";


        /** Possible values for Product Offer Flag */
        public static final int PRODUCT_ON_OFFER = 1;
        public static final int PRODUCT_NOT_ON_OFFER = 0;


        /** Returns whether or not the given Product Offer Flag is valid */
        public static boolean isValidOfferFlag(int offerFlag) {
            if (offerFlag == PRODUCT_ON_OFFER || offerFlag == PRODUCT_NOT_ON_OFFER) {
                return true;
            }
            return false;
        }
    }
}
