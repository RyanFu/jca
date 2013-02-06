package services;

/**
 * @author wujinliang
 * @since 9/25/12
 */
public interface CacheCallback<T> {
    /**
     * 得到缓存key名称
     * @return cache name
     */
    public String getCacheName();

    /**
     * 得到超时时间
     * @see play.libs.Time#parseDuration(String)
     * @return 时间，如：10s, 3d
     */
    public String getExpireTime();

    /**
     * 执行，得到需要缓存的内容
     * @return 内容
     */
    public T execute();
}
