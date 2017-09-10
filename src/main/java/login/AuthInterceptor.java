package login;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AuthInterceptor implements HandlerInterceptor {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
		System.out.println(" Pre handle ");
		String accessToken = request.getParameter("accessToken");
		String[] userInfo = new String[3];
		boolean auth = false;
		// extract userID, last access time and access token
		System.out.println("accessToken:  " + accessToken);
		if (accessToken != null) {
			String decryptedToken = Base64Encryption.decryption(accessToken);
			if (decryptedToken != null) {
				userInfo = decryptedToken.split(":");
				// userInfo[0] = user_id, userInfo[1] = local_access_token
				try {
					// auth the token
					Class.forName("com.mysql.cj.jdbc.Driver");
					Connection con = DriverManager.getConnection(
							"jdbc:mysql://127.0.0.1:3306/lares_beauty?autoReconnect=true&useSSL=false", "root",
							"123456");
					if (con != null) {
						System.out.println("Connected to the database");
					}
					PreparedStatement pstmt = con.prepareStatement(
							"select * from user_local_auth where user_id = ?  and  local_access_token = ?;");
					pstmt.setString(1, userInfo[0]);
					pstmt.setString(2, userInfo[1]);
					ResultSet rs = pstmt.executeQuery();
					if (rs.next()) {
						LocalDateTime lastAccess = rs.getTimestamp("last_access_time").toLocalDateTime();
						LocalDateTime currentAccess = LocalDateTime.now();
						System.out.println(currentAccess);
						System.out.println(lastAccess);
						long time = lastAccess.until(currentAccess, ChronoUnit.SECONDS);
						System.out.println("time: " + time);
						int expireTime = rs.getInt("local_expires") * 60;
						if (time > expireTime) {
							// send timeout msg
						} else if (time > 0 && time <= expireTime) {
							auth = true;
							User user = new User();
							user.setUserId(rs.getInt("user_id"));
							user.setUsername(rs.getString("username"));
							UserContext.current.set(user);
							// update access time
								Timestamp now = new Timestamp(System.currentTimeMillis());
								PreparedStatement update = con.prepareStatement(
										"UPDATE user_local_auth SET last_access_time = ? WHERE user_id = ?;");
								update.setTimestamp(1, now);
								update.setString(2, rs.getString("user_id"));
								update.execute();
						}
					}
					con.close();
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