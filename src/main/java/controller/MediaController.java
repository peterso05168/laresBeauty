package controller;

import bean.Post;
import dao.FileDAO;
import dao.PostDAO;
import jsonobject.JSONResult;
import util.CommonUtil;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value = "media")

@CrossOrigin
@Transactional("tjtJTransactionManager")
@RestController
public class MediaController {

	@Autowired
	PostDAO postDAO;
	
	@Autowired
	FileDAO fileDAO;
	
	private static final Logger logger = Logger.getLogger(MediaController.class);
	
	@RequestMapping(value = "/get_posts", headers = "Accept=application/json")
	public JSONResult getPosts(@RequestParam(value = "post_status", defaultValue = "A") String postStatus,
			@RequestParam(value = "post_type", defaultValue = "F") String postType) {
		logger.info("getPosts() started");
		JSONResult jsonResult = new JSONResult();
		List<Post> postList = postDAO.getPosts(postStatus, postType);
		if (!CommonUtil.isNullOrEmpty(postList)) {
			jsonResult.setCode("S");
			jsonResult.setData(postList);
			logger.info("getPosts() success");
		}else {
			jsonResult.setCode("F");
			jsonResult.setDetail("No result found");
			logger.info("getPosts() failed with error: No result found");
		}
		return jsonResult;
	}
	
	@RequestMapping(value = "/add_posts", headers = "Accept=application/json")
	public JSONResult addPosts(@RequestParam(value = "post_title") String postTitle,
			@RequestParam(value = "post_content") String postContent,
			@RequestParam(value = "post_status") String postStatus,
			@RequestParam(value = "post_type") String postType,
			@RequestParam("post_img") MultipartFile postImg) {
		logger.info("addPosts() started");
		JSONResult jsonResult = new JSONResult();
		
		try {
			String fileName = new String(fileDAO.fileUpload(postImg));

			int successFlag = 0;

			successFlag += postDAO.addPost(postTitle, postContent, postStatus, postType, fileName);

			if (successFlag == 0) {
				jsonResult.setCode("F");
				jsonResult.setDetail("Add item failed.");
				logger.error("addProducts() failed");
			} else {
				jsonResult.setCode("S");
				logger.info("addProducts() success");
			}
		} catch (Exception e) {
			jsonResult.setCode("F");
			jsonResult.setDetail("Add item failed due to : " + e.getMessage());
			logger.error("addProducts() failed with error: " + e.getMessage());
		}
		return jsonResult;
	}
	
	@RequestMapping(value = "/edit_posts", headers = "Accept=application/json")
	public JSONResult editPosts(@RequestParam(value = "post_id") Integer productId,
			@RequestParam(value = "post_title") String productTitle,
			@RequestParam(value = "post_content") String productDesc,
			@RequestParam(value = "post_type") Double productPrice,
			@RequestParam(value = "post_status") String productType) {
		
		logger.info("editPosts() started");
		JSONResult jsonResult = new JSONResult();
		
		return jsonResult;
	}
	
	@RequestMapping(value = "/add_to_favourite", headers = "Accept=application/json")
	public Post addToFavourtiePosts() {
		return new Post();
	}

}