package controllers.ajax;

import libs.DBCounter;
import libs.EasyMap;
import models.Episode;
import models.Movie;
import models.MovieItem;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author wujinliang
 * @since 1/29/13
 */
public class Movies extends Controller {
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * 保存排序
     * @param order 字符串，如：17=1;18=3;30=5
     */
    public static void saveOrder(String order) {
        String[] orders = StringUtils.split(order, ";");
        for (String s : orders) {
            String[] idAndNo = StringUtils.split(s, "=");
            if (idAndNo.length == 2) {
                Movie movie = Movie.findById(idAndNo[0]);
                if (movie != null) {
                    movie.no = NumberUtils.toInt(idAndNo[1]);
                    movie.save();
                }
            }
        }
        renderJSON(new EasyMap<String, String>("status", "200"));
    }

    public static void save(String name, String cover, String cover_title, String rate) {
        Movie movie = Movie.find("byName", name).first();
        if (movie == null) {
            movie = new Movie();
            movie.name = name;
            movie.cover = cover;
            movie.cover_title = cover_title;
            movie.rate = rate;
            movie.no = 0;
            movie.id = DBCounter.generateUniqueCounter(Movie.class) + "";
            movie.save();
        } else {
            renderJSON(new EasyMap<String, String>("error", "此" + name + "已经存在"));
        }
    }

    public static void update(String id, List<Map> details) {
        Movie movie = Movie.findById(id);
        if (movie == null) {
            renderJSON(new EasyMap<String, String>("error", id + "的电影不存在"));
        } else {
            try {
                List<MovieItem> items = new ArrayList<MovieItem>();
                for (Map detail : details) {
                    MovieItem item = new MovieItem();
                    item.season = detail.get("season").toString();
                    item.from = detail.get("from").toString();
                    item.brief = detail.get("brief").toString();
                    item.actors = Arrays.asList(StringUtils.split(detail.get("actors").toString(), ","));

                    List<Episode> es = new ArrayList<Episode>();
                    List<Map> episodes = mapper.readValue(detail.get("episodes").toString(), List.class);
                    for (Map episode : episodes) {
                        Episode e = new Episode();
                        e.e = NumberUtils.toInt(episode.get("e").toString());
                        e.url = episode.get("url").toString();
                        es.add(e);
                    }

                    item.episodes = es;
                    items.add(item);
                }
                movie.details = items;
                movie.save();
            } catch (Exception e) {
                renderJSON(new EasyMap<String, String>("error", e.getMessage()));
            }
        }
    }
}
