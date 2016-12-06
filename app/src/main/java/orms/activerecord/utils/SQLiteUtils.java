package orms.activerecord.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by thanhbui on 2016/11/22.
 */

public class SQLiteUtils {
    //モデールにプライマリキーの変数を定義する
    public static String ID = "id";

    public static String getSQLiteTypeString(Class<?> c) {
        String name = c.getName();
        if (name.equals("java.lang.String"))
            return "text";
        if (name.equals("short")
                || name.equals("java.lang.Short"))
            return "int";
        if (name.equals("int"))
            return "int";
        if (name.equals("java.lang.Long")
                || name.equals("long"))
            return "int";
        if (name.equals("float"))
            return "real";
        if (name.equals("double"))
            return "real";
        if (name.equals("java.util.Date"))
            return "date";
        if (name.equals("[B"))
            return "blob";
        if (name.equals("java.lang.Boolean"))
            return "bool";

        DBLog.log("データタイプ以外：" + name);
        return null;
    }

    public static <T> T getMetaData(Context context, String name) {
        try {
            final ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);

            if (ai.metaData != null) {
                return (T) ai.metaData.get(name);
            }
        }
        catch (Exception e) {
            DBLog.log("Couldn't find meta-data: " + name);
        }

        return null;
    }
}