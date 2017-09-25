package jsonobject;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import dto.ProductDTO;

public class JSONProductDataList {
	private List<ProductDTO> productTypeF;
	private List<ProductDTO> productTypeS;

	public List<ProductDTO> getProductTypeF() {
		return productTypeF;
	}

	public void setProductTypeF(List<ProductDTO> productTypeF) {
		this.productTypeF = productTypeF;
	}

	public List<ProductDTO> getProductTypeS() {
		return productTypeS;
	}

	public void setProductTypeS(List<ProductDTO> productTypeS) {
		this.productTypeS = productTypeS;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}