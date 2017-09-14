package controller;

import bean.Product;
import dao.ProductDAO;
import jsonobject.JSONObject;
import util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
@RequestMapping(value = "product")

@RestController
public class ProductController {
 
	@Autowired  
	ProductDAO productDAO;
	
	
	//FOR TESTING PURPOSE
	@RequestMapping(value = "featured", method = RequestMethod.GET, headers="Accept=application/json")
	public JSONObject getFeaturedProducts() {
		return null;
	}
	
	@RequestMapping(value = "get_product", method = RequestMethod.POST, headers="Accept=application/json")
	public JSONObject getCategoryProducts(@RequestParam(value = "product_status", defaultValue = "A") String productStatus, 
											@RequestParam(value = "product_type") String productType) {
	
		JSONObject jsonObject = new JSONObject();
		
		try {
			List<Product> categoryProductList = productDAO.getCategoryProducts(productStatus, productType);
			if (!CommonUtil.isNullOrEmpty(categoryProductList)) {
					jsonObject.setCode("S");
					
					//special handling for result data
					List<List<Product>> finalDataList = new ArrayList<List<Product>>();
					List<Product> twoPerData = new ArrayList<Product>();
					int counter = 0;
					for (int i = 0; i < categoryProductList.size(); i++, counter++) {
						twoPerData.add(categoryProductList.get(i));
						if (counter == 1) {
							finalDataList.add(twoPerData);
							twoPerData = new ArrayList<Product>();
							counter = 0;
							continue;
						}
						if (categoryProductList.size() == 1) {
							finalDataList.add(twoPerData);
							continue;
						}
					}
					
					jsonObject.setData(finalDataList);		
			}else {
				jsonObject.setCode("F");
				jsonObject.setDetail("No result is found.");
			}
		}catch(Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Error occured: " + e.getMessage());
		}
		
		return jsonObject;
	}
	
	@RequestMapping(value = "get_product_detail", method = RequestMethod.POST, headers="Accept=application/json")
	public JSONObject getProductsDetail(@RequestParam(value = "product_id") Integer productId) {
		JSONObject jsonObject = new JSONObject();
		try {
			List<Product> productDetail = productDAO.getProductDetail(productId);
			if (!CommonUtil.isNullOrEmpty(productDetail)) {
				jsonObject.setCode("S");
				jsonObject.setData(productDetail);
			}else {
				jsonObject.setCode("F");
				jsonObject.setDetail("No result is found.");
			}
		}catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Error occured: " + e.getMessage());
		}
		
		return jsonObject;
	}
	
}