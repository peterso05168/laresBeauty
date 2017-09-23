package controller;

import jsonobject.JSONLogin;
import jsonobject.JSONRegister;
import login.Base64Encryption;
import login.FBConnection;
import login.FBGraph;
import login.TokenHashing;
import util.CommonUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bean.UserFacebookAuth;
import bean.UserLocalAuth;
import dao.LoginDAO;



@RequestMapping(value = "login")

@RestController
public class LoginController {
	
	@Autowired  
	LoginDAO loginDAO;
	
	private static final String[] IP_HEADER_CANDIDATES = { 
		    "X-Forwarded-For",
		    "Proxy-Client-IP",
		    "WL-Proxy-Client-IP",
		    "HTTP_X_FORWARDED_FOR",
		    "HTTP_X_FORWARDED",
		    "HTTP_X_CLUSTER_CLIENT_IP",
		    "HTTP_CLIENT_IP",
		    "HTTP_FORWARDED_FOR",
		    "HTTP_FORWARDED",
		    "HTTP_VIA",
		    "REMOTE_ADDR" };


	@RequestMapping(value = "/localLogin")
	public JSONLogin localLogin(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) throws IOException, NoSuchAlgorithmException {
		JSONLogin jsonLogin = new JSONLogin();
		try {
			List<UserLocalAuth> userLocalAuth = loginDAO.localAuth(username,password);
			if (!CommonUtil.isNullOrEmpty(userLocalAuth) && userLocalAuth.size() == 1) {
				int userId = userLocalAuth.get(0).getUserId();
				Timestamp lastAccessTime = new Timestamp(System.currentTimeMillis());
				int expires = 5;
				TokenHashing newHash = new TokenHashing();
				String newSalt = newHash.getSalt();
				String newSalt2 = newHash.getSalt();
				String token = (userId + ":" + newSalt + ":" + newSalt2);
				String hashedToken = newHash.hash(token, newSalt);
				int successFlag = loginDAO.updateUserLocalToken(userId,hashedToken,expires,lastAccessTime);
				if(successFlag == 1) {
					// use base64 encryption to generate token for client
					String rawLocalAccessToken = (userId + ":" + hashedToken);
					String localAccessToken = Base64Encryption.encryption(rawLocalAccessToken);
					jsonLogin.setAccessToken(localAccessToken);
					jsonLogin.setUsername(username);
				} else {
					jsonLogin.setError("Error: cannot get accesstoken.Please Login again");
				}
			} else {
				jsonLogin.setError("Error: Wrong Username or Password");
			}
		}catch (Exception e) {
			jsonLogin.setError("Please Contact Support.Error Message: " + e );
			System.out.println(e);
		}
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver");
//			Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lares_beauty?autoReconnect=true&useSSL=false", "root", "123456");
//			if (con != null) {
//				System.out.println("Connected to the database");
//			}
//			PreparedStatement pstmt = con.prepareStatement("select * from user_local_auth where username = ? and password = ?");
//			pstmt.setString(1, username);
//			pstmt.setString(2, password);
//			ResultSet rs = pstmt.executeQuery();
//			if (rs.next()) {
//				System.out.println("User " + username + " Login Successful.");
//				Timestamp now = new Timestamp(System.currentTimeMillis());
//				TokenHashing newHash = new TokenHashing();
//				String newSalt = newHash.getSalt();
//				String newSalt2 = newHash.getSalt();
//				System.out.println("newSalt: " + newSalt);
//				System.out.println("newSalt2 " + newSalt2);
//				String token = (rs.getString("user_id") + ":" + newSalt + ":" + newSalt2);
//				String hashedToken = newHash.hash(token, newSalt);
//				PreparedStatement update = con.prepareStatement("UPDATE user_access_token SET user_local_token = ?, token_expire_time = ?, last_access_time = ? WHERE user_id = ?;");
//				update.setString(1, hashedToken);
//				update.setInt(2, 5);
//				update.setTimestamp(3, now);
//				update.setString(4, rs.getString("user_id"));
//				update.execute();
//				String rawAccessToken = (rs.getString("user_id") + ":" + hashedToken);
//				String accessToken = Base64Encryption.encryption(rawAccessToken);
//				jsonLogin.setAccessToken(accessToken);
//			}
//			con.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		 return jsonLogin;
	}
	
