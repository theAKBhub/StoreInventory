package com.example.android.storeinventory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    public static final String LOG_TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class ProductPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        SharedPreferences preferences;
        SharedPreferences.Editor editor;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            Preference orderByPref = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderByPref);

            Preference onOfferPref = findPreference(getString(R.string.settings_on_offer_key));
            onOfferPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preferences.edit().putBoolean(getString(R.string.settings_on_offer_key), ((Boolean) newValue));
                    return true;
                }
            });
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
