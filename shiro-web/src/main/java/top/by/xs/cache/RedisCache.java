package top.by.xs.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import top.by.xs.util.JedisUtil;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

@Component
public class RedisCache<K, V> implements Cache<K, V> {

    private static final String CACHE_PREFIX = "shiro-web-cache:";

    @Resource
    private JedisUtil jedisUtil;

    private byte[] getKey(K k) {
        if (k instanceof String) {
            return (CACHE_PREFIX + k).getBytes();
        }

        return SerializationUtils.serialize(k);
    }


    public V get(K k) throws CacheException {
        byte[] value = jedisUtil.get(getKey(k));
        System.out.println("get redis roles => " + value);
        if (value != null) {
            return (V) SerializationUtils.deserialize(value);
        }

        // TODO
        // 每次从redis中去取的话，redis server压力过大
        // 可以在此处实现二级缓存
        // 缓存到本地内存中
        // 从而提升性能
        // 当本地缓存中取不到的时候在去redis中取

        return null;
    }

    public V put(K k, V v) throws CacheException {
        byte[] key   = getKey(k);
        byte[] value = SerializationUtils.serialize(v);

        jedisUtil.set(key, value);
        // 设置超时时间
        jedisUtil.expire(key, 10 * 60);

        return v;
    }

    public V remove(K k) throws CacheException {
        byte[] key   = getKey(k);
        byte[] value = jedisUtil.get(key);
        jedisUtil.del(key);

        if (value != null) {
            return (V) SerializationUtils.deserialize(value);
        }

        return null;
    }

    public void clear() throws CacheException {
        // TODO
        // 不去重写
        // 有可能清空redis中的数据
    }

    public int size() {
        // TODO
        return 0;
    }

    public Set<K> keys() {
        // TODO
        return null;
    }

    public Collection<V> values() {
        // TODO
        return null;
    }
}
