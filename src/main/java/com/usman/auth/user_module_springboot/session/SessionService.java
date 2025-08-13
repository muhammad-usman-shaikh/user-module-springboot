package com.usman.auth.user_module_springboot.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.usman.auth.user_module_springboot.user.User;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;

    }

    /**
     * Creates a new session and saves it to the database.
     *
     * @param user         the user that this session belongs to
     * @param refreshToken the refresh token for this session
     * @return the newly created session
     */
    public Session createSession(User user, String refreshToken) {
        Session session = new Session();
        session.setUser(user);
        session.setRefreshToken(refreshToken);
        return sessionRepository.save(session);
    }
}
