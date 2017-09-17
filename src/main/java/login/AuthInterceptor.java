package login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import bean.UserAccessToken;
import dao.AuthInterceptorDAO;
import dao.LoginDAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AuthInterceptor implements HandlerInterceptor {

	@Autowired  
	AuthInterceptorDAO authInterceptorDAO;
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		System.out.println(" Pre handle ");
		String accessToken = request.getParameter("access_token");
		String[] userInfo = new String[3];
		boolean auth = false;
		// extract userID, last access time and access token
		System.out.println("accessToken:  " + accessToken);
		if (accessToken != null) {
			String decryptedToken = Base64Encryption.decryption(accessToken);
			if (decryptedToken != null) {
				userInfo = decryptedToken.split(":");
				int userId = Integer.parseInt(userInfo[0]);
				String token = userInfo[1];
				// userInfo[0] = user_id, userInfo[1] = local_access_token
				try {
					// auth the token
					List<UserAccessToken> correctTokenRecord = authInterceptorDAO.getUserAccessToken(userId, token);
					if (correctTokenRecord.size() == 1) {
						LocalDateTime lastAccess = correctTokenRecord.get(0).getLastAccessTime().toLocalDateTime();
						LocalDateTime currentAccess = LocalDateTime.now();
						System.out.println(currentAccess);
						System.out.println(lastAccess);
						long time = lastAccess.until(currentAccess, ChronoUnit.SECONDS);
						System.out.println("time: " + time);
						int expireTime = correctTokenRecord.get(0).getTokenExpireTime() * 60;
						if (time > expireTime) {
							// send timeout msg
						} else if (time > 0 && time <= expireTime) {
							User user = new User();
							user.setUserId(userId);
							UserContext.current.set(user);
							// update access time
								Timestamp now = new Timestamp(System.currentTimeMillis());
								int successUpdate = authInterceptorDAO.updateLastAccessTime(userId,now);
								if (successUpdate == 1){
									auth = true;
								} else {
									System.out.println("Cannot Update Access Time");
								}
						}
					}
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}
		if (auth == false) {
			response.sendRedirect("https://www.google.com.hk/");
		}
		return auth;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView model)
			throws Exception {
		System.out.println(" Post handle ");
		UserContext.current.remove();
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e)
			throws Exception {
		// TODO Auto-generated method stub

	}
}