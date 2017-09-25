package dao;

import bean.UserAddress;
import util.CommonUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

public class UserDAO {

	JdbcTemplate template;

	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}

	public List<UserAddress> getUserAddress(final Integer userId) {
		String sqlStr = "SELECT * FROM user_address_info WHERE user_id = ? AND status <> 'N' ";

		List<UserAddress> userAddressList = template.query(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userId);
			}
		}, new RowMapper<UserAddress>() {
			public UserAddress mapRow(ResultSet rs, int row) throws SQLException {
				UserAddress e = setUserAddress(rs);
				return e;
			}
		});

		return userAddressList;
	}

	public Integer deleteUserAddress(final Integer userAddressInfoId) {
		String sqlStr = "UPDATE user_address_info SET status = 'N' WHERE user_address_info_id = ? ";

		int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userAddressInfoId);
			}
		});

		return successFlag;
	}

	public Integer changeDefaultUserAddress(final Integer userId, final Integer userAddressInfoId) throws Exception {
		String sqlStr = "SELECT * FROM user_address_info WHERE user_id = ? AND status = 'S' ";

		final List<UserAddress> userAddressList = template.query(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userId);
			}
		}, new RowMapper<UserAddress>() {
			public UserAddress mapRow(ResultSet rs, int row) throws SQLException {
				UserAddress e = setUserAddress(rs);
				return e;
			}
		});

		if (!CommonUtil.isNullOrEmpty(userAddressList)) {
			sqlStr = "UPDATE user_address_info SET status = 'A' WHERE user_address_info_id = ? ";

			int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
				public void setValues(PreparedStatement preparedStatement) throws SQLException {
					preparedStatement.setInt(1, userAddressList.get(0).getUserAddressInfoId());
				}
			});

			if (successFlag == 0) {
				throw new Exception("Failed to reset current selected user address to active status.");
			}
		}

		sqlStr = "UPDATE user_address_info SET status = ? WHERE user_address_info_id = ? ";

		int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, "S");
				preparedStatement.setInt(2, userAddressInfoId);
			}
		});

		return successFlag;
	}

	public Integer updateUserAddress(final String status, final Integer userAddressInfoId) {
		String sqlStr = "UPDATE user_address_info SET status = ? WHERE user_address_info_id = ? ";

		int successFlag = template.update(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, status);
				preparedStatement.setInt(2, userAddressInfoId);
			}
		});

		return successFlag;
	}

	private UserAddress setUserAddress(ResultSet rs) {
		UserAddress userAddress = new UserAddress();
		try {
			userAddress.setUserId(rs.getInt("user_id"));
			userAddress.setUserAddressInfoId(rs.getInt("user_address_info_id"));
			userAddress.setRecipientName(rs.getString("recipient_name"));
			userAddress.setRecipientTel(rs.getString("recipient_tel"));
			userAddress.setRecipientAddress(rs.getString("recipient_address"));
			userAddress.setStatus(rs.getString("status"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userAddress;
	}

}