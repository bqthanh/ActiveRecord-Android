package activerecord.interfaces;

/**
 * Created by thanhbui on 2016/12/03.
 */

public interface ModelOperator {
    long getId();
    boolean equals(Object targetObj);
    void save();
    int delete();
}