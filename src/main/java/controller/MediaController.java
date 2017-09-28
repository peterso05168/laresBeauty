package controller;

import bean.Post;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "media")

@CrossOrigin
@Transactional("tjtJTransactionManager")
@RestController
public class MediaController {

	@RequestMapping(value = "/posts", headers = "Accept=application/json")
	public Post getPosts() {
		return new Post();
	}

}