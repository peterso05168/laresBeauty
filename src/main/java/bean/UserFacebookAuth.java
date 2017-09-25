package bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class UserFacebookAuth {
	private int id;
	private int userId;
	private String facebookId;
	private String facebookAccessToken;
	private int facebookExpires;

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

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getFacebookAccessToken() {
		return facebookAccessToken;
	}

	public void setFacebookAccessToken(String facebookAccessToken) {
		this.facebookAccessToken = facebookAccessToken;
	}

	public int getFacebookExpires() {
		return facebookExpires;
	}

	public void setFacebookExpires(int facebookExpires) {
		this.facebookExpires = facebookExpires;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
