package controller;

import bean.Product;
import dao.ProductDAO;
import jsonobject.JSONProduct;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
public class ProductController {
 
	@RequestMapping(value = "/shop/featured", method = RequestMethod.GET, headers="Accept=application/json")
	public JSONProduct getFeaturedProducts() {
		List<Product> featuredProductList = ProductDAO.getFeaturedProducts();
		JSONProduct jsonProduct = new JSONProduct();
		jsonProduct.setCode("s");
		jsonProduct.setMessage("");
		jsonProduct.setData(featuredProductList);
		return jsonProduct;
	}
	
	@RequestMapping(value = "/shop/category_detail", method = RequestMethod.POST, headers="Accept=application/json")
	public List<Product> getCategoryProducts(HttpServletRequest request) {
		String productType = request.getParameter("category_id");
		List<Product> categoryProductList = ProductDAO.getCategoryProducts(productType);
		return categoryProductList;
	}
	
}