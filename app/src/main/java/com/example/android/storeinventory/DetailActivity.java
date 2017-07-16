package com.example.android.storeinventory;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.storeinventory.data.ProductContract;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();
    final Context mContext = this;
    private static final int PRODUCT_LOADER = 1;

    /** All UI Components */
    private TextView mTextViewLabelProduct;
    private TextView mTextViewLabelPrice;
    private TextView mTextViewLabelDiscount;
    private TextView mTextViewLabelStock;
    private TextView mTextViewLabelSupplier;
    private TextView mTextViewLabelPhone;
    private TextView mTextViewLabelEmail;
    private TextView mTextViewProduct;
    private TextView mTextViewProductPrice;
    private TextView mTextViewProductDiscount;
    private TextView mTextViewProductStock;
    private TextView mTextViewSupplier;
    private TextView mTextViewSupplierPhone;
    private TextView mTextViewSupplierEmail;
    private ImageButton mButtonPhone;
    private ImageButton mButtonEmail;
    private ImageButton mButtonDecrease;
    private ImageButton mButtonIncrease;
    private ImageView mImageProduct;

    private Uri mCurrentProductUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize all UI components
        initializeUIElements();

        // Set custom font on views
        setCustomTypeface();

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if(mCurrentProductUri != null) {
            getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
    }

    /**
     * This method initializes all UI components used in the Activity
     */
    public void initializeUIElements() {
        mTextViewLabelProduct = (TextView) findViewById(R.id.text_label_product);
        mTextViewLabelPrice = (TextView) findViewById(R.id.text_label_price);
        mTextViewLabelDiscount = (TextView) findViewById(R.id.text_label_discount);
        mTextViewLabelStock = (TextView) findViewById(R.id.text_label_stock);
        mTextViewLabelSupplier = (TextView) findViewById(R.id.text_label_supplier);
        mTextViewLabelPhone = (TextView) findViewById(R.id.text_label_phone);
        mTextViewLabelEmail = (TextView) findViewById(R.id.text_label_email);

        mTextViewProduct = (TextView) findViewById(R.id.text_product_name);
        mTextViewProductPrice = (TextView) findViewById(R.id.text_product_price);
        mTextViewProductDiscount = (TextView) findViewById(R.id.text_product_discount);
        mTextViewProductStock = (TextView) findViewById(R.id.text_stock);
        mTextViewSupplier = (TextView) findViewById(R.id.text_supplier_name);
        mTextViewSupplierPhone = (TextView) findViewById(R.id.text_supplier_phone);
        mTextViewSupplierEmail = (TextView) findViewById(R.id.text_supplier_email);
        mButtonEmail = (ImageButton) findViewById(R.id.button_email);
        mButtonPhone = (ImageButton) findViewById(R.id.button_phone);
        mButtonIncrease = (ImageButton) findViewById(R.id.button_increase);
        mButtonDecrease = (ImageButton) findViewById(R.id.button_decrease);
        mImageProduct = (ImageView) findViewById(R.id.image_product);
    }

    /**
     * This method sets custom font for all views
     */
    public void setCustomTypeface() {
        Utils.setCustomTypeface(mContext, mTextViewProduct);
        Utils.setCustomTypeface(mContext, mTextViewProductPrice);
        Utils.setCustomTypeface(mContext, mTextViewProductDiscount);
        Utils.setCustomTypeface(mContext, mTextViewProductStock);
        Utils.setCustomTypeface(mContext, mTextViewSupplier);
        Utils.setCustomTypeface(mContext, mTextViewSupplierEmail);
        Utils.setCustomTypeface(mContext, mTextViewSupplierPhone);

        Utils.setCustomTypeface(mContext, mTextViewLabelProduct);
        Utils.setCustomTypeface(mContext, mTextViewLabelPrice);
        Utils.setCustomTypeface(mContext, mTextViewLabelDiscount);
        Utils.setCustomTypeface(mContext, mTextViewLabelStock);
        Utils.setCustomTypeface(mContext, mTextViewLabelSupplier);
        Utils.setCustomTypeface(mContext, mTextViewLabelEmail);
        Utils.setCustomTypeface(mContext, mTextViewLabelPhone);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,                       // Parent activity context
                mCurrentProductUri,         // Table to query
                null,                       // Projection
                null,                       // Selection clause
                null,                       // Selection arguments
                null                        // Default sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if(cursor.moveToFirst()) {
            DatabaseUtils.dumpCursor(cursor);

            int productColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int discountColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_DISCOUNT_PRICE);
            int stockColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK);
            int offerColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_OFFER);
            int imageColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
            int supplierColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE);
            int emailColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL);

            // Extract out the value from the Cursor for the respective column index
            final String product = cursor.getString(productColumnIndex);
            Double price = cursor.getDouble(priceColumnIndex);
            Double discount = cursor.getDouble(discountColumnIndex);
            final int productStock = cursor.getInt(stockColumnIndex);
            int offerTag = cursor.getInt(offerColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            final String phone = cursor.getString(phoneColumnIndex);
            final String email = cursor.getString(emailColumnIndex);
            final String image = cursor.getString(imageColumnIndex);

            mTextViewProduct.setText(product);
            mTextViewProductPrice.setText(String.format("%.02f", price));
            mTextViewProductDiscount.setText(String.format("%.02f", discount));
            mTextViewProductStock.setText(Integer.toString(productStock));
            mTextViewSupplier.setText(supplier);
            mTextViewSupplierEmail.setText(email);

            if (!TextUtils.isEmpty(phone)) {
                mTextViewSupplierPhone.setText(phone);
                mButtonPhone.setVisibility(View.VISIBLE);
            } else {
                mButtonPhone.setVisibility(View.GONE);
            }

            // Display image attached to the product
            ViewTreeObserver viewTreeObserver = mImageProduct.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mImageProduct.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mImageProduct.setImageBitmap(Utils.getBitmapFromUri(Uri.parse(image), mContext, mImageProduct));
                }
            });

            // Set OnClickListener on stock decrease button
            mButtonDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adjustStock(mCurrentProductUri, (productStock - 1));
                }
            });

            // Set OnClickListener on stock increase button
            mButtonIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adjustStock(mCurrentProductUri, (productStock + 1));
                }
            });

            // Set OnClickListener on email
            mButtonEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderStockByEmail(email, getString(R.string.label_email_subject, product));
                }
            });

            // Set OnClickListener on call button
            if (mButtonPhone.getVisibility() == View.VISIBLE) {
                mButtonPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderStockByPhone(phone);
                    }
                });
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Default method overriden. No action here.
    }

    /**
     * Method to increase / decrease product stock
     * @param itemUri - uri of the product for which stock is to be updated
     * @param newStockCount - new stock
     * @return number of rows updated (this should be 1)
     */
    private int adjustStock(Uri itemUri, int newStockCount) {
        if (newStockCount < 0) {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK, newStockCount);
        int numRowsUpdated = getContentResolver().update(itemUri, values, null, null);
        return numRowsUpdated;
    }

    /**
     * Method to send email to supplier
     * @param emailAddress - email-id of supplier
     * @param emailSubject - email subject
     */
    public void orderStockByEmail (String emailAddress, String emailSubject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Method to call supplier
     * @param phoneNumber - supplier's phone number
     */
    public void orderStockByPhone(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Inflate the menu options from the res/menu/menu_delete.xml file.
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * Method to handle actions when individual menu item is clicked
     * @param item
     * @return true/false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.action_edit:
                editProduct();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                confirmDeleteProduct();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to launch EditorActivity with the product URI so the item can be edited
     */
    public void editProduct() {
        Intent intent = new Intent(DetailActivity.this, EditorActivity.class);
        intent.setData(mCurrentProductUri);
        startActivity(intent);
    }

    /**
     * Method to ask confirmation for deleting a product
     */
    private void confirmDeleteProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.dialog_delete));
        builder.setPositiveButton(getString(R.string.action_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.action_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Method to delete the product
     */
    private void deleteProduct() {

        // Only perform the delete if this is an existing product
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete
                Toast.makeText(this, getString(R.string.error_delete_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful
                Toast.makeText(this, getString(R.string.confirm_delete_successful), Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}