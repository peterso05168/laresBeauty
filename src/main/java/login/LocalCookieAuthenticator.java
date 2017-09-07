package login;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jni.User;

public class LocalCookieAuthenticator implements Authenticator {
    public User authenticate(HttpServletRequest request, HttpServletResponse response) {
        String cookie = getCookieFromRequest(request, "cookieName");
        if (cookie == null) {
            return null;
        }
        return getUserByCookie(cookie);
    }

	private String getCookieFromRequest(HttpServletRequest request, String string) {
		request.getCookies();
		
		return null;
	}

}