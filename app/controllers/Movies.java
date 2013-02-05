package controllers;

import controllers.withes.LogPrinter;
import controllers.withes.NoCookieFilter;
import controllers.withes.RequestParamChecker;
import libs.Constant;
import libs.EasyMap;
import models.Counter;
import models.Movie;
import models.Setting;
import play.mvc.Controller;
import play.mvc.With;
import services.CacheService;
import services.DefaultCacheCallback;
import services.serializers.MovieSerializer;

import java.util.List;
import java.util.Map;

/**
 * @author wujinliang
 * @since 2013-02-05
 */
@With({LogPrinter.class, RequestParamChecker.class, NoCookieFilter.class})
public class Movies extends Controller {
    public static void list() {
        renderText(CacheService.putIfAbsent(new DefaultCacheCallback<Map<String, Object>>() {
            @Override
            public String getCacheName() {
                return Constant.CACHE_PREFIX + "movies";
            }

            @Override
            public Map<String, Object> execute() {
                List<Movie> movies = Movie.all().fetch();
                List<Setting> settings = Setting.all().fetch();
                Counter counter = Counter.find("byName", Setting.class.getSimpleName()).first();
                return new EasyMap<String, Object>("setting", settings).easyPut("data", movies).easyPut("checksum", counter == null ? "1" : counter.count + "");
            }
        }));

    }

    public static void detail(final String id) {
         renderText(CacheService.putIfAbsent(new DefaultCacheCallback<Map<String, Object>>() {
            @Override
            public String getCacheName() {
                return Constant.CACHE_PREFIX + "movies_id=" + id;
            }

            @Override
            public Map<String, Object> execute() {
                Movie movie = Movie.find("byBid", id).first();
                Map<String, Object> map = new MovieSerializer().toMap(movie);
                // TODO: details and episodes
//                map.put("details", CacheService.mapper.readValue(movie.details, List.class));
                return map;
            }
        }));
    }
}
