package controllers.withes;

import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;

import java.util.Map;

/**
 * 日志打印器
 *
 * @author wujinliang
 * @since 9/20/12
 */
public class LogPrinter extends Controller {
    @Before
    public static void log() {
        Map<String, String> map = request.params.allSimple();
        map.remove("body");
        Logger.info("request url: " + request.url + ", param:" + map);
    }
}
