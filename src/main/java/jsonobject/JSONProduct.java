package jsonobject;

import java.util.List;

import bean.Product;

public class JSONProduct{
	private String code;
	private String detail;
	private List<Product> data;
	
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
	public List<Product> getData() {
		return data;
	}
	public void setData(List<Product> data) {
		this.data = data;
	}
}