package controller;

import java.util.ArrayList;
import java.util.List;
 
import bean.Country;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
public class PostController {
 
	@RequestMapping(value = "/posts", method = RequestMethod.GET, headers="Accept=application/json")
	public Country getCountries() {
		return new Country(1, "qq");
	}
	
}