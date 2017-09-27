package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class OrderDAO {
	JdbcTemplate template;

	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}

	public Integer addOrder(final Integer userId, final Integer userAddressInfoId) throws Exception {
		final String sqlStr = "INSERT INTO order_detail (user_id, user_address_info_id) VALUES (?, ?) ";

		KeyHolder holder = new GeneratedKeyHolder();

		int successFlag = template.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sqlStr, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, userId);
				ps.setInt(2, userAddressInfoId);
				return ps;
			}
		}, holder);

		Integer orderId = holder.getKey().intValue();

		if (successFlag == 0) {
			throw new Exception("Failed to create order.");
		}
		return orderId;
	}

}