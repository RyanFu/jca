package models;

import com.google.code.morphia.annotations.Embedded;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wujinliang
 * @since 1/29/13
 */
@Embedded
public class MovieItem {
    /** 第几季 */
    public String season;

    /** 来自哪个网站 */
    public String from;

    /** 简介 */
    public String brief;

    /** 演员 */
    public List<String> actors = new ArrayList<String>();

    /** 集数 */
    public List<Episode> episodes = new ArrayList<Episode>();

}
