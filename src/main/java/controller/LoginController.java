package controller;

import jsonobject.JSONLogin;
import jsonobject.JSONLoginResult;
import jsonobject.JSONLoginToken;
import jsonobject.JSONRegister;
import jsonobject.JSONRegisterResult;
import jsonobject.JSONResult;
import login.FBConnection;
import login.FBGraph;
import util.CommonUtil;
import util.MailUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bean.User;
import bean.UserFacebookAuth;
import bean.UserLocalAuth;
import dao.LoginDAO;

@RequestMapping(value = "login")

@CrossOrigin
@Transactional("tjtJTransactionManager")
@RestController
public class LoginController {

	@Autowired
	LoginDAO loginDAO;

	@RequestMapping(value = "/local_login", method = RequestMethod.POST)
	public JSONLoginResult localLogin(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "username") String username, @RequestParam(value = "password") String password)
			 {
		JSONLoginResult jsonLoginResult = new JSONLoginResult();
		JSONLogin jsonLogin = new JSONLogin();
		try {
			List<UserLocalAuth> getUser = loginDAO.getLocalUser(username);
			if (!CommonUtil.isNullOrEmpty(getUser) && getUser.size() == 1) {
				String salt = getUser.get(0).getSalt();
				String hashedPassword = CommonUtil.SHA512Hashing(password, salt);
				List<UserLocalAuth> userLocalAuth = loginDAO.localAuth(username, hashedPassword);
				if (!CommonUtil.isNullOrEmpty(userLocalAuth) && userLocalAuth.size() == 1) {
					int userId = userLocalAuth.get(0).getUserId();
					Timestamp lastAccessTime = new Timestamp(System.currentTimeMillis());
					int expires = 5;
					String newSalt = CommonUtil.getSalt();
					String newSalt2 = CommonUtil.getSalt();
					String token = (userId + ":" + newSalt + ":" + newSalt2);
					String hashedToken = CommonUtil.SHA256Hashing(token, newSalt);

					int successFlag = loginDAO.updateUserLocalToken(userId, hashedToken, expires, lastAccessTime);
					if (successFlag == 1) {
						// use base64 encryption to generate token for client
						String rawLocalAccessToken = (userId + ":" + hashedToken);
						String localAccessToken = CommonUtil.base64Encryption(rawLocalAccessToken);
						jsonLogin.setAccessToken(localAccessToken);
						jsonLogin.setUsername(username);
						jsonLogin.setUserId(userId);
						jsonLoginResult.setCode("S");
						jsonLoginResult.setData(jsonLogin);
					} else {
						jsonLoginResult.setCode("F");
						jsonLoginResult.setDetail("Error: cannot get accesstoken.Please Login again");
					}
				} else {
					jsonLoginResult.setCode("F");
					jsonLoginResult.setDetail("Error: Wrong Username or Password");
				}
			} else {
				jsonLoginResult.setCode("F");
				jsonLoginResult.setDetail("Error: User not exists");
			}
		} catch (Exception e) {
			jsonLoginResult.setCode("F");
			jsonLoginResult.setDetail("Please Contact Support.Error Message: " + e.getMessage());
		}
		// try {
		// Class.forName("com.mysql.cj.jdbc.Driver");
		// Connection con =
		// DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/lares_beauty?autoReconnect=true&useSSL=false",
		// "root", "123456");
		// if (con != null) {
		// System.out.println("Connected to the database");
		// }
		// PreparedStatement pstmt = con.prepareStatement("select * from user_local_auth
		// where username = ? and password = ?");
		// pstmt.setString(1, username);
		// pstmt.setString(2, password);
		// ResultSet rs = pstmt.executeQuery();
		// if (rs.next()) {
		// System.out.println("User " + username + " Login Successful.");
		// Timestamp now = new Timestamp(System.currentTimeMillis());
		// TokenHashing newHash = new TokenHashing();
		// String newSalt = newHash.getSalt();
		// String newSalt2 = newHash.getSalt();
		// System.out.println("newSalt: " + newSalt);
		// System.out.println("newSalt2 " + newSalt2);
		// String token = (rs.getString("user_id") + ":" + newSalt + ":" + newSalt2);
		// String hashedToken = newHash.hash(token, newSalt);
		// PreparedStatement update = con.prepareStatement("UPDATE user_access_token SET
		// user_local_token = ?, token_expire_time = ?, last_access_time = ? WHERE
		// user_id = ?;");
		// update.setString(1, hashedToken);
		// update.setInt(2, 5);
		// update.setTimestamp(3, now);
		// update.setString(4, rs.getString("user_id"));
		// update.execute();
		// String rawAccessToken = (rs.getString("user_id") + ":" + hashedToken);
		// String accessToken = Base64Encryption.encryption(rawAccessToken);
		// jsonLogin.setAccessToken(accessToken);
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
		return jsonLoginResult;
	}

	@RequestMapping(value = "/get_registration_token")
	public JSONLoginToken getRegistrationToken() {
		// 13digits token
		// 1,2,3digit = Reminder(2): 3-digit number divided by 13.(((8-76)*13)+2)
		// 4,5digit is useless;
		// 6,7digit = 2-digit odd number(11-99)
		// 8,9,10,11digit = Reminder(57):4-digit number divided by 83.(((13-120)*83)+57)
		// 12,13digit is garbage;
		Random rand = new Random();
		Integer oneTwoThree = ((rand.nextInt(69) + 8) * 13) + 2;
		Integer fourFive = (rand.nextInt(90) + 10);
		Integer sixSeven = ((rand.nextInt(45) + 6) * 2) - 1;
		Integer eightNineTenEleven = ((rand.nextInt(108) + 13) * 83) + 57;
		Integer twelveThirteen = (rand.nextInt(90) + 30) / 2;
		String token = oneTwoThree.toString() + fourFive.toString() + sixSeven.toString()
				+ eightNineTenEleven.toString() + twelveThirteen.toString();
		JSONLoginToken registrationToken = new JSONLoginToken();
		registrationToken.setCode("S");
		registrationToken.setData(token);
		return registrationToken;
	}

	@RequestMapping(value = "/local_register", method = RequestMethod.POST)
	public JSONRegisterResult localRegister(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "username") String username, @RequestParam(value = "password") String password,
			@RequestParam(value = "email") String email,
			@RequestParam(value = "registration_token") String registrationToken) {
		char[] token = registrationToken.toCharArray();
		JSONRegisterResult jsonRegResult = new JSONRegisterResult();
		JSONRegister json = new JSONRegister();
		// 13digits token
		// 1,2,3digit = Reminder(2): 3-digit number divided by 13.(((8-76)*13)+2)
		// 4,5digit is useless;
		// 6,7digit = 2-digit odd number(11-99)
		// 8,9,10,11digit = Reminder(57):4-digit number divided by 83.(((13-120)*83)+57)
		// 12,13digit is garbage;
		int oneTwoThree = getInt(token[0]) * 100 + getInt(token[1]) * 10 + getInt(token[2]);
		int sixSeven = getInt(token[5]) * 10 + getInt(token[6]);
		int eightNineTenEleven = getInt(token[7]) * 1000 + getInt(token[8]) * 100 + getInt(token[9]) * 10
				+ getInt(token[10]);
		if (token.length == 13 && (oneTwoThree % 13) == 2 && (sixSeven % 2) == 1 && (eightNineTenEleven % 83) == 57) {
			List<UserLocalAuth> localUser = loginDAO.getLocalUser(email);
			if (!CommonUtil.isNullOrEmpty(localUser)) {
				jsonRegResult.setCode("f");
				jsonRegResult.setDetail("Username already exists");
			} else {
				int userId = loginDAO.createNewLocalUser(email);
				String salt = "";
				try {
					salt = CommonUtil.getSalt();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String hashedPassword = CommonUtil.SHA512Hashing(password, salt);
				int success_local_auth = loginDAO.createNewUserLocalAuth(email, hashedPassword, userId, salt);
				int success_local_token = loginDAO.createNewUserLocalTokenRecord(userId);
				if (success_local_auth == 1 && success_local_token == 1) {
					json.setUsername(username);
					json.setUserId(userId);
					jsonRegResult.setCode("S");
					jsonRegResult.setData(json);
				} else {
					jsonRegResult.setCode("F");
					jsonRegResult.setDetail("Cannot create user,please try again");
				}
			}
		} else {
			jsonRegResult.setCode("F");
			jsonRegResult.setDetail("Invalid Token,please try again");
		}
		return jsonRegResult;
	}

	@RequestMapping(value = "/fb_login", method = RequestMethod.POST)
	public void fbLogin(HttpServletRequest request, HttpServletResponse response){
		System.out.println("fbLogin STARTED ");
		System.out.println("IP: " + request.getRemoteAddr());
		FBConnection fbConnection = new FBConnection();
		String redirectUrl = fbConnection.getFBAuthUrl();
		try {
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("IP: " + request.getRemoteAddr());
	}

	@RequestMapping(value = "/fb_auth")
	public JSONLoginResult fbAuth(HttpServletRequest request, HttpServletResponse response)  {
		System.out.println("fbAUTH STARTED ");
		JSONLoginResult jsonLoginResult = new JSONLoginResult();
		JSONLogin jsonLogin = new JSONLogin();
		String code = null;
		String fbAccessToken = null;
		int fbexpiresIn = 0;
		code = request.getParameter("code");
		System.out.println("code: " + code);
		if (code == null || code.equals("")) {
			fbAccessToken = request.getParameter("token");
			if (fbAccessToken == null || fbAccessToken.equals("")) {
				jsonLoginResult.setCode("F");
				jsonLoginResult.setDetail("Error: cannot get Facebook accesstoken.Please Login again");
			}
		} else {
			FBConnection fbConnection = new FBConnection();
			JSONObject FBJSON = null;
			FBJSON = fbConnection.getAccessToken(code);
			if (FBJSON != null) {
				fbAccessToken = FBJSON.getString("access_token");
				fbexpiresIn = FBJSON.getInt("expires_in");
			} else {
				jsonLoginResult.setCode("F");
				jsonLoginResult.setDetail("Error: cannot decrypt Facebook accesstoken.Please Login again");
			}
		}
		if (code != null || fbAccessToken != null) {
			FBGraph fbGraph = new FBGraph(fbAccessToken);
			String graph = fbGraph.getFBGraph();
			JSONObject fbProfileData = fbGraph.getGraphData(graph);
			if (fbProfileData != null) {
				// extract fb id,name and email
				String username = fbProfileData.getString("name");
				String facebookId = fbProfileData.getString("id");
				String email = fbProfileData.getString("email");
				// access DB and update data
				try {
					List<UserFacebookAuth> userFacebookAuth = loginDAO.getFacebookUser(facebookId);
					// check if user exists
					if (!CommonUtil.isNullOrEmpty(userFacebookAuth) && userFacebookAuth.size() == 1) {
						int userId = userFacebookAuth.get(0).getUserId();
						Timestamp lastAccessTime = new Timestamp(System.currentTimeMillis());
						int expires = 5;
						String newSalt = CommonUtil.getSalt();
						String newSalt2 = CommonUtil.getSalt();
						String token = (userId + ":" + newSalt + ":" + newSalt2);
						String hashedToken = CommonUtil.SHA256Hashing(token, newSalt);
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
								String localAccessToken = CommonUtil.base64Encryption(rawLocalAccessToken);
								jsonLogin.setAccessToken(localAccessToken);
								jsonLogin.setUsername(username);
								jsonLogin.setUserId(userId);
								jsonLoginResult.setCode("s");
								jsonLoginResult.setData(jsonLogin);
							} else {
								jsonLoginResult.setCode("F");
								jsonLoginResult.setDetail("Error: cannot get Facebook accesstoken.Please Login again");
							}
						} else {
							jsonLoginResult.setCode("F");
							jsonLoginResult.setDetail("Error: cannot get accesstoken(FB).Please Login again");
						}
					} else if (CommonUtil.isNullOrEmpty(userFacebookAuth)) {
						// create new user who logins via Facebook
						int newUserId = loginDAO.createNewFacebookUser(username, email);
						System.out.println("NEW USER: " + newUserId);
						Timestamp lastAccessTime = new Timestamp(System.currentTimeMillis());
						int expires = 5;
						String newSalt = CommonUtil.getSalt();
						String newSalt2 = CommonUtil.getSalt();
						String token = (newUserId + ":" + newSalt + ":" + newSalt2);
						String hashedToken = CommonUtil.SHA256Hashing(token, newSalt);
						// create new user's local token record
						int sccuessCreateNewUserLocalToken = loginDAO.createNewUserLocalToken(newUserId, hashedToken,
								expires, lastAccessTime);
						if (sccuessCreateNewUserLocalToken == 1) {
							// create new user's facebook auth record
							int sccuessCreateNewFacebookAuth = loginDAO.createNewUserFacebook(newUserId, facebookId,
									fbAccessToken, fbexpiresIn);
							if (sccuessCreateNewFacebookAuth == 1) {
								// use base64 encryption to generate token
								// for client
								String rawLocalAccessToken = (newUserId + ":" + hashedToken);
								String localAccessToken = CommonUtil.base64Encryption(rawLocalAccessToken);
								jsonLogin.setAccessToken(localAccessToken);
								jsonLogin.setUsername(username);
								jsonLogin.setUserId(newUserId);
								jsonLoginResult.setCode("S");
								jsonLoginResult.setData(jsonLogin);
							}
						}
					} else {
						jsonLoginResult.setCode("F");
						jsonLoginResult.setDetail("Error: Duplicated Facebook User");
					}
				} catch (Exception e) {
					jsonLoginResult.setCode("F");
					jsonLoginResult.setDetail("Please Contact Support.Error Message: " + e.getMessage());
				}
		

			} else {
				jsonLoginResult.setCode("f");
				jsonLoginResult.setDetail("Error: cannot get Facebook User Detail.Please Login again");
			}

		}
		return jsonLoginResult;
	}

	protected int getInt(char c) {
		return Character.getNumericValue(c);
	}
	
	@RequestMapping(value = "/forget_password", method = RequestMethod.POST)
	public JSONResult forgetPassword(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "email") String email)  {
		System.out.println("forgetPassword STARTED.");
		JSONResult jsonObject = new JSONResult();
		String newPassword = CommonUtil.randomChar();
		List<User> user = loginDAO.getUserbyEmail(email);
		Integer userId = user.get(0).getUserId();
		String newSalt = "";
		try {
			newSalt = CommonUtil.getSalt();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String newHashPassword = CommonUtil.SHA512Hashing(newPassword, newSalt);
		int successFlag = loginDAO.updateLocalUserPassword(userId, newHashPassword, newSalt);
		if (successFlag == 1) {
			boolean sendMail = MailUtil.MailSender(email, newPassword);
			if (sendMail) {
				jsonObject.setCode("S");
			} else {
				jsonObject.setCode("F");
				jsonObject.setDetail("Failed to send reset password email");
			}
		} else {
			jsonObject.setCode("F");
			jsonObject.setDetail("Failed to change password,please contact support");
		}

		
		return jsonObject;
	}
}
