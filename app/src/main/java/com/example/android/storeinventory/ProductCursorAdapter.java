package com.example.android.storeinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.android.storeinventory.data.ProductContract;
import static android.content.ContentValues.TAG;


/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of products data as its data source.
 */

public class ProductCursorAdapter extends CursorAdapter {

    private static Context mContext;

    /**
     * Constructs a new {@link ProductCursorAdapter}
     * @param context - Activity context
     * @param cursor - Cursor containing data loaded from table
     */
    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
    }

    /**
     * This class describes the view items to create a list item
     */
    public static class ProductViewHolder {

        TextView textViewProductName;
        TextView textViewPrice;
        TextView textViewDiscount;
        TextView textViewStock;
        ImageButton buttonSale;

        // Find various views within ListView and set custom typeface on them
        public ProductViewHolder(View itemView) {
            textViewProductName = (TextView) itemView.findViewById(R.id.text_product_name);
            textViewPrice = (TextView) itemView.findViewById(R.id.text_product_price);
            textViewDiscount = (TextView) itemView.findViewById(R.id.text_product_discount);
            textViewStock = (TextView) itemView.findViewById(R.id.text_product_stock);
            buttonSale = (ImageButton) itemView.findViewById(R.id.button_sale);

            Utils.setCustomTypeface(mContext, textViewProductName);
            Utils.setCustomTypeface(mContext, textViewPrice);
            Utils.setCustomTypeface(mContext, textViewDiscount);
            Utils.setCustomTypeface(mContext, textViewStock);
        }
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     * @param context - Activity context
     * @param cursor - Cursor containing data loaded from table
     * @param parent - Parent view
     * @return new list item view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent ,false);
        ProductViewHolder holder = new ProductViewHolder(view);
        view.setTag(holder);

        return view;
    }

    /**
     * This method binds the data in the current row pointed to by cursor to the given
     * list item layout.
     * @param view - ListView
     * @param context - Activity context
     * @param cursor - Cursor containing data loaded from table
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ProductViewHolder holder = (ProductViewHolder)view.getTag();

        // Set data to respective views within ListView
        final int productId = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry._ID));
        String productName = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
        Double discountPrice = cursor.getDouble(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_DISCOUNT_PRICE));
        final int stock = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK));

        holder.textViewProductName.setText(productName);
        holder.textViewPrice.setText(mContext.getString(R.string.display_product_price, price.doubleValue()));
        holder.textViewStock.setText(mContext.getString(R.string.display_product_stock, stock));


        if (discountPrice > 0) {
            holder.textViewDiscount.setText(mContext.getString(R.string.display_product_sale_price, discountPrice.doubleValue()));
        } else if (discountPrice == null || discountPrice == 0.0) {
            holder.textViewDiscount.setText("");
        }

        // Bind sale event to list item button so quantity is reduced with each sale
        holder.buttonSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri productUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, productId);
                adjustStock(context, productUri, stock);
            }
        });
    }

    /**
     * This method reduced product stock by 1
     * @param context - Activity context
     * @param productUri - Uri used to update the stock of a specific product in the ListView
     * @param currentStock - current stock of that specific product
     */
    private void adjustStock(Context context, Uri productUri, int currentStock) {

        // Reduce stock, check if new stock is less than 0, in which case set it to 0
        int newStock = (currentStock >= 1) ? currentStock - 1 : 0;

        // Update table with new stock of the product
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductContract.ProductEntry.COLUMN_PRODUCT_STOCK, newStock);
        int numRowsUpdated = context.getContentResolver().update(productUri, contentValues, null, null);

        // Display error message in Log if product stock fails to update
        if (!(numRowsUpdated > 0)) {
            Log.e(TAG, context.getString(R.string.error_stock_update));
        }
    }
}
