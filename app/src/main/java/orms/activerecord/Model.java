package orms.activerecord;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import orms.activerecord.anotations.Column;
import orms.activerecord.anotations.Table;
import orms.activerecord.interfaces.IModel;
import orms.activerecord.utils.DBLog;
import orms.activerecord.utils.SQLiteUtils;

/**
 * Created by thanhbui on 2016/11/17.
 */

public class Model implements IModel {
    //モデールのクラスタイプ
    //private Class <? extends Model> type;
    //メーデールの主なカギ
    private long id = 0;
    //モデール名
    public ModelManager table;
    //データベース
    public SQLiteDatabase db;

    public Model() {
        db = Database.db;
        table = Database.getTableManger(this.getClass());
    }

    //オブジェクトidを取得する
    public long getId() { return id; }

    //対象オブジェクトと比較する
    public boolean equals(Object targetObj) {
        if(targetObj == null) return false;
        if (targetObj instanceof Model && this.id != 0) {
            final Model other = (Model) targetObj;

            return this.id == other.id
                    && (this.table.getName().equals(other.table.getName()));
        } else {
            return this == targetObj;
        }
    }

    //データベースにモデルオブジェクトを保存する
    public void save() {
        List<Field> columns = table.getColumnFields();
        ContentValues values = new ContentValues(table.getSize());
        String colName;

        for (Field column : columns) {
            column.setAccessible(true);

            if(SQLiteUtils.ID.equals(column.getName())) {
                if(id == 0) continue;
                colName = table.id;
            } else {
                colName = column.getAnnotation(Column.class).name();
            }
            try {
                Object obj = column.get(this);
                values.put(colName, (obj == null) ? "" : String.valueOf(obj));
            } catch (Exception ex) {
                DBLog.log(ex.getLocalizedMessage());
            }
        }
        if (id == 0) {
            id = Database.insert(table.getName(), values);
        }
        else {
            int cnt = Database.update(table.getName(), values, table.id + "= ?", new String[] {String.valueOf(id)});
        }
    }

    public int delete() {
        int toRet = Database.delete(table.getName(), table.id + " = ?",
                new String[] { String.valueOf(id) });
        return toRet;
    }

    /*
     *クラスインタフェース
     */
    public static <T extends Model> T findById(Class<T> type, long id) {
        T entity = null;

        try {
            entity = (T) type.newInstance();
        } catch (Exception ex) {
            DBLog.log(ex.getLocalizedMessage());
        }

        Cursor c = null;
        try {
            c = Database.query(entity.table.getName(), null, entity.table.id + " = ?",
                    new String[] { String.valueOf(id) });
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
        }

        entity = null;
        try {
            while (c.moveToNext()) {
                entity = (T) type.newInstance();
                entity.loadRecord(c);
                break;
            }
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
        }
        finally {
            c.close();
        }
        return entity;
    }

    /*
     *クラスインタフェース
     */
    public static <T extends Model> List<T> findByIds(Class<T> type, long[] ids) {
        T entity = null;

        try {
            entity = (T) type.newInstance();
        } catch (Exception ex) {
            DBLog.log(ex.getLocalizedMessage());
        }

        List<T> toRet = new ArrayList<>();
        Cursor c = null;
        try {
            String idList = "";
            for (long id : ids) {
                idList += ("".equals(idList)) ? id : ", " + id;
            }

            c = Database.query(entity.table.getName(), null,
                    String.format(entity.table.id + " IN ( %s )", idList), null);
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
        }

        try {
            while (c.moveToNext()) {
                entity = (T) type.newInstance();
                entity.loadRecord(c);
                toRet.add(entity);
            }
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
        }
        finally {
            c.close();
        }
        return toRet;
    }

    public static <T extends Model> List<T> find(Class<T> type, String whereClause, String[] whereArgs) {
        T entity = null;

        try {
            entity = (T) type.newInstance();
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
        }

        List<T> toRet = new ArrayList<T>();
        Cursor c = Database.query(entity.table.getName(), null, whereClause,
                whereArgs);
        try {
            while (c.moveToNext()) {
                entity = (T) type.newInstance();
                entity.loadRecord(c);
                toRet.add(entity);
            }
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
        } finally {
            c.close();
        }
        return toRet;
    }

    public static <T extends Model> List<T> findByColumn(Class<T> type, String column, String value) {
        return find(type, String.format("%s = ?", column),
                new String[] { value });
    }

    public static <T extends Model> List<T> findAll(Class<T> type) {
        return find(type, null, null);
    }

    //レコード削除する
    public static <T extends Model> int deleteById(Class<T> type, long id) {
        T entity = null;
        try {
            entity = (T) type.newInstance();
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
        }
        return delete(type, entity.table.id,
                new String[] {String.valueOf(id)});
    }

