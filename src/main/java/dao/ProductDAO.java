package dao;

import bean.Product;
import connection.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
 
public class ProductDAO {
	public static List<Product> getFeaturedProducts() {
		List<Product> returnlist = new ArrayList<Product>();
		
		Connection con = ConnectionManager.getConnection();
		try {
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM product;");
			while (rs.next()) {
				returnlist.add(setProduct(rs));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnlist;
	}
	
	
	public static List<Product> getCategoryProducts(String productType) {
		List<Product> returnlist = new ArrayList<Product>();
		
		Connection con = ConnectionManager.getConnection();
		try {
			PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM product WHERE product_type = ?;");
			preparedStatement.setString(1, productType);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				returnlist.add(setProduct(rs));
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnlist;
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