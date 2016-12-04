package orms.activerecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import orms.activerecord.sql.SQLScripts;
import utils.OrmLog;
import utils.SQLiteUtils;

/**
 * Created by thanhbui on 2016/11/17.
 */

public class Database  {

    public static SQLiteDatabase db;
    public static DatabaseHelper dbHelper;

    public Database(Context c, DatabaseBuilder dbBuilder) {
        dbHelper = new DatabaseHelper(c, dbBuilder);
        open();
    }

    public static Long insert(String table, ContentValues values) {
        long id = -1;
        db.beginTransaction();
        try {
            id = db.insert(table, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public static int update(String table, ContentValues values, String whereClause,
                      String[] whereArgs) {
        int cnt = -1;
        db.beginTransaction();
        try {
            cnt = db.update(table, values, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return cnt;
    }

    public static int delete(String table, String whereClause, String[] whereArgs) {
        int id = -1;
        db.beginTransaction();
        try {
            id = db.delete(table, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public static Cursor query(String table, String[] selectColumns, String where,
                        String[] whereArgs) {
        Cursor c = null;
        c = db.query(table, selectColumns, where, whereArgs, null, null,
                null, null);
        return c;
    }

    //クリエする
    public static void execute(String sql) { db.execSQL(sql); }

    //インタフェース
    public static Cursor rawQuery(String sql, String[] params) {
        return db.rawQuery(sql, params);
    }

    public List queryWithNoKey(String sql, String[] params) {
        Cursor cursor = db.rawQuery(sql, params);

        List<Map<String, String>> results = new ArrayList<>();
        int index, count;
        count = cursor.getColumnCount();

        index = 0;
        Map<String, String> row = new HashMap<String, String>();

        while(cursor.moveToNext()) {
            //OrmLog.log("value: " + cursor.getString(index));
            index ++;
            if (index >= count) {
                index = 0;
                results.add(row);
                row = new HashMap<String, String>();
            }
            row.put(cursor.getColumnName(index - 1), cursor.getString(index - 1));
        }

        return results;
    }

    //クラスタイプからモデール情報を取得する
    public static Model.ModelManager getTableManger(Class<? extends Model> type) {
        return dbHelper.getDbBuilder()
                .tables.get(type);
    }

    //データベースを開く
    public static void open() {
        db = dbHelper.open();
    }

    //データベースをクローズする
    public static void close() { dbHelper.close(); }

    //データベースヘルプ
    static class DatabaseHelper extends SQLiteOpenHelper {
        private DatabaseBuilder dbBuilder;

        public DatabaseHelper(Context c, DatabaseBuilder dbBuilder) {
            super(c, dbBuilder.dbname, null, dbBuilder.version);
            this.dbBuilder = dbBuilder;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            OrmLog.log("onCreate !");
            executeMigrations(db, -1, dbBuilder.version);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            OrmLog.log("onUpgrade !");
            executeMigrations(db, oldVersion, newVersion);
        }

        public boolean executeMigrations(SQLiteDatabase db, int oldversion, int newVersion) {
            boolean ret = false;

            db.beginTransaction();
            try {
                if (SQLiteUtils.AUTO_CREATE_DB = true) {
                    OrmLog.log("upgradeFromModel !");
                    upgradeFromModel(db);
                } else {
                    OrmLog.log("upgradeFromSQLScript !");
                    upgradeFromSQLScript(db, oldversion, newVersion);
                }

                OrmLog.log("executeMigrations !");
                ret = true;
                db.setTransactionSuccessful();
            } catch (Exception e) {
                OrmLog.log(e.getLocalizedMessage());
            } finally {
                db.endTransaction();
            }
            return ret;
        }

        //モデールからテブールを自動に作成する
        public void upgradeFromModel(SQLiteDatabase db) {
            try {
                for (Class<? extends Model> table : dbBuilder.getModels()) {
                    String sql;

                    ////テブールドをロップする
                    sql = dbBuilder.getSQLDrop(table);
                    if (sql != null) {
                        db.execSQL(sql);
                    }
                    //テブールを作成する
                    sql = dbBuilder.getSQLCreate(table);
                    OrmLog.log("SQLCreate: " + sql);
                    if (sql != null) {
                        db.execSQL(sql);
                    }
                }
            } catch (Exception e) {
                OrmLog.log(e.getLocalizedMessage());
            }
        }

        //SQLスクリプからアップグレード
        public void upgradeFromSQLScript(SQLiteDatabase db, int oldVersion, int newVersion) throws Exception {
            int index = 0;

            for (String sql : SQLScripts.getScripts()) {
                if(index > oldVersion && index <= newVersion) {
                    db.execSQL(sql);
                }
                index ++;
            }
        }

        public DatabaseBuilder getDbBuilder() { return dbBuilder; }

        public SQLiteDatabase open() { return this.getWritableDatabase(); }

        public void close() { this.close(); }
    }
}