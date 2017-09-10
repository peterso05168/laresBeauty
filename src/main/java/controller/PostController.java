package controller;

import bean.Post;
import login.UserContext;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
@RequestMapping(value = "post")

@RestController
public class PostController {
 
	@RequestMapping(value = "/posts", headers="Accept=application/json")
	public Post getPosts() {
		System.out.println("Testing in getPost: " + UserContext.getCurrentUser().getUserId());
		return new Post();
	}
	
}