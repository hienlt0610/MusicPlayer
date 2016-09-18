package hienlt.app.musicplayer.ui.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import hienlt.app.musicplayer.R;
import hienlt.app.musicplayer.utils.Common;

/**
 * Created by hienl_000 on 5/11/2016.
 */
public class SettingActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,new MainSettingFragment()).commit();
    }

    public static class MainSettingFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_setting);
        }
    }
}
