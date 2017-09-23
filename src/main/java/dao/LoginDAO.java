package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import bean.User;
import bean.UserFacebookAuth;
import bean.UserLocalAuth;

public class LoginDAO {

	JdbcTemplate template;

	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}
	
	public List<User> getUser(final Integer userId) {
		String sqlStr = "SELECT * FROM user WHERE user_id = ?";

		List<User> user = template.query(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userId);
			}
		}, new RowMapper<User>() {
			public User mapRow(ResultSet rs, int row) throws SQLException {
				User e = setUser(rs);
				return e;
			}
		});

		return user;
	}
	
	public List<UserLocalAuth> getLocalUser(final String username) {
		String sqlStr = "SELECT * FROM user_local_auth WHERE username = ?";

		List<UserLocalAuth> userLocalAuth = template.query(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, username);
			}
		}, new RowMapper<UserLocalAuth>() {
			public UserLocalAuth mapRow(ResultSet rs, int row) throws SQLException {
				UserLocalAuth e = setUserLocalAuth(rs);
				return e;
			}
		});

		return userLocalAuth;
	}
	
	public List<UserLocalAuth> getLocalUserById(final Integer userId) {
		String sqlStr = "SELECT * FROM user_local_auth WHERE user_id = ?";

		List<UserLocalAuth> userLocalAuth = template.query(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userId);
			}
		}, new RowMapper<UserLocalAuth>() {
			public UserLocalAuth mapRow(ResultSet rs, int row) throws SQLException {
				UserLocalAuth e = setUserLocalAuth(rs);
				return e;
			}
		});

		return userLocalAuth;
	}
	public List<UserLocalAuth> localAuth(final String username, final String password) {
		String sqlStr = "SELECT * FROM user_local_auth WHERE username = ? AND password = ?";

		List<UserLocalAuth> userLocalAuth = template.query(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, username);
				preparedStatement.setString(2, password);
			}
		}, new RowMapper<UserLocalAuth>() {
			public UserLocalAuth mapRow(ResultSet rs, int row) throws SQLException {
				UserLocalAuth e = setUserLocalAuth(rs);
				return e;
			}
		});

		return userLocalAuth;
	}
	
	public Integer updateUserLocalToken(final Integer userId, final String token, final Integer expires,
			final Timestamp lastAccessTime) {
		String sqlStr = "UPDATE user_access_token SET user_local_token = ?, token_expire_time = ?, last_access_time = ? WHERE user_id = ?;";
		int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, token);
				preparedStatement.setInt(2, expires);
				preparedStatement.setTimestamp(3, lastAccessTime);
				preparedStatement.setInt(4, userId);
			}
		});

		return successFlag;
	}
	
	public List<UserFacebookAuth> getFacebookUser(final String facebookId) {
		String sqlStr = "SELECT * from user_facebook_auth WHERE facebook_id = ?";

		List<UserFacebookAuth> facebookUser = template.query(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, facebookId);
			}
		}, new RowMapper<UserFacebookAuth>() {
			public UserFacebookAuth mapRow(ResultSet rs, int row) throws SQLException {
				UserFacebookAuth e = setUserFacebookAuth(rs);
				return e;
			}
		});

		return facebookUser;
	}
	
	public Integer updateUserFacebookToken(final Integer userId,final String fbAccessToken,final Integer fbexpiresIn,final String facebookId) {
		String sqlStr = "UPDATE user_facebook_auth SET facebook_id = ?,facebook_access_token = ?, facebook_expires = ? WHERE user_id = ?;";
          int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, facebookId);
				preparedStatement.setString(2, fbAccessToken);
				preparedStatement.setInt(3, fbexpiresIn);
				preparedStatement.setInt(4, userId);
			}
		});

		return successFlag;
	}
	
	public Integer updateLocalUserPassword(final Integer userId,final String newPassword,final String newSalt) {
		String sqlStr = "UPDATE user_local_auth SET password = ?,salt = ?WHERE user_id = ?;";
          int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, newPassword);
				preparedStatement.setString(2, newSalt);
				preparedStatement.setInt(3, userId);
			}
		});

		return successFlag;
	}
	public Integer createNewFacebookUser(final String username) {
		final PreparedStatementCreator psc = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
				final PreparedStatement ps = connection.prepareStatement("INSERT INTO user (user_name)VALUES(?);",
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, username);
				return ps;
			}
		};
		final KeyHolder holder = new GeneratedKeyHolder();

		template.update(psc, holder);

		return holder.getKey().intValue();
	}
	
	public Integer createNewLocalUser(final String email) {
		final PreparedStatementCreator psc = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
				final PreparedStatement ps = connection.prepareStatement("INSERT INTO user (user_email)VALUES(?);",
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, email);
				return ps;
			}
		};
		final KeyHolder holder = new GeneratedKeyHolder();

		template.update(psc, holder);

		return holder.getKey().intValue();
	}
	
	public Integer createNewUserLocalAuth(final String email,final String password,final int userId,final String salt) {
		final PreparedStatementCreator psc = new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
				final PreparedStatement ps = connection.prepareStatement("INSERT INTO user_local_auth (user_id,username,password,salt)VALUES(?,?,?,?);",
						Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, userId);
				ps.setString(2, email);
				ps.setString(3, password);
				ps.setString(4, salt);
				return ps;
			}
		};
		final KeyHolder holder = new GeneratedKeyHolder();

		int successFlag = template.update(psc, holder);

		return successFlag;
	}
	
	public Integer createNewUserLocalTokenRecord(final Integer userId) {
		String sqlStr = "INSERT INTO user_access_token (user_id) VALUES (?);";
		int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userId);
			}
		});

		return successFlag;
	}
	
	public Integer createNewUserLocalToken(final Integer userId, final String token, final Integer expires,
			final Timestamp lastAccessTime) {
		String sqlStr = "INSERT INTO user_access_token (user_id,user_local_token, token_expire_time , last_access_time) VALUES (?,?,?,?);";
		int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userId);
				preparedStatement.setString(2, token);
				preparedStatement.setInt(3, expires);
				preparedStatement.setTimestamp(4, lastAccessTime);
			}
		});

		return successFlag;
	}
	
	public Integer createNewUserFacebook(final Integer userId, final String facebookId, final String fbAccessToken,
			final int fbexpiresIn) {
		String sqlStr = "INSERT INTO user_facebook_auth (user_id, facebook_id, facebook_access_token , facebook_expires) VALUES (?,?,?,?);";
		int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userId);
				preparedStatement.setString(2, facebookId);
				preparedStatement.setString(3, fbAccessToken);
				preparedStatement.setInt(4, fbexpiresIn);
			}
		});

		return successFlag;
	}

	private User setUser(ResultSet rs) {
		User user = new User();
		try {
			user.setUserId(rs.getInt("user_id"));
			user.setUserNameEng(rs.getString("user_name"));
			user.setUserNameChi(rs.getString("user_name_chi"));
			user.setUserTypeId(rs.getInt("user_type_id"));
			user.setUserStatId(rs.getInt("user_stat_id"));
			user.setUserLoyalty(rs.getInt("user_loyalty"));
			user.setCreatedDate(rs.getTimestamp("created_date"));
			user.setLastUpdatedDate(rs.getTimestamp("last_updated_date"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	
	private UserLocalAuth setUserLocalAuth(ResultSet rs) {
		UserLocalAuth userLocalAuth = new UserLocalAuth();
		try {
			userLocalAuth.setId(rs.getInt("id"));
			userLocalAuth.setUserId(rs.getInt("user_id"));
			userLocalAuth.setUsername(rs.getString("username"));
			userLocalAuth.setPassword(rs.getString("password"));
			userLocalAuth.setSalt(rs.getString("salt"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userLocalAuth;
	}
	
	private UserFacebookAuth setUserFacebookAuth(ResultSet rs) {
		UserFacebookAuth userFacebookAuth = new UserFacebookAuth();
		try {
			userFacebookAuth.setId(rs.getInt("id"));
			userFacebookAuth.setUserId(rs.getInt("user_id"));
			userFacebookAuth.setFacebookId(rs.getString("facebook_id"));
			userFacebookAuth.setFacebookAccessToken(rs.getString("facebook_access_token"));
			userFacebookAuth.setFacebookExpires(rs.getInt("facebook_expires"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userFacebookAuth;
	}
}
