package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import bean.OrderDetail;

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
	
	public List<OrderDetail> getOrder(final Integer userId) throws Exception {
		String sqlStr = "SELECT a.order_id, a.created_date, b.product_id, b.product_quantity, c.product_price, c.product_title  "
				+ "FROM order_detail a "
				+ "JOIN shopping_detail b "
				+ "JOIN product c "
				+ "WHERE a.order_id = b.order_id AND b.product_id = c.product_id AND a.user_id = ?";

		List<OrderDetail> orderDetailList = template.query(sqlStr, new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userId);
			}
		}, new RowMapper<OrderDetail>() {
			public OrderDetail mapRow(ResultSet rs, int row) throws SQLException {
				OrderDetail e = setOrderDetail(rs);
				return e;
			}
		});
		return orderDetailList;
	}
	
	private OrderDetail setOrderDetail(ResultSet rs) throws SQLException {
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setOrderId(rs.getInt("order_id"));
		orderDetail.setCreatedDate(rs.getTimestamp("created_date"));
		orderDetail.setProductId(rs.getInt("product_id"));
		orderDetail.setProductPrice(rs.getBigDecimal("product_price"));
		orderDetail.setProductQuantity(rs.getInt("product_quantity"));
		orderDetail.setProductTitle(rs.getString("product_title"));
		return orderDetail;
	}
}