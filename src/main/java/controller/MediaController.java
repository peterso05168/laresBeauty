package controller;

import bean.Post;
import bean.UserFavouritePost;
import dao.FileDAO;
import dao.PostDAO;
import jsonobject.JSONResult;
import util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
		} else {
			postList = new ArrayList<Post>();
			jsonResult.setCode("S");
			jsonResult.setDetail("No result found");
			logger.info("getPosts() failed with error: No result found");
		}
		return jsonResult;
	}
	
	@RequestMapping(value = "/get_posts_detail", headers = "Accept=application/json")
	public JSONResult getPostsDetail(@RequestParam(value = "post_id") Integer postId) {
		logger.info("getPostsDetail() started");
		JSONResult jsonResult = new JSONResult();
		List<Post> postList = postDAO.getPostsDetail(postId);
		if (!CommonUtil.isNullOrEmpty(postList)) {
			jsonResult.setCode("S");
			jsonResult.setData(postList);
			logger.info("getPostsDetail() success");
		} else {
			postList = new ArrayList<Post>();
			jsonResult.setCode("S");
			jsonResult.setDetail("No result found");
			logger.info("getPostsDetail() failed with error: No result found");
		}
		return jsonResult;
	}

	@RequestMapping(value = "/add_posts", headers = "Accept=application/json")
	public JSONResult addPosts(@RequestParam(value = "post_title") String postTitle,
			@RequestParam(value = "post_content") String postContent,
			@RequestParam(value = "post_status") String postStatus, @RequestParam(value = "post_type") String postType,
			@RequestParam("post_img") MultipartFile postImg) {
		logger.info("addPosts() started with post_title = " + postTitle + ", postContent = " + postContent
				+ ", postStatus = " + postStatus + ", postType = " + postType + ", postImg = " + postImg);
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
	public JSONResult editPosts(@RequestParam(value = "post_id") Integer postId,
			@RequestParam(value = "post_title") String postTitle,
			@RequestParam(value = "post_content") String postContent,
			@RequestParam(value = "post_type") String postType,
			@RequestParam(value = "post_status") String postStatus,
			@RequestParam("post_img") MultipartFile postImg) {

		logger.info("editPosts() started with post_title = " + postTitle + ", postContent = " + postContent
				+ ", postStatus = " + postStatus + ", postType = " + postType + ", postId = " + postId);
		JSONResult jsonResult = new JSONResult();

		int successFlag = 0;
		try {
			String fileName = new String(fileDAO.fileUpload(postImg));
			
			successFlag += postDAO.editPost(postId, postTitle, postContent, postStatus, postType, fileName);

			if (successFlag == 0) {
				jsonResult.setCode("F");
				jsonResult.setDetail("Edit item failed, possible due to wrong post_id.");
				logger.error("editPosts() failed with error: wrong productId");
			} else {
				jsonResult.setCode("S");
				logger.info("editPosts() success");
			}
		} catch (Exception e) {
			jsonResult.setCode("F");
			jsonResult.setDetail("Fail to edit item due to : " + e.getMessage());
			logger.error("editPosts() failed with error: " + e.getMessage());
		}
		
		return jsonResult;
	}
	
	@RequestMapping(value = "search_post_by_title", method = RequestMethod.POST)
	public JSONResult searchPostByTitle(@RequestParam(value = "post_title") String postTitle) {
		logger.info("searchPostByTitle() started with postTitle = " + postTitle);
		JSONResult jsonObject = new JSONResult();
		try {
			List<Post> postList = postDAO.searchPostByTitle(postTitle);
			if (!CommonUtil.isNullOrEmpty(postList)) {
				jsonObject.setCode("S");
				jsonObject.setData(postList);
				logger.info("searchPostByTitle() success with return value = " + postList);
			} else {
				postList = new ArrayList<Post>();
				jsonObject.setCode("S");
				jsonObject.setDetail("No result is found.");
				logger.error("searchPostByTitle() failed with error: no result is found");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Error occured: " + e.getMessage());
			logger.error("searchPostByTitle() failed with error: " + e.getMessage());
		}

		return jsonObject;
	}
	
	@RequestMapping(value = "/add_to_favourite_post", headers = "Accept=application/json")
	public JSONResult addToFavourite(@RequestParam(value = "post_id") Integer postId,
			@RequestParam(value = "user_id") Integer userId) {

		logger.info("addToFavourite() started with postId = " + postId +", userId = " + userId);
		JSONResult jsonResult = new JSONResult();

		int successFlag = 0;
		try {
			List<UserFavouritePost> existPostLst = postDAO.checkFavouritePostExist(postId, userId);
			if (!CommonUtil.isNullOrEmpty(existPostLst)) {
				jsonResult.setCode("F");
				jsonResult.setDetail("add item failed, post already favourited");
				logger.error("addToFavourite() failed with error: post already favourited");
				return jsonResult;
			}
			
			successFlag += postDAO.addToFavourite(postId, userId);

			if (successFlag == 0) {
				jsonResult.setCode("F");
				jsonResult.setDetail("add item failed, possible due to wrong post_id.");
				logger.error("addToFavourite() failed with error: wrong productId");
			} else {
				jsonResult.setCode("S");
				logger.info("addToFavourite() success");
			}
		} catch (Exception e) {
			jsonResult.setCode("F");
			jsonResult.setDetail("Fail to add item due to : " + e.getMessage());
			logger.error("addToFavourite() failed with error: " + e.getMessage());
		}
		
		return jsonResult;
	}

}