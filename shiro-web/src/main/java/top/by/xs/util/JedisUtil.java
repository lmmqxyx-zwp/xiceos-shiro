package top.by.xs.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Set;

@Component
public class JedisUtil {

    @Resource
    private JedisPool jedisPool;

    /**
     * 获取redis连接
     *
     * @return
     */
    private Jedis getJedis() {
       return jedisPool.getResource();
    }

    /**
     * 写入redis键值对
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public byte[] set(byte[] key, byte[] value) {
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }

        return value;
    }

    /**
     * 设置超时时间
     *
     * @param key    键
     * @param expire 超时时间：秒
     */
    public void expire(byte[] key, int expire) {
        Jedis jedis = getJedis();
        try {
            jedis.expire(key, expire);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    /**
     * 根据键取得值
     *
     * @param key 键
     * @return
     */
    public byte[] get(byte[] key) {
        Jedis jedis = getJedis();
        byte[] value = null;
        try {
            value = jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }

        return value;
    }

    /**
     * 删除指定的键值
     *
     * @param key 键
     */
    public void del(byte[] key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
    }

    public Set<byte[]> keys(String prefix) {
        Jedis jedis = getJedis();

        Set<byte[]> keys = null;

        try {
            keys = jedis.keys((prefix + "*").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }

        return keys;
    }

}
