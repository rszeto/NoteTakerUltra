/* 
 *  This code specifies the activity that lets the user choose settings.
 *  
 *  Author: Ryan Szeto
 *  Last edit date: 1/16/2013
 */

package example.notetakerultra;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences_settings);
    }
}