	@RequestMapping(value = "/registrationRequest")
	public jsonobject.JSONObject registrationRequest() {
		//13digits token
		//1,2,3digit = Reminder(2): 3-digit number divided by 13.(((8-76)*13)+2)
		//4,5digit is useless;
		//6,7digit = 2-digit odd number(11-99)
		//8,9,10,11digit = Reminder(57):4-digit number divided by 83.(((13-120)*83)+57)
		//12,13digit is garbage;
		Random rand = new Random();
		Integer oneTwoThree = ((rand.nextInt(69) + 8) * 13) + 2;
		Integer fourFive = (rand.nextInt(90) + 10);
		Integer sixSeven = ((rand.nextInt(45) + 6) * 2) - 1;
		Integer eightNineTenEleven = ((rand.nextInt(108) + 13) * 83) + 57;
		Integer twelveThirteen = (rand.nextInt(90) + 30) / 2;
		String token = oneTwoThree.toString() + fourFive.toString() + sixSeven.toString() + eightNineTenEleven.toString() + twelveThirteen.toString();
		jsonobject.JSONObject registrationToken = new jsonobject.JSONObject();
        registrationToken.setCode("s");
        registrationToken.setDetail(token);
		return registrationToken;
	}
	
	
	@RequestMapping(value = "/localRegister", method = RequestMethod.POST)
	public JSONRegister localRegister(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "username") String username, @RequestParam(value = "password") String password,
			@RequestParam(value = "email") String email,@RequestParam(value = "registration_token" ) String registrationToken) {
		char[] token = registrationToken.toCharArray();
		JSONRegister json = new JSONRegister();
		//13digits token
		//1,2,3digit = Reminder(2): 3-digit number divided by 13.(((8-76)*13)+2)
		//4,5digit is useless;
		//6,7digit = 2-digit odd number(11-99)
		//8,9,10,11digit = Reminder(57):4-digit number divided by 83.(((13-120)*83)+57)
		//12,13digit is garbage;
		int oneTwoThree = getInt(token[0]) * 100 + getInt(token[1]) * 10 + getInt(token[2]);
		int sixSeven = getInt(token[5]) * 10 + getInt(token[6]);
		int eightNineTenEleven = getInt(token[7]) * 1000 + getInt(token[8]) * 100 + getInt(token[9]) * 10 + getInt(token[10]);
		if (token.length == 13 && (oneTwoThree % 13) == 2 && (sixSeven % 2) == 1 && (eightNineTenEleven % 83) == 57) {
			List<UserLocalAuth> localUser = loginDAO.getLocalUser(email);
			if (!CommonUtil.isNullOrEmpty(localUser)) {
				json.setUsername(username);
				json.setRegistration_status(false);
				json.setError("Username already exists");
			} else {
				int userId = loginDAO.createNewLocalUser(email);
				int success_local_auth = loginDAO.createNewUserLocalAuth(email, password, userId);
				int success_local_token = loginDAO.createNewUserLocalTokenRecord(userId);
				if (success_local_auth == 1 && success_local_token == 1) {
					json.setUsername(username);
					json.setRegistration_status(true);
				} else {
					json.setUsername(username);
					json.setRegistration_status(false);
					json.setError("Cannot create user,please try again");
				}
			}
		} else {
			json.setUsername(username);
			json.setRegistration_status(false);
			json.setError("Invalid Token,please try again");
		}
		return json;
	}
	
	@RequestMapping(value = "/fbLogin")
	public void fbLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("fbLogin STARTED ");
		System.out.println("IP: " + request.getRemoteAddr());
		FBConnection fbConnection = new FBConnection();
		String redirectUrl = fbConnection.getFBAuthUrl();
		response.sendRedirect(redirectUrl);
		for (String header : IP_HEADER_CANDIDATES) {
	        String ip = request.getHeader(header);
	        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
	        	System.out.println("header: " + ip);
	        }
	    }
		System.out.println("IP: " + request.getRemoteAddr());
	}
	
	@RequestMapping(value = "/fbAuth")
	public JSONLogin fbAuth(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		System.out.println("fbAUTH STARTED ");
		JSONLogin jsonLogin = new JSONLogin();
		String code = "";
		code = request.getParameter("code");
		System.out.println("code: " + code);
		if (code == null || code.equals("")) {
			jsonLogin.setError("Error: cannot get Facebook accesstoken.Please Login again");
		} else {
			FBConnection fbConnection = new FBConnection();
			JSONObject FBJSON = null;
			FBJSON = fbConnection.getAccessToken(code);
			if (FBJSON != null) {
				String fbAccessToken = FBJSON.getString("access_token");
				int fbexpiresIn = FBJSON.getInt("expires_in");
				FBGraph fbGraph = new FBGraph(fbAccessToken);
				String graph = fbGraph.getFBGraph();
				JSONObject fbProfileData = fbGraph.getGraphData(graph);
				if (fbProfileData != null) {
					String username = fbProfileData.getString("name");
					String facebookId = fbProfileData.getString("id");
					// access DB and update data
					try {
						List<UserFacebookAuth> userFacebookAuth = loginDAO.getFacebookUser(facebookId);
						// check if user exists
						if (!CommonUtil.isNullOrEmpty(userFacebookAuth) && userFacebookAuth.size() == 1) {
							int userId = userFacebookAuth.get(0).getUserId();
							Timestamp lastAccessTime = new Timestamp(System.currentTimeMillis());
							int expires = 5;
							TokenHashing newHash = new TokenHashing();
							String newSalt = newHash.getSalt();
							String newSalt2 = newHash.getSalt();
							String token = (userId + ":" + newSalt + ":" + newSalt2);
							String hashedToken = newHash.hash(token, newSalt);
							int successUpdatelocaltoken = loginDAO.updateUserLocalToken(userId, hashedToken, expires,
									lastAccessTime);
							// check if local token is updated
							if (successUpdatelocaltoken == 1) {
								int successUpdateFBtoken = loginDAO.updateUserFacebookToken(userId, fbAccessToken,
										fbexpiresIn, facebookId);
								// check if Facebook token is updated
								if (successUpdateFBtoken == 1) {
									// use base64 encryption to generate token
									// for client
									String rawLocalAccessToken = (userId + ":" + hashedToken);
									String localAccessToken = Base64Encryption.encryption(rawLocalAccessToken);
									jsonLogin.setAccessToken(localAccessToken);
									jsonLogin.setUsername(username);
								} else {
									jsonLogin.setError("Error: cannot get Facebook accesstoken.Please Login again");
								}
							} else {
								jsonLogin.setError("Error: cannot get accesstoken.Please Login again");
							}
						} else if (CommonUtil.isNullOrEmpty(userFacebookAuth)) {
							// create new user who logins via Facebook
							int newUserId = loginDAO.createNewFacebookUser(username);
							System.out.println("NEW USER: " + newUserId);
							Timestamp lastAccessTime = new Timestamp(System.currentTimeMillis());
							int expires = 5;
							TokenHashing newHash = new TokenHashing();
							String newSalt = newHash.getSalt();
							String newSalt2 = newHash.getSalt();
							String token = (newUserId + ":" + newSalt + ":" + newSalt2);
							String hashedToken = newHash.hash(token, newSalt);
							// create new user's local token record
							int sccuessCreateNewUserLocalToken = loginDAO.createNewUserLocalToken(newUserId,
									hashedToken, expires, lastAccessTime);
							if (sccuessCreateNewUserLocalToken == 1) {
								// create new user's facebook auth record
								int sccuessCreateNewFacebookAuth = loginDAO.createNewUserFacebook(newUserId, facebookId,
										fbAccessToken, fbexpiresIn);
								if (sccuessCreateNewFacebookAuth == 1) {
									// use base64 encryption to generate token
									// for client
									String rawLocalAccessToken = (newUserId + ":" + hashedToken);
									String localAccessToken = Base64Encryption.encryption(rawLocalAccessToken);
									jsonLogin.setAccessToken(localAccessToken);
									jsonLogin.setUsername(username);
								}
							}
						} else {
							jsonLogin.setError("Error: Duplicated Facebook User");
						}
					} catch (Exception e) {
						jsonLogin.setError("Please Contact Support.Error Message: " + e);
						System.out.println(e);
					}
						// try {
						// Class.forName("com.mysql.cj.jdbc.Driver");
						// Connection con = DriverManager.getConnection(
						// "jdbc:mysql://127.0.0.1:3306/lares_beauty?autoReconnect=true&useSSL=false",
						// "root",
						// "123456");
						// if (con != null) {
						// System.out.println("Connected to the database");
						// }
						// PreparedStatement selectUser = con
						// .prepareStatement("select * from user_facebook_auth
						// where facebook_id = ?");
						// selectUser.setString(1,
						// fbProfileData.getString("id"));
						// ResultSet rsExistUser = selectUser.executeQuery();
						// if (rsExistUser.next()) {
						// System.out.println("User exists ");
						// int userId = rsExistUser.getInt("user_id");
						// Timestamp now = new
						// Timestamp(System.currentTimeMillis());
						// TokenHashing newHash = new TokenHashing();
						// String newSalt = newHash.getSalt();
						// String newSalt2 = newHash.getSalt();
						// String token = (userId + ":" + newSalt + ":" +
						// newSalt2);
						// String hashedToken = newHash.hash(token, newSalt);
						// // update user's local auth info
						// PreparedStatement updateLocal = con.prepareStatement(
						// "UPDATE user_access_token SET user_local_token = ?,
						// token_expire_time = ?, last_access_time = ? WHERE
						// user_id = ?;");
						// updateLocal.setString(1, hashedToken);
						// updateLocal.setString(2, "5");
						// updateLocal.setTimestamp(3, now);
						// updateLocal.setInt(4, userId);
						// updateLocal.execute();
						// // update user's facebook auth info
						// PreparedStatement updateFB = con.prepareStatement(
						// "UPDATE user_facebook_auth SET facebook_access_token
						// = ?, facebook_expires = ? WHERE user_id = ?;");
						// updateFB.setString(1, fbAccessToken);
						// updateFB.setInt(2, fbexpiresIn);
						// updateFB.setInt(3, userId);
						// updateFB.execute();
						// // use base64 encryption to generate token for client
						// String rawAccessToken = (userId + ":" + hashedToken);
						// String localAccessToken =
						// Base64Encryption.encryption(rawAccessToken);
						// jsonLogin.setAccessToken(localAccessToken);
						// jsonLogin.setUsername(username);
						// } else {
						// System.out.println("User creates ");
						// // create new user
						// PreparedStatement createUserStatement =
						// con.prepareStatement("INSERT INTO user
						// ()VALUES();",Statement.RETURN_GENERATED_KEYS);
						// createUserStatement.execute();
						// ResultSet rs =
						// createUserStatement.getGeneratedKeys();
						// if (rs.next()) {
						// System.out.println("RS: " + rs);
						// int newUserId = rs.getInt(1);
						// System.out.println("new user's id:" + newUserId);
						// Timestamp now = new
						// Timestamp(System.currentTimeMillis());
						// TokenHashing newHash = new TokenHashing();
						// String newSalt = newHash.getSalt();
						// String newSalt2 = newHash.getSalt();
						// String token = (newUserId + ":" + newSalt + ":" +
						// newSalt2);
						// String hashedToken = newHash.hash(token, newSalt);
						// // create new user's local auth record
						// PreparedStatement createLocal = con.prepareStatement(
						// "INSERT INTO user_access_token
						// (user_id,user_local_token, token_expire_time ,
						// last_access_time) VALUES (?,?,?,?);");
						// createLocal.setInt(1, newUserId);
						// createLocal.setString(2, hashedToken);
						// createLocal.setInt(3, 5);
						// createLocal.setTimestamp(4, now);
						// createLocal.execute();
						// // create new user's facebook auth record
						// PreparedStatement createFb = con.prepareStatement(
						// "INSERT INTO user_facebook_auth (user_id,
						// facebook_id, facebook_access_token ,
						// facebook_expires) VALUES (?,?,?,?);");
						// createFb.setInt(1, newUserId);
						// createFb.setString(2, fbProfileData.getString("id"));
						// createFb.setString(3, fbAccessToken);
						// createFb.setInt(4, fbexpiresIn);
						// createFb.execute();
						// // use base64 encryption to generate token for
						// // client
						// String rawAccessToken = (newUserId + ":" +
						// hashedToken);
						// String localAccessToken =
						// Base64Encryption.encryption(rawAccessToken);
						// jsonLogin.setAccessToken(localAccessToken);
						// jsonLogin.setUsername(username);
						// }
						// }
						// con.close();
						// } catch (SQLException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// } catch (ClassNotFoundException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// } catch (Exception e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
					
				} else {
					jsonLogin.setError("Error: cannot get Facebook User Detail.Please Login again");
				}
			} else {
				jsonLogin.setError("Error: cannot decrypt Facebook accesstoken.Please Login again");
			}
		}
		return jsonLogin;
	}
	
	
	protected int getInt(char c) {
		return Character.getNumericValue(c);
	}
}
