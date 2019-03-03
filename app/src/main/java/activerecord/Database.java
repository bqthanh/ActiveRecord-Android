package activerecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //共通インタフェース
    public static List rawQuery(String sql, String[] params) {
        List<List<String>> toRet = new ArrayList<>();
        int cnt;
        List<String> row;

        Cursor c;
        try {
            c = db.rawQuery(sql, params);
        } catch (Exception e) {
            AppLog.log(e.getLocalizedMessage());
            return null;
        }
        cnt = c.getColumnCount();

        while(c.moveToNext()) {
            row = new ArrayList<>();
            for (int i = 0; i < cnt; i++) {
                row.add(c.getString(i));
            }
            toRet.add(row);
        }

        return toRet;
    }

    public static List queryWithNoKey(String sql, String[] params) {
        List<Map<String, String>> toRet = new ArrayList<>();
        int cnt;
        Map<String, String> row;

        Cursor c;
        try {
            c = db.rawQuery(sql, params);
        } catch (Exception e) {
            AppLog.log(e.getLocalizedMessage());
            return null;
        }
        cnt = c.getColumnCount();

        while(c.moveToNext()) {
            row = new HashMap<String, String>();
            for (int i = 0; i < cnt; i++) {
                row.put(c.getColumnName(i), c.getString(i));
            }
            toRet.add(row);
        }

        return toRet;
    }

    //個別インタフェース
    public static boolean transactionWithSQL(List<String> sqlList) {
        boolean toRet = false;

        db.beginTransaction();
        try {
            for (String sql : sqlList) {
                db.execSQL(sql);
            }
            db.setTransactionSuccessful();
            toRet = true;

        } catch (Exception e) {
            AppLog.log(e.getLocalizedMessage());
        }
        finally {
            db.endTransaction();
        }
        return toRet;
    }

    //個別インタフェース
    public static Long insert(String table, ContentValues values) {
        long id = -1;
        db.beginTransaction();
        try {
            id = db.insert(table, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            AppLog.log(e.getLocalizedMessage());
        }
        finally {
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
        } catch (Exception e) {
            AppLog.log(e.getLocalizedMessage());
        }
        finally {
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
        } catch (Exception e) {
            AppLog.log(e.getLocalizedMessage());
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public static Cursor query(String table, String[] selectColumns, String where,
                        String[] whereArgs) {
        Cursor c = null;
        try {
            c = db.query(table, selectColumns, where, whereArgs, null, null,
                    null, null);
        } catch (Exception e) {
            AppLog.log(e.getLocalizedMessage());
        }
        return c;
    }

    //クリエする
    public static void execute(String sql) {
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            AppLog.log(e.getLocalizedMessage());
        }
        finally {
            db.endTransaction();
        }
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
        //モデール定義からデータベースを自動に作成する
        public static boolean AUTO_CREATE_DB = false;

        private DatabaseBuilder dbBuilder;

        public DatabaseHelper(Context c, DatabaseBuilder dbBuilder) {
            super(c, dbBuilder.dbname, null, dbBuilder.version);
            this.dbBuilder = dbBuilder;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            AppLog.log("On create!");
            executeMigrations(db, -1, dbBuilder.version);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            AppLog.log("On upgrade!");
            executeMigrations(db, oldVersion, newVersion);
        }

        public boolean executeMigrations(SQLiteDatabase db, int oldversion, int newVersion) {
            boolean ret = false;

            db.beginTransaction();
            try {
                if (AUTO_CREATE_DB == true) {
                    upgradeFromModel(db);
                } else {
                    upgradeFromSQLScript(db, oldversion, newVersion);
                }
                ret = true;
                db.setTransactionSuccessful();
            } catch (Exception e) {
                AppLog.log(e.getLocalizedMessage());
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
                    if (sql != null) {
                        db.execSQL(sql);
                    }
                    AppLog.log("SQL create: " + sql);
                }
            } catch (Exception e) {
                AppLog.log(e.getLocalizedMessage());
            }
        }

        //SQLスクリプからアップグレード
        public void upgradeFromSQLScript(SQLiteDatabase db, int oldVersion, int newVersion) {
            int index = 1;

            for (List<String> builder : QueryBuilder.getBuilder()) {
                if(index > oldVersion && index <= newVersion) {
                    for (String sql : builder) {
                        try {
                            db.execSQL(sql);
                        } catch (Exception e) {
                            AppLog.log(e.getLocalizedMessage());
                        }
                        AppLog.log("Upgrade from sql script: " + sql);
                    }
                }
                index ++;
            }
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            AppLog.log("On downgrade!");
        }

        public DatabaseBuilder getDbBuilder() { return dbBuilder; }

        public SQLiteDatabase open() { return this.getWritableDatabase(); }

        public void close() { this.close(); }
    }
}