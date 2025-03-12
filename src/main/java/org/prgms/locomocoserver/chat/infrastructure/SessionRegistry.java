package org.prgms.locomocoserver.chat.infrastructure;

import org.prgms.locomocoserver.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SessionRegistry {

    private final ConcurrentMap<String, User> sessions = new ConcurrentHashMap<>();

    public void addSession(String sessionId, User user) {
        sessions.put(sessionId, user);
    }

    public User getUser(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
