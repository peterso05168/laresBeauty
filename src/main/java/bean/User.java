package bean;

import java.sql.Timestamp;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class User {
	private int userId;
	private String userNameEng;
	private String userNameChi;
	private int userTypeId;
	private int userStatId;
	private int userLoyalty;
	private Timestamp createdDate;
	private Timestamp lastUpdatedDate;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserNameEng() {
		return userNameEng;
	}

	public void setUserNameEng(String userNameEng) {
		this.userNameEng = userNameEng;
	}

	public String getUserNameChi() {
		return userNameChi;
	}

	public void setUserNameChi(String userNameChi) {
		this.userNameChi = userNameChi;
	}

	public int getUserTypeId() {
		return userTypeId;
	}

	public void setUserTypeId(int userTypeId) {
		this.userTypeId = userTypeId;
	}

	public int getUserStatId() {
		return userStatId;
	}

	public void setUserStatId(int userStatId) {
		this.userStatId = userStatId;
	}

	public int getUserLoyalty() {
		return userLoyalty;
	}

	public void setUserLoyalty(int userLoyalty) {
		this.userLoyalty = userLoyalty;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
