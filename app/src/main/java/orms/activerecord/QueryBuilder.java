package orms.activerecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thanhbui on 2016/12/03.
 */

public class QueryBuilder {

        public static List<List<String>> getBuilder() {
            List<List<String>> builder = new ArrayList<>();

            // Version 1
            List<String> ver1 = new ArrayList<>();

            String TABLE_SPORT = "CREATE TABLE t_sport (t_id INTEGER PRIMARY KEY AUTOINCREMENT, t_category text, " +
                    "dt_created date, t_hours int)";

            String TABLE_PERSON = "CREATE TABLE t_person (t_id INTEGER PRIMARY KEY AUTOINCREMENT, t_address text, " +
                    "dt_birthday date, dt_created date, t_email text, t_gender bool, t_password text, t_sport int, " +
                    "t_username text)";

            ver1.add(TABLE_PERSON);
            ver1.add(TABLE_SPORT);
            builder.add(ver1);

            //Version 2
            List<String> ver2 = new ArrayList<>();
            String ALTER_PERSON = "ALTER TABLE t_person ADD COLUMN dt_modified TIMESTAMP";

            String ALTER_SPORT = "ALTER TABLE t_sport ADD COLUMN dt_modified timestamp TIMESTAMP";

            ver2.add(ALTER_PERSON);
            ver2.add(ALTER_SPORT);
            builder.add(ver2);

            //Version 3
            List<String> ver3 = new ArrayList<>();
            String ALTER_SPORT_v3 = "ALTER TABLE t_sport ADD COLUMN t_name text";

            ver3.add(ALTER_SPORT_v3);
            builder.add(ver3);

            return builder;
        }
}