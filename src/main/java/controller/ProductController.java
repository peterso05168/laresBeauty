package controller;

import bean.Product;
import dao.ProductDAO;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
public class ProductController {
 
	@RequestMapping(value = "/shop/featured", method = RequestMethod.GET, headers="Accept=application/json")
	public List<Product> getFeaturedProducts() {
		List<Product> featuredProductList = new ArrayList<Product>();
		//HARDCODED TO TEST API ONLY.
		Product testingPurpose = new Product();
		featuredProductList.add(testingPurpose);
		featuredProductList.add(testingPurpose);
		return featuredProductList;
	}
	
	@RequestMapping(value = "/shop/category_detail", method = RequestMethod.POST, headers="Accept=application/json")
	public List<Product> getCategoryProducts(HttpServletRequest request) {
		String productType = request.getParameter("category_id");
		List<Product> categoryProductList = ProductDAO.getCategoryProducts(productType);
		//HARDCODED TO TEST API ONLY.
		Product testingPurpose = new Product();
		categoryProductList.add(testingPurpose);
		categoryProductList.add(testingPurpose);
		return categoryProductList;
	}
	
}