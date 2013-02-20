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
     * 得到缓存key显示名称，可以是中文的，方便运营人员查看
     * @return cache name
     */
    public String getCacheDisplayName();

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
    public T execute() throws Exception;

    /**
     * 持久化缓存，目的是为了便于知道目前缓存的详细信息以及将来可以清除指定的缓存
     *
     * @param cacheName 缓存Key名称
     * @param obj 缓存内容
     */
    void persist(String cacheName, Object obj);
}
