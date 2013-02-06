package services;

/**
 * @author wujinliang
 * @since 9/28/12
 */
public abstract class DefaultCacheCallback<T> implements CacheCallback<T> {
    public String getExpireTime() {
        return null;
    }
}
