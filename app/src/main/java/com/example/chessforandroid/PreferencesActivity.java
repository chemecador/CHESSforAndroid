package com.example.chessforandroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * PrefrencesActivity.
 */
public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
    }
}