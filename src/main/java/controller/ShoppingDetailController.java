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
 
@RequestMapping(value = "shop")

@RestController
public class ShoppingDetailController {
 
	@Autowired  
	ProductDAO productDAO;
	
	@RequestMapping(value = "add_to_shopping_detail", method = RequestMethod.POST, headers="Accept=application/json")
	public JSONObject getCategoryProducts(@RequestParam(value = "user_id") Integer userId, 
											@RequestParam(value = "product_detail") String productDetail) {
	
		JSONObject jsonObject = new JSONObject();
		
		
		return jsonObject;
	}
	
//	@RequestMapping(value = "get_product_detail", method = RequestMethod.POST, headers="Accept=application/json")
//	public JSONObject getProductsDetail(@RequestParam(value = "product_id") Integer productId) {
//		JSONObject jsonObject = new JSONObject();
//		try {
//			List<Product> productDetail = productDAO.getProductDetail(productId);
//			if (!CommonUtil.isNullOrEmpty(productDetail)) {
//				jsonObject.setCode("S");
//				jsonObject.setData(productDetail);
//			}else {
//				jsonObject.setCode("F");
//				jsonObject.setDetail("No result is found.");
//			}
//		}catch (Exception e) {
//			jsonObject.setCode("F");
//			jsonObject.setDetail("Error occured: " + e.getMessage());
//		}
//		
//		return jsonObject;
//	}
	
}