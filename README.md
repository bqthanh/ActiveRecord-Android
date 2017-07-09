#1. 要求
テーブール毎にモデールを作る。
注意：テブールにプライマーキーは「AUTOINCREMENT」が必須だ。

#2. 使い方
2.1 データベース初期
```java
//データベースとテブール情報を管理するクラス
DatabaseBuilder dbBuilder = new DatabaseBuilder(dbname, version, queryBuilder);
注意：「queryBuilder」は以下のような物データベース作成、変更スクリプだ。
List<String> ver1 = new ArrayList<>();
String CREATE_SPORT = "CREATE TABLE t_sport (t_id INTEGER PRIMARY KEY AUTOINCREMENT, t_category text, dt_created date)";
ver1.add(CREATE_SPORT);
builder.add(ver1);
```
//テブール一覧を入力する
dbBuilder.addModel(Person.class);
dbBuilder.addModel(Sport.class);
//データベースを初期する
db = new Database(c, dbBuilder);

2.2 インタフェース
Modelインタフェース
オブジェクトメソッド
+ long getId()
+ boolean equals(Object targetObj)
+ void save()
+ int delete()

クラスメソッド
+ <T extends Model> T findById(Class<T> type, long id)
+ <T extends Model> List<T> findByIds(Class<T> type, long[] ids)
+ <T extends Model> List<T> find(Class<T> type, String whereClause, String[] whereArgs)
+ <T extends Model> List<T> findByColumn(Class<T> type, String column, String value)
+ <T extends Model> int deleteById(Class<T> type, long id)
+ <T extends Model> int deleteByIds(Class<T> type, long[] ids)
+ <T extends Model> int delete(Class<T> type, String whereClause, String[] whereArgs)
+ <T extends Model>int deleteByColumn(Class<T> type, String column, String value)

Databaseインタフェース
クラスメソッド
+ List rawQuery(String sql, String[] params)
+ List queryWithNoKey(String sql, String[] params)
+ boolean transactionWithSQL(List<String> sqlList)
+ void execute(String sql)
