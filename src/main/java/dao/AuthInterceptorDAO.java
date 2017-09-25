package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import bean.UserAccessToken;

public class AuthInterceptorDAO {

	JdbcTemplate template;

	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}

	public List<UserAccessToken> getUserAccessToken(final Integer userId, final String userToken) {
		String sqlStr = "SELECT * FROM user_access_token WHERE user_id = ? AND user_local_token = ?;";

		List<UserAccessToken> userAccessToken = template.query(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userId);
				preparedStatement.setString(2, userToken);
			}
		}, new RowMapper<UserAccessToken>() {
			public UserAccessToken mapRow(ResultSet rs, int row) throws SQLException {
				UserAccessToken e = setUserAccessToken(rs);
				return e;
			}
		});

		return userAccessToken;
	}

	public Integer updateLastAccessTime(final Integer userId, final Timestamp lastAccessTime) {
		String sqlStr = "UPDATE user_access_token SET last_access_time = ? WHERE user_id = ?;";
		int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setTimestamp(1, lastAccessTime);
				preparedStatement.setInt(2, userId);
			}
		});

		return successFlag;
	}

	private UserAccessToken setUserAccessToken(ResultSet rs) {
		UserAccessToken userAccessToken = new UserAccessToken();
		try {
			userAccessToken.setId(rs.getInt("id"));
			userAccessToken.setUserId(rs.getInt("user_id"));
			userAccessToken.setUserLocalToken(rs.getString("user_local_token"));
			userAccessToken.setTokenExpireTime(rs.getInt("token_expire_time"));
			userAccessToken.setLastAccessTime(rs.getTimestamp("last_access_time"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userAccessToken;
	}
}
