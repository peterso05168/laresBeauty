package jsonobject;

public class JSONRegister {

	
	private String username;
	private boolean registration_status;
	private String error;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isRegistration_status() {
		return registration_status;
	}
	public void setRegistration_status(boolean registration_status) {
		this.registration_status = registration_status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}
