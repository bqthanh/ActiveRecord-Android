package models;

import java.util.Date;

import orms.activerecord.Model;
import orms.activerecord.anotations.Column;
import orms.activerecord.anotations.Table;

/**
 * Created by thanhbui on 2016/12/06.
 */

@Table(id="t_id", name="t_sport")
public class Sport extends Model {
    @Column(name="t_name")
    public String name;

    @Column(name="t_category")
    public String category;

    @Column(name="t_hours")
    public Short hours;

    @Column(name="dt_created")
    public Date createdDate;

    @Column(name="dt_modified")
    public Date modifiedDate;
}