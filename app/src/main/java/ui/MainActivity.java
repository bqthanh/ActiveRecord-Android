package ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

    public static void initilize(Context c) {
        String dbname = "db_name";
        int version = 1;

        DatabaseBuilder dbBuilder = new DatabaseBuilder(dbname, version);
        dbBuilder.addModel(Person.class);
        db = new Database(c, dbBuilder);

        long pid;
        Person p;
        for (int i = 0; i < 10; i++) {
            p = new Person("thanhbui" + i, "t.buiquang@bs" + i, "Tokyo" + i, true, "1991//11/09");
            p.save();

            p.address = p.address + "港区芝浦";
            p.save();

            if (i < 5) {
                p.delete();
            }
        }

        p = new Person("huong", "huongbt@bs", "BacGiang", false, "1994//01/20");
        p.save();

        p.email = "cogiaonho@yahoo.com";
        p.save();

        p.name = "huongbt";
        p.save();

        p.delete();

        List<Person> lists = Person.find(String.format("t_id IN (1, 8, 97)"), null);
        for (Person pe : lists) {
            OrmLog.log("Person: " + pe.getId() + " : " + pe.name + " : " + pe.address + " : " + pe.email);
            pe.delete();
        }
//
//        List<Person> cursor = Person.rawQuery("SELECT * FROM t_person WHERE t_id = 21", null);
//        if (cursor != null && cursor.size() > 0)
//        for (Person pe : cursor) {
//            OrmLog.log("Person: " + pe.getId() + " : " + pe.name + " : " + pe.address + " : " + pe.email);
//        }
//  cursor.close();

//        List results = IModel.queryWithNoKey("SELECT * FROM t_person WHERE t_id = ?", new String[]{"1"});
//        System.out.print(results);
//
        List<Person> persons = Person.findAll();

        for(Person pe : persons) {
            OrmLog.log("Person: " + pe.getId() + " : " + pe.name + " : " + pe.address + " : " + pe.email);
        }

        //cursor.moveToNext();
        // while (cursor.moveToNext());
        //
    }
}