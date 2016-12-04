package utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thanhbui on 2016/11/22.
 */

public class SQLiteUtils {
    //モデール定義からデータベースを自動に作成する
    public static boolean AUTO_CREATE_DB = true;

    //モデールにプライマリキーの変数を定義する
    public static String ID = "id";

    //データベースコマンドを定義する
    public enum  SQLCommand {
        CREATE(0), READ(1), UPDATE(2), DELETE(3);

        public int value;

        SQLCommand(int value) {
            this.value = value;
        }
    }

    public static String getSQLiteTypeString(Class<?> c) {
        String name = c.getName();
        if (name.equals("java.lang.String"))
            return "text";
        if (name.equals("short"))
            return "int";
        if (name.equals("int"))
            return "int";
        if (name.equals("long"))
            return "int";
        if (name.equals("java.lang.Long"))
            return "int";
        if (name.equals("java.sql.Timestamp"))
            return "int";
        if (name.equals("double"))
            return "real";
        if (name.equals("float"))
            return "real";
        if (name.equals("[B"))
            return "blob";
        if (name.equals("java.lang.Boolean"))
            return "bool";

        OrmLog.log("データタイプ以外：" + name);
        return null;
    }

    public List<String> convertToJson() throws JSONException {
        JSONObject obj = new JSONObject();
        List<String> sList = new ArrayList<String>();
        sList.add("val1");
        sList.add("val2");
        obj.put("list", sList);


        JSONArray jArray = obj.getJSONArray("list");
        for(int ii=0; ii < jArray.length(); ii++)
            System.out.println(jArray.getString(ii));

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
            OrmLog.log("Couldn't find meta-data: " + name);
        }

        return null;
    }
}