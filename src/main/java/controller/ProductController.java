package controller;

import bean.Product;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
public class ProductController {
 
	@RequestMapping(value = "/products", method = RequestMethod.GET, headers="Accept=application/json")
	public Product getProducts() {
		return new Product();
	}
	
}