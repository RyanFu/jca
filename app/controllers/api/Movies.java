package controllers.api;

import controllers.withes.LogPrinter;
import controllers.withes.NoCookieFilter;
import libs.Constant;
import libs.EasyMap;
import models.Movie;
import models.Setting;
import org.codehaus.jackson.map.ObjectMapper;
import play.mvc.Controller;
import play.mvc.With;
import services.CacheService;
import services.DefaultCacheCallback;
import services.serializers.MovieSerializer;

import java.util.List;
import java.util.Map;

/**
 * @author wujinliang
 * @since 2/20/13
 */
@With({LogPrinter.class, NoCookieFilter.class})
public class Movies extends Controller {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void list() {
        renderText(CacheService.putIfAbsent(new DefaultCacheCallback<Map>() {
            @Override
            public String getCacheDisplayName() {
                return null;
            }

            @Override
            public String getCacheName() {
                return Constant.CACHE_PREFIX + "movies";
            }

            @Override
            public Map execute()throws Exception {
                List<Movie> movies = Movie.find("order by no asc").fetch();
                return new EasyMap<String, Object>("data", movies).easyPut("setting", Setting.all().fetch()).easyPut("checksum", "1");
            }
        }));
    }

    public static void detail(final long id) {
        renderText(CacheService.putIfAbsent(new DefaultCacheCallback<Map>() {
            @Override
            public String getCacheDisplayName() {
                return null;
            }

            @Override
            public String getCacheName() {
                return Constant.CACHE_PREFIX + "movie_id=" + id;
            }

            @Override
            public Map execute() throws Exception {
                Movie m = Movie.findById(id);
                Map<String, Object> map = new MovieSerializer().toMap(m);
                map.put("details", mapper.readValue(m.details, List.class));
                return map;
            }
        }));
    }
}
