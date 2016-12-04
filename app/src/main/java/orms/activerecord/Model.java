package orms.activerecord;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import orms.activerecord.anotation.Column;
import orms.activerecord.anotation.Table;
import orms.activerecord.realization.IModel;
import utils.OrmLog;
import utils.SQLiteUtils;

/**
 * Created by thanhbui on 2016/11/17.
 */

public abstract class Model implements IModel {
    //モデールのクラスタイプ
    public static Class <? extends Model> type;
    //メーデールの主なカギ
    private long id = 0;
    //モデール名
    public ModelManager table;
    //データベース
    public SQLiteDatabase db;

    public Model() {
        db = Database.db;
        table = Database.getTableManger(type);
    }

    //オブジェクトidを取得する
    public long getId() { return id; }

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
                values.put(colName,
                        String.valueOf(column.get(this)));
            } catch (Exception ex) {
                OrmLog.log(ex.getLocalizedMessage());
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

    //対象オブジェクトと比較する
    public boolean equals(Object obj) {
        if (obj instanceof Model && this.id != 0) {
            final Model other = (Model) obj;

            return this.id == other.id
                    && (this.table.getName().equals(other.table.getName()));
        } else {
            return this == obj;
        }
    }

    /*
     *クラスインタフェース
     */
    public static <T extends Model> T findById(long id) {
        T entity = null;
        OrmLog.log("T123456: " + type);

        try {
            entity = (T) type.newInstance();
        } catch (Exception ex) {
            OrmLog.log(ex.getLocalizedMessage());
        }

        Cursor cursor = null;
        try {
            cursor = Database.query(entity.table.getName(), null, entity.table.id + " = ?",
                    new String[] { String.valueOf(id) });
        } catch (Exception e) {
            OrmLog.log(e.getLocalizedMessage());
        }

        entity = null;
        try {
            while (cursor.moveToNext()) {
                entity = (T) type.newInstance();
                entity.loadRecord(cursor);
            }
        } catch (Exception e) {
            OrmLog.log(e.getLocalizedMessage());
        }
        finally {
            cursor.close();
        }
        return entity;
    }

    public static <T extends Model> List<T> find(String whereClause, String[] whereArgs) {
        T entity = null;

        try {
            entity = (T) type.newInstance();
        } catch (Exception e) {
            OrmLog.log(e.getLocalizedMessage());
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
            OrmLog.log(e.getLocalizedMessage());
        } finally {
            c.close();
        }
        return toRet;
    }

    public <T extends Model> List<T> findByColumn(String column, String value) throws Exception {
        return find(String.format("%s = ?", column),
                new String[] { value });
    }

    public static <T extends Model> List<T> findAll() {
        return find(null, null);
    }

    public static <T extends Model> List<T> rawQuery(String sql, String[] params) {
        T entity = null;

        try {
            entity = (T)type.newInstance();
        } catch (Exception e) {
            OrmLog.log(e.getLocalizedMessage());
        }

        List<T> toRet = new ArrayList<T>();
        Cursor c = Database.rawQuery(sql, params);

        try {
            while (c.moveToNext()) {
                entity = (T)type.newInstance();
                entity.loadRecord(c);
                toRet.add(entity);
            }
        } catch (Exception e) {
            OrmLog.log(e.getLocalizedMessage());
        }
        finally {
            c.close();
        }
        return toRet;
    }

    //レコード削除する
    public static <T extends Model> long delete(String whereClause, String[] whereArgs) {
        T entity = null;
        try {
            entity = (T) type.newInstance();
        } catch (Exception e) {
            OrmLog.log(e.getLocalizedMessage());
        }
        return Database.delete(entity.table.getName(), whereClause, whereArgs);
    }

    public static <T extends Model> long deleteById(long id) {
        T entity = null;
        try {
            entity = (T) type.newInstance();
        } catch (Exception e) {
            OrmLog.log(e.getLocalizedMessage());
        }
        return delete(entity.table.id,
                new String[] {String.valueOf(id)});
    }

    public static <T extends Model> long deleteByIds(long[] ids) {
        T entity = null;
        try {
            entity = (T) type.newInstance();
        } catch (Exception e) {
            OrmLog.log(e.getLocalizedMessage());
        }
        String idList = null;
        for (long id : ids) {
            idList += (idList == null) ? id : ", " + id;
        }

        return delete(String.format(entity.table.id + " IN ( %s )", idList) , null);
    }

    //カラムデータに一致するレコドを削除する
    public static <T extends Model> long deleteByColumn(Class<T> type,
                                                        String column, String value) throws Exception {
        return delete(String.format("%s = ?", column),
                new String[] { value });
    }

    public static void execute(String sql) {
        Database.execute(sql);
    }

    //カソールからレコードコラムデータを取得する
    void loadRecord(Cursor cursor) throws Exception {
        for (Field field : table.getColumnFields()) {
            field.setAccessible(true);
            try {
                String typeString = field.getType().getName();
                String colName = (SQLiteUtils.ID.equals(field.getName())) ? table.id
                        : field.getAnnotation(Column.class).name();

                if (typeString.equals("long")) {
                    field.set(this, cursor.getLong(cursor
                            .getColumnIndex(colName)));
                } else if (typeString.equals("java.lang.String")) {
                    String val = cursor.getString(cursor.getColumnIndex(colName));
                    field.set(this, val.equals("null") ? null : val);
                } else if (typeString.equals("double")) {
                    field.setDouble(this, cursor.getDouble(cursor
                            .getColumnIndex(colName)));
                } else if (typeString.equals("java.lang.Boolean")) {
                    field.set(this, cursor.getString(
                           cursor.getColumnIndex(colName)).equals("true"));
                } else if (typeString.equals("[B")) {
                    field.set(this, cursor.getBlob(cursor.getColumnIndex(colName)));
                } else if (typeString.equals("int")) {
                    field.setInt(this, cursor.getInt(cursor
                            .getColumnIndex(colName)));
                } else if (typeString.equals("float")) {
                    field.setFloat(this, cursor.getFloat(cursor
                            .getColumnIndex(colName)));
                } else if (typeString.equals("short")) {
                    field.setShort(this, cursor.getShort(cursor
                            .getColumnIndex(colName)));
                } else if (typeString.equals("java.sql.Timestamp")) {
                    long l = cursor.getLong(cursor.getColumnIndex(colName));
                    field.set(this, new Timestamp(l));
                } else
                    throw new Exception(
                            "データタイプ以外：" + typeString + " : " + colName);
            } catch (IllegalArgumentException e) {
                OrmLog.log("モデール：" + e.getLocalizedMessage());
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