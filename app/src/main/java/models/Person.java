package models;

import java.util.Date;

import orms.activerecord.Model;
import orms.activerecord.anotations.Column;
import orms.activerecord.anotations.Table;

/**
 * Created by thanhbui on 2016/11/19.
 */

@Table(id="t_id", name="t_person")
public class Person extends Model {
    @Column(name="t_username")
    public String username;

    @Column(name="t_password")
    public String password;

    @Column(name="t_email")
    public String email;

    @Column(name="t_sport")
    public short sport_id;

    @Column(name="t_address")
    public String address;

    @Column(name="t_gender")
    public Boolean gender;

    @Column(name="dt_birthday")
    public Date birthday;

    @Column(name="dt_created")
    public Date createdDate;

    @Column(name="dt_modified")
    public Date modifiedDate;

    public Person() {};

    public Person(String username, String passwd, String email, String address, Boolean gender, Date birthday) {
        this.username = username;
        this.password = passwd;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.birthday = birthday;
    }
}