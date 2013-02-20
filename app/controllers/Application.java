package controllers;

import models.Movie;
import models.enums.Role;
import org.codehaus.jackson.map.ObjectMapper;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.With;
import controllers.withes.Check;
import controllers.withes.LogPrinter;
import controllers.withes.Secure;

import java.util.List;
import java.util.Map;

@With({LogPrinter.class, Secure.class})
public class Application extends Controller {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Check({Role.Operator})
    public static void index() {
        List<Movie> items = Movie.find("order by no asc").fetch();
        render(items);
    }

    @Check({Role.Operator})
    public static void detail(String id) throws Exception {
        Movie movie = Movie.findById(id);
        List<Map> details = mapper.readValue(movie.details, List.class);
        render(movie, details);
    }

    @Check({Role.Operator})
    public static void proxy(String url) {
        renderBinary(WS.url(url).setHeader("Referer", "http://video.baidu.com/tvplayindex/").get().getStream());
    }
}