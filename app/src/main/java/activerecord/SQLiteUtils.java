package activerecord;

/**
 * Created by thanhbui on 2016/11/22.
 */

public class SQLiteUtils {
    //モデールにプライマリキーの変数を定義する
    public static String ID = "id";

    public static String getSQLiteTypeString(Class<?> c) {
        String name = c.getName();
        if (name.equals("java.lang.String")) {
            return "text";
        } else if (name.equals("short")
                || name.equals("java.lang.Short")) {
            return "int";
        } else if (name.equals("int")
                || name.equals("java.lang.Integer")) {
            return "int";
        } else if (name.equals("long")
                || name.equals("java.lang.Long")) {
            return "int";
        } else if (name.equals("float")
                || name.equals("java.lang.Float")) {
            return "real";
        } else if (name.equals("double")
                || name.equals("java.lang.Double")) {
            return "real";
        } else if (name.equals("java.util.Date")) {
            return "date";
        } else if (name.equals("java.lang.Boolean")) {
            return "bool";
        } else if (name.equals("[B")) {
            return "blob";
        }
        AppLog.log("データタイプ以外：" + name);
        return null;
    }
}