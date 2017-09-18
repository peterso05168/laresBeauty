package controller;

import bean.Post;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 

@RequestMapping(value = "media")

@RestController
public class MediaController {

	@RequestMapping(value = "/posts", headers="Accept=application/json")
	public Post getPosts() {
		return new Post();
	}
	
}