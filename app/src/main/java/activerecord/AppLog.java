package activerecord;

import android.util.Log;

/**
 * Created by thanhbui on 2016/11/17.
 */

public class AppLog {
    public static String TAG = "ActiveRecord";

    public static void log(String content) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, content);
        }
    }
}