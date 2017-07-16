package com.example.android.storeinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.storeinventory.data.ProductContract;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = CatalogActivity.class.getName();

    final Context mContext = this;
    private static final int PRODUCT_LOADER = 1;
    private ListView mListViewProducts;
    private ProductCursorAdapter mCursorAdapter;
    private View mEmptyStateView;

    // UI Components
    private TextView mTextViewEmptyTitle;
    private TextView mTextViewEmptySubtitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Initialize UI components
        mTextViewEmptyTitle = (TextView) findViewById(R.id.text_empty_title);
        mTextViewEmptySubtitle = (TextView) findViewById(R.id.text_empty_subtitle);

        // Set custom font on views
        setCustomTypeface();

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the list data
        mListViewProducts = (ListView) findViewById(R.id.list_products);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items
        mEmptyStateView = findViewById(R.id.empty_view);
        mListViewProducts.setEmptyView(mEmptyStateView);

        // Set up adapter to create a list item for each row of data in the Cursor
        mCursorAdapter = new ProductCursorAdapter(this, null);
        mListViewProducts.setAdapter(mCursorAdapter);

        // Setup the item click listener
        mListViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

               // Create new intent to go to {@link DetailActivity}
               Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);

                // Form the content URI that represents the specific list item that was clicked on,
                // by appending the "id" onto the {@link ProductEntry#CONTENT_URI}.
                // Example => content://com.example.android.storeinventory/products/2, for product id = 2
                Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentProductUri);

                // Launch the {@link DetailActivity} to display the data for the current item
                startActivity(intent);
            }
        });

        // Start the loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    /**
     * This method sets custom font for all views
     */
    public void setCustomTypeface() {
        Utils.setCustomTypeface(mContext, mTextViewEmptyTitle);
        Utils.setCustomTypeface(mContext, mTextViewEmptySubtitle);
    }

    /**
     * Method to load the cursor with records fetched from database
     * @param i
     * @param bundle
     * @return cursor
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String querySortOder = null;
        String selectClause = null;
        final String SORT_ORDER_ASC = " ASC";
        final String SORT_ORDER_DESC = " DESC";

        // Get preference values
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        boolean isOnOffer = sharedPrefs.getBoolean(
                getString(R.string.settings_on_offer_key), false
        );

        // Create order by string for query based on preference values (order-by)
        if (orderBy.equals(getString(R.string.settings_order_by_oldest_value))) { // Order by ID ASC
            querySortOder = ProductContract.ProductEntry._ID + SORT_ORDER_ASC;
        } else if (orderBy.equals(getString(R.string.settings_order_by_newest_value))) { // Order by ID DESC
            querySortOder = ProductContract.ProductEntry._ID + SORT_ORDER_DESC;
        } else if (orderBy.equals(getString(R.string.settings_order_by_name_asc_value))) { // Order by Product Name ASC
            querySortOder = ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + SORT_ORDER_ASC;
        } else if (orderBy.equals(getString(R.string.settings_order_by_name_desc_value))) { // Order by Product Name DESC
            querySortOder = ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + SORT_ORDER_DESC;
        } else if (orderBy.equals(getString(R.string.settings_order_by_price_asc_value))) { // Order by Product Price ASC
            querySortOder = ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + SORT_ORDER_ASC;
        } else if (orderBy.equals(getString(R.string.settings_order_by_price_desc_value))) { // Order by Product Price DESC
            querySortOder = ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + SORT_ORDER_DESC;
        } else if (orderBy.equals(getString(R.string.settings_order_by_stock_asc_value))) { // Order by Product Stock ASC
            querySortOder = ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK + SORT_ORDER_ASC;
        } else if (orderBy.equals(getString(R.string.settings_order_by_stock_desc_value))) { // Order by Product Price DESC
            querySortOder = ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK + SORT_ORDER_DESC;
        }

        // Create selection clause for query based on preference values (on-offer)
        if (isOnOffer) {
            selectClause = "((" + ProductContract.ProductEntry.COLUMN_PRODUCT_OFFER + " = " +
                    ProductContract.ProductEntry.PRODUCT_ON_OFFER + "))";
        }

        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_DISCOUNT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,                       // Parent activity context
                ProductContract.ProductEntry.CONTENT_URI,   // Provider content URI to query
                projection,                                 // Columns to include in the resulting Cursor
                selectClause,                               // No selection clause
                null,                                       // No selection arguments
                querySortOder                               // Default sort order
        );
    }

    /**
     * Method to execute when cursor has finished loading
     * @param loader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update {@link ProductCursorAdapter} with this new cursor containing updated data
        mCursorAdapter.swapCursor(cursor);
    }

    /**
     * Method to execute when data needs to be reset
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    /**
     * Method to inflate menu
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    /**
     * Method to invoke actions when menu item clicked
     * @param item - menu item clicked
     * @return true/false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_settings:
                openSettingScreen();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method displays confirmation dialog prompting user to confirm before deleting all products
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_msg_delete);

        builder.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete all" button, confirming deleting all products
                deleteAllItems();
            }
        });
        builder.setNegativeButton(R.string.action_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
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
     * Method invoked when menu item clicked. Deletes all product items at one go.
     */
    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, null, null);
        if (rowsDeleted > 0) {
            Toast.makeText(CatalogActivity.this, getString(R.string.confirm_delete_all_entries),
                    Toast.LENGTH_SHORT).show();
        } else {
            Log.e(LOG_TAG, getString(R.string.error_delete_all_entries));
        }
    }

    /**
     * Method to open Settings Activity
     */
    public void openSettingScreen() {
        startActivity(new Intent(CatalogActivity.this, SettingsActivity.class));
    }

}
