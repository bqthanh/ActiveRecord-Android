package model;

import java.util.Date;

import orms.activerecord.Model;
import orms.activerecord.anotation.Column;
import orms.activerecord.anotation.Table;

/**
 * Created by thanhbui on 2016/11/19.
 */

@Table(id="t_id", name="t_person")
public class Person extends Model {
    @Column(name="t_name")
    public String name;

    @Column(name="t_email")
    public String email;

    @Column(name="t_address")
    public String address;

    @Column(name="t_gender")
    public Boolean gender;

    @Column(name="t_birthday")
    public String birthday;

    @Column(name="dt_created")
    public Date dt_created;

    public Person() {};

    public Person(String name, String email, String address, Boolean gender, String birthday) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.birthday = birthday;
    }
}