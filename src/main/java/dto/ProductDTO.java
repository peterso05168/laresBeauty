package dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ProductDTO{
	private int productId;
	private String productTitle;
	private String productImg;
	private List<ProductImg> detailImgs;
	private String productDesc;
	private String productPrice;
	private String productStatus;
	private String productType;
	
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
	public String getProductImg() {
		return productImg;
	}
	public void setProductImg(String productImg) {
		this.productImg = productImg;
	}
	public List<ProductImg> getDetailImgs() {
		return detailImgs;
	}
	public void setDetailImgs(List<String> imgList) {
		List<ProductImg> detailImgs = new ArrayList<ProductImg>();
		for (String img : imgList) {
			ProductImg productImg = new ProductImg();
			productImg.setProductImg(img);
			detailImgs.add(productImg);
		}
		this.detailImgs = detailImgs;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public String getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}
	public String getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	@Override
	public String toString() {
	  return ToStringBuilder.reflectionToString(this);
	}
}

class ProductImg {
	
	private String productImg;

	public String getProductImg() {
		return productImg;
	}

	public void setProductImg(String productImg) {
		this.productImg = productImg;
	}
	
	@Override
	public String toString() {
	  return ToStringBuilder.reflectionToString(this);
	}
	
}