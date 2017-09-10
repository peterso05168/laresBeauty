package login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jni.User;

public class LocalAuthenticator implements Authenticator {
    public User authenticate(HttpServletRequest request, HttpServletResponse response) {
        String auth = getHeaderFromRequest(request, "Authorization");
        if (auth == null) {
            return null;
        }
        String username = parseUsernameFromAuthorizationHeader(auth);
        String password = parsePasswordFromAuthorizationHeader(auth);
        return authenticateUserByPassword(username, password);
    }

	private User authenticateUserByPassword(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	private String parsePasswordFromAuthorizationHeader(String auth) {
		// TODO Auto-generated method stub
		return null;
	}

	private String parseUsernameFromAuthorizationHeader(String auth) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getHeaderFromRequest(HttpServletRequest request, String string) {
		// TODO Auto-generated method stub
		return null;
	}
}