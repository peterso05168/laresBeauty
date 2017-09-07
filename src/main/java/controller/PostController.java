package controller;

import bean.Post;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
public class PostController {
 //23342
	@RequestMapping(value = "/posts", method = RequestMethod.GET, headers="Accept=application/json")
	public Post getPosts() {
		return new Post();
	}
	
}