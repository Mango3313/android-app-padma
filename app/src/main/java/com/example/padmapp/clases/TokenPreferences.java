package com.example.padmapp.clases;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class TokenPreferences {
    public static final String KEY_PREFS_SMS_BODY = "header_token";
    private static final String APP_SHARED_PREFS = TokenPreferences.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    public TokenPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    public String getToken() {
        return _sharedPrefs.getString(KEY_PREFS_SMS_BODY, "");
    }

    public void setToken(String text) {
        _prefsEditor.putString(KEY_PREFS_SMS_BODY, text);
        _prefsEditor.commit();
    }
    public void destroyToken(){
        _prefsEditor.clear();
    }
}
