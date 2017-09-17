package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

public class OrderDAO {
	JdbcTemplate template;  
	  
	public void setTemplate(JdbcTemplate template) {  
	    this.template = template;  
	} 

	public int addOrder() {
		String sqlStr = "INSERT INTO product (product_title, product_desc, product_price, product_type) VALUES (?, ?, ?, ?) ";
		
//		int successFlag = template.update(sqlStr, 
//	    		new PreparedStatementSetter() {
//			public void setValues(PreparedStatement preparedStatement) throws SQLException {
//				preparedStatement.setString(1, productTitle);	
//				preparedStatement.setString(2, productDesc);
//				preparedStatement.setDouble(3, productPrice);
//				preparedStatement.setString(4, productType);
//			}
//		});
		
		return 0;
	}
}