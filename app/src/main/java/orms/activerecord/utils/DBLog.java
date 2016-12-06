package orms.activerecord.utils;

import android.util.Log;

import orms.activerecord.BuildConfig;

/**
 * Created by thanhbui on 2016/11/17.
 */

public class DBLog {

    public static String TAG = "Orms";

    public static void log(String content) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, content);
        }
    }
}