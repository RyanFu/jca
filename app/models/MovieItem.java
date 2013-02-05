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
public class MovieItem extends Model {
    /** 第几季 */
    public String season;

    /** 来自哪个网站 */
    public String from;

    /** 简介 */
    public String brief;

    /** 演员 */
    @Embedded
    public List<String> actors = new ArrayList<String>();

    /** 集数 */
    @OneToMany(fetch= FetchType.LAZY, cascade = {CascadeType.ALL})
    public Set<Episode> episodes = new HashSet<Episode>();
}
