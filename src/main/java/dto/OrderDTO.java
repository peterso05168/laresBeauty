package dto;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class OrderDTO {
	private Integer orderId;
	private String date;
	private List<OrderProductDTO> orders;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<OrderProductDTO> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderProductDTO> orders) {
		this.orders = orders;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
