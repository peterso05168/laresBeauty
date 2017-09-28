package jsonobject;

public class JSONLoginResult {
	private String code;
	private String detail;
	private JSONLogin data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public JSONLogin getData() {
		return data;
	}

	public void setData(JSONLogin data) {
		this.data = data;
	}

}