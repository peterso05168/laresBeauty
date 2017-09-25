package jsonobject;

public class JSONProduct{
	private String code;
	private String detail;
	private JSONProductDataList data;
	
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
	public JSONProductDataList getData() {
		return data;
	}
	public void setData(JSONProductDataList data) {
		this.data = data;
	}
}