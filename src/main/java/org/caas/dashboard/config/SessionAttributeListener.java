package org.caas.dashboard.config;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SessionAttributeListener implements HttpSessionAttributeListener {

    public static Logger logger = LoggerFactory.getLogger(SessionAttributeListener.class);

    public static ConcurrentHashMap<HttpSession, String> sessionMap = new ConcurrentHashMap<>();

    public void init(ServletConfig config) {
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        HttpSession session = event.getSession();
        String attributeName = event.getName();

        logger.info("HttpSessionBindingEvent getSource={},getValue={},attributeName={}]",event.getSource(),event.getValue(),attributeName);

        if (attributeName.equals("SPRING_SECURITY_CONTEXT")) {
            if (event.getValue() instanceof SecurityContextImpl) {
                SecurityContextImpl securityContextImpl = (SecurityContextImpl) event.getValue();
                Authentication authentication = securityContextImpl.getAuthentication();
                String userId = authentication.getName();

                duplicateUserCheck(sessionMap, session, userId);


                if (sessionMap.get(session) == null) {

                    logger.info("SessionAttributeListener attributeAdded put[userId={},session={}]", userId, session);

                    sessionMap.put(session, userId);
                }
            }
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        String attributeName = event.getName();
        HttpSession session = event.getSession();
        // if (attributeName.equals("SPRING_SECURITY_CONTEXT")) {
        // if (event.getValue() instanceof SecurityContextImpl) {
        // if (sessionMap.get(session) != null) {
        // sessionMap.remove(session);
        // }
        // }
        // }

        if (attributeName.equals("SPRING_SECURITY_CONTEXT")) {
            SecurityContextImpl securityContextImpl = (SecurityContextImpl) event.getValue();
            Authentication authentication = securityContextImpl.getAuthentication();
            String userId = authentication.getName();
            logger.debug("attributeRemoved userId = {},session={}", userId,session);

            if (sessionMap.get(session) == null) {
                logger.info("{} 는 중복권한으로 로그 아웃 되었습니다.", userId);
                logger.info("Session = {}", userId);
            } else {
                sessionMap.remove(session);
            }
        }

    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
    }

    private void duplicateUserCheck(ConcurrentHashMap<HttpSession, String> sessionMap,HttpSession session,String userId) {

        if(ObjectUtils.isEmpty(sessionMap))
            return ;
        Enumeration<HttpSession> keys = sessionMap.keys();
        while (keys.hasMoreElements()) {
            HttpSession saveSession = (HttpSession) keys.nextElement();



            if (sessionMap.get(saveSession).equals(userId)&&!saveSession.equals(session)) {
                logger.info("SessionAttributeListener duplicateUserCheck diff session = {},saveSession={},userId={}", session,saveSession,userId);
                sessionMap.remove(saveSession);
            }
        }
    }

}

