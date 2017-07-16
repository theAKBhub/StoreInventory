package com.example.android.storeinventory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * This class contains common methods
 * Author : aditibhattacharya
 */


public class Utils {

    public static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * This is a private constructor and only meant to hold static variables and methods,
     * which can be accessed directly from the class name Utils
     */
    private void Utils() {
    }

    /**
     * Method to set custom typeface to UI elements
     */
    public static void setCustomTypeface(Context context, View view) {
        Typeface typefaceRegular = Typeface.createFromAsset(context.getAssets(),
                "fonts/merriweathersans_r.otf");
        Typeface typefaceBold = Typeface.createFromAsset(context.getAssets(),
                "fonts/merriweathersans_b.otf");
        Typeface typefaceItalic = Typeface.createFromAsset(context.getAssets(),
                "fonts/merriweathersans_i.otf");
        Typeface typefaceTilte = Typeface.createFromAsset(context.getAssets(),
                "fonts/amaranth_r.otf");

        // Get tag on the view
        String viewTag = view.getTag().toString();

        // Cast view to appropriate view element based on tag received and set typefaces
        if (viewTag.equals(context.getString(R.string.tag_typeface_r))) {
            TextView textView = (TextView) view;
            textView.setTypeface(typefaceRegular);
        } else if (viewTag.equals(context.getString(R.string.tag_typeface_b))) {
            TextView textView = (TextView) view;
            textView.setTypeface(typefaceBold);
        } else if (viewTag.equals(context.getString(R.string.tag_typeface_i))) {
            TextView textView = (TextView) view;
            textView.setTypeface(typefaceItalic);
        } else if (viewTag.equals(context.getString(R.string.tag_typeface_title))) {
            TextView textView = (TextView) view;
            textView.setTypeface(typefaceTilte);
        }
    }

    /**
     * Method to display the image
     * Credit => Used function from https://github.com/crlsndrsjmnz/MyShareImageExample
     * as was recommended as best practice for image display by forum mentor @sudhirkhanger
     * @param uri - image path
     * @return Bitmap
     */
    public static Bitmap getBitmapFromUri(Uri uri, Context context, ImageView imageView) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        InputStream input = null;
        try {
            input = context.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            input = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, context.getString(R.string.exception_image_load_failed), fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, context.getString(R.string.exception_image_load_failed), e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }
}
