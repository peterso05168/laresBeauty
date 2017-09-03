package connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {      
	
    static Connection con = null;
    
    public static Connection getConnection() {
        if (con != null) {
        		return con;
        }
        // get db, user, pass from settings file
        return getDatabaseConnection();
    }

    private static Connection getDatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/lares_beauty?useSSL=false&user=root&password=hi7rr9dp3mp1");
        } catch(Exception e) {
            e.printStackTrace();
        }

        return con;        
    }
} 