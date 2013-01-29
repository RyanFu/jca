package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import play.modules.morphia.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wujinliang
 * @since 1/29/13
 */
@Entity
public class Movie extends Model {
    @Id
    public String id;

    /** 名称 */
    public String name;

    /** 封面图 */
    public String cover;

    /** 封面标题 */
    public String cover_title;

    /** 评分 */
    public String rate;

    /** 顺序号 */
    public int no;

    /** 电影详细信息，包括了几部几集 */
    public List<MovieItem> details = new ArrayList<MovieItem>();

    @Override
    public Object getId() {
        return id;
    }

    @Override
    protected void setId_(Object id) {
        this.id = id.toString();
    }
}
