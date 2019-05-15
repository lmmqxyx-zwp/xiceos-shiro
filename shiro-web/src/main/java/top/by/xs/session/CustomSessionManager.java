package top.by.xs.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

public class CustomSessionManager extends DefaultWebSessionManager {

    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {

        // 重写的思想
        // 先从reques中取
        // 若request中没有，则从redis中取
        // 此处需要获取request
        // 第一次request中肯定没有shiro session
        // 需要从redis中取得session
        // 存入request中
        // 以后就可以从request中取
        // 减少redis的压力

        Serializable sessionId = getSessionId(sessionKey);

        ServletRequest request = null;

        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }

        Session session = null;

        if (request != null && sessionId != null) {
            session = (Session) request.getAttribute(sessionId.toString());
        }

        // 若没有取到session
        // 则从redis中取
        if (session == null) {
            session = super.retrieveSession(sessionKey);
            if (sessionId != null) {
                request.setAttribute(sessionId.toString(), session);
            }
        }

        return session;
    }
}
