package ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Date;
import java.util.List;

import model.Person;
import orms.activerecord.Database;
import orms.activerecord.DatabaseBuilder;
import orms.activerecord.R;
import utils.OrmLog;

public class MainActivity extends AppCompatActivity {

    public static Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initilize(this);
    }

    //データベースを初期する
    public static void initilize(Context c) {
        //データベース名
        String dbname = "db_name";
        //バージョンは１以上を指定してください
        int version = 2;

        DatabaseBuilder dbBuilder = new DatabaseBuilder(dbname, version);
        //テブール一覧を入力する
        dbBuilder.addModel(Person.class);
        db = new Database(c, dbBuilder);

        Person p;
        for (int i = 0; i < 10; i++) {
            Date dt = null;
            if (i % 2 == 0) {
                dt = new Date();
            }
            p = new Person("thanhbui" + i, "t.buiquang@bs" + i, "東京" + i, true, dt);
            p.save();
            p.address = p.address + "港区芝浦";

            p.save();
            if (i < 5) {
                p.delete();
            }
        }

        List results = Database.queryWithNoKey("SELECT t_id, t_name, dt_birthday FROM t_person WHERE t_id IN ( 6, 2, 4, 8, 9, 10 )", null);
        Log.e("TAG", "Results: " + results);

        results = Database.rawQuery("SELECT t_id, t_name, dt_birthday FROM t_person WHERE t_id IN ( 6, 2, 4, 8, 9, 10 )", null);
        Log.e("TAG", "Results: " + results);

        Person.deleteByIds(new long[] {1, 2, 4, 8, 9, 10});

        List<Person> persons = Person.findByColumn("t_id", "7");
        Log.e("TAG", "Size: " + persons.size());
        for(Person pe : persons) {
            OrmLog.log("PE: "
                    + pe.getId() + " | "
                    + pe.name + " | "
                    + pe.address + " | "
                    + pe.email + " | "
                    + pe.birthday + " | "
                    + pe.dt_created + " | "
                    + pe.mon);
        }

        int cnt = Person.deleteByColumn("t_id", "7");
        Log.e("TAG", "Size: " + cnt);

        //
        persons = Person.findAll();
        Log.e("TAG", "Size: " + persons.size());
        for(Person pe : persons) {
            OrmLog.log("PE: "
                    + pe.getId() + " | "
                    + pe.name + " | "
                    + pe.address + " | "
                    + pe.email + " | "
                    + pe.birthday + " | "
                    + pe.dt_created + " | "
                    + pe.mon);
        }
    }
}