package controllers;

import models.Movie;
import play.libs.WS;
import play.mvc.Controller;

import java.util.List;

public class Application extends Controller {

    public static void index() {
        List<Movie> items = Movie.findAll();
        render(items);
    }

    public static void proxy(String url) {
        renderBinary(WS.url(url).setHeader("Referer", "http://video.baidu.com/tvplayindex/").get().getStream());
    }
}