package dao;

import bean.ShoppingDetail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
 
public class ShoppingDetailDAO {
	
	JdbcTemplate template;  
	  
	public void setTemplate(JdbcTemplate template) {  
	    this.template = template;  
	}  
	
	public Integer addShoppingDetail(final Integer userId, final Integer productId, final Integer productQuantity) {  
		String sqlStr = "INSERT INTO shopping_detail (user_id, product_id, product_quantity, status) VALUES (?, ?, ?, 'C') ";
		
		int successFlag = template.update(sqlStr, 
	    		new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userId);
				preparedStatement.setInt(2, productId);
				preparedStatement.setInt(3, productQuantity);
			}
		});
		
		return successFlag;
	}
	
	public Integer addShoppingDetailWithOrder(final Integer userId, final Integer productId, final Integer productQuantity, final String status, final Integer orderId) {  
		String sqlStr = "INSERT INTO shopping_detail (user_id, product_id, product_quantity, status, order_id) VALUES (?, ?, ?, ?, ?) ";
		
		int successFlag = template.update(sqlStr, 
	    		new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, userId);
				preparedStatement.setInt(2, productId);
				preparedStatement.setInt(3, productQuantity);
				preparedStatement.setString(4, status);
				preparedStatement.setInt(5, orderId);
			}
		});
		
		return successFlag;
	}
	
	public List<ShoppingDetail> checkShoppingDetail(final Integer userId, final Integer productId) {  
		String sqlStr = "SELECT * FROM shopping_detail WHERE user_id = ? AND product_id = ? AND status <> 'N' ";
		
		List<ShoppingDetail> shoppingDetailList = template.query(sqlStr, 
		    		new PreparedStatementSetter() {
		    			public void setValues(PreparedStatement preparedStatement) throws SQLException {
		    				preparedStatement.setInt(1, userId);
		    				preparedStatement.setInt(2, productId);
		    			}
		    		}, 
		    		new RowMapper<ShoppingDetail>(){  
		    			public ShoppingDetail mapRow(ResultSet rs, int row) throws SQLException {  
		    				ShoppingDetail e = setShoppingDetail(rs);
		    				return e;  
		    			}  
		    		}); 
		
		return shoppingDetailList; 
	}  
	
	public List<ShoppingDetail> getShoppingDetail(final Integer userId) {  
		String sqlStr = "SELECT * FROM shopping_detail WHERE user_id = ? AND status <> 'N' ";
		
		List<ShoppingDetail> shoppingDetailList = template.query(sqlStr, 
		    		new PreparedStatementSetter() {
		    			public void setValues(PreparedStatement preparedStatement) throws SQLException {
		    				preparedStatement.setInt(1, userId);
		    			}
		    		}, 
		    		new RowMapper<ShoppingDetail>(){  
		    			public ShoppingDetail mapRow(ResultSet rs, int row) throws SQLException {  
		    				ShoppingDetail e = setShoppingDetail(rs);
		    				return e;  
		    			}  
		    		}); 
		
		return shoppingDetailList; 
	} 
	
	public Integer updateShoppingDetailQuantity(final Integer userId, final Integer productId, final Integer productQuantity) {  
		String sqlStr = "UPDATE shopping_detail SET product_quantity = ? WHERE product_id = ? AND user_id = ? ";
		
		int successFlag = template.update(sqlStr, 
	    		new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, productQuantity);
				preparedStatement.setInt(2, productId);
				preparedStatement.setInt(3, userId);			
			}
		});
		
		return successFlag;
	}
	
	public Integer updateShoppingDetailStatus(final Integer userId, final Integer productId, final String status, final Integer orderId) {  
		String sqlStr = "UPDATE shopping_detail SET status = ?, order_id = ? WHERE product_id = ? AND user_id = ? ";
		
		int successFlag = template.update(sqlStr, 
	    		new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, status);
				preparedStatement.setInt(2, orderId);
				preparedStatement.setInt(3, productId);
				preparedStatement.setInt(4, userId);			
			}
		});
		
		return successFlag;
	}
	
	public Integer deleteShoppingDetail(final Integer userId, final Integer productId) {  
		String sqlStr = "UPDATE shopping_detail SET status = 'N' WHERE product_id = ? AND user_id = ? ";
		
		int successFlag = template.update(sqlStr, 
	    		new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, productId);
				preparedStatement.setInt(2, userId);			
			}
		});
		
		return successFlag;
	}
	
	private static ShoppingDetail setShoppingDetail(ResultSet rs) {
		ShoppingDetail shoppingDetail = new ShoppingDetail();
		try {
			shoppingDetail.setUserId(rs.getInt("user_id"));
			shoppingDetail.setProductId(rs.getInt("product_id"));
			shoppingDetail.setProductQuantity(rs.getInt("product_quantity"));
			shoppingDetail.setCreatedDate(rs.getTimestamp("created_date"));
			shoppingDetail.setLastUpdatedDate(rs.getTimestamp("last_updated_date"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return shoppingDetail;		
	}
	
}