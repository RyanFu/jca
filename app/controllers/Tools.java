package controllers;

import controllers.withes.LogPrinter;
import controllers.withes.Secure;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;

/**
 * @author wujinliang
 * @since 1/29/13
 */
@With({LogPrinter.class})
public class Tools extends Controller {

    /**
     * 生成md5
     * @param str 字符串
     */
    public static void md5(String str) {
        renderText(Codec.hexMD5(str));
    }
}
