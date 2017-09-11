package bean;

public class UserAddress{
	private Integer userAddressInfoId;
	private Integer userId;
	private String recipientName;
	private String recipientTel;
	private String recipientAddress;
	private String status;
	
	public Integer getUserAddressInfoId() {
		return userAddressInfoId;
	}
	public void setUserAddressInfoId(Integer userAddressInfoId) {
		this.userAddressInfoId = userAddressInfoId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getRecipientName() {
		return recipientName;
	}
	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}
	public String getRecipientTel() {
		return recipientTel;
	}
	public void setRecipientTel(String recipientTel) {
		this.recipientTel = recipientTel;
	}
	public String getRecipientAddress() {
		return recipientAddress;
	}
	public void setRecipientAddress(String recipientAddress) {
		this.recipientAddress = recipientAddress;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}