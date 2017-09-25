package bean;

import java.sql.Timestamp;

import org.apache.commons.lang3.builder.ToStringBuilder;

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
