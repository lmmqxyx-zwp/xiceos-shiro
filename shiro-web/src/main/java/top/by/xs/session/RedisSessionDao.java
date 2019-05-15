package top.by.xs.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;
import top.by.xs.util.JedisUtil;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RedisSessionDao extends AbstractSessionDAO {

    @Resource
    private JedisUtil jedisUtil;

    // 前缀
    private static final String SHIRO_SESSION_PREFIX = "shiro-web-session";

    private byte[] getKey(String key) {
        return (SHIRO_SESSION_PREFIX + key).getBytes();
    }

    /**
     * 创建session
     *
     * @param session
     * @return
     */
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);

        // 需要在此处把生成的sessionId和session进行捆绑
        assignSessionId(session, sessionId);

        saveSession(session);
        return sessionId;
    }

    /**
     * 获取session
     *
     * @param sessionId
     * @return
     */
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            return null;
        }

        // 使用原生的session manager
        // 导致在此处调用多次读取session
        // 需要重写shiro的session manager
        System.out.println("read session = > " + sessionId);

        byte[] key   = getKey(sessionId.toString());
        byte[] value = jedisUtil.get(key);

        Session session = (Session) SerializationUtils.deserialize(value);

        return session;
    }

    /**
     * 更新session
     *
     * @param session
     * @throws UnknownSessionException
     */
    public void update(Session session) throws UnknownSessionException {
        saveSession(session);
    }

    /**
     * 删除session
     *
     * @param session
     */
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }

        byte[] key = getKey(session.getId().toString());
        jedisUtil.del(key);
    }

    /**
     * 获取到指定的存活的session
     *
     * @return
     */
    public Collection<Session> getActiveSessions() {
        // 通过前缀获取所有的key
        Set<byte[]> keys = jedisUtil.keys(SHIRO_SESSION_PREFIX);

        Set<Session> sessions = new HashSet<Session>();

        // 判断是否为空
        if (CollectionUtils.isEmpty(keys)) {
            return null;
        }

        for (byte[] key : keys) {
            Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
            sessions.add(session);
        }

        return sessions;
    }

    /**
     * 保存session
     *
     * @param session
     */
    private void saveSession(Session session) {
        if (session != null && session.getId() != null) {
            byte[] key   = getKey(session.getId().toString());
            byte[] value = SerializationUtils.serialize(session);

            jedisUtil.set(key, value);
            // 设置超时时间
            // 10分钟
            jedisUtil.expire(key, 10 * 60);
        }
    }
}
