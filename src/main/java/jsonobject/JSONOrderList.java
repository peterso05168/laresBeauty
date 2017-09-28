package jsonobject;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import dto.OrderDTO;

public class JSONOrderList {
	private String code;
	private String detail;
	private List<OrderDTO> data;

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

	public List<OrderDTO> getData() {
		return data;
	}

	public void setData(List<OrderDTO> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}