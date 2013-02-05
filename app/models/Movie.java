package models;

import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wujinliang
 * @since 1/29/13
 */
@Entity
public class Movie extends Model {
    public String bid;

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
    @OneToMany(fetch= FetchType.LAZY, cascade = {CascadeType.ALL})
    public Set<MovieItem> details = new HashSet<MovieItem>();
}
