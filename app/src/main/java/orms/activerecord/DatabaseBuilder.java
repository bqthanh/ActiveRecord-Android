package orms.activerecord;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import orms.activerecord.anotations.Column;
import orms.activerecord.utils.DBLog;
import orms.activerecord.utils.SQLiteUtils;

/**
 * Created by thanhbui on 2016/11/015.
 */

//データベース情報を持つクラス
public class DatabaseBuilder {

    protected String dbname;
    protected int version;

    //タブール情報を持つマッップ
    protected Map<Class<? extends Model>, Model.ModelManager> tables;

    public DatabaseBuilder(String dbname, int version) {
        this.dbname = dbname;
        this.version = version;
    }

    //テブール追加
    public <T extends Model> void addModel(Class<T> type) {
        if(tables == null) {
            tables = new LinkedHashMap<>();
        }

        Model.ModelManager tblManager = new Model.ModelManager(type);
        tables.put(type, tblManager);

        //モデールのクラスタイプを初期する
        try {
//            Field typeField = type.getField("type");
//            typeField.setAccessible(true);
//            typeField.set(null, type);

//            DBLog.log("type: " + type);
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
        }
    }

    //テブール一覧を取得する
    public Set<Class<? extends Model>> getModels() {
        return tables.keySet();
    }

    //初期の場合、テブール作成SQLを生成する
    public <T extends Model> String getSQLCreate(Class<T> type) {
        if (!tables.containsKey(type)) {
            DBLog.log("宣言していないテブール: " + type.getSimpleName());
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Model.ModelManager tbl = tables.get(type);
        List<Field> columns = tbl.getColumnFields();

        for (Field column : columns) {
            String colName = (SQLiteUtils.ID.equals(column.getName())) ? tbl.id
                    : column.getAnnotation(Column.class).name();

            String stype = SQLiteUtils.getSQLiteTypeString(
                    column.getType());

            if (!TextUtils.isEmpty(sb.toString())) {
                sb.append(", ");
            }
            sb.append(colName)
                    .append(" ");

            if (SQLiteUtils.ID.equals(column.getName())) {
                sb.append("INTEGER PRIMARY KEY AUTOINCREMENT");
            } else {
                sb.append(stype);
            }
        }

        return String.format("CREATE TABLE %s (%s)", tbl.getName(), sb.toString());
    }

    public String getSQLDrop(Class<? extends Model> type) {
        String table = tables.get(type).getName();
        return "DROP TABLE IF EXISTS " + table;
    }
}