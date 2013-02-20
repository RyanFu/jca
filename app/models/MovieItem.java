package models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wujinliang
 * @since 1/29/13
 */
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
