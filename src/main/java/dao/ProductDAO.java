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
	
	//TESTED
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
	
	//TESTED
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
	
	//TESTED
	public Integer deleteProduct(final Integer productId) {  
		String sqlStr = "UPDATE product SET product_status = 'N' WHERE product_id = ? ";
		
		int successFlag = template.update(sqlStr, 
	    		new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setInt(1, productId);	
			}
		});
		
		return successFlag;
	}
	
	//TESTED
	public List<Product> searchProductByTitle(final String productTitle) {
		String sqlStr = "SELECT * FROM product WHERE product_title like ? ";
		
		List<Product> productList = template.query(sqlStr, 
		    		new PreparedStatementSetter() {
		    			public void setValues(PreparedStatement preparedStatement) throws SQLException {
		    				preparedStatement.setString(1, "%" + productTitle + "%");
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

	//TESTED
	public int addProduct(final String productTitle, final String productDesc, final Double productPrice, final String productType, 
			final String productImgName, final String productImg2Name, final String productImg3Name) {
		String sqlStr = "INSERT INTO product (product_title, product_desc, product_price, product_type, product_img, product_img2, product_img3) VALUES (?, ?, ?, ?, ?, ?, ?) ";
		
		int successFlag = template.update(sqlStr, 
	    		new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, productTitle);	
				preparedStatement.setString(2, productDesc);
				preparedStatement.setDouble(3, productPrice);
				preparedStatement.setString(4, productType);
				preparedStatement.setString(5, productImgName);
				preparedStatement.setString(6, productImg2Name);
				preparedStatement.setString(7, productImg3Name);
			}
		});
		
		return successFlag;
	}
	
	//TESTED
	public Integer editProduct(final Integer productId, final String productTitle, final String productDesc, final Double productPrice, final String productType) {
		String sqlStr = "UPDATE product SET product_title = ?, product_desc = ?, product_type = ?, product_price = ? WHERE product_id = ? ";
		
		int successFlag = template.update(sqlStr, 
	    		new PreparedStatementSetter() {
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, productTitle);
				preparedStatement.setString(2, productDesc);
				preparedStatement.setString(3, productType);
				preparedStatement.setDouble(4, productPrice);
				preparedStatement.setInt(5, productId);	
			}
		});
		
		return successFlag;
	}
	
	
	private static Product setProduct(ResultSet rs) {
		Product product = new Product();
		try {
			product.setProductId(rs.getInt("product_id"));
			product.setProductImg(rs.getString("product_img"));
			product.setProductImg2(rs.getString("product_img2"));
			product.setProductImg3(rs.getString("product_img3"));
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