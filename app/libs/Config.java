package libs;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author wujinliang
 * @since 2012-02-04
 */
public class Config {
    private static ObjectMapper mapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(Config.class);
    private static Map<String, Object> map;
    private static String env;

    public static Object get(String key) {
        if (map == null) init();
        return map == null ? null : map.get(env != null ? "%" + env + "." + key : key);
    }

    public static String getStr(String key, String defaultValue) {
        Object value = get(key);
        return value == null ? defaultValue : value.toString();
    }

    public static int getInt(String key, int defaultValue) {
        Object value = get(key);
        return value == null ? defaultValue : NumberUtils.toInt(value.toString(), defaultValue);
    }

    public static Map getMap(String key, Map defaultValue) {
        Object value = get(key);
        return value != null && value instanceof Map ? (Map) value : defaultValue;
    }

    public static List getList(String key, List defaultValue) {
        Object value = get(key);
        return value != null && value instanceof List ? (List) value : defaultValue;
    }

    public static void init() {
        init(null);
    }

    public static void init(String propFile) {
        env = System.getProperty("env");
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        if (propFile == null) propFile = "config.json";
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFile);
        try {
            if (is == null) is = Config.class.getResourceAsStream(propFile);
            if (is == null) is = new FileInputStream(propFile);
            String content = IOUtils.toString(is, "UTF-8");
            map = mapper.readValue(content, Map.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
