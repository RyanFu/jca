package libs;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;

/**
 * @author wujinliang
 * @since 2012-02-05
 */
public class Objects {
    private static Logger logger = LoggerFactory.getLogger(Objects.class);

    public static int toInt(Object obj, int defaultValue) {
        return obj == null ? defaultValue : NumberUtils.toInt(obj.toString(), defaultValue);
    }

    public static boolean toBoolean(Object obj) {
        return obj == null ? false : BooleanUtils.toBoolean(obj.toString());
    }

    public static String de4(Object obj, String defaultValue) {
        return obj == null ? defaultValue : obj.toString();
    }

    public static Date parseDate(String str, String pattern) {
        try {
            return DateUtils.parseDate(str, new String[]{pattern});
        } catch (ParseException e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

    public static Object instance(String className) {
        try {
            Class<?> aClass = Class.forName(className);
            return aClass.newInstance();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * <p>把大写中文转换成数字</p>
     * <p>e.g: 五千三百零七=5307</p>
     *
     * @param number 大写中文
     * @return int
     */
    public static int toIntFromChinese(String number) {
        if (number.startsWith("十")) number = "1" + number;
        if (number.endsWith("十")) number += "0";
        if (number.endsWith("百")) number += "00";
        if (number.endsWith("千")) number += "000";
        number = number.replace("一", "1").replace("二", "2").replace("三", "3").replace("四", "4")
                .replace("五", "5").replace("六", "6").replace("七", "7").replace("八", "8")
                .replace("九", "9").replace("零", "0").replace("十", "").replace("百", "").replace("千", "");
        return NumberUtils.toInt(number);
    }


    public static String strip(String html) {
        if (html == null) return null;
        return html.replaceAll("(?msi)<script.+?</script>", "").replaceAll("(?msi)<style.+?</style>", "")
                    .replaceAll("(?msi)<br\\s*/?>\\s*<br\\s*/?>", "<br/>").replaceAll("(?msi)</(p|div|h1|h2|h3|h4|table|ul)>(\\\\n)*\\s*(<br\\s*/?>)+", "</$1>")
                    .replace("(?msi)&nbsp;", " ");
    }
}
