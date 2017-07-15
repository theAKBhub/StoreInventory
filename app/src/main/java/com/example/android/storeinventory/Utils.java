package com.example.android.storeinventory;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

/**
 * This class contains common methods
 * Author : aditibhattacharya
 */


public class Utils {

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

}
