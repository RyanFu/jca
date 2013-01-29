package controllers;

import models.Movie;
import models.enums.Role;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.With;
import controllers.withes.Check;
import controllers.withes.LogPrinter;
import controllers.withes.Secure;

import java.util.List;

@With({LogPrinter.class, Secure.class})
public class Application extends Controller {
    @Check({Role.Operator})
    public static void index() {
        List<Movie> items = Movie.find().order("-no").asList();
        render(items);
    }

    @Check({Role.Operator})
    public static void detail(String id) {
        Movie movie = Movie.findById(id);
        render(movie);
    }

    @Check({Role.Operator})
    public static void proxy(String url) {
        renderBinary(WS.url(url).setHeader("Referer", "http://video.baidu.com/tvplayindex/").get().getStream());
    }
}