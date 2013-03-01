package services;

import com.sina.sae.memcached.SaeMemcache;
import libs.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import play.Logger;
import play.Play;
import play.libs.Time;

import java.util.Collection;

/**
 * @author wujinliang
 * @since 9/20/12
 */
public class CacheService {
    private static SaeMemcache mc = null;//new SaeMemcache("127.0.0.1", 11211);
    private static ObjectMapper mapper = new ObjectMapper();
    static {
        if (!"prod".equalsIgnoreCase(Play.id)) {
            mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        }
//        mc.init();
    }

    /**
     * @see #putIfAbsent(CacheCallback, boolean)
     */
    public static <T> String putIfAbsent(CacheCallback<T> callback) {
        return (String) putIfAbsent(callback, true);
    }

    /**
     * <p>从缓存中获取，若缓存不存在，则获取内容并放入缓存中</p>
     *
     * @param callback 缓存回调函数
     * @param jsonify 是否需要json化成string返回，为的是不需要每次反序列化序列化而消耗的时间，提高性能
     * @return 内容
     */
    public static <T> Object putIfAbsent(CacheCallback<T> callback, boolean jsonify) {
        try {
            String cacheName = callback.getCacheName();
            if (StringUtils.isNotBlank(cacheName)) {
                Object obj = null;//mc.get(cacheName);
                if (obj != null) {
                    if (!jsonify) obj = mapper.readValue(obj.toString(), Object.class);
                } else {
                    obj = callback.execute();
                    if (obj != null) {
                        String value;
                        if (obj instanceof String) {
                            value = (String) obj;
                        } else {
                            value = mapper.writeValueAsString(obj);
                        }
                        // obj若是列表类型且不为空，则加入缓存中或者obj不是列表类型的也加入缓存中
                        if (!(obj instanceof Collection) || !((Collection) obj).isEmpty()) {
                            set(cacheName, value, callback.getExpireTime());
                            callback.persist(cacheName, obj);
                        }
                        if (jsonify) obj = value;
                    }
                }
                return obj;
            }
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void delete(String key) {
        //mc.delete(key);
    }

    public static Object get(String key) {
        return null;//mc.get(key);
    }

    public static void set(String key, Object value, String expire) {
        //mc.set(key, value, Time.parseDuration(expire));
    }
}
