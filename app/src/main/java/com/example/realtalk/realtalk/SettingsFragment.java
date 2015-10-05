package com.example.realtalk.realtalk;


import android.os.Bundle;
import android.preference.PreferenceActivity;

import android.widget.Toast;

/**
 * Created by alexgomes on 2015-09-29. - alex.09hg@gmail.com
 */
public class SettingsFragment extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_settings);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Toast.makeText(SettingsFragment.this(), "HELLLO", Toast.LENGTH_SHORT).show();

    }

}
