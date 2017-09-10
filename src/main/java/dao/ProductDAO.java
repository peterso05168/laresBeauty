package dao;

import bean.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
 
public class ProductDAO {
	
	JdbcTemplate template;  
	  
	public void setTemplate(JdbcTemplate template) {  
	    this.template = template;  
	}  
	
	public List<Product> getCategoryProducts (final String productStatus, final String productType) {  
		String sqlStr = "SELECT * FROM product WHERE product_status IN ( ?, 'F' )";
		
		if (!productStatus.equals("F")) {
			sqlStr += " AND product_type = ?";
		}
		
		List<Product> productList = template.query(sqlStr, 
		    		new PreparedStatementSetter() {
		    			public void setValues(PreparedStatement preparedStatement) throws SQLException {
		    				preparedStatement.setString(1, productStatus);
		    				if (!productStatus.equals("F")) {
		    					preparedStatement.setString(2, productType);
		    				}
		    			}
		    		}, 
		    		new RowMapper<Product>(){  
		    			public Product mapRow(ResultSet rs, int row) throws SQLException {  
		    				Product e = setProduct(rs);
		    				return e;  
		    			}  
		    		}); 
		return productList; 
	} 
	
	public List<Product> getProductDetail(final Integer productId) {  
		String sqlStr = "SELECT * FROM product WHERE product_id = ? ";
		
		List<Product> productList = template.query(sqlStr, 
		    		new PreparedStatementSetter() {
		    			public void setValues(PreparedStatement preparedStatement) throws SQLException {
		    				preparedStatement.setInt(1, productId);
		    			}
		    		}, 
		    		new RowMapper<Product>(){  
		    			public Product mapRow(ResultSet rs, int row) throws SQLException {  
		    				Product e = setProduct(rs);
		    				return e;  
		    			}  
		    		}); 
		
		return productList; 
	}  
	
	
	private static Product setProduct(ResultSet rs) {
		Product product = new Product();
		try {
			product.setProductId(rs.getInt("product_id"));
			product.setProductImg(rs.getString("product_img"));
			product.setProductDesc(rs.getString("product_desc"));
			product.setProductPrice(rs.getBigDecimal("product_price"));
			product.setProductStatus(rs.getString("product_status"));
			product.setProductType(rs.getString("product_type"));
			product.setProductTitle(rs.getString("product_title"));
			product.setCreatedDate(rs.getTimestamp("created_date"));
			product.setLastUpdatedDate(rs.getTimestamp("last_updated_date"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return product;		
	}
	
}