package orms.activerecord.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by thanhbui on 2016/11/23.
 */

@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(value = RetentionPolicy.RUNTIME)

public @interface Table {

    public static final String ID_NAME = "id";

    public String id() default ID_NAME;

    public String name();
}