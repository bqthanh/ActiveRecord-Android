package ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;
import java.util.List;

import activerecord.AppLog;
import activerecord.R;
import models.Person;
import models.Sport;
import activerecord.Database;
import activerecord.DatabaseBuilder;
import activerecord.Model;

public class MainActivity extends AppCompatActivity {
    public static Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize(this);
    }

    //データベースを初期する
    public static void initialize(Context c) {
        //データベース名
        String dbname = "db_name";
        //バージョンは１以上を指定してください
        int version = 3;

        //以下の宣言が必須です
        DatabaseBuilder dbBuilder = new DatabaseBuilder(dbname, version);
        //テブール一覧を入力する
        dbBuilder.addModel(Person.class);
        dbBuilder.addModel(Sport.class);
        //データベースを初期する
        db = new Database(c, dbBuilder);
        //此処まで

        Person person;
        for (int i = 0; i < 10; i++) {
            Date dt = null;
            if (i % 2 == 0) {
                dt = new Date();
            }
            person = new Person("thanhbui" + i, "12345678", "t.buiquang@bs" + i, "東京" + i, true, dt);
            person.save();
            person.address = person.address + "港区芝浦";

            person.save();
            if (i < 5) {
                person.delete();
            }
        }

        //Database API
        List results = Database.queryWithNoKey("SELECT t_id, t_password, t_username, dt_birthday FROM t_person WHERE t_id IN ( 6, 2, 4, 8, 9, 10 )", null);
        AppLog.log("queryWithNoKey: " + results);

        results = Database.rawQuery("SELECT t_id, t_password, t_username, dt_birthday FROM t_person WHERE t_id IN ( 6, 2, 4, 8, 9, 10 )", null);
        AppLog.log("rawQuery: " + results);

        Database.execute("UPDATE t_person SET t_username = 'dbhelper' WHERE t_id = 7");
        Model.deleteByIds(Person.class, new long[] {1, 2, 4, 8, 9, 10});

        //Model API
        List<Person> persons = Model.findByIds(Person.class, new long[] {1, 6, 9});
        AppLog.log("1. Size: " + persons.size());

        persons = Model.find(Person.class, String.format("%s LIKE ?", "t_password"),
                new String[] {"123456%"});
        AppLog.log("2. Size: " + persons.size());

        persons = Model.findByColumn(Person.class, "t_id", "7");
        AppLog.log("3. Size: " + persons.size());
        for(Person p : persons) {
            AppLog.log("Detailed person: "
                    + p.getId() + " | "
                    + p.username + " | "
                    + p.password + " | "
                    + p.address + " | "
                    + p.email + " | "
                    + p.birthday + " | "
                    + p.createdDate + " | "
                    + p.gender);
        }

        int cnt = Model.deleteByColumn(Person.class, "t_id", "7");
        AppLog.log("4. Size: " + cnt);

        persons = Model.findAll(Person.class);
        AppLog.log("5. Size: " + persons.size());
        for(Person p : persons) {
            AppLog.log("Detailed person: "
                    + p.getId() + " | "
                    + p.username + " | "
                    + p.password + " | "
                    + p.address + " | "
                    + p.email + " | "
                    + p.birthday + " | "
                    + p.createdDate + " | "
                    + p.gender + " | "
                    + p.modifiedDate);
        }

        Sport sport = new Sport();
        sport.name = "Soccer";
        sport.category = "Category 01";
        sport.hours = 1000;
        sport.save();

        Database.execute("Update t_sport SET t_category = 'Swimming' WHERE t_id = 1");

        List<Sport> sportList = Model.findAll(Sport.class);
        for(Sport s : sportList) {
            AppLog.log("Detailed sport: "
                    + s.name + " | "
                    + s.category + " | "
                    + s.hours + " | "
                    + s.createdDate + " | "
                    + s.modifiedDate);
        }
    }
}