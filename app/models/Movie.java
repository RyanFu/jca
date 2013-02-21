package models;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import play.db.jpa.Model;
import services.serializers.MovieSerializer;

import javax.persistence.*;

/**
 * @author wujinliang
 * @since 1/29/13
 */
@Entity
@JsonSerialize(using = MovieSerializer.class)
public class Movie extends Model {

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
    @Lob
    public String details;
}
