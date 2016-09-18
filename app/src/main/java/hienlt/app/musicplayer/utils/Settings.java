package hienlt.app.musicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by hienl_000 on 4/6/2016.
 */
public class Settings {
    private static Settings ourInstance;
    private SharedPreferences preferences;
    private Context context;

    public static Settings getInstance() {
        if(ourInstance == null)
            ourInstance = new Settings();
        return ourInstance;
    }

    // Initialize the internal setting
    public void init(Context context){
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Reset all setting to default
     */
    public void reset(){
        this.preferences.edit().clear().commit();
    }

    //Query method

    /**
     * Get Boolean setting
     */
    public boolean get(String key, boolean defaultValue){
        if(preferences==null)
            return defaultValue;
        return preferences.getBoolean(key,defaultValue);
    }

    /**
     * Get String setting
     */
    public String get(String key, String defaultValue){
        if(preferences==null)
            return defaultValue;
        return preferences.getString(key, defaultValue);
    }

    /**
     * Get int setting
     */
    public int get(String key, int defaultValue){
        if(preferences==null)
            return defaultValue;
        return preferences.getInt(key, defaultValue);
    }

    /**
     * put boolean setting
     * @param key
     * @param bool
     */
    public void put(String key, boolean value){
        if(preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    /**
     * Put int setting
     * @param key
     * @param value
     */
    public void put(String key, int value){
        if(preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key,value);
        editor.commit();
    }

    /**
     * Put String setting
     * @param key
     * @param value
     */
    public void put(String key, String value){
        if(preferences == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private Settings() {
    }
}
