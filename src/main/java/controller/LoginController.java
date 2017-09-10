package controller;

import dao.ProductDAO;
import jsonobject.JSONLogin;
import login.Base64Encryption;
import login.TokenHashing;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "login")

@RestController
public class LoginController {
	@RequestMapping(value = "/login")
	public JSONLogin login(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) throws IOException, NoSuchAlgorithmException {
		JSONLogin jsonLogin = new JSONLogin();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lares_beauty?autoReconnect=true&useSSL=false", "root", "123456");
			if (con != null) {
				System.out.println("Connected to the database");
			}
			PreparedStatement pstmt = con.prepareStatement("select * from user_local_auth where username = ? and password = ?");
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				System.out.println("User " + username + " Login Successful.");
				Timestamp now = new Timestamp(System.currentTimeMillis());
				TokenHashing newHash = new TokenHashing();
				String newSalt = newHash.getSalt();
				String newSalt2 = newHash.getSalt();
				System.out.println("newSalt: " + newSalt);
				System.out.println("newSalt2 " + newSalt2);
				String token = (rs.getString("user_id") + ":" + newSalt + ":" + newSalt2);
				String hashedToken = newHash.hash(token, newSalt);
				PreparedStatement update = con.prepareStatement("UPDATE user_local_auth SET local_access_token = ?, local_expires = ?, last_access_time = ? WHERE user_id = ?;");
				update.setString(1, hashedToken);
				update.setString(2, "5");
				update.setTimestamp(3, now);
				update.setString(4, rs.getString("user_id"));
				update.execute();
				String rawAccessToken = (rs.getString("user_id") + ":" + hashedToken);
				String accessToken = Base64Encryption.encryption(rawAccessToken);
				jsonLogin.setAccessToken(accessToken);
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return jsonLogin;
	}
}
