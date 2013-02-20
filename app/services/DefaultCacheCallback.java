package services;

/**
 * @author wujinliang
 * @since 9/28/12
 */
public abstract class DefaultCacheCallback<T> implements CacheCallback<T> {
    @Override
    public String getExpireTime() {
        return "24h";
    }

    @Override
    public String getCacheDisplayName() {
        return getCacheName();
    }

    @Override
    public void persist(String cacheName, Object obj) {

    }
}
