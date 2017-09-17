package bean;

import java.sql.Timestamp;

public class UserAccessToken {
	private int id;
	private int userId;
	private String userLocalToken;
	private int tokenExpireTime;
	private Timestamp lastAccessTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserLocalToken() {
		return userLocalToken;
	}
	public void setUserLocalToken(String userLocalToken) {
		this.userLocalToken = userLocalToken;
	}
	public int getTokenExpireTime() {
		return tokenExpireTime;
	}
	public void setTokenExpireTime(int tokenExpireTime) {
		this.tokenExpireTime = tokenExpireTime;
	}
	public Timestamp getLastAccessTime() {
		return lastAccessTime;
	}
	public void setLastAccessTime(Timestamp lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

}
