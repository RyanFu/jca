package controllers.withes;

import play.Play;
import play.mvc.Controller;
import play.mvc.Finally;
import play.mvc.Http;

import java.util.HashMap;
import java.util.Map;

/**
 * Removes cookies from all responses.
 * <p/>
 * This is because cookies are not required in stateless webservice
 * and
 * we don't want to send any unnecessary information to the client.
 *
 * @author Alex Jarvis
 */
public class NoCookieFilter extends Controller {
    /**
     * An empty cookie map to replace any cookies in the response.
     */
    private static final Map<String, Http.Cookie> cookies = new
            HashMap<String, Http.Cookie>(0);

    /**
     * When the configuration property 'cookies.enabled' equals false,
     * this Finally filter will replace the cookies in the response with
     * an empty Map.
     */
    @Finally
    protected static void removeCookies() {
        boolean cookiesEnabled =
                Boolean.parseBoolean(Play.configuration.getProperty("cookies.enabled"));
        if (!cookiesEnabled) {
            response.cookies = cookies;
        }
    }
}