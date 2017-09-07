package login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jni.User;

public interface Authenticator {
    // successful return user, fail return error and no authentication msg reutrn null
    User authenticate(HttpServletRequest request, HttpServletResponse response);
}
