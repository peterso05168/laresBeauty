package bean;

import java.sql.Timestamp;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class UserFavouritePost {
	private Integer postId;
	private Integer userId;
	private Integer userFavouritePostId;
	private Timestamp createdDate;
	private Timestamp lastUpdatedDate;

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserFavouritePostId() {
		return userFavouritePostId;
	}

	public void setUserFavouritePostId(Integer userFavouritePostId) {
		this.userFavouritePostId = userFavouritePostId;
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