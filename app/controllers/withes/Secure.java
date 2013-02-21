package controllers.withes;

import libs.Constant;
import models.User;
import models.enums.Role;
import org.apache.commons.lang.StringUtils;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Crypto;
import play.mvc.*;
import services.CacheService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 2010-6-27 Time: 22:17:13
 * Author: wujinliang.
 */
public class Secure extends Controller {
    /**
     * url.
     */
    private static String LOGIN_URL = "Application/login.html";

    /**
     * 检查要调用的Action方法是否可访问
     * 支持验证链式：token验证以及SSO验证.
     */
    @Before(unless = { "login", "logout", "loginWithXHR", "authenticate" })
    public static void checkAccess() {
        String username = getCookie(Constant.COOKIE_LOGIN_USER);
        String lastLoginCookie = getCookie(Constant.COOKIE_LAST_LOGIN);
        // 当一个用户在多处登录，且有一处用户退出或修改了密码，其他地方也应该跟着同时退出登录状态，所以根据last_login这个cookie来判断
        if (username == null || CacheService.get(Constant.CACHE_USER_PREFIX + username) == null || lastLoginCookie == null
                || !checkRememberme() || !lastLoginCookie.equals(CacheService.get(Constant.CACHE_USER_LAST_LOGIN_PREFIX + username))) {
            // 若是ajax来的, 则直接抛出异常
            if (request.isAjax()) { // TODO: error的消息可以做成json格式, 方便客户端解析
                error("action=reload");
            }
            flash.put("url", "GET".equals(request.method) ? request.url : "/");
            login();
        }

        boolean success = false;
        Check check = getActionAnnotation(Check.class);
        if (check != null) {
            success = check(username, check);
        }
        if (!success) {
            check = getControllerInheritedAnnotation(Check.class);
            if (check != null) {
                check(username, check);
            }
        }
    }

    public static void login() {
        flash.keep("url");
        render(LOGIN_URL);
    }

    public static void logout() {
        flash.clear();
        session.clear();
        response.removeCookie(Constant.COOKIE_LAST_LOGIN);
        response.removeCookie(Constant.COOKIE_REMEMBER);
        String username = getCookie(Constant.COOKIE_LOGIN_USER);
        CacheService.delete(Constant.CACHE_USER_PREFIX + username);
        CacheService.delete(Constant.CACHE_USER_LAST_LOGIN_PREFIX + username);
        login();
    }

    public static void authenticate(String username, String password) {
        boolean matched = User.count("byNameAndPassword", username, Codec.hexMD5(password)) > 0;
        if (!matched) {
            flash.error(Messages.get("validation.password.error"));
            login();
        } else {
            storeUserInfo(username);
            redirectToOriginUrl();
        }
    }

    public static void loginWithXHR(String username, String password) {
        User user = User.find("byName", username).first();
        Map<String, String> result = new HashMap<String, String>();
        result.put("url", flash.get("url"));
        String error = null;
        if (user == null) {
            error = Messages.get("error.user.no.exist", username);
        } else if (user.password != null && !user.password.equals(Codec.hexMD5(password))) {
            error = Messages.get("error.user.password.not.match");
        } else {
            storeUserInfo(username);
        }
        if (error != null) result.put("error", error);
        renderJSON(result);
    }

    @Util
    public static User getLoginUser() {
        String username = getCookie(Constant.COOKIE_LOGIN_USER);
        if (username != null) {
            Object user = CacheService.get(Constant.CACHE_USER_PREFIX + username);
            if (user != null && user instanceof User) {
                return (User) user;
            }
            return User.findById(username);
        }
        return null;
    }

    @Util
    public static User storeUserInfo(String username) {
        User user = User.find("byName", username).first();

        CacheService.set(Constant.CACHE_USER_PREFIX + username, user, Scope.COOKIE_EXPIRE);
        String lastLogin = System.currentTimeMillis() + "";
        CacheService.set(Constant.CACHE_USER_LAST_LOGIN_PREFIX + username, lastLogin, Scope.COOKIE_EXPIRE);

        // 设置cookie
        response.setCookie(Constant.COOKIE_LOGIN_USER, username, Scope.COOKIE_EXPIRE);
        response.setCookie(Constant.COOKIE_LAST_LOGIN, lastLogin, Scope.COOKIE_EXPIRE);
        response.setCookie(Constant.COOKIE_REMEMBER, Crypto.sign(username) + "-" + username, Scope.COOKIE_EXPIRE);
        return user;
    }

    private static boolean check(String username, Check check) {
        User user = (User) CacheService.get(Constant.CACHE_USER_PREFIX + username);
        if (user == null) user = storeUserInfo(username);
        if (user != null) {
            for (Role role : check.value()) {
                // 角色与指定的相同或者角色的层次比指定的角色要高
                if (user.role == role || user.role.level > role.level) {
                    return true;
                }
            }
        }
        List<String> roles = new ArrayList<String>();
        for (Role role : check.value()) {
            roles.add(Messages.get("label.setting.user.role." + role.toString()));
        }
        forbidden(StringUtils.join(roles, ","));
        return false;
    }

    private static void redirectToOriginUrl() {
        String url = flash.get("url");
        if(url == null) {
            url = "/";
        }
        redirect(url);
    }

    private static String getCookie(String cookieName) {
        Http.Cookie cookie = request.cookies.get(cookieName);
        return cookie != null ? cookie.value : null;
    }

    private static boolean checkRememberme() {
        String remember = getCookie("rememberme");
        if(remember != null && remember.indexOf("-") > 0) {
            String sign = remember.substring(0, remember.indexOf("-"));
            String username = remember.substring(remember.indexOf("-") + 1);
            return Crypto.sign(username).equals(sign);
        }
        return false;
    }
}