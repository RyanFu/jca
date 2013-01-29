package controllers;

import play.libs.Codec;
import play.mvc.Controller;

/**
 * @author wujinliang
 * @since 1/29/13
 */
public class Tools extends Controller {

    /**
     * 生成md5
     * @param str 字符串
     */
    public static void md5(String str) {
        renderText(Codec.hexMD5(str));
    }
}
