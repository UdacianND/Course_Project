package course_project.utils;

import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SessionManager {

    private static final Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession getSessionByUsername(String username){
        return sessions.get(username);
    }

    public static void removeSession(String username){
        sessions.remove(username);
    }

    public static void newSession(String username, HttpSession session){
        sessions.put(username,session);
    }

}