    public static <T extends Model> int deleteByIds(Class<T> type, long[] ids) {
        T entity = null;
        try {
            entity = (T) type.newInstance();
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
        }
        String idList = "";
        for (long id : ids) {
            idList += ("".equals(idList)) ? id : ", " + id;
        }

        return delete(type, String.format(entity.table.id + " IN ( %s )", idList) , null);
    }

    public static <T extends Model> int delete(Class<T> type, String whereClause, String[] whereArgs) {
        T entity = null;
        try {
            entity = (T) type.newInstance();
        } catch (Exception e) {
            DBLog.log(e.getLocalizedMessage());
        }
        return Database.delete(entity.table.getName(), whereClause, whereArgs);
    }

    //カラムデータに一致するレコドを削除する
    public static <T extends Model>int deleteByColumn(Class<T> type, String column, String value) {
        return delete(type, String.format("%s = ?", column),
                new String[] { value });
    }

//    public static void execute(String sql) {
//        Database.execute(sql);
//    }

    //カソールからレコードコラムデータを取得する
    void loadRecord(Cursor c) throws Exception {
        String typeString = null, colName = null;

        for (Field field : table.getColumnFields()) {
            field.setAccessible(true);
            try {
                typeString = field.getType().getName();
                colName = (SQLiteUtils.ID.equals(field.getName())) ? table.id
                        : field.getAnnotation(Column.class).name();


                if (typeString.equals("java.lang.String")) {
                    String val = c.getString(c.getColumnIndex(colName));
                    field.set(this, val.equals("null") ? null : val);
                } else if (typeString.equals("short")
                        || typeString.equals("java.lang.Short")) {
                    field.set(this, c.getShort(
                            c.getColumnIndex(colName)));
                } else if (typeString.equals("int")) {
                    field.setInt(this, c.getInt(
                            c.getColumnIndex(colName)));
                } else if (typeString.equals("long")
                        || typeString.equals("java.lang.Long")) {
                    field.set(this, c.getLong(
                            c.getColumnIndex(colName)));
                } else if (typeString.equals("float")) {
                    field.setFloat(this, c.getFloat(
                            c.getColumnIndex(colName)));
                } else if (typeString.equals("double")) {
                    field.setDouble(this, c.getDouble(c
                            .getColumnIndex(colName)));
                } else if (typeString.equals("java.util.Date")) {
                    String s = c.getString(c.getColumnIndex(colName));
                    if (s != null && !TextUtils.isEmpty(s)) {
                        field.set(this, new Date(s));
                    }
                } else if (typeString.equals("java.lang.Boolean")) {
                    field.set(this, c.getString(
                            c.getColumnIndex(colName)).equals("true"));
                } else if (typeString.equals("[B")) {
                    field.set(this, c.getBlob(c.getColumnIndex(colName)));
                }
                else
                    throw new Exception(
                            "データタイプ以外：" + typeString + " : " + colName);
            } catch (IllegalArgumentException e) {
                DBLog.log("モデール" + typeString + " : " + e.getLocalizedMessage());
            }
        }
    }

    //テブール情報を持つクラス
    public static class ModelManager {
        protected Class<? extends Model> type;
        protected String id;
        protected String name;

        //テブールにフィールド一覧を持つマップ
        private List<Field> columns = new ArrayList<>();

        //コンストラクタ
        public ModelManager(Class<? extends Model> type) {
            this.type = type;
            Table an = type.getAnnotation(Table.class);

            if (an != null) {
                id = an.id();
                name = an.name();
            } else {
                id = Table.ID_NAME;
                name = type.getSimpleName();
            }

            columns.add(this.getIdField(type));
            columns.addAll(this.getColumnFieldsWithoutId(type));
        }

        //主なキーを取得する
        protected Field getIdField(Class type) {
            if (Model.class.equals(type)) {
                try {
                    return type.getDeclaredField(SQLiteUtils.ID);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            } else if (type.getSuperclass() != null) {
                return getIdField(type.getSuperclass());
            }

            return null;
        }

        //モデールフィルード一覧を取得する
        public List<Field> getColumnFieldsWithoutId(Class<?> type) {
            Field[] fields = type.getDeclaredFields();
            List<Field> columns = new ArrayList<>();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    columns.add(field);
                }
            }

            Class<?> parentType = type.getSuperclass();
            if(parentType != null) {
                columns.addAll(getColumnFieldsWithoutId(parentType));
            }

            return columns;
        }

        //モデールフィルード一覧を取得する
        public List<Field> getColumnFields() {
            return columns;
        }

        //テブール名を返す
        public String getName() {
            return this.name;
        }

        public int getSize() {
            return columns.size();
        }
    }
}